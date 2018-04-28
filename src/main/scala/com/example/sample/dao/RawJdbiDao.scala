package com.example.sample.dao

import org.skife.jdbi.v2.DBI
import collection.JavaConverters._

abstract class RawJdbiDao[T <: Product](val dbi: DBI) extends Dao[T] {
  def insert(values: Map[String, Any]): Int = {
    this.dbi.inTransaction((handle, status) => {
      handle.createStatement(insertionStatement(values))
        .bindFromMap(values.asJava)
        .executeAndReturnGeneratedKeys()
        .first()
        .values()
        .stream()
        .findFirst()
        .get()
        .asInstanceOf[java.lang.Long]
        .toInt
    })
  }

  def insertWithPrimaryKey(values: Map[String, Any]): Int = {
    this.dbi.inTransaction((handle, status) => {
      handle.createStatement(insertionStatement(values))
        .bindFromMap(values.asJava)
        .execute()
    })
  }

  protected def insertionStatement(values: Map[String, Any]): String = {
    val columns = values.keys.toList.sorted
    val params = columns.map(":" + _).mkString(", ")
    val statement = s"Insert into ${this.tableName} (${columns.mkString(", ")}) values ($params)"
    statement
  }
}
