package com.example.sample.dao

import java.util

import com.example.sample.dao.RawJdbiDao.Queryable
import org.skife.jdbi.v2.{DBI, Query}

import collection.JavaConverters._

abstract class RawJdbiDao[T <: Product](val dbi: DBI) extends Dao[T] {
  def insert(values: Map[String, Any]): Int = {
    this.dbi.inTransaction((handle, _) => {
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
    this.dbi.inTransaction((handle, _) => {
      handle.createStatement(insertionStatement(values))
        .bindFromMap(values.asJava)
        .execute()
    })
  }

  def find[U](key: (String, Any), mapper: Queryable => U): U = {
    this.dbi.inTransaction((handle, _) => {
      val query = handle.createQuery(s"Select * from ${this.tableName} where ${key._1} = :key")
        .bind("key", key._2)
      mapper(query)
    })
  }

  def delete(key: (String, Any)): Int = {
    this.dbi.inTransaction((handle, _) => {
      handle.createStatement(s"Delete from ${this.tableName} where ${key._1} = :key")
        .bind("key", key._2)
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

object RawJdbiDao {
  type Queryable = Query[util.Map[String, AnyRef]]
}