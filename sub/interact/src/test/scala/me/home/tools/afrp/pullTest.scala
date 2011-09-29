package me.home.tools.afrps

import org.scalatest.matchers.MustMatchers
import org.scalatest.matchers.BePropertyMatcher
import org.scalatest.matchers.BePropertyMatchResult
import org.scalatest.FreeSpec

object PullTest {
  def anInstanceOf[T](implicit manifest: Manifest[T]) = {
    val clazz = manifest.erasure.asInstanceOf[Class[T]]
    new BePropertyMatcher[AnyRef] {
      def apply(left: AnyRef) = BePropertyMatchResult(clazz.isAssignableFrom(left.getClass), "an instance of " + clazz.getName)
    }
  }
}

class PullTest extends FreeSpec with MustMatchers {
  import PullTest._
  import SF._
  
  implicit val sfops = SimpleSF
  
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
      val sf = arr { i: Int => i.toString } >>> arr { s: String => s + s } // Signal function level

      // Immutable
      val s = sf(`return`(10)) // Signal level
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
    val sf2 = arr { t: (Int, Int) => (t._1 + t._2, t._2 + 1) }
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
    }
  }
}