package me.scalatoys.shell.batik

// Simple SVG based gui prototype

import scala.util.logging._

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
import org.w3c.dom.svg.SVGDocument


object SVGCanvas {
  object State extends Enumeration {
    val Prepare = Value("Prepare")
    val Started = Value("Started")
    val Completed = Value("Completed")
    val InProgress = Value("In Progress")
  }
  abstract class CanvasEvent {
    def state: State.Value
  }
  case class DocumentLoading(val source: Component, val state: State.Value, val doc: Option[SVGDocument]) extends CanvasEvent with ComponentEvent
  case class GVTBuild(val source: Component, val state: State.Value) extends CanvasEvent with ComponentEvent
  case class GVTRender(val source: Component, val state: State.Value) extends CanvasEvent with ComponentEvent
}

class SVGCanvas extends Component with Container.Wrapper { me =>
  import SVGCanvas._
  override lazy val peer = new JSVGCanvas
  lazy val document = new Publisher {
    peer.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
      override def documentLoadingStarted(e: SVGDocumentLoaderEvent) { publish(SVGCanvas.DocumentLoading(me, State.Started, Option(e.getSVGDocument))) }
      override def documentLoadingCompleted(e: SVGDocumentLoaderEvent) { publish(SVGCanvas.DocumentLoading(me, State.Completed, Option(e.getSVGDocument))) }
      })
    }
  lazy val gvtTree = new Publisher {
    peer.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
      override def gvtBuildStarted(e: GVTTreeBuilderEvent) { publish(SVGCanvas.GVTBuild(me, State.Started)) }
      override def gvtBuildCompleted(e: GVTTreeBuilderEvent) { publish(SVGCanvas.GVTBuild(me, State.Completed)) }
    })
    peer.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
      override def gvtRenderingPrepare(e: GVTTreeRendererEvent) { publish(SVGCanvas.GVTRender(me, State.Prepare)) }
      override def gvtRenderingCompleted(e: GVTTreeRendererEvent) { publish(SVGCanvas.GVTRender(me, State.Completed)) }
      })
  }
}


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

