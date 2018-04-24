package com.example.sample.dao

import java.time.Instant
import java.util.UUID

import com.example.sample.api.{AccessToken, User}

import scala.collection.mutable

class InMemoryAccessTokenDao extends AccessTokenDao {
  val store: mutable.Map[UUID, AccessToken] = mutable.Map.empty

  override def find(id: UUID): Option[AccessToken] = {
    this.store.get(id)
  }

  override def issue(user: User, created: Instant): AccessToken = {
    val now = Instant.now()
    val token = AccessToken(UUID.randomUUID(), user.id, now, now)
    this.store += (token.id -> token)
    token
  }

  override def update(token: AccessToken, lastAccess: Instant): AccessToken = {
    val updated = token.copy(lastAccess = lastAccess)
    this.store += (token.id -> updated)
    updated
  }
}
