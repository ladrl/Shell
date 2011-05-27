import sbt._

trait TestProject { self: DefaultProject  =>
  val scalaTest = "org.scalatest" % "scalatest" % "1.3"
}

class Project(info: ProjectInfo) extends ParentProject(info) {
    val prototypes = project("prototype", "Prototypes", new Prototype(_))
    class Prototype(info: ProjectInfo) extends ParentProject(info) {
        val swing = project("swing", "swing-prototype", new Swing(_))
        class Swing(info: ProjectInfo) extends DefaultProject(info) {
            val swing = "org.scala-lang" %% "swing"
        }
        val batik = project("batik", "batik-prototype", new Batik(_))
        class Batik(info: ProjectInfo) extends DefaultProject(info) {
            override def fork = forkRun
            val scalaSwing = "org.scala-lang" % "scala-swing" % "2.8.1"
            val batik = "org.apache.xmlgraphics" % "batik-swing" % "1.7"
        }
        
        val interactionModele = project("interactionModel", "interactionModel", new InteractionModel(_))
        class InteractionModel(info: ProjectInfo) extends DefaultProject(info) with TestProject
    }
}