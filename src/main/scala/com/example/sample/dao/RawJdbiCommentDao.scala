package com.example.sample.dao

import java.sql.Timestamp

import com.example.sample.api.Comment
import org.skife.jdbi.v2.DBI
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class RawJdbiCommentDao(dbi: DBI) extends RawJdbiDao[Comment](dbi) with CommentDao {
  override val tableName = "comments"

  val passwordEncoder = new BCryptPasswordEncoder()

  override def create(comment: Comment): Int = {
    val values: Map[String, AnyRef] =
      Map("user_id" -> comment.userId, "article_id" -> comment.articleId, "body" -> comment.body, "created" -> comment.created)
    insert(values)
  }

  override def find(id: Int): Option[Comment] = {
    this.dbi.withHandle(handle => {
      val record = handle.createQuery(s"Select id, user_id, article_id, body, created from $tableName where id = :id")
        .bind("id", id)
      Option(record.first())
        .map(this.convert)
    })
  }

  def convert(record: java.util.Map[String, AnyRef]): Comment = {
    val id = record.get("id").asInstanceOf[Int]
    val userId = record.get("user_id").asInstanceOf[Int]
    val articleId = record.get("article_id").asInstanceOf[Int]
    val body = record.get("body").asInstanceOf[String]
    val created = record.get("created").asInstanceOf[Timestamp].toInstant
    Comment(id, userId, articleId, body, created)
  }
}
