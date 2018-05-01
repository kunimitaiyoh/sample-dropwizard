package com.example.sample.api

import java.time.Instant

import scala.beans.BeanProperty

case class Comment(
  @BeanProperty
  id: Int,

  @BeanProperty
  userId: Int,

  @BeanProperty
  articleId: Int,

  @BeanProperty
  body: String,

  @BeanProperty
  created: Instant
)
