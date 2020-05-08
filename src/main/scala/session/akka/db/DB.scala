package session.akka.db

import com.typesafe.config.Config
import scala.concurrent.Future

case class User(number: Long, password: String)

trait DBT{
  import scala.concurrent.ExecutionContext.Implicits.global
  import io.getquill._

  val ctx = new PostgresJdbcContext(SnakeCase, "db-quill")

  //  val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)

  import ctx._

  def runQuery[T](quoted: Quoted[Query[T]]): Future[List[T]] = Future.apply{
    ctx.transaction(

    )
    ctx.run(quoted)
  }
}

class DB(config: Config) extends DBT {

  import ctx._

  private val  userQuery = quote {
    query[User].filter(_.number == 123).take(1)
  }

  runQuery(userQuery)

//  val (profile, database) = {
//    val dbConfig = DatabaseConfig.forConfig[JdbcProfile]("db-default", config = config)
//    val profile = dbConfig.profile
//    val db = dbConfig.db
//
//
//    profile -> db
//  }
//  type Database = profile.backend.Database
//
//  val api = profile.api
//  val tables: Tables = new Tables {
//    override val profile: JdbcProfile = DB.this.profile
//  }

  testConnection()

  private def testConnection(): Unit = {
    val value1 = ctx.run(
      quote {
        infix"Select 1".as[Int]
      }
    )
    assert(value1 == 1)
  }
//
//  @inline
//  def run[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] = database.run(a)
}
