package com.stratio.edu.http.routes

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.stratio.edu.http._
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
class exercise4_ComposedDirectivesRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
  with JsonSupport with ComposedDirectivesRoutes {

  lazy val routes = composedRoutes

  "Composed Routes" should {

    "Users context" should {

      val user = User(id = "1", name = "nouniversity", lastName = "stratio", email = "people@stratio.com")
      "when upsert with post a user get a confirmation message" in {
        Post("/composed/users/upsert", Marshal(user).to[MessageEntity]) ~> routes ~> check {
          status should ===(StatusCodes.Created)
          contentType should ===(ContentTypes.`application/json`)
          entityAs[ActionPerformed] shouldEqual ActionPerformed(s"User ${user.name} upserted")
        }
      }

      "delete and existing user return a successful message" in {
        Delete("/composed/users/1") ~> routes ~> check {
          status should ===(StatusCodes.OK)
          contentType should ===(ContentTypes.`application/json`)
          entityAs[ActionPerformed].description shouldEqual s"User ${user.name} deleted"
        }
      }

      "trying to delete and nonexistent user return a message" in {
        Delete("/composed/users/10") ~> routes ~> check {
          status should ===(StatusCodes.OK)
          responseAs[String] shouldEqual """{"description":"User with id 10 not found"}"""
        }
      }

      "get all users from a future (GET /composed/users)" in {
        Get("/composed/users") ~> routes ~> check {
          status should ===(StatusCodes.OK)
          contentType should ===(ContentTypes.`application/json`)
          entityAs[List[User]] shouldEqual (List(user))
        }
      }
    }

    "Services context" should {
      "get method extract parameters into caseclass" in {
        Get("/composed/services/upgrade?id=1&version=0.0.1") ~> routes ~> check {
          status should ===(StatusCodes.OK)
          contentType should ===(ContentTypes.`application/json`)
          entityAs[Service].version shouldEqual "SeviceUpgraded 0.0.1"
        }
      }

      "other method is not allowed" in {
        Get("/composed/invalidadPath") ~> routes ~> check {
          status should ===(StatusCodes.MethodNotAllowed)
        }
      }
    }
  }
}
