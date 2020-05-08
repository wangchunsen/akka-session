package session.akka

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class DemoActor extends Actor with ActorLogging{
  override def receive = {
    case any => log.info(s"receive msg {$any} from $sender")
  }

  override def preStart(): Unit = {
    log.info(s"I am $self start in system ${context.system}")
  }
}

object SystemA extends App{
  val system = ActorSystem("system-a", Some(ConfigFactory.parseString(
    """
      |akka {
      |  actor {
      |    provider = remote
      |  }
      |  remote {
      |    artery {
      |      enabled = on
      |      canonical.hostname = "127.0.0.1"
      |      canonical.port = 25520
      |    }
      |  }
      |}
    """.stripMargin)))

  val demoActor = system.actorOf(Props(new Actor{
    override def receive = {
      case any => println(s"received msg $any form $sender")
    }
  }), "demo")
}



object SystemB extends App{
  val system = ActorSystem("system-b", Some(ConfigFactory.parseString(
    """
      |akka {
      |  actor {
      |    provider = remote
      |    deployment {
      |      /remote {
      |        remote = "akka://system-a@127.0.0.1:25520"
      |      }
      |    }
      |  }
      |  remote {
      |    artery {
      |      enabled = on
      |      }
      |  }
      |}
    """.stripMargin)))

  //use remote actor
  val remoteActor = system.actorSelection("akka://system-a@127.0.0.1:25520/user/demo")
  remoteActor ! "Hello"


  //create actor on remote system

  val createdOnRemoteActor  = system.actorOf(Props[DemoActor], "remote")

  createdOnRemoteActor ! "Who are you"
}