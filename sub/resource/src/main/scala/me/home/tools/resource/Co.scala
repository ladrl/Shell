package me.home.tools.resource


// Holds the promise that it will create an A (somehow)
trait CanCreate[A] {
  def create: A
}

// Holds the promise that it will create an object which creates an M[B], from an A
// (The simple case of CanCreateFrom[A, B] can be reduced to Function1[A, B])
trait CanCreateFrom[M[_], A, B] {
  def from(a: A) : CanCreate[M[B]]
}

trait MetaData {
}

object Co {
  
  trait Rank { 
    def hasLast: Boolean
  }
  
  trait Infinite extends Rank {
    def hasLast = false
  }
  
  trait Finite extends Rank {
    def hasLast = true
  }
  
  trait Deterministic extends Finite {
  	def hasNext: Boolean
  }
  
  trait Determined extends Finite {
  	def isValid(offset: Int): Boolean
  }
  
  trait Defined extends Finite {
    def size: Int
  }
  
  
  object Indexable {
    trait Self { type λ[E, R <: Rank] = Dimension[E] with R }  
  }
  trait Indexable[-B] { self: Indexable.Self#λ[_,_] =>
    override type D[+_] = self.type with Indexable[B]
    def at(i: B): ELEM
  }
  
  // TODO: Use the same idea as the scala iterator? (=> an iterable can generate iterators for multi pass...)
  object Iterable {
    trait Self { type λ[E, R <: Defined] = Dimension[E] with R }
  }
  trait Iterable { self: Iterable.Self#λ[_, _] =>
    override type D[+_] = self.type with Iterable
    def iterator: Iterator[ELEM]
  }

  abstract class Dimension[+A] { // A co collection with metadata
    type D[+_] <: Dimension[_]
    type ELEM = A
  }
  
  // An algorithm takes dimension and creates a set of dimension from it
  // To process more than one dimension, one needs to create a zipped dimension
  // TODO: preserve the type of the dimension (it may add functionality)
  trait Algorithm[A, +B, AA, BB] extends Function1[(Dimension[A] with AA, MetaData), (Dimension[B] with BB, MetaData)] {
    def transformMeta(m: MetaData): MetaData
    def transform(a: Dimension[A] with AA): Dimension[B] with BB
    
    override def apply(a: (Dimension[A] with AA, MetaData)): (Dimension[B] with BB, MetaData) = (transform(a._1), transformMeta(a._2))
  }
}