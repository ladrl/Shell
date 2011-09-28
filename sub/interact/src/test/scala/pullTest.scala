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
  
  "return 10" must "be a function0" in {
    `return` (10) must be (anInstanceOf[Function0[Int]])
    
    `return`(10)() must be (10)
  }
  
  "return a var" must "be a Function0 which samples the var" in {
    var a: Int = 0;
    
    val r = `return`(a);
    
    r() must be (0)
    a = 10
    r() must be (10)
  }
  "sequencing two sfs" must "sequence their operations" in {
    val sf = arr { i:Int => i.toString } >>> arr { s: String => s + s } // Signal function level
    val s = sf(`return`(10)) // Signal level
    var a = 0
    val s_var = sf(`return`(a))
    
    s() must be ("1010")
    s_var() must be("00")
    a = 123
    s_var() must be("123123")
  }
}