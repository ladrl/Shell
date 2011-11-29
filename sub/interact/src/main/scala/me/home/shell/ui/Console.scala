package me.home.shell.ui

import scala.swing.event._
import scala.swing.{ TextArea, ScrollPane, ListView }
import me.home.tools.afrp.AkkaEF.async
import me.home.tools.afrp.{ EFops, EF, SFops, SF, E, S }
import EF._
import SF._
import javax.swing.JTextArea
import java.awt.Graphics
import me.home.tools.afrp.SimpleEF
import me.home.tools.afrp.SimpleSF

/*
 *  TODO:
 *  Convert these ad-hoc class constructor functions with classes which take a
 *  set of signals/events and nothing more. These signals and events are then 
 *  in turn generated from a component.
 *  ==> There is no update or display logic in the signals or events. All state too
 *      is encapsulated inside loops
 *  The rationale for this is the simplification
 *  of tests for these GUI related logics.
 *  
 */

/*
 * Ideas:
 *  - To simplify the configuration of the components, there could be some sort of
 *    config object which is then passed via event to the component. It should
 *    involve stuff like visibility. fonts, ppp.
 */

object Console {

  def createList(implicit efops: EFops) = new ListView[String] {
    fixedCellHeight = font.getSize()
    import javax.swing.JList
    override lazy val peer = new JList with SuperMixin {
      override def paintComponent(g: Graphics) {
        super.paintComponent(g)
      }
    }

    val e_insert = accept { entry: (String, Int) =>
      listData = listData.splitAt(entry._2) match {
        case (Nil, Nil) => List[String]().padTo(entry._2, "") :+ entry._1
        case (p1, Nil) => p1.padTo(entry._2, "") :+ entry._1
        case (p1, p2) => p1 ++ List(entry._1) ++ p2
      }
    }
    val e_update = accept { entry: (String, Int) =>
      listData = listData.splitAt(entry._2) match {
        case (Nil, Nil) => List[String]().padTo(entry._2, "") :+ entry._1
        case (p1, Nil) => p1.padTo(entry._2 - 1, "") :+ entry._1
        case (p1, p2) => (p1.drop(1) :+ entry._1) ++ p2
      }
    }
  }

  /**
   * Input component
   * Displays an area of text in which the user can type commands. It supports self-clearing
   * and tab-completion callback (currently as a special case)
   *
   * It needs:
   * - An event for key events
   * - A signal for the content
   * - An async event for text update
   * - An async event for caret update
   *
   * It provides:
   * - An event for tab-completion requests
   * - An event for entered commands
   * 
   * 
   * The provided event for entered commands must be wired to the component later.
   * 
   * 
   */
  case class TextOutputEvents(val e_content: E[String], val e_caret: E[Int])
  case class TextInputSignals(val s_content: S[String], val s_caret: S[Int])
  def createInput(e_enter: E[String], out: TextOutputEvents, in: TextInputSignals)(implicit efops: EFops) = {
  	accept { k: KeyEvent =>
  	  k match {
  	    case KeyTyped(_, '\n', _, _) => {
  	      e_enter(in.s_content())
  	      out.e_content("")
  	      out.e_caret(0)
  	    }
  	    case _ =>
  	  }
  	}
  }
  
  def createLabel(implicit efops: EFops) = new ScrollPane {
    val ta = new TextArea {
      editable = false
    }
    focusable = false
    val length = EF.arr { (i: Int, s: Int) => (i, i) } loop (0)
    val e_length = length(accept { i: Int => })

    val lines = EF.arr { (s: String, ss: List[String]) =>
      val newSs = ss :+ s
      val toDrop = newSs.length - length.state()
      if (toDrop > 0)
        (s, newSs.drop(toDrop))
      else
        (s, newSs)

    } loop (Nil)
    val e_line = lines(accept { s => ta.text = (lines.state() :+ s).reduce { _ + "\n" + _ } })

    contents = ta
  }
}