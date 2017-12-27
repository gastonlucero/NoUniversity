package com.stratio.edu.http.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import com.stratio.edu.http._
import spray.json.DefaultJsonProtocol

import com.stratio.edu.http.User

/**
  * This trait contains all implicit (un) marshalling
  * Uses internally spray-json
  */
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val serviceJsonFormat = jsonFormat4(Service)
  implicit val servicesJsonFormat = jsonFormat1(Services)
  implicit val upgradeServiceJsonFormat = jsonFormat2(UpgradeService)

  implicit val userJsonFormat = jsonFormat4(User)
  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
