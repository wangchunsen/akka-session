package session.akka

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, PoisonPill, Props, ReceiveTimeout}
import akka.util.Timeout

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.io.StdIn
import scala.language.postfixOps
import scala.util.Try

//Exclusive Locks
//ReentrantLock
class LockHub extends Actor {
  context.setReceiveTimeout(5.minutes)
  private var queue = new mutable.HashMap[String,mutable.ArrayBuffer[ActorRef]]();
  override def receive = {
    case "lock" =>
      var key = ""
      var waiters = queue.get(key)
      if(waiters.isDefined){
        waiters.foreach(list => list += null)
      } else {
        sender ! "continue"
      }
    case "release" =>
      var key =""

    case ReceiveTimeout => context.stop(self)

  }
}

//object ActorsMain extends App {
//  val system = ActorSystem("akka-session")
//  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher
//  implicit val timeout: Timeout = Timeout(100 milliseconds)
//
//  val mCache = system.actorOf(Props[MCache], "cache")
//  println(s"Cache actor is $mCache")
//
//  def nthOrNull(array:Array[String], nth:Int) = Try(array(nth)) getOrElse(null)
//
//  def parseCommand(command:String) ={
//    val strings = command.split("\\s+")
//
//    strings.headOption map(_.toUpperCase()) match{
//      case Some("GET") => Get(nthOrNull(strings, 1))
//      case Some("SET") => Set(nthOrNull(strings, 1), nthOrNull(strings, 2))
//      case Some("STOP") => PoisonPill
//      case other => command
//    }
//  }
//
//  implicit  val sender = system.actorOf(Props(new Actor with ActorLogging{
//    override def receive: Receive = {
//      case any => log.info(s"received $any form $sender")
//    }
//  }))
//
//  var stop = false
//  println("Please input your command")
//  while(!stop){
//    parseCommand(StdIn.readLine()) match {
//      case "STOP-SYS" =>
//        stop = true
//        system.terminate()
//      case other => mCache ! other
//    }
//  }
//
//  mCache ! Set("name", "chunsen")
////  val name = mCache ? Get("name")
////  name.onComplete(res => {
////    println(res)
////  })
//
//  //make this actor crash
//  mCache ! Set("address", null)
//  Thread.sleep(300)
//
//  //test actor selection
//  system.actorSelection("/user/cache") ! Set("name", "no one")
//
//  // test dead letter
//  val notExistActor = system.actorSelection("/user/not_exist")
//  notExistActor ! "unKnowMsg"
//
//}