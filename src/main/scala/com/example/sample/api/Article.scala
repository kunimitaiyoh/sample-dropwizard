package com.example.sample.api

import java.time.Instant

import scala.beans.BeanProperty

case class Article(
  @BeanProperty
  id: Int,

  @BeanProperty
  userId: Int,

  @BeanProperty
  title: String,

  @BeanProperty
  body: String,

  @BeanProperty
  created: Instant
)
