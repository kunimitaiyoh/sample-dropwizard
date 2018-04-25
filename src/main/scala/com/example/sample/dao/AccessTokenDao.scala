package com.example.sample.dao

import java.time.Instant
import java.util.UUID

import com.example.sample.api.{AccessToken, User}

trait AccessTokenDao extends Dao[AccessToken] {
  def find(id: UUID): Option[AccessToken]
  def issue(user: User, created: Instant): AccessToken
  def update(token: AccessToken, lastAccess: Instant): AccessToken
}
