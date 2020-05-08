package session.akka

object Conf {
  def cluster(selfPort:Int, systemName:String,  roles:collection.Set[String] = collection.Set.empty) =
    s"""
      |akka {
      |  actor {
      |    provider = cluster
      |    allow-java-serialization = off
      |  }
      |  remote {
      |    log-remote-lifecycle-events = off
      |    artery{
      |      enabled = on
      |      canonical.hostname = "127.0.0.1"
      |      canonical.port = $selfPort
      |    }
      |  }
      |
      |  cluster {
      |    seed-nodes = [
      |      "akka://$systemName@127.0.0.1:2551"]
      |
      |    # auto downing is NOT safe for production deployments.
      |    # you may want to use it during development, read more about it in the docs.
      |    #
      |    # auto-down-unreachable-after = 10s
      |    roles = [${roles.mkString(",")}]
      |  }
      |  # extensions = []
      |  extensions = ["akka.cluster.client.ClusterClientReceptionist"]
      |}
    """.stripMargin
}
