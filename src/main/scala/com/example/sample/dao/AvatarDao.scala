package com.example.sample.dao

import com.example.sample.api.Avatar

trait AvatarDao extends Dao[Avatar] {
  def create(avatar: Avatar): Int
  def find(name: String): Option[Avatar]
  def findNameByUserId(userId: Int): Option[String]
  def deleteByUserId(userId: Int): Int
}
