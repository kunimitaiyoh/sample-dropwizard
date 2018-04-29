package com.example.sample.dao

import java.sql.Timestamp

import com.example.sample.api.User
import org.skife.jdbi.v2.DBI
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import collection.JavaConverters._

class RawJdbiUserDao(dbi: DBI) extends RawJdbiDao[User](dbi) with UserDao {
  override val tableName = "users"

  val passwordEncoder = new BCryptPasswordEncoder()

  override def create(user: User): Int = {
    val values = Map("name" -> user.name, "mail" -> user.mail, "password_digest" -> user.passwordDigest, "created" -> user.created)
    this.insert(values)
  }

  override def find(id: Int): Option[User] = {
    this.dbi.withHandle(handle => {
      val record = handle.createQuery("Select id, name, mail, password_digest, created from users where id = :id")
        .bind("id", id)
      Option(record.first())
        .map(this.convert(_).hidePassword())
    })
  }

  override def findByMail(mail: String): Option[User] = {
    this.dbi.withHandle(handle => {
      val record = handle.createQuery(s"Select * from ${this.tableName} where mail = :mail")
        .bind("mail", mail)
      Option(record.first())
        .map(this.convert)
    })
  }

  override def findByArticleId(articleId: Int): Seq[User] = {
    this.dbi.withHandle(handle => {
      val record = handle.createQuery(s"Select users.id, users.name, users.mail, users.password_digest, users.created " +
        s"from ${this.tableName} left join articles on users.id = articles.user_id where articles.id = :article_id")
        .bind("article_id", articleId)
      record.list().asScala.map(this.convert)
    })
  }

  override def verify(userName: String, password: String): Option[User] = {
    this.findByMail(userName)
      .filter(user => this.passwordEncoder.matches(password, user.passwordDigest))
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
