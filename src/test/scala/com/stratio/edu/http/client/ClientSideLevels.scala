package com.stratio.edu.http.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.{HostConnectionPool, OutgoingConnection}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future
import scala.util._

/**
  * For test the clients,first execute "LowLevelHttpServer" class, because the endpoints in the uris, are related to server endpoints
  */
object ClientSideLevels extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  //RequestLevel - with prefix "http://"

  val requestLevel: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://localhost:9000/ping"))
  requestLevel.onComplete {
    case Success(res) => println(s"RequestLevel response $res")
    case Failure(_) => println("Error")
  }

  //HostLevel - without prefix "http://"
  val hostLevel: Flow[(HttpRequest, String), (Try[HttpResponse], String), HostConnectionPool] = Http().cachedHostConnectionPool[String](host = "localhost", port = 9000)
  val response = Source.single(HttpRequest(uri = "/ping") -> "Wish I receive a pong") //The Input is a pair (HttpRequest, String) => Source[(HttpRequest, String)]
    .via(hostLevel) //The "logic" receives Input of (HttpRequest, String) and produces Output of (Try[HttpResponse], String)
    .to(Sink.foreach(response => {
    println(s"HostLevel response is a pair of (HttpResponse,String) => (${response._1},${response._2})")
  }))
    .run() //Materializer step


  //ConnectionLevel - without prefix "http://"
  val connectionLevel : Flow[HttpRequest, HttpResponse, Future[OutgoingConnection]] = Http().outgoingConnection(host = "localhost", port = 9000)
  val responseFuture : Future[HttpResponse] = Source.single(HttpRequest(uri = "/ping"))
          .via(connectionLevel)
        .runWith(Sink.head)
  responseFuture.onComplete {
    case Success(res) => println(s"RequestLevel response $res")
    case Failure(_) => println("Error")
  }
}
