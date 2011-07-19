package me.home.tools.resource

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers

class RODim[T](i: => Iterator[T]) extends Dimension[T] with Readable with Iterable {
  override val model = None
  def iterator = i
}

class WODim[T](g: => scala.collection.generic.Growable[T]) extends Dimension[T] with Writable with Growable {
  override val model = None
  def growable = g
}

class DimensionTest extends FlatSpec with MustMatchers {

  import DimensionOperation._

  "A readable, iterable dimension" must "behave like an iterable" in {
    val l = List[Int]()
    val dim = new RODim[Int](l.toIterator)

    dim.iterator.toList must be(l)
  }

  it must "be chainable to a new dimension" in {
    val l = List(2, 3, 4, 5)

    val dim = new RODim[Int](l.toIterator)

    val dim2 = transformRead(dim, (_: Int) * 10)
    dim2.iterator.toList must be(List(20, 30, 40, 50))
    val dim3 = transformRead(dim2, (_: Int) + "a")
    dim3.iterator.toList must be(List("20a", "30a", "40a", "50a"))
  }

  it must "be buildable from other dimensions" in {
    val l1 = List(1, 2, 3, 4, 5)
    val l2 = "abcde".toList
    val dim1 = new RODim(l1.toIterator)
    val dim2 = new RODim(l2.toIterator)

    val zipped = zip(dim1, dim2, ((_: Int), (_: Char)))

    zipped.iterator.toList must be(l1 zip l2)

    val combined = transformRead(zipped, { t: (Int, Char) => t._1.toString + " ==> " + t._2.toString })

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

    val composite = zip(dim1, dim2, { i: Char => (new Alphabet(i), new Alphabet(i)) }, { (_: Int).toString + (_: String) })
    val i = new Alphabet('a')
    composite.getAt(i) must be(("0iaeiae"))
    composite.getAt(i.next) must be(("1leile"))
  }

  "A writable, buildable dimension" must "behave like an event sink" in {
    val l = scala.collection.mutable.ListBuffer[Int]()
    val dim = new WODim[Int](l)
    dim.growable ++= List(0, 1, 2, 3)
    l must be(List(0, 1, 2, 3))
  }

  it must "be able to fork into multiple dimensions" in {
    import scala.collection.mutable.{
      ListBuffer
    }
    val l1 = ListBuffer[String]()
    val l2 = ListBuffer[Option[Int]]()

    val dim1 = new WODim(l1)
    val dim2 = new WODim(l2)

    val f: (String) => (String, Option[Int]) =
      s => {
        val a = s.takeWhile(_ != ':')
        val b = s.dropWhile(_ != ':').drop(1)
        (a, Converter.toInteger(b))
      }
    val dim = zip(dim1, dim2, f)

    dim.growable ++= List("test:2", "noInt", ":12345")
    l1.toList must be(List("test", "noInt", ""))
    l2.toList must be(List(Some(2), None, Some(12345)))
  }
}