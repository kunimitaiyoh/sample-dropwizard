package com.example.sample.jdbi

import org.skife.jdbi.v2.sqlobject.SqlQuery

trait ConstantDao {
  @SqlQuery("select 1")
  def findOne(): Int
}
