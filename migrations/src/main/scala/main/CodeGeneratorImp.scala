package main

import slick.basic.DatabaseConfig
import slick.codegen.{OutputHelpers, SourceCodeGenerator}
import slick.jdbc.JdbcProfile
import slick.jdbc.meta.MTable
import slick.model.Model

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

class CodeGeneratorImp(model: Model) extends SourceCodeGenerator(model)
  with OutputHelpers {
  override val ddlEnabled: Boolean = false

  override def entityName: String => String = (dbName: String) => dbName.toCamelCase

  override def tableName: String => String = (dbName: String) => "T" + dbName.toCamelCase

}

object CodeGeneratorImp {
  private val ignoreTables = Seq("flyway_schema_history")

  def main(args: Array[String]): Unit = {
    val dbConfig = DatabaseConfig.forConfig[JdbcProfile]("db-default")
    val db = dbConfig.db
    val profile = dbConfig.profile
    val outputDir = "src/main/scala"
    val pkg = "generated"

    implicit val exeContext = ExecutionContext.global
    try {
      val action = MTable.getTables.map(tables => tables.filterNot(table => ignoreTables contains table.name.name.toLowerCase))
      val getModels = profile.createModel(Some(action), false).withPinnedSession
      val m = Await.result(db.run(getModels), Duration.Inf)
      new CodeGeneratorImp(m).writeToFile(dbConfig.profileName, outputDir, pkg)
    } finally db.close
  }
}
