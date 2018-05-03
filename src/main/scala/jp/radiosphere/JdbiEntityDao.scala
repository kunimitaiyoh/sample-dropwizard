package jp.radiosphere

import org.skife.jdbi.v2.DBI

abstract class JdbiEntityDao[T](val db: DBI) extends EntityDao[T] {
}
