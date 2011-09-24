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
  trait Algorithm1[A, +B, AA, BB] extends Function1[(Dimension[A] with AA, MetaData), (Dimension[B] with BB, MetaData)] {
    def transformMeta(m: MetaData): MetaData
    def transform(a: Dimension[A] with AA): Dimension[B] with BB
    
    type _A = Dimension[A] with AA
    type _B = Dimension[B] with BB
    type __A = (_A, MetaData)
    type __B = (_B, MetaData)
    
    override def apply(a: __A): __B = (transform(a._1), transformMeta(a._2))
  }
  
  trait Algorithm2[A, B, +C, AA, BB, CC] extends Function2[(Dimension[A] with AA, MetaData), (Dimension[B] with BB, MetaData), (Dimension[C] with CC, MetaData)] {
    def transformMeta(a: MetaData, b: MetaData): MetaData
    def transform(a: Dimension[A] with AA, b: Dimension[B] with BB): Dimension[C] with CC
    
    type _A = Dimension[A] with AA
    type _B = Dimension[B] with BB
    type _C = Dimension[C] with CC
    type __A = (_A, MetaData)
    type __B = (_B, MetaData)
    type __C = (_C, MetaData)
    
    override def apply(a: __A, b: __B): __C = (transform(a._1, b._1), transformMeta(a._2, b._2))
  }
}