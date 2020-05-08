//package session.akka.db
//
//import slick.jdbc.JdbcProfile
//
//trait Tables{
//  val profile:JdbcProfile
//  import profile.api._
//  import session.akka.mode.{User => MUser}
//
//  class User(tag:Tag) extends Table[MUser](tag, "USER"){
//    def id =column[Long]("ID", O.PrimaryKey, O.AutoInc)
//    def name = column[String]("NAME")
//    override def * = (id, name) <> ((MUser.apply _).tupled, MUser.unapply)
//  }
//
//  val user = TableQuery[User]
//}
