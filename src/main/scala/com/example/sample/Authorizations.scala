package com.example.sample

import java.util.{Optional, UUID}
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response

import com.example.sample.api.User
import com.example.sample.dao.{AccessTokenDao, UserDao}
import io.dropwizard.auth.Authenticator

object Authorizations {

  /**
    * @see https://github.com/remmelt/dropwizard-oauth2-provider/blob/master/src/main/java/com/remmelt/examples/auth/SimpleAuthenticator.java
    */
  class SampleOAuthAuthenticator(tokens: AccessTokenDao, users: UserDao) extends Authenticator[String, User] {
    override def authenticate(accessTokenId: String): Optional[User] = {
      try {
        // TODO: now omitting to test if the access token is not expired
        val id = UUID.fromString(accessTokenId)
        val token = this.tokens.find(id)
        token.flatMap(t => users.find(t.userId))
          .map(Optional.of[User])
          .getOrElse(Optional.empty[User]())
      } catch {
        case e: IllegalArgumentException => throw new WebApplicationException(Response.Status.UNAUTHORIZED)
      }
    }
  }
}
