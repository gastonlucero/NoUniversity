package com.stratio.edu.http.actors

import akka.actor.Actor

class ActorBackend extends Actor {

  override def receive = {
    case msg: String => {
      println(s"String message $msg")
      sender ! "Pong"
    }
    case _ => unhandled()
  }
}
