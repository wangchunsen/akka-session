
val commonSetting = Seq(
  version := "0.1",
  scalaVersion := "2.12.3"
)

val akkaVersion = "2.5.19"
val akkaHttpVersion = "10.1.7"

lazy val akkDeps = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  //play json integrate
  "de.heikoseeberger" %% "akka-http-play-json" % "1.23.0",
  "com.typesafe.play" %% "play-json" % "2.6.10",

  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
)


lazy val scalaDiDeps = Seq(
  "org.scaldi" %% "scaldi-akka" % "0.5.8",
  "org.scaldi" %% "scaldi-jsr330" % "0.5.9"
)

//lazy val dbDeps = List(
//  "com.h2database" % "h2" % "1.4.197"
//)
//
//lazy val slickDeps = Seq(
//  "com.typesafe.slick" %% "slick" % "3.2.3",
//  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3"
//)

lazy val dataBase = Seq(
  "org.postgresql" % "postgresql" % "42.2.8",
  "io.getquill" %% "quill-jdbc" % "3.4.10"
)

lazy val loggingDeps = Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

lazy val catsDeps = Seq(
  "org.typelevel" %% "cats-core" % "1.5.0"
)

lazy val root = project.in(file("."))
  .withId("akka-session")
  .settings(commonSetting: _*)
  .settings(
    libraryDependencies ++= Seq(akkDeps, scalaDiDeps, dataBase, loggingDeps, catsDeps).flatten
  )

lazy val migrations =
  project.in(file("migrations"))
    .dependsOn(root)
    .settings(commonSetting: _*)
    .settings {
      libraryDependencies ++= Seq(
        "org.flywaydb" % "flyway-core" % "5.2.4",
        "com.typesafe.slick" %% "slick-codegen" % "3.2.3"
      )
    }

addCommandAlias("mg", "migrations/runMain main.Main")
addCommandAlias("codeGen", "migrations/runMain main.CodeGeneratorImp")