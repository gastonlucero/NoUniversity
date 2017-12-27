package com.stratio.edu.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer
import com.stratio.edu.http.routes.ComposedDirectivesRoutes
import com.stratio.edu.http.utils.ConfigComponent

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


object Main extends App{
  val server = new StratioAkkaHttpServer
  server.bindHttpServer()

}

class StratioAkkaHttpServer extends ConfigComponent {

  //Used for actors
  implicit val system: ActorSystem = ActorSystem("StratioActorSystem")
  //Used for streams
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  //Used for futures and onComplete
  implicit val executionContext: ExecutionContext = system.dispatcher

  def bindHttpServer(): Future[ServerBinding] = {

    //This will create an Http server,that be used below to binding the httpRoutes
    val httpServer: HttpExt = Http() //TODO

    //This class contains all the httproutes accessible through the webServer
//    val httpRoutes = new SimpleRoutes()

val httpRoutes = new ComposedDirectivesRoutes()
    val serverBindingFuture: Future[ServerBinding] = httpServer.bindAndHandle(httpRoutes.routes, "0.0.0.0", config.getInt("port"))

    //Because the bind return a Future, if, it was not succesfull , the app is terminate
    serverBindingFuture.onComplete {
      case Success(s) => logger.info(s"Server running at [${s.localAddress.getHostName}] port [${s.localAddress
        .getPort}]")
      case Failure(f) => {
        logger.error(s"Server not running, shutdown server, ${f.getMessage}")
        system.terminate()
        System.exit(1)
      }
    }
    serverBindingFuture
  }
}
