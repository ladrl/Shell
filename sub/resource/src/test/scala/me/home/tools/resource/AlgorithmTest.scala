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
  "A class with trait CanCreateFrom" must "allow the creation of elements from others" in {
    class TestCreator[I <: In] extends CanCreateFrom[Option[I], Option[Out]] {
      override def from(i: Option[I]) = new CanCreate[Option[Out]] {
        override def create = i.map { Out(_) }
      }
    }

    val creator = new TestCreator[In]
    creator.from(Some(In2())).create must be(Some(Out(In2())))
    creator.from(Some(In1())).create must be(Some(Out(In1())))
  }
}

class CanExtractTest extends FlatSpec with MustMatchers {
  "A class with trait CanExtractFrom" must "allow the extraction of elements from others" in {
    class TestExtractor[I >: In] extends CanExtractFrom[Option[I], Option[Out]] {
      override def from(o: Option[Out]) = new CanExtract[Option[I]] {
        def extract = o.map { case Out(i) => i }
      }
    }

    val extractor = new TestExtractor[In]

    extractor.from(Some(Out(In2()))).extract must be(In2())
    extractor.from(Some(Out(In1()))).extract must be(In1())

  }
}

class DimensionTest extends FlatSpec with MustMatchers {
  "A dimension" must "give access to its metadata" in {
    pending
  }

  it must "give access to its contents" in {
    pending
  }
}

class CoDimensionTest extends FlatSpec with MustMatchers {
  
//  type A[_] = { type λ[_] = CoDimension[_] with Co.Iterable }
//  implicit val ccf = new CanCreateFrom[A[Int]#λ, Int, String] {
//    def from(i: A[Int]) = new CanCreate[String] {
//      def create = i.toString
//    }
//  }
//
  /*
  class CoDimension[+A](val coll: Iterator[A]) extends Co.Dimension[A] with Co.Iterable {
    override def comap[C](f: A => C) = {
      new CoDimension(coll map f)
    }
    override def next = {
      coll.next
    }
  }*/

//  "A co dimension" must "allow comapping to a new dimension while preserving its type" in {
//    val l = 0 until 100 toList
//    val dim1 = new CoDimension[Int](l.toIterator) with Co.Iterable
//    val dim2 = dim1 comap { (_: Int).toString }
//    //dim2.next must be ("0")
//    //dim2.next must be ("1")
//  }
}

class AlgorithmTest extends FlatSpec with MustMatchers {
  "An algorithm" must "give access to its metadata" in {
    pending
  }
  it must "connect a dimension to another" in {
    pending
  }

}