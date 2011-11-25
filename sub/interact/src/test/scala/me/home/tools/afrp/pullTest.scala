package me.home.tools.afrp

import org.scalatest.matchers.MustMatchers
import org.scalatest.FreeSpec

import me.home.tools.testUtil._

class SimpleSFTest extends SFTest(SimpleSF)

abstract class SFTest(val ops: SFops) extends FreeSpec with MustMatchers {
  import SF._

  implicit val sfops = ops

  "The arr of a function f: {i:Int => i.toString} must be SF[Int, String]" - {
    arr { i: Int => i.toString } must be(anInstanceOf[SF[Int, String]])
  }

  "The return of a value must" - {
    val r = `return`(10)
    "be a function" - {
      r must be(anInstanceOf[Function0[Int]])
    }

    "provide access to its constant argument" - {
      r() must be(10)
    }

    "provide access to its variable argument" - {
      var a: Int = 0;

      val r = `return`(a);

      r() must be(0)
      a = 10
      r() must be(10)
    }
  }

  "The >>> of two sf's must" - {
    "sequence the operations of two sf's (both mutable and immutable)" - {
      val sf = arr { i: Int => i.toString } >>> arr { s: String => s + s }

      // Immutable
      val s = sf(`return`(10))
      s() must be("1010")

      // Mutable
      var a = 0
      val s_var = sf(`return`(a))
      s_var() must be("00")
      a = 123
      s_var() must be("123123")
    }
  }

  "The &&& of two sf's must" - {
    val sf = arr { i: Int => i.toString } &&& arr { i: Int => i * 100 }
    "process a return of a val in parallel" - {
      val s = sf(`return`(10))
      s() must be(("10", 1000))
    }
    "process a return of a var in parallel" - {
      var a = 0
      val s_var = sf(`return`(a))
      s_var() must be(("0", 0))
      a = 111
      s_var() must be(("111", 11100))
    }
  }

  "first must" - {
    "be a Function0 of a Tuple" - {
      val sf = (arr { i: Int => i / 100 } &&& arr { i: Int => i * 100 }) >>> first(arr { i: Int => i.toString })

      var a = 1000
      val s = sf(`return`(a))

      s() must be(("10", 100000))
    }
  }

  "loop must" - {
    val sf2 = arr { (i: Int, j: Int) => (i + j, j + 1) }
    "create a SF[A, B] from a SF2[A, B, C] and a C" - {
      val sf = sf2.loop(0)
      sf must be(anInstanceOf[SF[Int, Int]])
    }
    "act as a stateful function" - {
      val sf = sf2.loop(0) // Defines the scope of the state!
      "with a return of val" - {
        sf must be(anInstanceOf[SF[Int, Int]])
        val s = sf(`return`(10))
        s() must be(10)
        s() must be(11)
      }

      var a = 10
      "with a return of var" - {
        val s = sf(`return`(a))
        s() must be(12)
        s() must be(13)

        a = 0
        s() must be(4)
      }
      "which keeps its state when a new signal is created" - {
        sf(`return`(a))() must be(5)
      }
      
      "giving access to its state via a signal" - {
        val s = sf(`return`(0))
        sf.state() must be (6)
        s() must be (6)
        sf.state() must be (7)
        sf.state() must be (7)
      }
    }
  }
}

class AkkaSFTest extends FreeSpec with MustMatchers {
  import SF._
  import AkkaSF._
  implicit val sfops = SimpleSF

  "The async of a sf must act identical as normal sf" - {
    "with arr" - {
      var a = ""
      val s = `return`(a)

      val sf = arr { (s: String) => s + "test" + s }
      val async_sf = async(sf)

      val async_s = async_sf(s)
      val sync_s = sf(s)

      async_s() must be("test")
      sync_s() must be("test")

      a = "gugus"
      async_s() must be("gugustestgugus")
      sync_s() must be("gugustestgugus")
    }
    "with >>> of arr's" - {
      var a = 0
      val s = `return`(a)

      val sf1 = arr { (i: Int) => i.toString }
      val sf2 = arr { (s: String) => "Sampled %s" format s }

      val sf = sf1 >>> sf2
      val async_sf = async(sf1) >>> async(sf2)

      val sync_s = sf(s)
      val async_s = async_sf(s)

      sync_s() must be("Sampled 0")
      async_s() must be("Sampled 0")
    }
  }
  "The async of an sf must use more than one process" - {
  }
}
