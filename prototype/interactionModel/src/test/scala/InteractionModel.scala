package me.scalatoys.shell.prototype.interactionModel

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers

trait Scope 


trait EventModel {
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

class EventModelTest extends FlatSpec with MustMatchers with EventModel {

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

trait SignalModel {
  trait Signal[+T] {
    def sample: T
  }
  
  trait Sink[+T, S[+_]] {
    def source: S[T]
  }
  
  trait SignalTransformer[+A, +B] extends Sink[A, Signal] with Signal[B] {
    def source: Signal[A]
    def process(s: this.type): B
    def sample: B = process(this)
  }
}

class SignalModelTest extends FlatSpec with MustMatchers with SignalModel {
  "A signal" must "allow sampling" in {
    var source: Option[String] = None
    val s = new Signal[Option[String]] { override def sample = source }
    
    s.sample must be (None)
    source = Some("string")
    s.sample must be (Some("string"))
    s.sample must be (Some("string"))
    source = None
    s.sample must be (None)
    s.sample must be (None)
  }
  
  "A signal transformer" must "be able to transform a signal while it's sampled" in {
    var source: Option[String] = None
    val s = new Signal[Option[String]] { override def sample = source }
    
    val f : SignalTransformer[Option[String], Option[Int]] => Option[Int] = _.source.sample.flatMap { str => try { Some(Integer.parseInt(str)) } catch { case _ => None }}
    
    val st = new SignalTransformer[Option[String], Option[Int]] {
      def source = s
      def process(s: this.type): Option[Int] = f (s)
    }
    st.sample must be (None)
    source = Some("1233")
    st.sample must be (Some(1233))
    source = Some("3uietnuiae")
    st.sample must be (None)
  }
}
