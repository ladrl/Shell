package me.home.tools.afrps

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers
import org.scalatest.matchers.BePropertyMatcher
import org.scalatest.matchers.BePropertyMatchResult

class PullTest extends FlatSpec with MustMatchers {
  def anInstanceOf[T](implicit manifest: Manifest[T]) = {
    val clazz = manifest.erasure.asInstanceOf[Class[T]]
    new BePropertyMatcher[AnyRef] {
      def apply(left: AnyRef) = BePropertyMatchResult(clazz.isAssignableFrom(left.getClass), "an instance of " + clazz.getName)
    }
  }

  import SF._
  "The arr of a function f: {i:Int => i.toString}" must "be SF[Int, String]" in {
    arr { i: Int => i.toString } must be(anInstanceOf[SF[Int, String]])
  }

  "return" must "be a function" in {
    `return`(10) must be(anInstanceOf[Function0[Int]])
  }

  it must "provide access to its constant argument" in {
    `return`(10)() must be(10)
  }

  it must "provide access to its variable argument" in {
    var a: Int = 0;

    val r = `return`(a);

    r() must be(0)
    a = 10
    r() must be(10)
  }

  ">>>" must "sequence the operations of two sf's (both mutable and immutable)" in {
    val sf = arr { i: Int => i.toString } >>> arr { s: String => s + s } // Signal function level

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

  "&&&" must "parallelize the operations of two sf's into a tuple (both mutable and immutable)" in {
    val sf = arr { i: Int => i.toString } &&& arr { i: Int => i * 100 }

    // Immutable
    val s = sf(`return`(10))
    s() must be(("10", 1000))

    var a = 0
    val s_var = sf(`return`(a))
    s_var() must be(("0", 0))
    a = 111
    s_var() must be(("111", 11100))
  }

  "first" must "be a Function0 of a Tuple" in {
    val sf = (arr { i: Int => i / 100 } &&& arr { i: Int => i * 100 }) >>> first(arr { i: Int => i.toString })

    var a = 1000
    val s = sf(`return`(a))

    s() must be(("10", 100000))
  }
  
  "loop" must "create a SF[A, B] from a SF2[A, B, C] and a C" in {
    val sf2 = arr { t: (Int, Int) => (t._1 + t._2, t._2 + 1) }
    val sf = sf2.loop(0) // Defines the scope of the state!
    sf must be (anInstanceOf[SF[Int, Int]])
    var a = 10
    val s = sf(`return`(a))
    s() must be (10)
    s() must be (11)
    a = 0
    
    s() must be (2)
    sf(`return`(a))() must be (3) // Check if state gets reset by recreating s...
  }
}