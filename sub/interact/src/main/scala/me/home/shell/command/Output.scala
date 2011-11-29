package me.home.shell.command
import me.home.tools.afrp._
import java.io.OutputStream
import java.io.InputStream
import java.io.OutputStreamWriter
import java.io.InputStreamReader
import java.io.BufferedReader
import akka.actor.Actor

object Tools {
  def storeAsList[A](length: Int)(implicit efops: EFops) = {
    val ef = EF.arr { (o: A, l: List[A]) => (l.drop(length), List(o) ++ l.take(length)) }
    ef.loop(Nil)
  }

  trait Command[+A]
  case class Add[A](val o: A) extends Command[A]
  case object Clear extends Command[Nothing]
  def EventBuffer[A](length: Int)(implicit efops: EFops) = EF.arr { (c: Command[A], l: List[A]) =>
    c match {
      case Add(a) => (l.drop(length), List(a) ++ l.take(length))
      case Clear => (l, Nil)
    }
  }.loop(Nil)

  trait Output[+A]
  case class Message(val msg: String) extends Output[Nothing]
  case class Query[A](val msg: String, val respond: E[A]) extends Output[A]

  def outputProcessor[A] = {
    val ef = EF.arr { (o: Output[A], _: Option[E[A]]) =>
      o match {
        case Message(msg) => (msg, None)
        case Query(msg, respond) => (msg, Some(respond))
      }
    }.loop(None)
    (ef, EF.accept { ans: A => ef.state().map { e => e(ans) } })
  }
}