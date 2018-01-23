package com.stratio.edu.http.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
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
class exercise2_SimpleRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with SimpleRoutes {

  val routes = simpleRoutes

  "Simple Routes" should {

    "handle (GET /ping)" in {
      Get("/") ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`text/html(UTF-8)`)
        responseAs[String] shouldEqual "<html><body><b>Stratio No University!</b></body></html>"
      }
    }

    "return pong response for (GET /ping)" in {
      val request = HttpRequest(method = HttpMethods.GET, uri = "/ping")
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual "Pong!"
      }
    }

    "when request with pathParam return pong $number response for (GET /ping/$number)" in {
      Get("/ping/10") ~> routes ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual "Pong 10!"
      }
    }

    "when urlParam return pong $number response for (GET /ping?$number)" in {
      val number = 20
      val request = HttpRequest(uri = s"/pingUrlParam?number=$number")
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        responseAs[String] shouldEqual s"Pong $number!"
      }
    }

    "when context doesnt exists it is not handled" in {
      Get("/invalid") ~> routes ~> check {
        handled shouldBe false
      }
    }

    "when you send a header " in {
      Get("/pingHeader") ~> RawHeader("myHeader", "stratio") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual "Pong with header = stratio"
      }
    }


  }
}
