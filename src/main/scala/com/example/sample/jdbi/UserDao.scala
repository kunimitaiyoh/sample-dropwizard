package com.example.sample.jdbi

import com.datasift.dropwizard.jdbi.tweak.BindProduct
import com.example.sample.api.User
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult
import org.skife.jdbi.v2.sqlobject.{Bind, GetGeneratedKeys, SqlQuery, SqlUpdate}

trait UserDao {
  @SqlUpdate("Insert into users (name, mail, password_digest, created) values (:user.name, :user.mail, :user.passwordDigest, :user.created)")
  @GetGeneratedKeys()
  def insert(@BindProduct("user") user: User): Int

  @SqlQuery("Select id, name, mail, '********' AS password_digest, created from users where id = :id")
  @SingleValueResult(classOf[User])
  def find(@Bind("id") id: Int): Option[User]

  @SqlQuery("Select id, name, mail, password_digest, created from users where id = :id")
  def verify(@Bind("id") id: Int): User
}
