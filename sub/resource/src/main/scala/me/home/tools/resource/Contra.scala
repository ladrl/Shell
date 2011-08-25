package me.home.tools.resource



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