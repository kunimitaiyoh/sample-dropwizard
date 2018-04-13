package com.example.sample

import io.dropwizard.Application
import io.dropwizard.setup.Environment

object SampleApplication extends Application[SampleConfig] {
  def main(args: Array[String]): Unit = {
    run(args:_*)
  }

  override def run(configuration: SampleConfig, environment: Environment) : Unit = {
  }
}
