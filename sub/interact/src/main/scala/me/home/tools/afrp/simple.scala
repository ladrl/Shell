package me.home.tools.afrp

object SimpleSF extends SFops {
  def `return`[A](a: => A): S[A] = new S[A] {
    def apply() = a
  }
  def arr[A, B](f: A => B) = new SF[A, B] {
    def apply(s: S[A]) = `return`(f(s()))
  }
  def arr[A, B, C](f: Tuple2[A, C] => Tuple2[B, C]) =
    new SF2[A, B, C] {
      def apply(s: S[(A, C)]) = `return`(f(s()))
    }
  def first[A, B, C](sf: SF[A, B]): SF2[A, B, C] =
    new SF2[A, B, C] {
      def apply(s: S[(A, C)]) = `return` { val sample = s(); (sf(`return`(sample._1))(), sample._2) }
    }

  def >>>[A, B, C](sf1: SF[A, B], sf2: SF[B, C]): SF[A, C] =
    new SF[A, C] {
      def apply(s: S[A]) = sf2(sf1(s))
    }
  def &&&[A, B, C](sf1: SF[A, B], sf2: SF[A, C]): SF[A, (B, C)] =
    new SF[A, (B, C)] {
      def apply(s: S[A]) = `return`((sf1(s)(), sf2(s)()))
    }
  def loop[A, B, C](sf: SF[(A, C), (B, C)], c_init: C): SFLoop[A, B, C] =
    new SFLoop[A, B, C] {
      var c = c_init
      val state = `return`(c)
      def apply(s: S[A]) = `return` {
        val res = sf(`return`((s(), c)))()
        c = res._2
        res._1
      }
    }
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
  def arr[A, B](f: E[B] => E[A])(implicit mf: Manifest[B]) =
    new EF[A, B] {
      def apply(eb: E[B]) = f(eb)
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
  def loop[A, B, C](ef: EF2[A, B, C], c_init: C): EFLoop[A, B, C] = {
    new EFLoop[A, B, C] {
      var c = c_init
      val state = SF.`return`(c)(SimpleSF)
      def apply(eb: E[B]): E[A] = accept { a: A =>
        ef(accept { t: (B, C) => eb(t._1); c = t._2 })((a, c))
      }
    }
  }
}
