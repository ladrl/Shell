package me.scalatoys.shell.batik

// Simple SVG based gui prototype


import scala.swing._
import scala.swing.event._
import java.io.File

import org.apache.batik.swing.JSVGCanvas
import org.apache.batik.swing.gvt.{
  GVTTreeRendererAdapter, GVTTreeRendererEvent
}
import org.apache.batik.swing.svg.{
  SVGDocumentLoaderAdapter,
  SVGDocumentLoaderEvent,
  GVTTreeBuilderAdapter,
  GVTTreeBuilderEvent
}



class SVGCanvas extends Component with Container.Wrapper {
  override lazy val peer = new JSVGCanvas
}


object Batik extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "Batik Shell Prototype"
    contents = new BoxPanel(Orientation Vertical) {
      val canvas = new SVGCanvas 
      contents += canvas
      contents += new Button { me =>
        text = "Load"
        reactions += {
          case ButtonClicked(src) if(src == me) => {
            val chooser = new FileChooser(new File(".")) {
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
    }
  }
}

