package me.home.tools.afrps

trait EFops {
  def accept[A](f: (A) => Unit): E[A]
  def arr[A, B](f: (A) => B): EF[A, B]
  def arr[A, B, C](f: (A, C) => (B, C)): EF2[A, B, C]
  def first[A, B, C](ef: EF[A, B]): EF[(A, C), (B, C)]
  def >>>[A, B, C](ef1: EF[A, B], ef2: EF[B, C]): EF[A, C]
  def &&&[A, B, C](ef1: EF[A, B], ef2: EF[A, C]): EF[A, (B, C)]
  def loop[A, B, C](ef: EF2[A, B, C], c_init: C): EF[A, B]
}

trait E[A] extends Function1[A, Unit]

object EF {
  def accept[A](f: (A) => Unit)(implicit ops: EFops): E[A] = ops.accept(f)
  def arr[A, B](f: A => B)(implicit ops: EFops): EF[A, B] = ops.arr(f)
  def arr[A, B, C](f: (A, C) => (B, C))(implicit ops: EFops): EF2[A, B, C] = ops.arr(f)
  def first[A, B, C](ef: EF[A, B])(implicit ops: EFops): EF[(A, C), (B, C)] = ops.first(ef)
}

trait EF[A, B] extends Function1[E[B], E[A]] {
  def >>>[C](ef: EF[B, C])(implicit ops: EFops): EF[A, C] = ops.>>>(this, ef)
  def &&&[C](ef: EF[A, C])(implicit ops: EFops): EF[A, (B, C)] = ops.&&&(this, ef)
}

trait EF2[A, B, C] extends EF[(A, C), (B, C)] {
  def loop(c_init: C)(implicit ops: EFops): EF[A, B] = ops.loop(this, c_init)
}