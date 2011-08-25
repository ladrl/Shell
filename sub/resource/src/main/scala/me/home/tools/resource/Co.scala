package me.home.tools.resource

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