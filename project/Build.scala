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
    "org.twitter4j" % "twitter4j-core" % "3.0.3",
    "org.scribe" % "scribe" % "1.3.5"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
  ).settings(cloudBeesSettings :_*).settings(CloudBees.applicationId := Some("PlayConf"))

}
