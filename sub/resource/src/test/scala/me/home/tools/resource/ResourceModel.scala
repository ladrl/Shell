package me.home.tools.resource


import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers

class ResourceModelTest extends FlatSpec with MustMatchers {
  "A resource model" must "allow the definition of the access" in {
    val writable = new DimensionModel with IsWritable {
      val rank = Definite
    }
    writable must be ('writable)
    
    val readable = new DimensionModel with IsReadable {
      val rank = Definite
    }
    readable must be ('readable)
    
    val iterable = new DimensionModel with IsIterable {
      val rank = Definite
    }
    iterable must be ('iterable)
    
    val indexable = new DimensionModel with IsIndexable {
      val rank = Definite
    }
    indexable must be ('indexable)
  }
}