package com.stratio.edu.http.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.stratio.edu.http.{User, _}
import spray.json.DefaultJsonProtocol

/**
  * This trait contains all implicit (un) marshalling
  * Uses internally spray-json
  */
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val upgradeServiceJsonFormat = jsonFormat2(Service)

  implicit val userJsonFormat = jsonFormat4(User)
  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
