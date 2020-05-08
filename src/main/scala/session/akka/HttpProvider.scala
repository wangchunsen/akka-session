package session.akka

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives.{complete, get, path, pathPrefix, _}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import scaldi.{Injectable, Injector}
import Injectable._
import session.akka.Util._
import session.akka.service.UserService

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

class HttpProvider private(serviceBinding: ServerBinding) {
  def shutdownSync(): Unit = serviceBinding.unbind().sync
}

object HttpProvider {
  def startup(implicit injector: Injector): HttpProvider = {
    val port = inject[Int]("http.port")
    val userService = inject[UserService]
    implicit val system = inject[ActorSystem]

    import PlayJsonSupport._
    val route: Route =
      pathPrefix("api") {
        pathPrefix("users") {
          (pathEnd & get) {
            complete(userService.allUsers())
          } ~
            (put & path(Segment)) { name =>
              complete(userService.addUser(name))
            }
        }
      }

    implicit val materializer: ActorMaterializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val rest = Http().bindAndHandle(route, "localhost", port = port).sync(200 millisecond)
    system.log.info(s"Http service bind to port $port")
    new HttpProvider(rest)
  }
}
