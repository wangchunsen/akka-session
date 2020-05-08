package session.akka

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object Util {
  @inline
  def wrapException[T](fun: => T): T =
    try {
      fun
    } catch {
      case e: Exception => throw new Exception(e)
    }

  implicit class SyncFuture[T](val future: Future[T]) extends AnyVal {

    def sync: T = Util.wrapException(Await.result(future, Duration.Inf))

    def sync(duration: Duration):T = Util.wrapException(Await.result(future, duration))
  }

}
