package com.stratio.edu.http.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.{Directive0, Directive1, Route}

trait AdvancedDirectivesRoutes {

  val logIpTimestamp: Directive0 = {
    extractClientIP.flatMap(ra => {
      println(s"Ip = ${ra.getAddress().get().toString} - time = ${System.currentTimeMillis()}")
      mapInnerRoute(r => r)
    })
  }

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

  lazy val advancedRoutes =
    (pathPrefix("advanced") & logIpTimestamp){
      directivesRoutes
    }

  //Route es a type defined = RequestContext ⇒ Future[RouteResult]
  lazy val directivesRoutes: Route =
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
