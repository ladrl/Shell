package me.home.tools.resource


import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers

class DimensionTest extends FlatSpec with MustMatchers {
  "A readable dimension" must "behave like an iterable" in {
    val l = List[Int]()
    val dim = new Dimension[Int] with Readable with Iterable {
      val iterator = l.toIterator
    }
    
    dim.iterator.toList must be (l)
  }
  
  it must "behave like a list" in {
    val l = List(2,3,4,56,6)
    
    class TwoStep(val value: Int) extends Inductive[Int] {
      override def nextValue(i: Int) = new TwoStep(i + 2)
    }
    
    val dim = new Dimension[Int] with Readable with IndexReadable[Int] {
      type I = Inductive[Int]
      override def getAt(i: Inductive[Int]) = {
        if(l.size >= i.value / 2) l(i.value / 2) else error("Index %s exceeds size" format i.value)
      }
    }
    
    val i:Inductive[Int] = new TwoStep(0)
    dim.getAt(i) must be (2)
    dim.getAt(i.next) must be (3)
    dim.getAt(i.next.next) must be (4)
  }
  
  "A writable dimension" must "behave like an event sink" in {
    val l = scala.collection.mutable.ListBuffer[Int]()
    val dim = new Dimension[Int] with Writable with Growable {
      val growable = new scala.collection.generic.Growable[Int] {
        override def +=(i: Int) = {
          l += i
          this
        }
        override def clear = {
          l.clear
        }
      }
    }
    
    dim.growable ++= List(0,1,2,3)
    l must be (List(0, 1, 2, 3))
  }
}