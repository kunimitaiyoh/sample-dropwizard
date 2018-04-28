package com.example.sample.resources

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, InputStream}
import java.net.URLConnection
import java.time.Instant
import java.util.UUID
import javax.annotation.security.PermitAll
import javax.imageio.{ImageIO, ImageReader}
import javax.validation.Valid
import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.{BeanParam, Consumes, FormParam, GET, NotFoundException, POST, Path, PathParam, Produces}

import com.codahale.metrics.annotation.Timed
import com.example.sample.api.{Avatar, User}
import com.example.sample.dao.{AvatarDao, UserDao}
import com.example.sample.resources.UsersResource.{AvatarParams, UserParams}
import com.google.common.io.ByteStreams
import io.dropwizard.auth.Auth
import io.dropwizard.validation.ValidationMethod
import liquibase.util.file.FilenameUtils
import org.glassfish.jersey.media.multipart.{FormDataContentDisposition, FormDataParam}
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Path("/users")
@Produces(Array(MediaType.APPLICATION_JSON))
class UsersResource(val users: UserDao, val avatars: AvatarDao) {
  val passwordEncoder = new BCryptPasswordEncoder()

  @POST
  @Timed
  def create(@Valid @BeanParam user: UserParams): User = {
    val password = this.passwordEncoder.encode(user.password)
    val id = this.users.create(User(0, user.name, user.mail, password, Instant.now()))
    val createdUser = this.users.find(id).get
    createdUser
  }

  @GET
  @Path("/{id}")
  @PermitAll
  @Timed
  def get(@PathParam("id") id: Int): User = {
    this.users.find(id) match {
      case Some(user) => user
      case None => throw new NotFoundException("No such user.")
    }
  }

  @POST
  @Path("/avatars")
  @PermitAll
  @Consumes(Array(MediaType.MULTIPART_FORM_DATA))
  @Timed
  def createAvatar(@Auth user: User, @Valid @BeanParam params: AvatarParams): Response = {
    val avatar = params.toAvatar(UUID.randomUUID(), Instant.now(), user)
    this.avatars.create(avatar)
    Response.ok().build()
  }

  @GET
  @Path("/avatars/{name}")
  @Consumes(Array(MediaType.MULTIPART_FORM_DATA))
  @Timed
  def getAvatar(@PathParam("name") name: String): Response = {
    this.avatars.find(name) match {
      case Some(avatar) =>
        val contentType = URLConnection.guessContentTypeFromName(avatar.name)
        Response.ok(avatar.data, contentType)
          .build()
      case None =>
        throw new NotFoundException("No such avatar.")
    }
  }
}

object UsersResource {
  class UserParams() {
    @NotEmpty
    @FormParam("name")
    var name: String = _

    @NotEmpty
    @FormParam("mail")
    var mail: String = _

    @NotEmpty
    @FormParam("password")
    var password: String = _

    @NotEmpty
    @FormParam("passwordConfirm")
    var passwordConfirm: String = _

    @ValidationMethod(message = "password and passwordConfirm must be same.")
    def hasSamePasswords: Boolean = {
      this.password == this.passwordConfirm
    }
  }

  class AvatarParams() {
    @FormDataParam("file")
    var file: InputStream = _

    @FormDataParam("file")
    var disposition: FormDataContentDisposition = _

    lazy val data: Array[Byte] = ByteStreams.toByteArray(this.file)

    lazy val image: Option[BufferedImage] = {
      Option(ImageIO.read(new ByteArrayInputStream(this.data)))
    }

    lazy val extension: String = FilenameUtils.getExtension(this.disposition.getFileName).toLowerCase

    @ValidationMethod(message = "file size must be 2 MB or less.")
    def hasValidSize: Boolean = {
      this.data.length <= 2 * 1024 * 1024
    }

    @ValidationMethod(message = "file format must be PNG, JPEG, or GIF.")
    def hasValidType: Boolean = {
      try {
        this.image.isDefined && List("png", "jpg", "jpeg", "gif").exists(this.extension.endsWith)
      } catch {
        case _: NoSuchElementException => false
      }
    }

    def toAvatar(id: UUID, created: Instant, user: User): Avatar = {
      val image = this.image.get
      Avatar(s"$id.$extension", user.id, this.data, image.getWidth, image.getHeight, created)
    }
  }
}
