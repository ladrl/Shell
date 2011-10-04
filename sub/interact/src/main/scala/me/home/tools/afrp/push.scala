package me.home.tools.afrp

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
  def arr[A, B](f: A => B)(implicit ops: EFops): EF[A, B] = ops.arr(f)
  def arr[A, B, C](f: (A, C) => (B, C))(implicit ops: EFops): EF2[A, B, C] = ops.arr(f)
  def accept[A](f: (A) => Unit)(implicit ops: EFops): E[A] = ops.accept(f)
  def first[A, B, C](ef: EF[A, B])(implicit ops: EFops): EF[(A, C), (B, C)] = ops.first(ef)
}

trait EF[A, B] extends Function1[E[B], E[A]] {
  def >>>[C](ef: EF[B, C])(implicit ops: EFops): EF[A, C] = ops.>>>(this, ef)
  def &&&[C](ef: EF[A, C])(implicit ops: EFops): EF[A, (B, C)] = ops.&&&(this, ef)
}
trait EF2[A, B, C] extends EF[(A, C), (B, C)] {
  def loop(c_init: C)(implicit ops: EFops): EF[A, B] = ops.loop(this, c_init)
}

object SimpleEF extends EFops {
  def accept[A](f: (A) => Unit): E[A] =
    new E[A] {
      def apply(a: A): Unit = f(a)
    }
  def arr[A, B](f: (A) => B): EF[A, B] =
    new EF[A, B] {
      def apply(b: E[B]): E[A] = accept { a: A => b(f(a)) }
    }
  def arr[A, B, C](f: (A, C) => (B, C)): EF2[A, B, C] =
    new EF2[A, B, C] {
	  def apply(bc: E[(B, C)]): E[(A, C)] = accept { t: (A, C) => bc(f(t._1, t._2)) }
    }
  def first[A, B, C](ef: EF[A, B]): EF[(A, C), (B, C)] =
    new EF[(A, C), (B, C)] {
      def apply(bc: E[(B, C)]): E[(A, C)] = accept { t: (A, C) =>
        val ea: E[A] = ef(accept { b: B => bc { (b, t._2) } })
        ea(t._1)
      }
    }
  def >>>[A, B, C](ef1: EF[A, B], ef2: EF[B, C]): EF[A, C] = {
    new EF[A, C] {
      def apply(c: E[C]): E[A] = ef1(ef2(c))
    }
  }
  def &&&[A, B, C](ef1: EF[A, B], ef2: EF[A, C]): EF[A, (B, C)] = {
    new EF[A, (B, C)] {
      def apply(bc: E[(B, C)]) = accept { a: A =>
        ef1(accept { b: B =>
          ef2(accept { c: C =>
            bc((b, c))
          })(a)
        })(a)
      }
    }
  }
  def loop[A, B, C](ef: EF2[A, B, C], c_init: C): EF[A, B] = {
    new EF[A, B] {
      var c = c_init
      def apply(eb: E[B]): E[A] = accept { a: A =>
      	ef(accept { t: (B, C) => eb(t._1); c = t._2 })((a, c))
      }
    }
  }
}