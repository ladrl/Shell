package me.home.tools.resource


// Holds the promise that it will extract an object A from somewhere
trait CanExtract[A] {
  def extract: A
}

// Holds the promise that it will crate an object which extracts a B, from a M[A]
trait CanExtractFrom[M[_], A, B] {
  def from(a: M[A]): CanExtract[B]
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