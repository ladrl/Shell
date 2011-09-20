package me.home.tools.resource

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers

trait Base

case class Sub() extends Base

trait In

case class In1() extends In
case class In2() extends In

case class Out(val in: In)

import Co._

class CanCreateTest extends FlatSpec with MustMatchers {
  "A CanCreate" must "act like a constructor with no parameters" in {
    val cc = new CanCreate[Base] {
      def create = Sub()
    }
    cc.create must be(Sub())
  }

  "A CanCreateFrom" must "act like a container constructor" in {
    val ccf = new CanCreateFrom[Option, Int, Int] {
      def from(i: Int) = new CanCreate[Option[Int]] {
        def create = if (i > 0) Some(i) else None
      }
    }
    ccf.from(0).create must be(None)
    ccf.from(1).create must be(Some(1))
  }
}

class CoDimensionTest extends FlatSpec with MustMatchers {
  class IndexableDimension(val f: Int => Int, s: => Int) extends Dimension[Int] with Defined with Indexable[Int] {
    override def at(i: Int) = f(i)
    override def size = s
  }
  "An indexable dimesion" must "be built from a data block" in {
    val dataBlock = 0 to 1000 map { _ => (scala.math.random * 100).toInt }

    val dim = new IndexableDimension(dataBlock(_), dataBlock.size)
    
    0 to 1000 map { dim.at(_) } must be (dataBlock)
  }
  
  it must "be finite" in {
    val dataBlock = 0 until 100 map { _ => (scala.math.random * 100).toInt }
    
    val dim = new IndexableDimension(dataBlock(_), dataBlock.size)
    
    dim.size must be (100)
  }
  
  "An iterable dimension" must "be built from a data block" in {
    val dataBlock = 0 to 1000 map { _ => (scala.math.random * 100).toInt }
    
    val dim = new Dimension[Int] with Iterable {
      override def iterator = dataBlock.iterator
    }
    
    val i = dim.iterator
    0 to 1000 map { _ => i.next } must be (dataBlock)
  }
}

class CoAlgorithmTest extends FlatSpec with MustMatchers {
  "A co algorithm" must "be a Function2 taking a dimension and returning a more specific dimension" in {
    val algo = new Algorithm[Int, Option[Int], Indexable[Int], Indexable[String]] {
      override def transform(a: Dimension[Int] with Indexable[Int]): Dimension[Option[Int]] with Indexable[String] = {
        new Dimension[Option[Int]] with Indexable[String] {
          def at(s: String) = me.home.util.Converter.toInteger(s) map {
            a.at(_)
          }
        }
      }
      override def transformMeta(m: MetaData): MetaData = {
        //sys.error("not yet implemented")
        m
      }
    }
    
    val dataBlock = 0 to 1000 map { _ => (scala.math.random * 100).toInt }

    val dim = new Dimension[Int] with Indexable[Int] {
      override def at(i: Int) = dataBlock(i)
    }
    
    val result = algo((dim, new MetaData {} ))._1
    
    0 to 1000 map { _.toString } map { result.at(_) } must be (dataBlock map { Some(_) })
    result.at("test") must be (None)
  }
}

class CoTest extends FlatSpec with MustMatchers {
}