package me.home.tools.afrps

trait SFops {
  def arr[A, B](f: A => B): SF[A, B]
  def arr[A, B, C](f: Tuple2[A, C] => Tuple2[B, C]): SF2[A, B, C]
  def first[A, B, C](sf: SF[A, B]): SF2[A, B, C]
  def `return`[A](a: => A): Function0[A]   // Is using Function0 as the signal type a wise idea?
  def >>>[A, B, C](sf1: SF[A, B], sf2: SF[B, C]): SF[A, C]
  def &&&[A, B, C](sf1: SF[A, B], sf2: SF[A, C]): SF[A, (B, C)]
  def loop[A, B, C](sf2: SF[(A, C), (B, C)], c_init: C): SF[A, B]
}

// Signal Transformation Functions - SF
object SF {
  def arr[A, B](f: A => B)(implicit op: SFops): SF[A, B] = op.arr(f)
  def arr[A, B, C](f: Tuple2[A, C] => (B, C))(implicit op: SFops): SF2[A, B, C] = op.arr(f)
  def first[A, B, C](sf: SF[A, B])(implicit op: SFops): SF2[A, B, C] = op.first(sf)
  def `return`[A](a: => A)(implicit op: SFops): Function0[A] = op.`return`(a)
}

trait SF[A, B] {
  def apply(s: Function0[A]): Function0[B] // Core business
  def >>>[C](sf: SF[B,  C])(implicit op: SFops): SF[A, C] = op.>>>(this, sf)
  def &&&[C](sf: SF[A, C])(implicit op: SFops): SF[A, (B, C)] = op.&&&(this, sf)
}

trait SF2[A, B, C] extends SF[(A, C), (B, C)] {
  def loop(c_init: C)(implicit op: SFops): SF[A, B] = op.loop(this, c_init)
}