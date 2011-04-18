package me.scalatoys.shell.batik

// Simple SVG based gui prototype

import scala.util.logging._

import me.scalatoys.shell.Swing._

import scala.swing._
import scala.swing.event._
import java.io.File
import me.scalatoys.shell.xml._
import me.scalatoys.shell.xml.dom._

object Batik extends SimpleSwingApplication with Logged with ConsoleLogger {
  val svgNs = Option(new java.net.URI("http://www.w3.org/2000/svg"))
  var log: List[String] = Nil
  def top = new MainFrame {
    title = "Batik Shell Prototype"
    contents = new BoxPanel(Orientation Vertical) { mainPanel =>
      val canvas = new SVGCanvas
      contents += canvas

      listenTo(canvas.document)
      listenTo(canvas.gvtTree)
      reactions += {
        case SVGCanvas.DocumentLoading(src, state, doc) => log("Loading document %s from %s" format (state, doc.map { _.getURL })) 
        case SVGCanvas.GVTBuild(src, state) => {
          log("GVT build: %s" format (state))
          state match {
            case State.Completed => {
              mainPanel.listenTo(canvas.js)
            }
            case _ =>
          }
        }
        case SVGCanvas.GVTRender(src, state) => log("GVT render: %s" format (state))
        case SVGCanvas.JSEvent(src, t) => {
          log("JSEvent(%s)" format t)
          
          canvas.get("LogWindow.Text").map { x => 
            val node = DOMNode(x)
            log = log :+ t.toString
            node.children = for((i, logEntry) <- (0 until log.length) zip log) yield {
              val span = node.owner.get.createElement("tspan", svgNs)
              span.children = List(node.owner.get.createText(logEntry))
              span.attrsFromString(Map("y" -> ("%dmm" format (i * 5)), "x" -> "0mm"))
              println(span.attributes)
              span
            }
          }
        }
      }
      canvas.peer.setURI((new File("./prototype/batik/src/main/resources/Simple.svg")).toURL.toString)
    }
  }
}

