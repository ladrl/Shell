package me.home.tools.resource

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers

trait Base

case class Sub() extends Base

trait In

case class In1() extends In
case class In2() extends In

case class Out(val in: In)

class CanCreateTest extends FlatSpec with MustMatchers {
  "A CanCreate" must "act like a constructor with no parameters" in {
    val cc = new CanCreate[Base] {
      def create = Sub()
    }
    cc.create must be (Sub())
  }
  
  "A CanCreateFrom" must "act like a container constructor" in {
    val ccf = new CanCreateFrom[Option, Int, Int] {
      def from(i: Int) = new CanCreate[Option[Int]] {
        def create = if(i > 0) Some(i) else None
      }
    }
    ccf.from(0).create must be (None)
    ccf.from(1).create must be (Some(1))
  }
}

class CoTest extends FlatSpec with MustMatchers {
}