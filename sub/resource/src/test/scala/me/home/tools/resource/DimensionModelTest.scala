package me.home.tools.resource

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers
import scalaz._
import Scalaz._

class DimensionModelTest extends FlatSpec with MustMatchers {
	"A dimension model" must "enable the encoding of its rank" in {
	  val dM = new DimensionModel with IsReadable with IsCreatableFrom[List[Int]] { self =>
	    type DimensionType = Int
	    val rank = Definite
	    override def create(from: List[Int]): Validation[String, Dimension[Int]] = {
	      if(from.length >= 10) 
	        new Dimension[Int] with Readable with Iterable {
	    	  override val model = Some(self)
	    	  val iterator = from.toIterator
	      	} success
	      else
	        failure("Need at least 10 elements")
	    }
	  }
	  
	  dM.create(List(1,2,3)) must be ('failure)
	  val dim = dM.create(0 until 10 toList)
	  dim must be ('success)
	  
	}
	
	it must "enable the encoding of readablility / writablility" in {
	  pending
	}
}