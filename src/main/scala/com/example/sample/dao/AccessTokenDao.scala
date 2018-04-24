package com.example.sample.dao

import java.time.Instant

import com.example.sample.api.{AccessToken, User}

trait AccessTokenDao {
  def issue(user: User, created: Instant): AccessToken
  def update(token: AccessToken, lastAccess: Instant): AccessToken
}
