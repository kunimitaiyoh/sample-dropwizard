package com.example.sample.dao

import com.example.sample.api.{Avatar, User}

trait AvatarDao extends Dao[Avatar] {
  def create(avatar: Avatar): Int
  def findByUser(user: User): Option[Avatar]
  def deleteByUser(user: User): Int
}
