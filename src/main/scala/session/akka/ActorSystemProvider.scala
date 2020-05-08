package session.akka

import akka.actor.ActorSystem

import scala.concurrent.Await
import scala.concurrent.duration._

class ActorSystemProvider {
  def get(): ActorSystem = {
    val actorSystem = ActorSystem("main")
    actorSystem
  }
}


object ActorSystemProvider {
  def shutDownSync(actorSystem: ActorSystem): Unit = {
    println("Shutting down system")
    Await.result(actorSystem.terminate(), 1 minute)
    println("Actor system is down")
  }
}
