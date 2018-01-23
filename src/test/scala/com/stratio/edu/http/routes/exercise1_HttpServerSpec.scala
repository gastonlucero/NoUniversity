package com.stratio.edu.http.routes

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.stratio.edu.http.StratioAkkaHttpServer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class exercise1_HttpServerSpec extends WordSpec with Matchers with ScalatestRouteTest with ScalaFutures {


  "HttpServer" should {
    "binding should start the server and receives request" in {
      val stratioHttp = StratioAkkaHttpServer
      Http().singleRequest(HttpRequest(uri = "http://localhost:8080")).isCompleted === true
    }
  }
}
