package jp.radiosphere

abstract class EntityDao[T] {
  abstract def getEntityClass: Class[T]
}

object EntityDao {
}
