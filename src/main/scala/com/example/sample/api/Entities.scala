package com.example.sample.api

import scala.annotation.meta.field

object Entities {
  type Id = javax.persistence.Id @field
  type Column = javax.persistence.Column @field
}
