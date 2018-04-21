package com.example.sample.resources

import java.time.Instant
import javax.validation.Valid
import javax.ws.rs.core.MediaType
import javax.ws.rs.{BeanParam, FormParam, GET, NotFoundException, POST, Path, Produces, QueryParam}

import com.codahale.metrics.annotation.Timed
import com.example.sample.api.User
import com.example.sample.dao.UserDao
import com.example.sample.resources.UsersResource.UserParams
import io.dropwizard.hibernate.UnitOfWork
import io.dropwizard.validation.ValidationMethod
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Path("/users")
@Produces(Array(MediaType.APPLICATION_JSON))
class UsersResource(val users: UserDao) {
  val passwordEncoder = new BCryptPasswordEncoder()

  @POST
  @Timed
  @UnitOfWork
  def create(@Valid @BeanParam user: UserParams): User = {
    val password = this.passwordEncoder.encode(user.password)
    val createdUser = this.users.create(User(0, user.name, user.mail, password, Instant.now()))
    createdUser
  }

  @GET
  @Path("/{id}")
  @Timed
  @UnitOfWork
  def get(@QueryParam("id") id: Int): User = {
    this.users.find(id) match {
      case Some(user) => user
      case None => throw new NotFoundException("No such user.")
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
}
