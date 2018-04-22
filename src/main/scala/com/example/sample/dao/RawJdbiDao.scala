package com.example.sample.dao

import org.skife.jdbi.v2.DBI

abstract class RawJdbiDao[T <: Product](val dbi: DBI) {

}
