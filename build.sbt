organization := "com.github.jeremyrsmith"

name := "future-utils"

version := "0.2.0"

scalaVersion := "2.11.6"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.6"

libraryDependencies += "com.twitter" %% "util-core" % "6.24.0"

publishTo := {
  val artifactory = "http://acorns.artifactoryonline.com/"
  if(isSnapshot.value)
    Some("snapshots" at artifactory + "acorns/libs-snapshot-local")
  else
    Some("snapshots" at artifactory + "acorns/libs-release-local")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
