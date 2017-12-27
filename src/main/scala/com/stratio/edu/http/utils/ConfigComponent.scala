package com.stratio.edu.http.utils

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.apache.log4j.LogManager

trait ConfigComponent {

  implicit lazy val config: Config = ConfigFactory.load

  implicit val logger = LogManager.getLogger("NoUniversityAkkaHttp")
}
