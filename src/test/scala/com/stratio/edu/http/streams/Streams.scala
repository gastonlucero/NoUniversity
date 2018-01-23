package com.stratio.edu.http.streams

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, Merge, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape, OverflowStrategy}
import akka.{Done, NotUsed}

import scala.concurrent.Future
import scala.concurrent.duration._

object Streams extends App {

  implicit val system = ActorSystem("nouniversity")

  implicit val materializer = ActorMaterializer()

  simpleStream()
  graphStrem()

  def simpleStream() = {
    Source(1 to 100).via(Flow[Int].fold(0)((number, acc) => acc + number)).to(Sink.foreach(println)).run()
    system.terminate()
  }

  def graphStrem() = {
    val duration = FiniteDuration(1, TimeUnit.SECONDS)
    val source: Source[Int, NotUsed] = Source(1 to 100)
    val sink: Sink[Double, Future[Done]] = Sink.foreach(println)

    val areaFlow: Flow[Int, Double, _] = Flow[Int].map(i => {
      Math.PI * Math.pow(i, 2)
    })

    val streamToSink: RunnableGraph[NotUsed] = source.via(areaFlow).to(sink)

    val bufferedSink2 = Flow[Int].buffer(1, OverflowStrategy.fail)
      .toMat(Sink.foreach(println))(Keep.right)

    source.to(bufferedSink2).run()

    val streamViaMat: RunnableGraph[Future[Done]] = source.via(areaFlow).toMat(sink)(Keep.right)
    streamToSink.run()
    streamViaMat.run()

    val perimeterFlow: Flow[Int, Double, NotUsed] = Flow[Int].map(i => i * 2 * Math.PI)
    val sumFlow: Flow[Int, Double, NotUsed] = Flow[Int].fold(0.0)((acc, next) => acc + next)

    val circleGraph = RunnableGraph.fromGraph(GraphDSL.create() {
      implicit builder: GraphDSL.Builder[NotUsed] =>
        import GraphDSL.Implicits._
        val broadcast = builder.add(Broadcast[Int](3))
        val merge = builder.add(Merge[Double](3))
        source ~> broadcast //1 input N output
        broadcast ~> areaFlow ~> merge
        broadcast ~> perimeterFlow ~> merge
        broadcast ~> sumFlow ~> merge
        merge ~> sink //N inputs  1 output
        ClosedShape
    })

    circleGraph.run()

    system.terminate()
  }


}
