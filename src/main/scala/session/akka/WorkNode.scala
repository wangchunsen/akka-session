package session.akka

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.client.ClusterClientReceptionist
import com.typesafe.config.ConfigFactory
import session.akka.messages._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn

object WorkNode {
  def main(args:Array[String]): Unit ={
    val systemName = "cache-sys"
    val system = ActorSystem(systemName, Some(ConfigFactory.parseString(Conf.cluster(0, systemName, collection.Set("worker")))))
    system.registerOnTermination(() => System.exit(0))

    val cluster = Cluster(system)

    val cacheService = system.actorOf(Props(new Actor  with ActorLogging {
      override def receive: Receive = {
        case RequestCacheRef() =>
          log info s"Receive msg from http layer, ref $sender"
          log info s"Sender is ${sender.path}"
          sender ! ResponseCacheRef()
      }
    }), "cache-service")

    ClusterClientReceptionist(system).registerService(cacheService)

    while (true){
     val str = StdIn.readLine()
      if(str == "leave"){
        cluster leave cluster.selfAddress
      } else {
        println(str)
      }
    }
  }

  class CacheActor extends Actor  with  ActorLogging{
    var list = new ArrayBuffer[String]()
    var cacheValues = new mutable.HashMap[String,AnyRef]()

    override def receive = {
      case Get(key) =>
        if(key == null){
          throw new IllegalArgumentException()
        }
        sender ! cacheValues.get(key)

      case Set(key, value) =>
        if(value == null || key == null ){
          throw new IllegalArgumentException()
        }
        cacheValues += (key->value)
//        log.info(s"set cache for key:$key and value:$value success")
      case Clear => cacheValues.clear()
    }

    override def preStart(): Unit = log info "I am started"

    override def postStop(): Unit = log info "I am stopped"

    override def postRestart(reason: Throwable): Unit = log info "I am restarted"
  }
}
