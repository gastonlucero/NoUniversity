package com.stratio.edu.http.routes

import akka.http.scaladsl.model.{HttpMethod, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.{Directive1, Route}
import com.stratio.edu.http.utils.BackendServices._
import com.stratio.edu.http.utils.JsonSupport
import com.stratio.edu.http.{Service, User}

trait ComposedDirectivesRoutes extends JsonSupport {

  lazy val composedRoutes = pathPrefix("composed") {
    composedServicesRoutes ~ composedUserRoutes ~ {
      //In any other case
      complete(StatusCodes.MethodNotAllowed, "Only post or put are allowed")
    }
  }

  /**
    * the directive is composed by an LOGICAL OR between directives, the main idea is if the path /users with either
    * post or put method is called, they have a common route entry point
    */
  final val postOrPutDirective: Directive1[HttpMethod] = (post | put) & extractMethod

  lazy val composedUserRoutes: Route = pathPrefix("users") {
    path("upsert") {
      postOrPutDirective { method => {
        entity(as[User]) {
          user => {
            println(s"Method invoked ${method.value}")
            onSuccess(userBackend.postOrPut(user)) {
              userUpsert => {
                complete(201, userUpsert)
              }
            }
          }
        }
      }
      }
    } ~
      path(Segment) { id =>
        delete {
          val deleted = userBackend.delete(id)
          onSuccess(deleted) {
            del => complete(del)
          }
        }
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

  final val getAndCaseClassExtraction = (get & parameters('id.as[String], 'version.as[String]).as(Service))

  lazy val composedServicesRoutes: Route =
    pathPrefix("services" / "upgrade") {
      getAndCaseClassExtraction {
        upgradeService =>
          complete(serviceBackend.upgrade(upgradeService))
      }
    }
}
