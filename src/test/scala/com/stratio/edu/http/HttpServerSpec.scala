package com.stratio.edu.http

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class HttpServerSpec extends WordSpec with Matchers with ScalatestRouteTest with ScalaFutures {

  var stratioHttp = new StratioAkkaHttpServer

  /**
    * Hint, you need to create the new object to start working
    */
  "HttpServer" should {
    "binding should start the server and receives request" in {
      stratioHttp.bindHttpServer()
      Http().singleRequest(HttpRequest(uri = "http://localhost:8080")).isCompleted === true
    }
  }
}
