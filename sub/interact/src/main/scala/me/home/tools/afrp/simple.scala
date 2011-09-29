package me.home.tools.afrps

object SimpleSF extends SFops {
    def arr[A, B](f: A => B) = new SF[A, B] {
      def apply(s: Function0[A]) = { () =>
        f(s())
      }
    }
    def arr[A, B, C](f: Tuple2[A, C] => Tuple2[B, C]) =
      new SF2[A, B, C] {
        def apply(s: Function0[(A, C)]) = { () => f(s()) }
      }
    def first[A, B, C](sf: SF[A, B]): SF2[A, B, C] =
      new SF2[A, B, C] {
        def apply(s: Function0[(A, C)]) = { () => val sample = s(); (sf(`return`(sample._1))(), sample._2) }
        // Referencing inside of a single implementation - is this a good idea? Why should it be a problem?
      }
    def `return`[A](a: => A): Function0[A] = () => { a }
    def >>>[A, B, C](sf1: SF[A, B], sf2: SF[B, C]): SF[A, C] =
      new SF[A, C] {
        override def apply(s: Function0[A]) = sf2(sf1(s))
      }
    def &&&[A, B, C](sf1: SF[A, B], sf2: SF[A, C]): SF[A, (B, C)] =
      new SF[A, (B, C)] {
        override def apply(s: Function0[A]) = { () =>
          val b: () => B = sf1(s)
          val c: () => C = sf2(s)
          (b(), c())
        }
      }
    def loop[A, B, C](sf: SF[(A, C), (B, C)], c_init: C): SF[A, B] =
      new SF[A, B] {
        var c = c_init
        def apply(s: Function0[A]) = { () =>
          val res = sf { () => (s(), c) }()
          c = res._2
          res._1
        }
    }
}
