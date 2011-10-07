package me.home.tools.afrp

trait SFops {
  def `return`[A](a: => A): S[A]
  def arr[A, B](f: A => B): SF[A, B]
  def arr[A, B, C](f: Tuple2[A, C] => Tuple2[B, C]): SF2[A, B, C]
  def first[A, B, C](sf: SF[A, B]): SF2[A, B, C]
  def >>>[A, B, C](sf1: SF[A, B], sf2: SF[B, C]): SF[A, C]
  def &&&[A, B, C](sf1: SF[A, B], sf2: SF[A, C]): SF[A, (B, C)]
  def loop[A, B, C](sf2: SF[(A, C), (B, C)], c_init: C): SF[A, B]
}

trait S[A] extends Function0[A]

object SF {
  def `return`[A](a: => A)(implicit ops: SFops): S[A] = ops.`return`(a)
  def arr[A, B](f: A => B)(implicit ops: SFops): SF[A, B] = ops.arr(f)
  def arr[A, B, C](f: Tuple2[A, C] => (B, C))(implicit ops: SFops): SF2[A, B, C] = ops.arr(f)
  def first[A, B, C](sf: SF[A, B])(implicit ops: SFops): SF2[A, B, C] = ops.first(sf)
}

trait SF[A, B] extends Function1[S[A], S[B]] {
  def >>>[C](sf: SF[B, C])(implicit ops: SFops): SF[A, C] = ops.>>>(this, sf)
  def &&&[C](sf: SF[A, C])(implicit ops: SFops): SF[A, (B, C)] = ops.&&&(this, sf)
}

trait SF2[A, B, C] extends SF[(A, C), (B, C)] {
  def loop(c_init: C)(implicit ops: SFops): SF[A, B] = ops.loop(this, c_init)
}