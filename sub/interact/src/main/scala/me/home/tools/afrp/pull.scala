package me.home.tools.afrps

object ClassInstanceTraits {
  // Unary
  trait Arr {
    def arr[A, B](f: A => B): SF[A, B]
  }
  trait ArrTuple {
    def arr[A, B, C](f: Tuple2[A, C] => Tuple2[B, C]): SF2[A, B, C]
  }
  trait First {
    def first[A, B, C](sf: SF[A, B]): SF2[A, B, C]
  }
  trait Return {
    def `return`[A](a: => A): Function0[A] // Is using Function0 as the signal type a wise idea?
  }

  // Binary
  trait >>> {
    def >>>[A, B, C](sf1: SF[A, B], sf2: SF[B, C]): SF[A, C]
  }

  trait &&& {
    def &&&[A, B, C](sf1: SF[A, B], sf2: SF[A, C]): SF[A, (B, C)]
  }

  // Recursive
  trait loop {
    def loop[A, B, C](sf2: SF[(A, C), (B, C)], c_init: C): SF[A, B]
  }
}

// Signal Transformation Functions - SF
object SF {
  import ClassInstanceTraits._
  def arr[A, B](f: A => B)(implicit op: Arr): SF[A, B] = op.arr(f)
  def arr[A, B, C](f: Tuple2[A, C] => (B, C))(implicit op: ArrTuple): SF2[A, B, C] = op.arr(f)

  def first[A, B, C](sf: SF[A, B])(implicit op: First): SF2[A, B, C] = op.first(sf)

  def `return`[A](a: => A)(implicit op: Return): Function0[A] = op.`return`(a)
}

trait SF[A, B] { self =>
  def apply(s: Function0[A]): Function0[B] // Core business

  import ClassInstanceTraits._
  def >>>[C](sf: SF[B, C])(implicit op: >>>): SF[A, C] = op.>>>(this, sf)

  def &&&[C](sf: SF[A, C])(implicit op: &&&): SF[A, (B, C)] = op.&&&(this, sf)
}

trait SF2[A, B, C] extends SF[(A, C), (B, C)] { self =>
  import ClassInstanceTraits._
  def loop(c_init: C)(implicit op: loop): SF[A, B] = op.loop(this, c_init)
}

object SimpleSF {
  import ClassInstanceTraits._
  import SF._
  implicit val arrOp = new Arr {
    def arr[A, B](f: A => B) = new SF[A, B] {
      def apply(s: Function0[A]) = { () =>
        f(s())
      }
    }
  }
  implicit val arrTupleOp = new ArrTuple {
    def arr[A, B, C](f: Tuple2[A, C] => Tuple2[B, C]) =
      new SF2[A, B, C] {
        def apply(s: Function0[(A, C)]) = { () => f(s()) }
      }
  }
  implicit val firstOp = new First {
    def first[A, B, C](sf: SF[A, B]): SF2[A, B, C] =
      new SF2[A, B, C] {
        def apply(s: Function0[(A, C)]) = { () => val sample = s(); (sf(returnOp.`return`(sample._1))(), sample._2) }
        // Referencing inside of a single implementation - is this a good idea? Why should it be a problem?
      }
  }
  implicit val returnOp = new Return {
    def `return`[A](a: => A): Function0[A] = () => { a }
  }

  implicit val seqOp = new >>> {
    def >>>[A, B, C](sf1: SF[A, B], sf2: SF[B, C]): SF[A, C] =
      new SF[A, C] {
        override def apply(s: Function0[A]) = sf2(sf1(s))
      }
  }

  implicit val parOp = new &&& {
    def &&&[A, B, C](sf1: SF[A, B], sf2: SF[A, C]): SF[A, (B, C)] =
      new SF[A, (B, C)] {
        override def apply(s: Function0[A]) = { () =>
          val b: () => B = sf1(s)
          val c: () => C = sf2(s)
          (b(), c())
        }
      }
  }

  implicit val loopOp = new loop {
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
}
