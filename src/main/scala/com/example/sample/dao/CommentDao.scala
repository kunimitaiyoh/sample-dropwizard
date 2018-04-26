package com.example.sample.dao

import com.example.sample.api.Comment

trait CommentDao extends Dao[Comment] {
  def create(comment: Comment): Int
  def find(id: Int): Option[Comment]
}
