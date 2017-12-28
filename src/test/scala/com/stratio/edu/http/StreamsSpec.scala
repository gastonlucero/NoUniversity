package com.stratio.edu.http

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape, OverflowStrategy}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, Merge, RunnableGraph, Sink, Source}
import akka.{Done, NotUsed}

import scala.concurrent.Future

object StreamsSpec extends App {

  implicit val system = ActorSystem("stratio")

  implicit val materializer = ActorMaterializer()

  val source: Source[Int, NotUsed] = Source(1 to 100)
  val sink: Sink[Double, Future[Done]] = Sink.foreach(println)

  val areaFlow: Flow[Int, Double, _] = Flow[Int].map(i => {
    Thread.sleep(10000)
//    Math.PI * Math.pow(i, 2)
    i*1.0
  })


  val streamToSink: RunnableGraph[NotUsed] = source.via(areaFlow).buffer(1, OverflowStrategy.dropNew).to(sink)
  val streamViaMat: RunnableGraph[Future[Done]] = source.via(areaFlow).toMat(sink)(Keep.right)
  streamToSink.run()
//  streamViaMat.run()

  val perimeterFlow: Flow[Int, Double, NotUsed] = Flow[Int].map(i => i * 2 * Math.PI)
  val sumFlow: Flow[Int, Double, NotUsed] = Flow[Int].fold(0.0)((acc, next) => acc + next)

  val circleGraph = RunnableGraph.fromGraph(GraphDSL.create(){
    implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._
      val broadcast = builder.add(Broadcast[Int](3))
      val merge = builder.add(Merge[Double](3))
      source ~> broadcast //1 input N output
                broadcast ~> areaFlow  ~> merge
                broadcast ~> perimeterFlow ~>merge
                broadcast ~> sumFlow ~> merge
                                        merge ~> sink //N inputs  1 output
      ClosedShape
  })

//  circleGraph.run()
  system.terminate()

}
