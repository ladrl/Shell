package me.scalatoys.shell.Swing

import scala.swing._
import scala.swing.event._

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
import org.apache.batik.bridge.{
  UpdateManagerListener,
  UpdateManagerEvent
}
import org.w3c.dom.svg.SVGDocument

object State extends Enumeration {
  val Prepare = Value("Prepare")
  val Started = Value("Started")
  val Stopped = Value("Stopped")
  val Completed = Value("Completed")
  val InProgress = Value("In Progress")
  val Suspended = Value("Suspended")
  val Failed = Value("Failed")
}

object SVGCanvas {
  abstract class CanvasEvent {
    def state: State.Value
  }
  case class DocumentLoading(val source: Component, val state: State.Value, val doc: Option[SVGDocument]) extends CanvasEvent with ComponentEvent
  case class GVTBuild(val source: Component, val state: State.Value) extends CanvasEvent with ComponentEvent
  case class GVTRender(val source: Component, val state: State.Value) extends CanvasEvent with ComponentEvent
  case class Manager(val source: Component, val state: State.Value) extends CanvasEvent with ComponentEvent
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
  lazy val manager = new Publisher {
    peer.addUpdateManagerListener(new UpdateManagerListener() {
      override def managerResumed(e: UpdateManagerEvent) {}
      override def managerStarted(e: UpdateManagerEvent) {}
      override def managerStopped(e: UpdateManagerEvent) {} 
      override def managerSuspended(e: UpdateManagerEvent) {}
      override def updateCompleted(e: UpdateManagerEvent) {}
      override def updateFailed(e: UpdateManagerEvent) {}
      override def updateStarted(e: UpdateManagerEvent) {}
    })
  }
}
