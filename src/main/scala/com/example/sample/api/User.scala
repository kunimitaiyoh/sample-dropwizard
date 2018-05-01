package com.example.sample.api

import java.security.Principal
import java.time.Instant

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import scala.beans.BeanProperty

@JsonIgnoreProperties(Array("passwordDigest"))
case class User(id: Int, @BeanProperty name: String, mail: String, passwordDigest: String, created: Instant)
  extends Principal {

  def hidePassword(): User = this.copy(passwordDigest = "")
}
