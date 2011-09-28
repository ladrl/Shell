package me.home.tools.afrps

trait SFTupleOps[A, B] {
  def first[C](sf: SF[A, C]): SF[(A, C), (B, C)]
  def loop[C](sf: SF[(A, B), (C, B)]): SF[A, C]
}

object SF {
  def arr[A, B](f: A => B): SF[A, B] =
    new SF[A, B] {
    def apply(s: Function0[A]) = { () =>
      f(s())
    }
  }
  
  def `return`[A](a: => A) = () => { a }
}

trait SF[A, B] { self =>
  def apply(s: Function0[A]): Function0[B] // Core business

  def >>>[C](sf: SF[B, C]): SF[A, C] =
    new SF[A, C] {
      override def apply(s: Function0[A]) = sf(self.apply(s))
    }
  def &&&[C](sf: SF[A, C]): SF[A, (B, C)] = new SF[A, (B, C)] {
    override def apply(s: Function0[A]) = { () =>
      val b: () => B = self.apply(s)
      val c: () => C = sf(s)
      (b(), c())
    }
  }
}

/*
trait SF2[A, B, C] extends SF[(A, C), (B, C)] {
  def first(sf: SF[A, B])
  def loop(implicit ops: SFBinaryOps[A, C]): SF[A, B] = ops.loop(this)
}
*/

case class Constant[A](val c: A) extends SF[Nothing, A] {
  override def apply(s: Function0[Nothing]) = { () => c }
}