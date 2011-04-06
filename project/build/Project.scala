import sbt._

class Project(info: ProjectInfo) extends ParentProject(info) {
    val prototypes = project("prototype", "Prototypes", new Prototype(_))
    class Prototype(info: ProjectInfo) extends ParentProject(info) {
        val swing = project("swing", "swing-prototype", new Swing(_))
        class Swing(info: ProjectInfo) extends DefaultProject(info) {
            val swing = "org.scala-lang" %% "swing"
        }
        val batik = project("batik", "batik-prototype", new Batik(_))
        class Batik(info: ProjectInfo) extends DefaultProject(info) {
            val scalaToolsSnapshots = ScalaToolsSnapshots
            val scalatest = "org.scalatest" % "scalatest" % "1.3"
            override def compileOptions = CompileOption("-g:source") :: Nil
            override def fork = forkRun("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5000" :: Nil)
            val scalaSwing = "org.scala-lang" % "scala-swing" % "2.8.1"
            
            val batikVersion = "1.7"
            val batik = "org.apache.xmlgraphics" % "batik-swing" % batikVersion
            val js = "org.apache.xmlgraphics" % "batik-script" % batikVersion
        }
    }
}