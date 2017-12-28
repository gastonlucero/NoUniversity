package com.stratio.edu.http

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, RunnableGraph, Sink, Source}
import akka.{Done, NotUsed}

import scala.concurrent.Future

object StreamsSpec extends App {

  implicit val system = ActorSystem("stratio")

  implicit val materializer = ActorMaterializer()

  val source: Source[Int, NotUsed] = Source(1 to 100)
  val sink: Sink[Double, Future[Done]] = Sink.foreach(println)

  val areaFlow: Flow[Int, Double, _] = Flow[Int].map(i => Math.PI * Math.pow(i, 2))


  val streamToSink: RunnableGraph[NotUsed] = source.via(areaFlow).to(sink)
  val streamViaMat: RunnableGraph[Future[Done]] = source.via(areaFlow).toMat(sink)(Keep.right)
  streamToSink.run()
  streamViaMat.run()

  val perimeterFlow: Flow[Int, Double, NotUsed] = Flow[Int].map(i => i * 2 * Math.PI)
  val sumFlow: Flow[Int, Double, NotUsed] = Flow[Int].fold(0.0)((acc, next) => acc + next)

  val circleGraph = RunnableGraph.fromGraph(GraphDSL.create(){
    implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._
      val broadcast = builder.add(Broadcast[Int](3))
      source ~> broadcast
      broadcast ~> areaFlow ~> sink
      broadcast ~> perimeterFlow ~> sink
      broadcast ~> sumFlow ~> sink

      ClosedShape
  })

  circleGraph.run()
  system.terminate()

}
