package com.stratio.edu.http

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration.FiniteDuration

class StreamsSpec extends WordSpec with Matchers with ScalaFutures {

  implicit val system = ActorSystem(
    "test",
    ConfigFactory.parseString("akka.test.single-expect-default=10 seconds")
      .withFallback(ConfigFactory.load())
  )

  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val defaultPatience = PatienceConfig(timeout = Span(60, Seconds), interval = Span(60000, Millis))



  "Akka Streams !!!" should {
    "Sum test" in {
      val sourceUnderTest = Source(1 to 4)
      val response = sourceUnderTest
        .runWith(TestSink.probe[Int])
        .expectNext(FiniteDuration(20, TimeUnit.SECONDS),1)
assert(response == 1)
    }
  }
}
