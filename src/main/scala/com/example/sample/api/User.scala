package com.example.sample.api

import java.time.Instant
import javax.persistence.{Column, Entity, Table}

import com.example.sample.api.Entities.Id

@Entity
@Table(name = "users")
case class User(@Id id: Int, name: String, mail: String, passwordDigest: String, created: Instant) {

}
