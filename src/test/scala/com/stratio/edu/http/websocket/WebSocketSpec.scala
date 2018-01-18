package com.stratio.edu.http.websocket

import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives.{handleWebSocketMessages, pathPrefix}
import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import akka.stream.scaladsl.{Flow, Sink, Source}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random

class WebSocketSpec  extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest{

  val random = Random
  val randomIterable = new scala.collection.immutable.Iterable[Long] {

    override def iterator = new Iterator[Long] {
      override def hasNext = true

      override def next() = {
        val ret = random.nextLong()
        ret
      }
    }
  }

  def randomFlow: Flow[Message, Message, Any] =
    Flow[Message].flatMapConcat {
      case tm: TextMessage =>
        tm.textStream.map(s => s.toLong).flatMapConcat { qty =>
          Source(randomIterable).take(qty).map(_.toString)
        } map { s =>
          TextMessage(Source.single(s))
        }
      case bm: BinaryMessage =>
        // ignore binary messages but drain content to avoid the stream being clogged
        bm.dataStream.runWith(Sink.ignore)
        Source.empty
    }

  val websocketRoute = pathPrefix("websocket") {
    handleWebSocketMessages(randomFlow)
  }

  val wsClient = WSProbe()

  // WS creates a WebSocket request for testing
  WS("/websocket", wsClient.flow) ~> websocketRoute ~> check {
      // check response for WS Upgrade headers
      isWebSocketUpgrade shouldEqual true

      // manually run a WS conversation
      wsClient.sendMessage("100")
      println(wsClient.expectMessage().toString)
  }

}
