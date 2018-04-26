package com.example.sample.dao

import com.example.sample.api.Article
import org.skife.jdbi.v2.DBI

class RawJdbiArticleDao(dbi: DBI) extends RawJdbiDao[Article](dbi) with ArticleDao {
  override val tableName = "articles"

  override def create(article: Article): Int = ???

  override def find(id: Int): Option[Article] = ???

  def convert(record: java.util.Map[String, AnyRef]): Article = ???
}
