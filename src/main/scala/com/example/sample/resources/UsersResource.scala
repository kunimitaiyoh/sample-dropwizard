package com.example.sample.resources

import java.time.Instant
import javax.ws.rs.core.MediaType
import javax.ws.rs.{BeanParam, FormParam, NotFoundException, POST, Path, Produces, QueryParam}

import com.example.sample.api.User
import com.example.sample.jdbi.UserDao
import com.example.sample.resources.UsersResource.UserParams
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Path("/users")
@Produces(Array(MediaType.APPLICATION_JSON))
class UsersResource(val users: UserDao) {
  val passwordEncoder = new BCryptPasswordEncoder()

  @POST
  def create(@BeanParam user: UserParams): User = {
    val password = this.passwordEncoder.encode(user.password)
    val id = this.users.insert(User(0, user.name, user.mail, password, Instant.now()))
    this.users.find(id).get
  }

  @Path("/{id}")
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
    var name: String = _
    @NotEmpty
    var mail: String = _
    @NotEmpty
    var password: String = _
    @NotEmpty
    var passwordConfirm: String = _
  }
}
