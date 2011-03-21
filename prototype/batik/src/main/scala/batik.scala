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
    contents = new BoxPanel(Orientation Vertical) {
      val canvas = new SVGCanvas
      contents += canvas
      contents += new Button { me =>
        text = "Load"
        reactions += {
          case ButtonClicked(src) if(src == me) => {
            val chooser = new FileChooser(new File("./prototype/batik/src/main/resources")) {
              multiSelectionEnabled = false
            }
            chooser.showOpenDialog(me) match {
              case FileChooser.Result.Approve => {
                val f = chooser.selectedFile
                canvas.peer.setURI(f.toURL.toString)
              }
              case _ =>
            }
          }
        }
      }
      listenTo(canvas.document)
      listenTo(canvas.gvtTree)
      reactions += {
        case SVGCanvas.DocumentLoading(src, state, doc) => log("Loading document %s from %s" format (state, doc.map { _.getURL }))
        case SVGCanvas.GVTBuild(src, state) => log("GVT build: %s" format (state))
        case SVGCanvas.GVTRender(src, state) => log("GVT render: %s" format (state))
      }
    }
  }
}

