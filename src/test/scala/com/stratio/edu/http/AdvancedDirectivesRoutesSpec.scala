package com.stratio.edu.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{HttpCookie, RawHeader, `Set-Cookie`}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.stratio.edu.http.routes.AdvancedDirectivesRoutes
import com.stratio.edu.http.utils.JsonSupport
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

/**
  * *
  * Basic structure of tests:
  * REQUEST ~> ROUTE ~> check {
  * ASSERTIONS
  * }
  */
class AdvancedDirectivesRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with JsonSupport {

  val httpRoutes = new AdvancedDirectivesRoutes()

  lazy val routes = httpRoutes.routes


  "Advanced Routes" should {

    "Service context" should {
      "response with valid cookie" in {
        val request = HttpRequest(uri = "/advanced/cookie")
        request ~> routes ~> check {
          status should ===(StatusCodes.OK)
          header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie("stratioCookie", value = "noUniversity")))
        }
      }
      "response with valid cookie when token is valid" in {
        val request = HttpRequest(uri = "/advanced/token")
        val token = "Stratio"
        request ~> RawHeader("token", s"$token") ~> routes ~> check {
          status should ===(StatusCodes.OK)
          header[`Set-Cookie`] shouldEqual Some(`Set-Cookie`(HttpCookie("stratioCookie", value = "noUniversity")))
          responseAs[String] shouldEqual s"Token equals $token"
        }
      }
    }
  }

}
