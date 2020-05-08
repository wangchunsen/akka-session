package session.akka

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.typesafe.config.ConfigFactory

object SeedNode {
  def main(args: Array[String]): Unit = {
    val systemName = "cache-sys"
    val system = ActorSystem(systemName, Some(ConfigFactory.parseString(Conf.cluster(2551, systemName))))
    system.registerOnTermination(() => System.exit(0))

    val cluster = Cluster(system)

    system.actorOf(Props(new Actor with ActorLogging {

      // subscribe to cluster changes, re-subscribe when restart
      override def preStart(): Unit = {
        cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
          classOf[MemberEvent], classOf[UnreachableMember])
      }

      override def postStop(): Unit = cluster.unsubscribe(self)

      def receive = {
        case MemberUp(member) =>
          log.info("Member is Up: {}, roles {}", member.address, member.roles)
        case UnreachableMember(member) =>
          log.info("Member detected as unreachable: {}", member)
        case MemberRemoved(member, previousStatus) =>
          log.info(
            "Member is Removed: {} after {}",
            member.address, previousStatus)
        case _: MemberEvent => // ignore
      }
    }))
  }
}