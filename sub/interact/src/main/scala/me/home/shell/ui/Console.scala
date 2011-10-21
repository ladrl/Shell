package me.home.shell.ui

import scala.swing._
import scala.swing.event._
import scala.swing.event.Key.Modifiers
import scala.swing.event.Key.Location.Value
import me.home.tools.afrp._
import me.home.tools.afrp.AkkaEF._
import EF._
import SF._

object Console {
  def createInput(chars: String, e_enter: E[(String, Char)])(implicit efops: EFops) = new TextArea {
    
    def mapEvents[A](events: Iterable[E[A]]): E[A] = accept { a: A => events.map { _(a) } }
    
    val selfClearEf = async { EF.arr { t: (String, Char) => t._2 } } // only send the char
    val selfClear = selfClearEf(accept { c: Char =>
      println("clearing")
      c match {
        case '\n' => text = ""
        case '\t' => text = text.dropRight(1)
      }
    })
    val events = e_enter :: selfClear :: Nil
    val e = mapEvents(events)
    
    listenTo(keys)
    reactions += {
      case KeyTyped(_, c, _, _) if chars.contains(c) => e((text, c))
    }
  }
  def createLabel(t: String)(implicit efops: EFops) = {
    val l = new Label(t)
    val e_out = accept { l.text = (_: String) }
    (l, e_out)
  }
}