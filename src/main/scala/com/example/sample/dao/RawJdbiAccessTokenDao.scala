package com.example.sample.dao

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

import com.example.sample.api.{AccessToken, User}
import org.skife.jdbi.v2.DBI

class RawJdbiAccessTokenDao(dbi: DBI) extends RawJdbiDao[AccessToken](dbi) with AccessTokenDao {
  override val tableName = "access_tokens"

  override def issue(user: User, created: Instant): AccessToken = {
    val token = AccessToken(randomId(), user.id, created, created)
    val values = Map("id" -> token.id.toString, "user_id" -> token.userId, "created" -> token.created, "last_access" -> token.lastAccess)
    this.insertWithPrimaryKey(values)
    token
  }

  override def find(id: UUID): Option[AccessToken] = {
    this.dbi.inTransaction((handle, status) => {
      val record = handle.createQuery(s"Select id, user_id, created, last_access from $tableName where id = :id")
        .bind("id", id.toString)
      Option(record.first())
        .map(this.convert)
    })
  }

  override def update(token: AccessToken, lastAccess: Instant): AccessToken = {
    throw new UnsupportedOperationException()
  }

  def convert(record: java.util.Map[String, AnyRef]): AccessToken = {
    val id = UUID.fromString(record.get("id").asInstanceOf[String])
    val userId = record.get("user_id").asInstanceOf[Int]
    val created = record.get("created").asInstanceOf[Timestamp].toInstant
    val lastAccess = record.get("last_access").asInstanceOf[Timestamp].toInstant
    AccessToken(id, userId, created, lastAccess)
  }

  protected def randomId(): UUID = UUID.randomUUID()
}
