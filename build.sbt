name := "dyplom"

version := "1.0"

//scalaVersion := "2.12.1"
scalaVersion := "2.11.8"

libraryDependencies += "org.jsoup" % "jsoup" % "1.10.2"
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.5"
libraryDependencies += "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.6"
libraryDependencies += "com.univocity" % "univocity-parsers" % "2.4.1"

// https://stackoverflow.com/questions/41564915/why-could-not-find-implicit-error-in-scala-intellij-scalatest-scalactic
//libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.1.0"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.0" exclude("org.scalatest", "scalatest_2.11")
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "3.0.1" % "test"

//libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.8.2"
libraryDependencies += "org.jboss.logging" % "jboss-logging" % "3.3.1.Final"
