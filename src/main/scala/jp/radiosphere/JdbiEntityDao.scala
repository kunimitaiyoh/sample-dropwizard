package jp.radiosphere

import jp.radiosphere.DaoConfig.{DaoContext, DefaultConfig}
import jp.radiosphere.JdbiEntityDao.insertionStatement
import org.skife.jdbi.v2.DBI

import collection.JavaConverters._

abstract class JdbiEntityDao[T](val db: DBI) extends EntityDao[T] {
  val entityClass: Class[T]
  val context: DaoContext[T] = new DaoContext(this.entityClass)
  val config: DaoConfig = new DefaultConfig(this.context)

  def insert(entity: T): Long = {
    this.db.inTransaction((handle, _) => {
      val values = this.config.toRecord(entity)
      handle.createStatement(insertionStatement(this.config.tableName, values))
        .bindFromMap(values.asJava)
        .executeAndReturnGeneratedKeys()
        .first()
        .values()
        .stream()
        .findFirst()
        .get()
        .asInstanceOf[java.lang.Long]
    })
  }

  def find(key: Int): Option[T] = {
    ???
  }
}

object JdbiEntityDao {
  protected def insertionStatement(table: String, values: Map[String, Any]): String = {
    val columns = values.keys.toList.sorted
    val params = columns.map(":" + _).mkString(", ")
    val statement = s"Insert into $table (${columns.mkString(", ")}) values ($params)"
    statement
  }
}