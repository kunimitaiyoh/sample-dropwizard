package com.example.sample.resources

import java.time.Instant
import javax.validation.Valid
import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.{BeanParam, FormParam, POST, Path, Produces, WebApplicationException}

import com.example.sample.dao.{AccessTokenDao, UserDao}
import com.example.sample.resources.AuthorizationResource.{AuthenticationParams, AuthenticationResponse}
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.validation.ValidationMethod
import org.hibernate.validator.constraints.NotEmpty

import scala.beans.BeanProperty

@Path("/oauth2")
@Produces(Array(MediaType.APPLICATION_JSON))
class AuthorizationResource(val accessTokens: AccessTokenDao, val users: UserDao) {
  @POST
  @Path("/token")
  def issueToken(@Valid @BeanParam params: AuthenticationParams): Response = {
    val user = this.users.verify(params.userName, params.password)
    user match {
      case Some(x) => {
        val accessToken = this.accessTokens.issue(x, Instant.now())
        Response.ok(AuthenticationResponse(accessToken.id.toString))
          .build()
      }
      case None => throw new WebApplicationException("mail or password is wrong.", Response.Status.UNAUTHORIZED)
    }
  }
}

object AuthorizationResource {
  class AuthenticationParams() {
    @NotEmpty
    @FormParam("grant_type")
    var grantType: String = _

    @NotEmpty
    @FormParam("username")
    var userName: String = _

    @NotEmpty
    @FormParam("password")
    var password: String = _

    @ValidationMethod(message = "grant_type must be \"password\".")
    def hasGrantType: Boolean = {
      this.grantType == "password"
    }
  }

  case class AuthenticationResponse(
    @BeanProperty
    @JsonProperty("access_token")
    accessToken: String,

    @BeanProperty
    @JsonProperty("token_type")
    tokenType: String = "bearer",
  )
}