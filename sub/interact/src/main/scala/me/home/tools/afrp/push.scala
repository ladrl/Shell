package me.home.tools.afrp

trait EFops {
  def accept[A](f: (A) => Unit): E[A]
  def arr[A, B](f: (A) => B): EF[A, B]
  def first[A, B, C](ef: EF[A, B]): EF[(A, C), (B, C)]
  def >>>[A, B, C](ef1: EF[A, B], ef2: EF[B, C]): EF[A, C]
}

trait E[A] extends Function1[A, Unit]

object EF {
  def arr[A, B](f: A => B)(implicit ops: EFops): EF[A, B] = ops.arr(f)
  def accept[A](f: (A) => Unit)(implicit ops: EFops): E[A] = ops.accept(f)
  def first[A, B, C](ef: EF[A, B])(implicit ops: EFops): EF[(A, C), (B, C)] = ops.first(ef)
}

trait EF[A, B] extends Function1[E[B], E[A]] {
  def >>>[C](ef: EF[B, C])(implicit ops: EFops): EF[A, C] = ops.>>>(this, ef)
}

object SimpleEF extends EFops {
  def accept[A](f: (A) => Unit): E[A] = {
    new E[A] {
      def apply(a: A): Unit = f(a)
    }
  }
  def arr[A, B](f: (A) => B): EF[A, B] = {
    new EF[A, B] {
      def apply(b: E[B]): E[A] = accept { a: A => b(f(a)) } 
    }
  }
  def first[A, B, C](ef: EF[A, B]): EF[(A, C), (B, C)] = {
     new EF[(A, C), (B, C)] {
      def apply(bc: E[(B, C)]): E[(A, C)] = accept { t: (A, C) =>
        val ea: E[A] = ef( accept { b: B => bc{ (b, t._2) } } )
        ea(t._1)
      }
    }
  }
  def >>>[A, B, C](ef1: EF[A, B], ef2: EF[B, C]): EF[A, C] = {
    new EF[A, C] {
      def apply(c: E[C]): E[A] = ef1(ef2(c))
    }
  }
}