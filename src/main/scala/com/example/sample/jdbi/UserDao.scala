package com.example.sample.jdbi

import com.datasift.dropwizard.jdbi.tweak.BindProduct
import com.datasift.dropwizard.scala.jdbi.tweak.ProductResultSetMapperFactory
import com.example.sample.api.User
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory
import org.skife.jdbi.v2.sqlobject.{Bind, GetGeneratedKeys, SqlQuery, SqlUpdate}

@RegisterMapperFactory(Array(classOf[ProductResultSetMapperFactory]))
trait UserDao {
  @SqlUpdate("Insert into users (name, mail, password_digest, created) values (:user.name, :user.mail, :user.passwordDigest, :user.created)")
  @GetGeneratedKeys()
  def insert(@BindProduct("user") user: User): Int

  @SqlQuery("Select id, name, mail, password_digest AS passwordDigest, created from users where id = :id")
  def find(@Bind("id") id: Int): User
}
