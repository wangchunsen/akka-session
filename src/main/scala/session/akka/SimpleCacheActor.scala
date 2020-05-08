package session.akka

import akka.actor.{Actor, ActorRef, Props}
import akka.routing.Broadcast
import akka.routing.ConsistentHashingPool
import session.akka.SimpleCacheActor._

import scala.collection.mutable

object SimpleCacheActor {

  case class ExpireAble(value: AnyRef, expireAt: Long)

  object Clean

  object CheckExpire

  trait CURDMessage {
    def key: String
  }

  case class Get(key: String) extends CURDMessage

  case class Delete(key: String) extends CURDMessage

  case class Set(key: String, value: AnyRef, expire: Option[Long]) extends CURDMessage

}

class Cache extends Actor {
  private val values = new mutable.HashMap[String, AnyRef]()

  override def receive: Receive = {
    case Get(key) => sender ! get(key)
  }

  private def get(key: String) = {
    values.get(key).map({
      case ExpireAble(value, expireAt) => value
      case any => any
    })
  }
}

class SimpleCacheActor extends Actor {
  private val caches: ActorRef = context.actorOf(ConsistentHashingPool(10, hashMapping = {
    case c: CURDMessage => c.key
  }).props(Props(new Actor {
    override def receive: Receive = ???
  })))


  override def receive: Receive = {
    case c: CURDMessage => caches.tell(c, sender)
    case a@ (Clean | CheckExpire) => caches ! Broadcast(a)
  }
}
