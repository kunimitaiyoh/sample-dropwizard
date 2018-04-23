package com.example.sample.dao
import java.util

import com.example.sample.api.User

trait UserDao extends Dao[User] {
  def create(user: User): Int
  def find(id: Int): Option[User]
  def convert(record: util.Map[String, AnyRef]): User
}
