package session.akka.service

import scaldi.{Injectable, Injector}
import Injectable._
import session.akka.db.DB
import session.akka.{mode => m}

import scala.concurrent.Future

class UserService(implicit injector: Injector) {
  private val db = inject[DB]

//  import db.api._
//  import db.tables.user
//  private val insert = user returning user.map(_.id) into ((mUser, id) => mUser.copy(id = id))

  def allUsers(): Future[Seq[m.User]] = ???

  def addUser(name: String): Future[m.User] = ???
}
