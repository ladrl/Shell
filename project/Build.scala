import sbt._
import Keys._

object BuildSettings {
  val buildOrganization = "me.home.tools"
  val buildVersion      = "0.1"
  val buildScalaVersion = "2.9.0-1"

  val buildSettings = Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    //shellPrompt  := Prompt.buildShellPrompt,
    scalacOptions ++= Seq("-unchecked", "-deprecation")    
  )
}


// Shell prompt which show the current project, 
// git branch and build version
//object Prompt {
//  object devnull extends ProcessLogger {
//    def info (s: => String) {}
//    def error (s: => String) { }
//    def buffer[T] (f: => T): T = f
//  }
//  
//  val current = """\*\s+([\w-]+)""".r
//  
//  def gitBranches = ("git branch --no-color" lines_! devnull mkString)
//  
//  val buildShellPrompt = { 
//    (state: State) => {
//      val currBranch = current findFirstMatchIn gitBranches map (_ group(1)) getOrElse "-"
//      val currProject = Project.extract (state).currentProject.id
//      "%s:%s> ".format (
//        currProject, currBranch
//      )
//    }
//  }
//}
//
object Dependencies {
  val scalatest	= "org.scalatest" % "scalatest_2.9.0" % "1.6.1" % "test"
  val scalaz    = "org.scalaz" % "scalaz-core_2.9.0-1" % "6.0" % "compile" withSources
  val junit     = "junit" % "junit" % "4.5" % "test"
  val akka      ="se.scalablesolutions.akka" % "akka-actor" % "1.2"
  
  val akkaRepo  = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases"
  
  val test = Seq(
    scalatest,
    junit
  )
  
  val common = Seq {
    scalaz
  }
}

object ShellBuild extends Build {
  import BuildSettings._
  import Dependencies._
  
  
  
  lazy val resource = Project(
    "resource", 
    file("sub/resource"),
    settings = Defaults.defaultSettings ++ buildSettings ++ Seq(libraryDependencies := test ++ common )
  )
  lazy val interact = Project(
    "interact",
    file("sub/interact"),
    settings = Defaults.defaultSettings ++ buildSettings ++ Seq(resolvers += akkaRepo) ++ Seq(libraryDependencies += akka) ++ Seq(libraryDependencies ++= test)
  )
}