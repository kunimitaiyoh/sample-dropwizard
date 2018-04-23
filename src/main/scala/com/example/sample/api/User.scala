package com.example.sample.api

import java.time.Instant

import com.example.sample.core.InstantSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize

import scala.beans.BeanProperty

case class User(
  @BeanProperty
  id: Int,

  @BeanProperty
  name: String,

  @BeanProperty
  mail: String,

  passwordDigest: String,

  @BeanProperty
  @JsonSerialize(
    using = classOf[InstantSerializer],
    as = classOf[String])
  created: Instant) {
}
