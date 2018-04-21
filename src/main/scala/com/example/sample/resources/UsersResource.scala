package com.example.sample.resources

import javax.ws.rs.core.MediaType
import javax.ws.rs.{FormParam, NotFoundException, POST, Path, Produces, QueryParam}

import com.example.sample.api.User
import com.example.sample.jdbi.UserDao
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
class UsersResource(val users: UserDao) {
  val passwordEncoder = new BCryptPasswordEncoder()

  @POST
  def create(user: User, @FormParam("password") password: String): User = {
    this.passwordEncoder.encode(password)
    val id = this.users.insert(user.copy(passwordDigest = password))
    this.users.find(id).get
  }

  @Path("/{id}")
  def get(@QueryParam id: Int): User = {
    this.users.find(id) match {
      case Some(user) => user
      case None => throw new NotFoundException("No such user.")
    }
  }
}

object UsersResource {

}
