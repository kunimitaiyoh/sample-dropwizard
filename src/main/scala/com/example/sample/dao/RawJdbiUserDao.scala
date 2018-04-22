package com.example.sample.dao

import java.sql.Timestamp

import com.example.sample.api.User
import org.skife.jdbi.v2.DBI

class RawJdbiUserDao(dbi: DBI) extends RawJdbiDao[User](dbi) with IUserDao {
  override def create(user: User): Int = {
    this.dbi.withHandle(handle => {
      val created = handle.createStatement("Insert into users (name, mail, password_digest, created) values (:name, :mail, :password_digest, :created)")
        .bind("name", user.name)
        .bind("mail", user.mail)
        .bind("password_digest", user.passwordDigest)
        .bind("created", user.created)
        .executeAndReturnGeneratedKeys()
      val id = created.first()
        .values()
        .stream()
        .findFirst()
        .get()
      id match {
        case x: java.lang.Long => x.toInt
        case _ => throw new IllegalArgumentException
      }
    })
  }

  override def find(id: Int): Option[User] = {
    this.dbi.withHandle(handle => {
      val record = handle.createQuery("Select id, name, mail, password_digest, created from users where id = :id")
        .bind("id", id)
      Option(record.first()).map(this.convert)
    })
  }

  override def convert(record: java.util.Map[String, AnyRef]): User = {
    val id = record.get("id").asInstanceOf[Int]
    val name = record.get("name").asInstanceOf[String]
    val mail = record.get("mail").asInstanceOf[String]
    val passwordDigest = record.get("password_digest").asInstanceOf[String]
    val created = record.get("created").asInstanceOf[Timestamp].toInstant
    User(id, name, mail, passwordDigest, created)
  }
}