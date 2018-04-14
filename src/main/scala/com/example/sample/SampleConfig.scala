package com.example.sample

import javax.validation.constraints.NotNull

import io.dropwizard.Configuration
import io.dropwizard.db.DataSourceFactory

import scala.beans.BeanProperty

class SampleConfig extends Configuration {
  @BeanProperty
  @NotNull
  var database: DataSourceFactory = _
}
