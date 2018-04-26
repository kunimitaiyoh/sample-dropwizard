package com.example.sample.dao

import org.skife.jdbi.v2.DBI
import collection.JavaConverters._

abstract class RawJdbiDao[T <: Product](val dbi: DBI) extends Dao[T] {
  def insert(values: Map[String, Any]): Int = {
    val columns = values.keys.toList.sorted
    val params = columns.map(":" + _).mkString(", ")
    val statement = s"Insert into ${this.tableName} (${columns.mkString(", ")}) values ($params)"
    this.dbi.withHandle(handle => {
      handle.createStatement(statement)
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
}
