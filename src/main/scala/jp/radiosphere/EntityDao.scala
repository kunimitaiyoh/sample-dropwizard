package jp.radiosphere

abstract class EntityDao[T] {
  abstract val entityClass: Class[T]
}

object EntityDao {
}
