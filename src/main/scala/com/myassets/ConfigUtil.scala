package com.myassets

import com.typesafe.config.{ConfigFactory, Config}

object ConfigUtil {

  def load(files: String*): Config = {
    var prev = ConfigFactory.defaultOverrides
    var config: Config = null
    for (file <- files) {
      config = ConfigFactory.load(file).withFallback(prev)
      prev = config
    }
    config
  }
}