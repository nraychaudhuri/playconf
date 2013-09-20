import play.Project._

name := "playconf"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "mysql" % "mysql-connector-java" % "5.1.26",
    "com.google.inject" % "guice" % "3.0",
    "javax.inject" % "javax.inject" % "1",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "org.jsoup" % "jsoup" % "1.7.2" % "test"
  )

playJavaSettings
