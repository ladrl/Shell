package me.home.shell.ui

import scala.swing.event.KeyTyped
import scala.swing.{TextArea, ScrollPane}
import me.home.tools.afrp.AkkaEF.async
import me.home.tools.afrp.EF.accept
import me.home.tools.afrp.E
import me.home.tools.afrp.{EFops, EF}
import javax.swing.JTextArea
import java.awt.Graphics

object Console {
  
  def createInput(e_enter: E[(String, Char)])(implicit efops: EFops) = new TextArea {
    rows = 1
    lineWrap = true
    def mapEvents[A](events: Iterable[E[A]]): E[A] = accept { a: A => events.map { _(a) } }

    val clear = accept { c: Char =>
      c match {
        case '\n' => text = ""
        case '\t' => {
          text = text.filter { _ != '\t' }
          caret.position = text.length
        }
      }
    }
    val selfClearEf = async { EF.arr { t: (String, Char) => t._2 } } // only send the char
    val selfClear = selfClearEf(clear)

    val events = e_enter :: selfClear :: Nil
    val e = mapEvents(events)

    listenTo(keys)
    reactions += {
      case KeyTyped(_, c, _, _) if "\n\t".contains(c) => e((text, c))
    }
  }
  def createLabel(implicit efops: EFops) = new ScrollPane {
    val ta = new TextArea {
      override lazy val peer: JTextArea = new JTextArea("", 0, 0) with SuperMixin {
        override def paintBorder(g: Graphics) {
          g.setColor(java.awt.Color.BLUE)
          g.drawRoundRect(0, 0, 20, 20, 5, 5)
        }
      }
      editable = false
    }
    focusable = false
    val length = EF.arr { (i: Int, s: Int) => (i, i) } loop (0)
    val e_length = length(accept { i: Int => println("Change #lines to %d" format i) })

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