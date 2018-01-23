package com.stratio.edu.http.routes

import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class exercise6_ActorRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with ActorDirectivesRoutes {

  lazy val routes = actorRoutes

  "Actors" should {
    " ??? " in {
      val request = HttpRequest(uri = "actors")
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] should be eq "Pong"
      }
    }
  }

}
