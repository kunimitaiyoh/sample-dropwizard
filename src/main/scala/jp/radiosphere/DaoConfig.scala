package jp.radiosphere

import com.google.common.base.CaseFormat
import jp.radiosphere.DaoConfig.DaoContext

abstract class DaoConfig(val context: DaoContext[_]) {
  val tableName: String

  def toRecord(entity: Any): Map[String, Any]
  def fromRecord[T](record: Map[String, Any], objective: Class[T]): T
}

object DaoConfig {
  class DaoContext[T](val entityClass: Class[T])

  class DefaultConfig[T](context: DaoContext[T]) extends DaoConfig(context) {
    override val tableName: String = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, context.entityClass.getName)

    override def toRecord(entity: Any): Map[String, Any] = {
      ???
    }

    override def fromRecord[T](record: Map[String, Any], objective: Class[T]): T = {
      ???
    }
  }
}