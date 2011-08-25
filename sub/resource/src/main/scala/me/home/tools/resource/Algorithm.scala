package me.home.tools.resource

trait Rank
trait Complexity

trait Actor // --> connect to Co and Contra

trait CanCreate[A] {
  def create: A
}

trait CanCreateFrom[A, B] {
  def from(a: A) : CanCreate[B]
}

trait CanCreateDimensionFrom[D[_], A, B] {
  def from(a: D[A]): CanCreate[D[B]]
}

trait CanExtract[A] {
  def extract: A
}

trait CanExtractFrom[A, B] {
  def from(a: A): CanExtract[B]
}

trait CanExtractFromDimension[D[_], A, T] {
  def from(t: D[T]): CanExtract[D[A]]
}

object Co {
  /*
   * The Co object traits need to create new elements from 
   * existing ones, i.e. for comap
   */

  trait Indexable[-B] { self: Dimension[_] =>
    override type D[+_] = self.type with Indexable[B]
    def at(i: B): ELEM
  }
  
  trait Iterable { self: Dimension[_] =>
    override type D[+_] = self.type with Iterable
    def next: ELEM
  }

  trait Dimension[+A] { // A passive (called) collection with metadata
    type D[+_] <: Dimension[_]
    type ELEM = A
    def comap[A, C](f: A => C): D[C]
  }
  trait Algorithm[-A, +B] // A passive (called) function with metadata
}

object Contra {
  /*
   * The Contra object traits need to extract results form
   * existing ones, i.e. for contramap
   */

  trait Dimension[-A] { // An active (calling) collection with metadata
    type D[_] <: Dimension[_]
    //def contramap[A, C](f: C => A)(implicit ce: CanExtractFrom[D, A, C]): Dimension[C]
  }
  trait Algorithm[+A, -B] // An active (calling) function with metadata
}