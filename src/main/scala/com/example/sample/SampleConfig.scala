package com.example.sample

import javax.validation.constraints.NotNull

import com.google.common.cache.CacheBuilderSpec
import io.dropwizard.Configuration
import io.dropwizard.db.DataSourceFactory

import scala.beans.BeanProperty

class SampleConfig extends Configuration {
  @BeanProperty
  @NotNull
  var database: DataSourceFactory = _

  @BeanProperty
  var authenticationCachePolicy: CacheBuilderSpec  = _
}
