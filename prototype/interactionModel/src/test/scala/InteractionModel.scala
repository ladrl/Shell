package me.scalatoys.shell.prototype.interactionModel

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers


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
    var source: String = ""
    val s = new Signal[String] { override def sample = source }
    
    val f : SignalTransformer[String, String] => String = s => s.source.sample + s.source.sample
    
    val st = new SignalTransformer[String, String] {
      def source = s
      def process(s: this.type): String = f (s)
    }
    
    val stt = new SignalTransformer[String, String] {
      def source = st
      def process(s: this.type): String = f (s)
    }
    
    st.sample must be ("")
    source = "1233"
    st.sample must be ("12331233")
    stt.sample must be ("1233123312331233")
  }
}
