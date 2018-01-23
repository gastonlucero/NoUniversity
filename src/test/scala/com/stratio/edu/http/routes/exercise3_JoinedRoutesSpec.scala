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
class exercise3_JoinedRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with JoinedRoutes {


  val routes = joinedRoutes


  "Joined Routes" should {
    "return a users with name when byname method is called (GET /joined/users/byname)" in {
      Get("/joined/users/byname?name=\"Gaston\"") ~> routes ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual "User with name \"Gaston\""
      }
    }

    "return paginated users when GET /joined/users/paginated is called" should {
      "when all parameters are present" in {
        Get("/joined/users/paginated?page=1&offset=1&limit=100") ~> routes ~> check {
          status should ===(StatusCodes.OK)
          responseAs[String] shouldEqual "/users/paginated or /users/all page=1, offset=1,limit=100"
        }
      }

      "when limit in omitted , the default value is injected" in {
        Get("/joined/users/paginated?page=1&offset=1") ~> routes ~> check {
          status should ===(StatusCodes.OK)
          responseAs[String] shouldEqual "/users/paginated or /users/all page=1, offset=1,limit=10"
        }
      }

      "return the same response for /all" in {
        Get("/joined/users/all?page=1&offset=1") ~> routes ~> check {
          status should ===(StatusCodes.OK)
          responseAs[String] shouldEqual "/users/paginated or /users/all page=1, offset=1,limit=10"
        }
      }
    }

    "return a services with id parama is called (GET /joined/services)" in {
      Get("/joined/services?id=\"myService\"") ~> routes ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual "Services with id \"myService\""
      }
    }

    "return default response when the request doesn´t match any context " in {
      Get("/joined/services") ~> routes ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual "Default entrypoint /joined/services because the request doesnt math any context"
      }
    }
  }
}
