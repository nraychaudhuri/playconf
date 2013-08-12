import sbt._
import Keys._
import play.Project._
import cloudbees.Plugin._

object ApplicationBuild extends Build {

  val appName         = "playconf"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "com.google.inject" % "guice" % "3.0",
    "javax.inject" % "javax.inject" % "1",
    "org.mockito" % "mockito-core" % "1.9.5" % "test"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
  ).settings(cloudBeesSettings :_*).settings(CloudBees.applicationId := Some("PlayConf"))

}
