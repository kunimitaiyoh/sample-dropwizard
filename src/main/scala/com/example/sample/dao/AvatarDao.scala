package com.example.sample.dao

import com.example.sample.api.Avatar

trait AvatarDao extends Dao[Avatar] {
  def create(avatar: Avatar): Int
  def findByUserId(userId: Int): Option[Avatar]
  def deleteByUserId(userId: Int): Int
}
