package me.scalatoys.shell.prototype.interactionModel

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers

trait InteractionModel {
  
  trait Sink[-T, E[+_]] {
    def accept(e: E[T])
  }
  
  trait Source[-T, E[+_]] {
    def sink: Sink[T, E]
  }
  
  case class Event[+T](val content:T)
  
  trait EventSink[T] extends Sink[T, Event] {
    def f(t: T): Unit
    def accept(e: Event[T]): Unit = {
      val Event(content:T) = e
      f(content)
    }
  }
  
  trait EventSource[T] extends Source[T, Event]{
    def sink: EventSink[T]
  }
  
  trait EventTransformer[A, B] extends Sink[A, Event] with EventSource[B] {
    def process(a: A) : B
    def accept(e: Event[A]): Unit = {
      val Event(content) = e
      val result = process(content)
      sink.accept(Event(result))
    }
  }
}


class InteractionModelTest extends FlatSpec with MustMatchers with InteractionModel {

  

  
  abstract class A
  case class AB(val b: Boolean) extends A
  case class AS(val s: String) extends A
  case class AI(val i: Int) extends A
  
  "An event sink" must "accept an event" in {
    var v: Option[A] = None
    val es = new EventSink[A] {
      def f(t: A): Unit = v = Some(t)
    }
    es.accept(Event(AB(true)))
    v must be (Some(AB(true)))
  }
  
  "An event transformer" must "accept an event, transform it and refire it" in {
    var v: Option[Int] = None
    val es = new EventSink[Option[Int]] { override def f(t: Option[Int]) = v = t }
    val et = new EventTransformer[A, Option[Int]] {
      val sink = es
      def process(a: A) : Option[Int] = a match {
        case AB(bool) => Some(if(bool == true) 1 else 0)
        case AI(int) => Some(int)
        case AS(str) => {
          try {
            Some(Integer.parseInt(str))
          } catch {
            case _: NumberFormatException => None
          }
        }
      }
    }
    et.accept(Event(AB(true)))
    v must be (Some(1))
    
    et.accept(Event(AI(11999)))
    v must be (Some(11999))
    
    et.accept(Event(AS("1234")))
    v must be (Some(1234))
    
    et.accept(Event(AS("iuteniare")))
    v must be (None)
  }
  
  "A function" must "transform and refire an event" in {
pending
  }
  "A buffer" must "be an event sink and a signal source" in {
    pending
  }
}