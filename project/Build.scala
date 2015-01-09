import sbt._
import Keys._

object BuildSettings {

  val paradiseVersion   = "2.0.1"

  val scalaBuildVersion = "2.11.5"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "de.bstudios",
    scalacOptions ++= Seq("-deprecation"),
    scalaVersion := scalaBuildVersion,
    crossScalaVersions := Seq("2.10.2", "2.10.3", "2.10.4", "2.11.0", "2.11.1", "2.11.2"),
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += Resolver.sonatypeRepo("releases"),
    addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)
  )
}

object MyBuild extends Build {
  import BuildSettings._

  lazy val macros: Project = Project(
    "macros",
    file("macros"),
    settings = buildSettings ++ Seq(
      name := "Laziness",
      version := "0.2-SNAPSHOT",
      libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _),
      libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-compiler" % _),
      libraryDependencies ++= (
        if (scalaVersion.value.startsWith("2.10")) List("org.scalamacros" %% "quasiquotes" % paradiseVersion)
        else Nil
      )
    )
  )

  lazy val examples: Project = Project(
    "examples",
    file("examples"),
    settings = buildSettings ++ Seq(
      publish := {},
      publishLocal := {},
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test",
      initialCommands in console := """import de.bstudios.laziness._"""
    )
  ) dependsOn(macros)

}
