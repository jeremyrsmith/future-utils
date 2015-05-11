name := "FutureUtils"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _)
libraryDependencies += "com.twitter" %% "util-core" % "6.24.0"