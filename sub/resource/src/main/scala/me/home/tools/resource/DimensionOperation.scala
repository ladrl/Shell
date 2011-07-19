package me.home.tools.resource

object DimensionOperation {
  def transformRead[A, B](
    a: Dimension[A] with Readable with Iterable,
    f: A => B): Dimension[B] with Readable with Iterable = {
    new Dimension[B] with Readable with Iterable {
      override val model = None
      def iterator = a.iterator map f
    }
  }

  def transformWrite[A, B](
    dim: Dimension[B] with Writable with Growable,
    f: A => B): Dimension[A] with Writable with Growable = {
   new Dimension[A] with Writable with Growable {
     override val model = None
     override val growable = new scala.collection.generic.Growable[A] {
       override def +=(a: A) = {
         dim.growable += f(a)
         this
       }
       override def clear = {
         dim.growable.clear
       }
     }
   }
  }
  
  def zip[A, B, C](
    dim1: Dimension[A] with Readable with Iterable,
    dim2: Dimension[B] with Readable with Iterable,
    f: (A, B) => C) = {
    new Dimension[C] with Iterable with Readable {
      override val model = None
      override def iterator = new Iterator[C] {
        val iter1 = dim1.iterator
        val iter2 = dim2.iterator
        override def next = f(iter1.next, iter2.next)
        override def hasNext = iter1.hasNext && iter2.hasNext
      }
    }
  }

  def zip[A, B, C](
    dim1: Dimension[A] with Writable with Growable,
    dim2: Dimension[B] with Writable with Growable,
    f: C => (A, B)) = {
    new Dimension[C] with Writable with Growable {
      override val model = None
      override val growable = new scala.collection.generic.Growable[C] {
        override def +=(c: C) = {
          val (a, b) = f(c)
          dim1.growable += a
          dim2.growable += b
          this
        }
        override def clear = {
          dim1.growable.clear
          dim2.growable.clear
        }
      }
    }
  }

  def zip[Ia, Ib, Ic, IIa[_] <: Index[_], IIb[_] <: Index[_], A, B, C](
    dim1: Dimension[A] with Readable with IndexReadable[Ia, IIa],
    dim2: Dimension[B] with Readable with IndexReadable[Ib, IIb],
    i: (Ic) => (IIa[Ia], IIb[Ib]), f: (A, B) => C) = {
    new Dimension[C] with Readable with IndexReadable[Ic, Index] {
      override val model = None
      override def getAt(ic: Index[Ic]) = {
        val (ia, ib) = i(ic.value)
        f(dim1.getAt(ia), dim2.getAt(ib))
      }
    }
  }
}