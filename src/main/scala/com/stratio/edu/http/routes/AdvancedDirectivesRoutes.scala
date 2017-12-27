package com.stratio.edu.http.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.{Directive1, Route}
import com.stratio.edu.http.actors.ServicesBackend
import com.stratio.edu.http.utils.{ConfigComponent, JsonSupport}

import scala.concurrent.ExecutionContext

class AdvancedDirectivesRoutes(implicit val system: ActorSystem) extends JsonSupport with ConfigComponent {

  implicit val ec: ExecutionContext = system.dispatcher

  final val serviceBackend: ServicesBackend = new ServicesBackend()

  val tokenFromHeader: Directive1[Option[String]] = optionalHeaderValueByName("token")
  val tokenFromParam: Directive1[Option[String]] = parameterMap.map(map => map.get("token"))

  /**
    * Custom directive combines thw above two custom defined directives
    */
  val hasTokenDirective: Directive1[String] = (tokenFromHeader & tokenFromParam) tflatMap {
    case (Some(token), _) => provide(token)
    case (_, Some(token)) => provide(token)
    case _ => reject
  }

  /**
    * This directive add custom cookie to the response
    *
    */
  val myCookieDirective = setCookie(HttpCookie("stratioCookie", value = "noUniversity"))

  lazy val routes =
    pathPrefix("advanced") {
      servicesRoutes
    }

  //Route es a type defined = RequestContext ⇒ Future[RouteResult]
  lazy val servicesRoutes: Route =
    pathPrefix("cookie") {
      pathEnd {
          get {
            myCookieDirective {
              complete(StatusCodes.OK, "Response with cookie")
            }
          }
      }
    } ~
      pathPrefix("token") {
        pathEnd {
          hasTokenDirective { token =>
            get {
              myCookieDirective {
                complete(StatusCodes.OK, s"Token equals $token")
              }
            }
          }
        }
      }
}
