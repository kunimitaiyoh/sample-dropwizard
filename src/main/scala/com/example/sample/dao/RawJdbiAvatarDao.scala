package com.example.sample.dao

import java.sql.Timestamp

import com.example.sample.api.Avatar
import org.skife.jdbi.v2.DBI

class RawJdbiAvatarDao(dbi: DBI) extends RawJdbiDao[Avatar](dbi) with AvatarDao {
  override val tableName = "avatars"

  override def create(avatar: Avatar): Int = {
    this.deleteByUserId(avatar.userId)

    val values = Map("name" -> avatar.name, "user_id" -> avatar.userId, "data" -> avatar.data,
      "width" -> avatar.width, "height" -> avatar.height, "created" -> avatar.created)
    this.insertWithPrimaryKey(values)
  }

  override def find(name: String): Option[Avatar] = {
    this.find("name" -> name, query => {
      Option(query.first())
        .map(this.convert)
    })
  }

  override def findNameByUserId(userId: Int): Option[String] = {
    this.dbi.withHandle(handle => {
      val record = handle.createQuery(s"Select name from ${this.tableName} where user_id = :user_id")
        .bind("user_id", userId)
        .first()
      Option(record).map(_.get("name").asInstanceOf[String])
    })
  }

  override def deleteByUserId(userId: Int): Int = this.delete("user_id", userId)

  protected def convert(record: java.util.Map[String, AnyRef]): Avatar = {
    val name = record.get("name").asInstanceOf[String]
    val userId = record.get("user_id").asInstanceOf[Int]
    val data = record.get("data").asInstanceOf[Array[Byte]]
    val width = record.get("width").asInstanceOf[Int]
    val height = record.get("height").asInstanceOf[Int]
    val created = record.get("created").asInstanceOf[Timestamp].toInstant
    Avatar(name, userId, data, width, height, created)
  }
}
