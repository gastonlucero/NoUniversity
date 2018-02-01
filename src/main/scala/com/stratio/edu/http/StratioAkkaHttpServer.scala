package com.stratio.edu.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer

import com.stratio.edu.http.routes._
import com.typesafe.config.ConfigFactory
import scala.concurrent.Future
import scala.util.{Failure, Success}

import akka.http.scaladsl.server.Route

trait AllRoutes extends SimpleRoutes with JoinedRoutes with ExceptionRejectionRoutes with ComposedDirectivesRoutes with AdvancedDirectivesRoutes with ActorDirectivesRoutes
with FileUploadDownload{

  lazy val httpRoutes : Route = simpleRoutes ~ joinedRoutes ~ composedRoutes ~ advancedRoutes ~ actorRoutes ~
    fileRoutes ~ exceptionRoutes
}


object StratioAkkaHttpServer extends App with AllRoutes {

  val config = ConfigFactory.load()

  //Used for actors
  implicit val system: ActorSystem = ActorSystem("StratioActorSystem")
  //Used for streams
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  //Used for futures and onComplete

  implicit val executionContext = system.dispatchers.lookup("my-blocking-dispatcher")

  //This will create an Http server,that be used below to binding the httpRoutes
  val httpServer: HttpExt = Http()

  val serverBindingFuture: Future[ServerBinding] = httpServer.bindAndHandle(???, "0.0.0.0", config.getInt("port"))

  //Because the bind return a Future, if, it was not succesfull , the app is terminate
  serverBindingFuture.onComplete {
    case Success(s) => println(s"Server running at [${s.localAddress.getHostName}] port [${
      s.localAddress
        .getPort
    }]")
    case Failure(f) => {
      println(s"Server not running, shutdown server, ${f.getMessage}")
      system.terminate()
      System.exit(1)
    }
  }

}
