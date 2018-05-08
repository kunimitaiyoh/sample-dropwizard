package com.example.sample.dao

import java.sql.Timestamp

import com.example.sample.api.Article
import org.skife.jdbi.v2.DBI

import collection.JavaConverters._

class RawJdbiArticleDao(dbi: DBI) extends RawJdbiDao[Article](dbi) with ArticleDao {
  override val tableName = "articles"

  override def create(article: Article): Int = {
    val values = Map("user_id" -> article.userId, "title" -> article.title, "body" -> article.body, "created" -> article.created)
    insert(values)
  }

  override def find(id: Int): Option[Article] = {
    this.dbi.withHandle(handle => {
      val record = handle.createQuery(s"Select id, user_id, title, body, created from $tableName where id = :id")
        .bind("id", id)
      Option(record.first())
        .map(this.convert)
    })
  }

  override def getAll: Seq[Article] = {
    this.dbi.withHandle(handle => {
      handle.createQuery(s"Select * from $tableName")
        .list()
        .asScala
        .map(this.convert)
    })
  }

  override def searchByUserId(userId: Int): Seq[Article] = {
    this.dbi.withHandle(handle => {
      handle.createQuery(s"Select * from $tableName where user_id = :user_id")
        .bind("user_id", userId)
        .list()
        .asScala
        .map(this.convert)
    })
  }

  def convert(record: java.util.Map[String, AnyRef]): Article = {
    val id = record.get("id").asInstanceOf[Int]
    val userId = record.get("user_id").asInstanceOf[Int]
    val title = record.get("title").asInstanceOf[String]
    val body = record.get("body").asInstanceOf[String]
    val created = record.get("created").asInstanceOf[Timestamp].toInstant
    Article(id, userId, title, body, created)
  }
}
