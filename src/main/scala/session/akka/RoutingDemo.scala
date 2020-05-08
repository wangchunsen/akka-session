package session.akka

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Stash}
import akka.pattern._
import akka.routing.{BroadcastPool, ConsistentHashingPool, RandomPool, RoundRobinPool}
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random
object RoutingDemo extends App{

  val system = ActorSystem("routing-demo")

  class DActor extends Actor with Stash{
    override def receive: Receive = {
      case any =>
        unstashAll()
        println(s"I am ${self.path}, receive $any")
        sender ! any
    }
  }

  def split(): Unit ={
    println(
      """
        |
        |==============================================================================
        |
      """.stripMargin)
  }

  def testRouting[A](actorRef: ActorRef, messages:Iterable[A]): Unit = {
    implicit val dispatcher = system.dispatcher
    implicit val timeout = Timeout(1 second)

    messages foreach { i=>
      val future = actorRef ? i
      Await.result(future, Duration.Inf)
    }
  }


  println("RoundRobin ")
  testRouting(system.actorOf(RoundRobinPool(5).props(Props[DActor])), 0 to 20)

  split()

  println("Random")
  testRouting(system.actorOf(RandomPool(5).props(Props[DActor])), 0 to 20)

  split()

  println("Broadcast")
  testRouting(system.actorOf(BroadcastPool(3).props(Props[DActor])), 0 to 5)

  split()

  println("Consistent hashing")
  val strings = 'a' to 'f' map {_.toString * 10} toArray
  val messages = 0 to 20 map{ _=>
     strings(Random.nextInt('f' - 'a'))
  }
  testRouting(system.actorOf(ConsistentHashingPool(5,hashMapping = {
    case any=> any
  }).props(Props[DActor])), messages)
}
