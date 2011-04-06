package me.scalatoys.shell.batik

// Simple SVG based gui prototype

import scala.util.logging._

import me.scalatoys.shell.Swing._

import scala.swing._
import scala.swing.event._
import java.io.File

object Batik extends SimpleSwingApplication with Logged with ConsoleLogger {
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
          
          canvas.get("Button.Label.Text").map { x => 
            x.setTextContent("aiueuia")
          }
        }
      }
      canvas.peer.setURI((new File("./prototype/batik/src/main/resources/Simple.svg")).toURL.toString)
    }
  }
}

