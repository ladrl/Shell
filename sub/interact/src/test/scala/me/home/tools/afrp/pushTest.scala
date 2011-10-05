package me.home.tools.afrps

import org.scalatest.FreeSpec
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.junit.JUnitSuite


class pushTest extends FreeSpec with MustMatchers {
  import EF._

  implicit val efops = SimpleEF

  "The accept to a value must" - {
    "update a var" - {
      var a = 0
      val e = accept { a = (_: Int) }

      e(10)
      a must be(10)
      e(0)
      a must be(0)
    }
    "prepend a list" - {
      val l = new scala.collection.mutable.ListBuffer[Int]
      val e = accept { l.prepend(_: Int) }

      l must be(Nil)
      e(10)
      l must be(10 :: Nil)
      e(0)
      l must be(0 :: 10 :: Nil)
    }
    "be equal to the accept of it" - {
      var a = 0
      val e = accept { a = (_:Int) }
      val e2 = accept { e }
      
      e(10)
      a must be (10)
      e(0)
      a must be (0)
      e2(10)
      a must be (10)
      e2(0)
      a must be (0)
      
    }
  }

  "The first of a ef" - {
    "must be an ef of a tuple" - {
      val ef:EF[(Int, Int), (String, Int)] = first (arr { i: Int => i.toString })
      
      var a = ""
      var b = 0
      val e = ef(accept { t:(String, Int) => a = t._1; b = t._2 })
      
      e((10, 10))
      a must be ("10")
      b must be (10)
    }
  }
  
  "The arr of a function" - {
    "must update a var with a transformed value" - {
      val ef = arr { i: Int => i.toString }

      var a = ""
      val e = ef(accept { a = (_: String) })
      e(1)
      a must be("1")
      e(-1000)
      a must be("-1000")
    }
  }

  "The >>> of two ef's" - {
    "must be the sequence of the ef's" - {
      val ef1 = arr { i: Int => i.toString }
      val ef2 = arr { d: Double => d.toInt }

      val ef = ef2 >>> ef1

      var a = ""
      val e = ef(accept { a = (_: String) })

      e(1.0)
      a must be("1")

      e(1221.5234)
      a must be("1221")
    }
  }
  
  "The &&& of two ef's" - {
    "must dispatch an event through two ef's" - {
      val ef1 = arr { i: Int => i.toString }
      val ef2 = arr { i: Int => i.toDouble }
      
      val ef = ef1 &&& ef2
      
      var a: String = ""
      var b: Double = 0
      val e = ef(accept { t => a = t._1; b = t._2})
      
      e(10)
      a must be ("10")
      b must be (10.0)
      
      e(100)
      a must be ("100")
      b must be (100.0)
    }
  }
  
  "The loop of a suitable ef" - {
    "must be a stateful ef" - {
      val ef = arr { (i: Int, d: Double) => ((i + d).toString, d + i) }
      val efL = ef.loop(0.0)
      
      var a = ""
      val e = efL(accept { a = (_:String)})
      e(10)
      a must be ("10.0")
      e(0)
      a must be ("10.0")
      e(-10)
      a must be ("0.0")
    }
  }
}