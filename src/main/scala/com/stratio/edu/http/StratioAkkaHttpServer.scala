package com.stratio.edu.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer
import com.stratio.edu.http.routes._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import akka.http.scaladsl.server.Directives._

object Main extends App{
  val server = new StratioAkkaHttpServer
  server.bindHttpServer()

}

class StratioAkkaHttpServer
  extends SimpleRoutes  with JoinedRoutes with ExceptionRejectionRoutes with ComposedDirectivesRoutes with AdvancedDirectivesRoutes{

  //Used for actors
  implicit val system: ActorSystem = ActorSystem("StratioActorSystem")
  //Used for streams
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  //Used for futures and onComplete
  implicit val executionContext: ExecutionContext = system.dispatcher

  def bindHttpServer(): Future[ServerBinding] = {

    //This will create an Http server,that be used below to binding the httpRoutes
    val httpServer: HttpExt = Http() //TODO

   val httpRoutes = simpleRoutes ~ joinedRoutes ~ exceptionRoutes ~ composedRoutes ~ advancedRoutes

    val serverBindingFuture: Future[ServerBinding] = httpServer.bindAndHandle(httpRoutes, "0.0.0.0", config.getInt("port"))

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
