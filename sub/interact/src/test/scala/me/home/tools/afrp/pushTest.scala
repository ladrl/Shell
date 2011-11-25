package me.home.tools.afrp

import org.scalatest.FreeSpec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.junit.JUnitSuite
import org.scalatest.FlatSpec
import scala.collection.mutable.ListBuffer

class pushTest extends FlatSpec with MustMatchers {
//  import EF._
//
//  implicit val efops = SimpleEF
//
//  "The accept to a value" must "update a var" in {
//    var a = 0
//    val e = accept { a = (_: Int) }
//
//    e(10)
//    a must be(10)
//    e(0)
//    a must be(0)
//  }
//  it must "prepend a list" in {
//    val l = new scala.collection.mutable.ListBuffer[Int]
//    val e = accept { l.prepend(_: Int) }
//
//    l must be(Nil)
//    e(10)
//    l must be(10 :: Nil)
//    e(0)
//    l must be(0 :: 10 :: Nil)
//  }
//  it must "be equal to the accept of it" in {
//    var a = 0
//    val e = accept { a = (_: Int) }
//    val e2 = accept { e }
//
//    e(10)
//    a must be(10)
//    e(0)
//    a must be(0)
//    e2(10)
//    a must be(10)
//    e2(0)
//    a must be(0)
//
//  }
//
//  "The first of a ef" must "must be an ef of a tuple" in {
//    val ef: EF[(Int, Int), (String, Int)] = first(arr { i: Int => i.toString })
//
//    var a = ""
//    var b = 0
//    val e = ef(accept { t: (String, Int) => a = t._1; b = t._2 })
//
//    e((10, 10))
//    a must be("10")
//    b must be(10)
//  }
//
//  "The arr of a function" must "update a var with a transformed value" in {
//    val ef = arr { i: Int => i.toString }
//
//    var a = ""
//    val e = ef(accept { a = (_: String) })
//    e(1)
//    a must be("1")
//    e(-1000)
//    a must be("-1000")
//  }
//
//  "The arr of an event function" must "filter the input" in {
//    val ef = arr { e: E[Int] =>
//      accept { s: String =>
//        try { e(Integer.parseInt(s)) }
//        catch { case _ => }
//      }
//    }
//
//    var a = 0
//    val ae = accept { a = (_: Int) }
//    val e = ef(ae)
//
//    e("1123")
//    a must be(1123)
//    e("iaternuiaten")
//    a must be(1123)
//    e("1234")
//    a must be(1234)
//  }
//
//  "The arrMap of a function returning a list" must "send each element of the List" in {
//    val ef = arr { e: E[Int] =>
//      accept { i: Int =>
//        (i match {
//          case 0 => Nil
//          case i if i > 0 => 0 to i toList
//          case i => i to 0 toList
//        }) map { e(_) }
//      }
//    }
//
//    val a = ListBuffer[Int]()
//    val ae = accept { a += (_: Int) }
//
//    val e = ef(ae)
//
//    e(10)
//    a must be(0 to 10 toList)
//    a.clear
//    e(-10)
//    a must be(-10 to 0 toList)
//  }
//
//  "The >>> of two ef's" must "be the sequence of the ef's" in {
//    val ef1 = arr { i: Int => i.toString }
//    val ef2 = arr { d: Double => d.toInt }
//
//    val ef = ef2 >>> ef1
//
//    var a = ""
//    val e = ef(accept { a = (_: String) })
//
//    e(1.0)
//    a must be("1")
//
//    e(1221.5234)
//    a must be("1221")
//  }
//
//  "The &&& of two ef's" must "dispatch an event through two ef's" in {
//    val ef1 = arr { i: Int => i.toString }
//    val ef2 = arr { i: Int => i.toDouble }
//
//    val ef = ef1 &&& ef2
//
//    var a: String = ""
//    var b: Double = 0
//    val e = ef(accept { t => a = t._1; b = t._2 })
//
//    e(10)
//    a must be("10")
//    b must be(10.0)
//
//    e(100)
//    a must be("100")
//    b must be(100.0)
//  }
//
//  "The loop of a suitable ef" must "be a stateful ef" in {
//    val ef = arr { (i: Int, d: Double) => ((i + d).toString, d + i) }
//    val efL = ef.loop(0.0)
//
//    var a = ""
//    val e = efL(accept { a = (_: String) })
//    e(10)
//    a must be("10.0")
//    e(0)
//    a must be("10.0")
//    e(-10)
//    a must be("0.0")
//  }
//}
//
//class AkkaEFTest extends FreeSpec with MustMatchers {
//  import EF._
//  import AkkaEF._
//  implicit val efops = SimpleEF
//
//  "The async of an ef must act identical as a normal ef (eventually)" - {
//    "with arr" - {
//      var a = ""
//      val ea = accept { a = (_: String) }
//
//      var b = ""
//      val eb = accept { b = (_: String) }
//
//      val ef = arr { i: Int => i.toString }
//
//      val e = ef(eb)
//
//      e(10)
//      b must be("10")
//
//      val async_ef = async(ef)
//      val async_e = async_ef(ea)
//
//      async_e(10)
//      Thread.sleep(10)
//      a must be("10")
//    }
//  }
}