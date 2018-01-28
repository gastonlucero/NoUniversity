package com.stratio.edu.http.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration.DurationInt

/**
  * *
  * Basic structure of tests:
  * REQUEST ~> ROUTE ~> check {
  * ASSERTIONS
  * }
  */
class exercise7_ExceptionRejectionRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with ExceptionRejectionRoutes {

  implicit val executionContext = system.dispatcher

  lazy val routes = exceptionRoutes

  "Rejections" should {
    "when the request method has invalid parameters GET(/rejections)" in {
      val request = HttpRequest(uri = "/myHandlers/rejections?number=34", method = HttpMethods.POST)
      request ~> routes ~> check {
        status should ===(StatusCodes.MethodNotAllowed)
      }
    }

    "when the method is not found GET(/norejections)" in {
      Get("/myHandlers/norejections") ~> routes ~> check {
        status should ===(StatusCodes.NotFound)
        responseAs[String] shouldEqual ("Method [/myHandlers/norejections] NotFound in Numa")
      }
    }
  }

  "Exceptions" should {
    "when division by zero GET(/exception)" in {
      Get("/myHandlers/exception?number=10") ~> routes ~> check {
        status should ===(StatusCodes.InternalServerError)
        responseAs[String] shouldEqual ("Division By Zero")
      }
    }
  }

  implicit val timeout = RouteTestTimeout(new DurationInt(10).second)

  "CircuitBreaker" should {
    "Open when" in {
      Get("/myHandlers/breaker/6s") ~> routes ~> check {
        responseAs[String] shouldEqual "An error occurred on breaker: Circuit Breaker Timed out."
      } //
    }
    "Reject when is still open" in {
      Get("/myHandlers/breaker/1s") ~> routes ~> check {
        responseAs[String] contains("Numa CircuitBreaker remaining time")
      }
    }
  }
}
