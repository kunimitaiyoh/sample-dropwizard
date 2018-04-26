package com.example.sample.dao

import com.example.sample.api.Article

trait ArticleDao extends Dao[Article] {
  def create(article: Article): Int
  def find(id: Int): Option[Article]
}
