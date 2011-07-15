package me.home.tools.resource

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers

class ListDimension[T](val l: List[T]) extends Dimension[T] with Readable with Iterable {
  override val model = None
  def iterator = l.toIterator
}

class DimensionTest extends FlatSpec with MustMatchers {
  def transform[A, B](a: Dimension[A] with Readable with Iterable, f: A => B): Dimension[B] with Readable with Iterable = {
    new Dimension[B] with Readable with Iterable {
      override val model = None
      def iterator = a.iterator map f
    }
  }

  def zip[A, B](dim1: Dimension[A] with Readable with Iterable, dim2: Dimension[B] with Readable with Iterable) = {
    new Dimension[(A, B)] with Iterable with Readable {
      override val model = None
      override def iterator = new Iterator[(A, B)] {
        val iter1 = dim1.iterator
        val iter2 = dim2.iterator
        override def next = (iter1.next, iter2.next)
        override def hasNext = iter1.hasNext && iter2.hasNext
      }
    }
  }

  "A readable, iterable dimension" must "behave like an iterable" in {
    val l = List[Int]()
    val dim = new ListDimension[Int](l)

    dim.iterator.toList must be(l)
  }

  it must "be chainable to a new dimension" in {
    val l = List(2, 3, 4, 5)

    val dim = new ListDimension[Int](l)

    val dim2 = transform(dim, (_: Int) * 10)
    dim2.iterator.toList must be(List(20, 30, 40, 50))
    val dim3 = transform(dim2, (_: Int) + "a")
    dim3.iterator.toList must be(List("20a", "30a", "40a", "50a"))
  }

  it must "be buildable from other dimensions" in {
    val l1 = List(1, 2, 3, 4, 5)
    val l2 = "abcde".toList
    val dim1 = new ListDimension(l1)
    val dim2 = new ListDimension(l2)

    val zipped = zip(dim1, dim2)

    zipped.iterator.toList must be(l1 zip l2)

    val combined = transform(zipped, { t: (Int, Char) => t._1.toString + " ==> " + t._2.toString })

    combined.iterator.toList must be((l1 zip l2) map { t: (Int, Char) => t._1.toString + " ==> " + t._2.toString })

    /*
	   * Why use my own zip/transform? 
	   * The reason is the metadata attached to the dimensions: model
	   * It contains information about the capabilities of the dimension and these must 
	   * be updated / combined
	   */
  }

  class MapDimension[K, T](val m: Map[K, T]) extends Dimension[T] with Readable with IndexReadable[K, Index] {
    override val model = None
    override def getAt(i: Index[K]) = {
      m(i.value)
    }
  }

  class Alphabet(val value: Char) extends Inductive[Char] {
    override def next = value match {
      case c if 'a' <= c && c <= 'z' => new Alphabet(c + 1 toChar)
      case _ => new Alphabet('a')
    }
  }

  "A readable, indexed dimension" must "behave like a Map" in {
    val m = Map(("abcdefg".toCharArray) zip (0 until 30): _*)
    val dim = new MapDimension[Char, Int](m)
    val i = new Alphabet('a')
    dim.getAt(i) must be(0)
    dim.getAt(i.next) must be(1)
  }

  it must "be buildable from other dimensions" in {
    val m1 = Map(("abcdefg" toCharArray) zip (0 until 30): _*)
    val m2 = Map(("abcdefg" toCharArray) zip ("iaeiae" :: "leile" :: "lleluolue" :: Nil): _*)
    val dim1 = new MapDimension[Char, Int](m1)
    val dim2 = new MapDimension[Char, String](m2)

    def zip[Ia, Ib, Ic, A, B, C](dim1: Dimension[A] with Readable with IndexReadable[Ia, Index], dim2: Dimension[B] with Readable with IndexReadable[Ib, Index], i: (Ic) => (Index[Ia], Index[Ib]), f: (A, B) => C) = {
      new Dimension[C] with Readable with IndexReadable[Ic, Index] {
        override val model = None
        override def getAt(ic: Index[Ic]) = {
          val (ia, ib) = i(ic.value)
          f(dim1.getAt(ia), dim2.getAt(ib))
        }
      }
    }

    val composite = zip(dim1, dim2, { i: Char => (new Alphabet(i), new Alphabet(i)) }, { (_: Int).toString + (_:String) })
    val i = new Alphabet('a')
    composite.getAt(i) must be (("0iaeiae"))
    composite.getAt(i.next) must be (("1leile"))
  }

  "A writable dimension" must "behave like an event sink" in {
    val l = scala.collection.mutable.ListBuffer[Int]()
    val dim = new Dimension[Int] with Writable with Growable {
      override val model = None
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

    dim.growable ++= List(0, 1, 2, 3)
    l must be(List(0, 1, 2, 3))
  }
}