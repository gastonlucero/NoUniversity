package com.stratio.edu.http.routes

import scala.concurrent.Future

import akka.http.scaladsl.model.{HttpMethod, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.{Directive1, Route}

import com.stratio.edu.http.utils.BackendServices._
import com.stratio.edu.http.utils.JsonSupport
import com.stratio.edu.http.{ActionPerformed, User}

trait ComposedDirectivesRoutes extends JsonSupport {

  lazy val composedRoutes = pathPrefix("composed") {
    composedServicesRoutes ~ composedUserRoutes ~ {
      //In any other case
      complete(StatusCodes.MethodNotAllowed, "Only post or put are allowed")
    }
  }

  final val postOrPutDirective: Directive1[HttpMethod] = (post | put) & extractMethod

  lazy val composedUserRoutes: Route = pathPrefix("users") {
    path("upsert") {
      postOrPutDirective { method => {
        entity(as[User]) {
          user => {
            println(s"Method invoked ${method.value}")
            val response :Future[ActionPerformed]= userBackend.postOrPut(user)
            ??? // use the correct directive
          }
        }
      }
      }
    } ~
      path(Segment) { id =>
       ??? // delete method
      } ~
      get {
        onSuccess(userBackend.getAll()) {
          result =>
            rejectEmptyResponse {
              complete(result)
            }
        }
      }
  }

  //This Directive combine "get" directive AND extract parameters as case class Service
  lazy val getAndCaseClassExtraction = ???

  lazy val composedServicesRoutes: Route =
    pathPrefix("services" / "upgrade") {
      ??? // use getAndCaseClassExtraction and complete the request
    }
}
