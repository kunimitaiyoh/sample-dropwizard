package jp.radiosphere

import jp.radiosphere.DaoConfig.DaoContext

abstract class EntityDao[T] {
  val context: DaoContext[T]
  val config: DaoConfig
}

object EntityDao {
}
