package com.stratio.edu.http.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpMethod, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.{Directive1, Route}
import com.stratio.edu.http.actors.{ServicesBackend, UsersBackend}
import com.stratio.edu.http.utils.{ConfigComponent, JsonSupport}
import com.stratio.edu.http.{UpgradeService, User}

import scala.concurrent.ExecutionContext

class ComposedDirectivesRoutes(implicit val system: ActorSystem) extends JsonSupport with ConfigComponent {

  //For use Futures
  implicit val ec: ExecutionContext = system.dispatcher

  final val serviceBackend: ServicesBackend = new ServicesBackend()
  final val userBackend: UsersBackend = new UsersBackend()

  lazy val routes = pathPrefix("composed") {
    servicesRoutes ~ userRoutes ~ {
      //In any other case
      complete(StatusCodes.MethodNotAllowed, "Only post or put are allowed")
    }
  }

  /**
    * the directive is composed by an LOGICAL OR between directives, the main idea is if the path /users with either
    * post or put method is called, they have a common route entry point
    */
  final val postOrPutDirective: Directive1[HttpMethod] = (post | put) & extractMethod

  lazy val userRoutes: Route = pathPrefix("users") {
    path("upsert") {
      postOrPutDirective { method => {
        entity(as[User]) {
          user => {
            logger.debug(s"Method invoked ${method.value}")
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

  final val getAndCaseClassExtraction = (get & parameters('id.as[String], 'version.as[String]).as(UpgradeService))

  lazy val servicesRoutes: Route =
    pathPrefix("services" / "upgrade") {
      getAndCaseClassExtraction {
        upgradeService =>
          complete(serviceBackend.upgrade(upgradeService))
      }
    }
}