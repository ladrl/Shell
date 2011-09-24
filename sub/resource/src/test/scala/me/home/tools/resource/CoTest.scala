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

object NoMeta extends MetaData

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
  "A co algorithm1" must "be a Function2 taking a dimension and returning a more specific dimension" in {
    val algo = new Algorithm1[Int, Option[Int], Indexable[Int], Indexable[String]] {
      override def transform(a: _A): _B = {
        new Dimension[Option[Int]] with Indexable[String] {
          def at(s: String) = me.home.util.Converter.toInteger(s) map {
            a.at(_)
          }
        }
      }
      override def transformMeta(m: MetaData): MetaData = {
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
  
  import java.net.URL
  
  "A co algorithm2" must "be a Function3 taking two dimensions an returning a derived dimension" in {
    val algo = new Algorithm2[String, Int, URL, Indexable[Int], Iterable, Indexable[Int]] {
      override def transform(a: _A, b: _B): _C = new Dimension[URL] with Indexable[Int] {
        val bIter = b.iterator
        override def at(i: Int) = {
          new URL(a.at(i) + bIter.next.toString)
        }
      }
      override def transformMeta(a: MetaData, b: MetaData): MetaData = {
        NoMeta
      }
    }
    
    val dataBlock = 0 to 100 map { _ => (scala.math.random * 100).toInt }
    val urls = 0 to 100 map { _ => "http://google.com/search?q=" }
        
    val dim1 = new Dimension[String] with Indexable[Int] {
      override def at(i: Int) = urls(i)
    }
        
    val dim2 = new Dimension[Int] with Iterable {
      override def iterator = dataBlock.iterator
    }
    
    val result = algo((dim1, NoMeta), (dim2, NoMeta))._1
    
    0 to 100 map { i => result.at(i) } must be (0 to 100 map { i => new URL("http://google.com/search?q=%d" format dataBlock(i)) })
  }
}

class CoTest extends FlatSpec with MustMatchers {
}