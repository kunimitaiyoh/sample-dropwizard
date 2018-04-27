package com.example.sample.dao

import java.time.Instant
import java.util.UUID

import com.example.sample.api.{AccessToken, User}
import org.skife.jdbi.v2.DBI

class RawJdbiAccessTokenDao(dbi: DBI) extends RawJdbiDao[AccessToken](dbi) with AccessTokenDao {
  override val tableName = "access_tokens"

  override def issue(user: User, created: Instant): AccessToken = {
    val token = AccessToken(randomId(), user.id, created, created)
    ???
  }

  override def find(id: UUID): Option[AccessToken] = {
    ???
  }

  override def update(token: AccessToken, lastAccess: Instant): AccessToken = {
    val updated = token.copy(lastAccess = lastAccess)
    ???
  }

  protected def randomId(): UUID = UUID.randomUUID()
}
