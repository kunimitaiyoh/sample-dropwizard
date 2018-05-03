package jp.radiosphere

import com.google.common.base.CaseFormat
import jp.radiosphere.DaoConfig.DaoContext

abstract class DaoConfig[T](val context: DaoContext[T]) {
  val tableName: String

  def toRecord(entity: T): Map[String, Any]
}

object DaoConfig {
  class DaoContext[T](val entityClass: Class[T])

  class DefaultConfig[T](context: DaoContext[T]) extends DaoConfig[T](context) {
    override val tableName: String = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, context.entityClass.getName)

    override def toRecord(entity: T): Map[String, Any] = {
      ???
    }
  }
}