package com.example.sample.dao

import org.skife.jdbi.v2.DBI

abstract class AbstractDao[T <: Product](val dbi: DBI) {

}
