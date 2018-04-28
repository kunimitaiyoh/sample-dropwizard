package com.example.sample.dao

import java.sql.Timestamp

import com.example.sample.api.{Avatar, User}
import org.skife.jdbi.v2.DBI

class RawJdbiAvatarDao(dbi: DBI) extends RawJdbiDao[Avatar](dbi) with AvatarDao {
  override val tableName = "avatars"

  override def create(avatar: Avatar): Int = {
    this.deleteByUserId(avatar.userId)

    val values = Map("name" -> avatar.name, "user_id" -> avatar.userId, "data" -> avatar.data,
      "width" -> avatar.width, "height" -> avatar.height, "created" -> avatar.created)
    this.insertWithPrimaryKey(values)
  }

  override def findByUserId(userId: Int): Option[Avatar] = {
    this.find("user_id" -> userId, query => {
      Option(query.first())
        .map(this.convert)
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
