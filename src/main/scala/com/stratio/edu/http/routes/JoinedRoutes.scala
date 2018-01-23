package com.stratio.edu.http.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives.{parameter, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete

/**
  * JoinedRoutes combines two differentes Routes, inside a common context, named "joined". For do that akka uses the ~
  * operator
  *
  * Now in the implementation of the Route ,we can join differents "akka directives" to see more graphics the definition
  * e.g get & parameters = in one line this directive says "the method is get and with urlParameters"
  *
  * or could be an or operator (path("paginated") | path("all")) = here the directive says "is the request is to context pagianted or all,
  * execute for both cases the given route
  */
trait JoinedRoutes{

  implicit val system: ActorSystem

  /**
    * When differents contexts have the same urlPrefix, is usefull combine then
    * In this case under /joined  there are two context , /users and /services
    */
  lazy val joinedRoutes = pathPrefix("joined") {
    userRoutes ~ servicesRoutes
  }

  /**
    * The entry point here is /joined/users ...
    * The first method has parameter directive
    * The second combine in single line the predicate : 'the method is a GET and receive parameters page and offset, and
    * limit but it is optional with default value
    */
  lazy val userRoutes: Route =
    pathPrefix("users") {
      (path("byname") & get) {
        parameter('name.as[String]) {
          name => {
            complete(s"Users with name $name")
          }
        }
      } ~
        (path("paginated") | path("all")) {
          (get & parameters('page.as[Int] ? 1, 'offset.as[Int] ? 0, 'limit.as[Int].?(10))) {
            (page, offset, limit) => {
              complete(s"/users/paginated or /users/all page=$page, offset=$offset,limit=$limit")
            }
          }
        }
    }

  /**
    * The entry point here is /joined/services ...
    * If the request match partially with services, but after that doesn´t match any context,
    * the default entry handles the request
    */
  lazy val servicesRoutes: Route =
    pathPrefix("services") {
      get {
        parameter('id.as[String]) {
          id => {
            complete(s"Services with id $id")
          }
        }
      } ~
        get {
          complete("Default entrypoint /joined/services because the request doesnt math any context")
        }
    }
}
