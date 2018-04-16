package com.example.sample.api

import java.time.Instant

case class User(id: Int, name: String, mail: String, passwordDigest: String, created: Instant) {

}
