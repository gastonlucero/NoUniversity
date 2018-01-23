package com.stratio.edu.http.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

/**
  * *
  * Basic structure of tests:
  * REQUEST ~> ROUTE ~> check {
  * ASSERTIONS
  * }
  */
class exercise7_ExceptionRejectionRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with ExceptionRejectionRoutes {

  lazy val routes = exceptionRoutes

  "Rejections" should {
    "when the request method has invalid parameters GET(/rejections)" in {
      val request = HttpRequest(uri = "/rejections?number=34", method = HttpMethods.POST)
      request ~> routes ~> check {
        status should ===(StatusCodes.MethodNotAllowed)
      }
    }

    "when the method is not found GET(/norejections)" in {
      Get("norejections") ~> routes ~> check {
        status should ===(StatusCodes.NotFound)
        responseAs[String] shouldEqual ("Method [norejections] NotFound")
      }
    }
  }

  "Exceptions" should {

  }
}
