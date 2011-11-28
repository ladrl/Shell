package me.home.tools.afrp

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers
import scala.collection.generic.{
  Growable
}
import scala.collection.mutable.{
  ListBuffer
}

object Methods {
  import EF._
  val consume = accept { _:Any => }
  def asE[T](lb: Growable[T]): E[T] = accept { lb += _ }
  
  def store[T](t: T) = arr((newT: T, currentT: T) => (currentT, newT))(EF.ef).loop(t)
  def cycle[T](es: List[E[T]]) = {
    arr((i: T, l: List[E[T]]) => {
      l.take(1).map { _(i) }
      (i, l.tail :+ l.head)
    })(EF.ef).loop(es)
  }
 

  def switch[T](t: T) = arr((`new`: T, current: T) => (current, `new`)).loop(t)
  def event_switch[T](et: E[T]) = {
    val sw = switch(et)
    (sw, accept(sw.state()(_: T)))
  }
}

class Examples extends FlatSpec with MustMatchers {
  import Methods._
  import EF._

  "A looped event sink" must "allow to dispatch in round robin fashion" in {
    val noBuffers = 10
    val count = 100
    val vars: List[ListBuffer[Int]] = (0 until noBuffers) map { _ => ListBuffer[Int]() } toList
    val es = vars.map { lb => asE(lb) }

    val looped = cycle(es)

    val e = looped(accept { _: Int => })
    (0 until count) map { e(_) }

    for ((lb, i) <- vars.zipWithIndex)
      lb must be(i until count by noBuffers)
  }

  "A event switch" must "allow to activate an event" in {
    val Var = ListBuffer[Int]()
    val v = asE(Var)

    val (sw, e) = event_switch(v)
    val e_switch = sw(accept { e: E[Int] => })

    val es = (0 to 10) map { i: Int => j: Int => j + (i * 20) } map { arr(_)(EF.ef)(v) }

    for (current <- es) {
      e_switch(current)
      for (i <- 0 to 10)
        e(i)
    }
    Var must be(for (i <- 0 to 10; j <- 0 to 10) yield i * 20 + j)
  }
  
  "A signal switch" must "allow to activate a signal" in {
	  import SF._
	  val stores = (0 to 10).toList map { store(_) }
	  val signals = stores map { _.state }
	  val updates = stores map { _(consume) } // A list of events to update the signals
	  
	  (updates zipWithIndex) map { t => t._1(t._2) }
	  
	  def cycle[T](l: List[S[T]]) = arr((t: T, l: List[S[T]]) => { (l(0)(), l.tail :+ l.head) }).loop(l)
	  
	  val cycled:SF[Int, Int] = cycle(signals)
	  val const = SF.`return` { 0 }(SF.sf)
	  val s = cycled(const)
	  
	  (0 to 10) map { _ => s() } must be (0 to 10)
	  
	  (updates zip (0 to 10 map { _ * 5 }) ) map { t => t._1(t._2) } 
	  
	  (0 to 10) map { _ => s() } must be (0 to 50 by 5)
  }

  "Signal sources of sub types" must "allow sampling as super type" in {
    abstract class Super(val value: Int)
    case class Sub1() extends Super(1)
    case class Sub2() extends Super(2)
    
    val data:List[Super] = Sub1() :: Sub2() :: Nil
    val ss = data.map { d => SF.`return`{ d }(SF.sf) }
    val ss_iter = ss.toIterator
    val s:S[Super] = SF.`return`{ if(ss_iter.hasNext) ss_iter.next.apply() else sys.error("") }(SF.sf)
    
    
    s() must be (Sub1())
    s() must be (Sub2())
  }
  
  "An event sink for a super type" must "allow passing in all subtypes" in {
    abstract class Super(val value: Int)
    case class Sub1() extends Super(1)
    case class Sub2() extends Super(2)

    val Var = ListBuffer[Super]()
    val v = asE(Var)
    val e1: E[Sub1] = v
    val e2: E[Sub2] = v

    e1(Sub1())
    e2(Sub2())
    Var must be(Sub1() :: Sub2() :: Nil)
  }
}