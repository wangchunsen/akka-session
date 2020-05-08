package session.akka


import akka.actor.{Actor, ActorLogging, ActorPath, ActorRef, ActorSelection, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.cluster.client._
import com.typesafe.config.ConfigFactory
import session.akka.ClusterMsgs.PingFromClient

import collection.immutable.{Set => _Set}
import scala.io.StdIn

object ClusterMsgs{
  case class PingFromClient(msg:String)
}

object BusinessNode extends App{
  val config =
    """
      |akka {
      |  actor {
      |    provider = "cluster"
      |  }
      |  remote {
      |    log-remote-lifecycle-events = off
      |    artery{
      |      enabled = on
      |      canonical.hostname = "127.0.0.1"
      |      canonical.port = 2555
      |    }
      |  }
      |
      |  cluster {
      |    seed-nodes = [
      |      "akka://demo-cluster@127.0.0.1:2551",
      |      "akka://demo-cluster@127.0.0.1:2552"]
      |
      |    # auto downing is NOT safe for production deployments.
      |    # you may want to use it during development, read more about it in the docs.
      |    #
      |    # auto-down-unreachable-after = 10s
      |  }
      |
      |  extensions = ["akka.cluster.client.ClusterClientReceptionist"]
      |}
    """.stripMargin
  val system = ActorSystem("demo-cluster", Some(ConfigFactory.parseString(config)))
  system.registerOnTermination(() => System.exit(0))

  val demoService = system.actorOf(Props(new Actor {
    override def receive: Receive = {
      case ClusterMsgs.PingFromClient(msg) => println(system + msg)
    }
  }), "demo-service")

  ClusterClientReceptionist(system).registerService(demoService)
}



object Client extends App{
  val initialContacts = collection.immutable.Set(
    ActorPath.fromString("akka://demo-cluster@127.0.0.1:2551/system/receptionist"),
    ActorPath.fromString("akka://demo-cluster@127.0.0.1:2552/system/receptionist"))

  val system = ActorSystem("client-system", ConfigFactory.parseString(
    """
      |akka.actor.provider = remote
      |akka.remote {
      |    log-remote-lifecycle-events = off
      |    artery{
      |      enabled = on
      |    }
      |}
    """.stripMargin))

  val settings = ClusterClientSettings(system).withInitialContacts(initialContacts)

  val clientActor = system.actorOf(ClusterClient.props(settings),"client")

  while(true){
    val msg = StdIn.readLine()
    clientActor ! ClusterClient.Send("/user/demo-service", PingFromClient(msg), localAffinity = true)
    println(s"send msg success $msg")
  }

//  import scala.concurrent.duration._
//  system.actorSelection("akka.tcp://seed-node@127.0.0.1:2551/user/demo-service").resolveOne(5 second).onComplete(println)(system.dispatcher)
}

