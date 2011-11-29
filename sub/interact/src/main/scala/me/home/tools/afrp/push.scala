package me.home.tools.afrp

trait EFops {
  def accept[A](f: (A) => Unit): E[A]
  def arr[A, B](f: (A) => B): EF[A, B]
  def arr[A, B](f: E[B] => E[A])(implicit mf: Manifest[B]): EF[A, B]
  def arr[A, B, C](f: (A, C) => (B, C)): EF2[A, B, C]
  def first[A, B, C](ef: EF[A, B]): EF[(A, C), (B, C)]
  def >>>[A, B, C](ef1: EF[A, B], ef2: EF[B, C]): EF[A, C]
  def &&&[A, B, C](ef1: EF[A, B], ef2: EF[A, C]): EF[A, (B, C)]
  def loop[A, B, C](ef: EF2[A, B, C], c_init: C): EFLoop[A, B, C]
}

trait E[-A] extends Function1[A, Unit]

object EF {
  def accept[A](f: (A) => Unit): E[A] = SimpleEF.accept(f)
  def arr[A, B](f: A => B): EF[A, B] = SimpleEF.arr(f)
  def arr[A, B](f: E[B] => E[A])(implicit mf: Manifest[B]): EF[A, B] = SimpleEF.arr(f)
  def arr[A, B, C](f: (A, C) => (B, C)): EF2[A, B, C] = SimpleEF.arr(f)
  def first[A, B, C](ef: EF[A, B]): EF[(A, C), (B, C)] = SimpleEF.first(ef)
}

trait EF[A, B] extends Function1[E[B], E[A]] {
  def >>>[C](ef: EF[B, C]): EF[A, C] = SimpleEF.>>>(this, ef)
  def &&&[C](ef: EF[A, C]): EF[A, (B, C)] = SimpleEF.&&&(this, ef)
}

trait EF2[A, B, C] extends EF[(A, C), (B, C)] {
  def loop(c_init: C): EFLoop[A, B, C] = SimpleEF.loop(this, c_init)
}

trait EFLoop[A, B, C] extends EF[A, B] {
  val state: S[C]
}