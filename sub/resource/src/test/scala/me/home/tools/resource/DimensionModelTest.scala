package me.home.tools.resource

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers
import scalaz._
import Scalaz._
import org.junit.runner.RunWith
import org.scalatest.junit._

@RunWith(classOf[JUnitRunner])
class DimensionModelTest extends FlatSpec with MustMatchers {
  "A dimension model" must "enable the encoding of its rank" in {
    val dM = new DimensionModel with IsReadable with IsCreatableFrom[List[Int]] { self =>
      type DimensionType = Int
      val rank = Definite
      override def create(from: List[Int]): Validation[String, Dimension[Int] with Readable with Iterable] = {
        if (from.length >= 10)
          new RODim(from.iterator, self) success
        else
          failure("Need at least 10 elements")
      }
    }

    val noDim = dM.create(List(1, 2, 3))
    noDim must be('failure)

    val dim = dM.create(0 until 10 toList)
    dim must be('success)
    dim.map { _.iterator.toList } must be(Success(0 until 10 toList))
  }

  it must "enable the encoding of readablility / writablility" in {
    val readable = new DimensionModel with IsReadable { val rank = Acyclic }
    readable must be('readable)
    val writable = new DimensionModel with IsWritable { val rank = Acyclic }
    writable must be('writable)

    val read_writable = new DimensionModel with IsReadable with IsWritable { val rank = Acyclic }
    read_writable must be('readable)
    read_writable must be('writable)
  }
  
  it must "enable the encoding of indexablility / iterability" in {
    val indexable = new DimensionModel with IsIndexable { val rank = Deterministic }
    val iterable  = new DimensionModel with IsIterable { val rank = Acyclic }
    indexable must be ('indexable)
    iterable  must be ('iterable)
  }
}