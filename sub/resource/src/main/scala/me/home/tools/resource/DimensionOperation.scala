package me.home.tools.resource
import scala.collection.generic.Growable

object DimensionOperation {
  def transformRead[A, B](
    a: Dimension[A] with Readable with Iterable,
    f: A => B): Dimension[B] with Readable with Iterable = {
    new Dimension[B] with Readable with Iterable {
      override val model = NoDimension
      def iterator = a.iterator map f
    }
  }

  def transformWrite[A, B](
    dim: Dimension[B] with Writable with Appendable,
    f: A => B): Dimension[A] with Writable with Appendable = {
   new Dimension[A] with Writable with Appendable {
     override val model = NoDimension
     override val growable = new Growable[A] {
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
      override val model = NoDimension
      override def iterator = new Iterator[C] {
        val iter1 = dim1.iterator
        val iter2 = dim2.iterator
        override def next = f(iter1.next, iter2.next)
        override def hasNext = iter1.hasNext && iter2.hasNext
      }
    }
  }

  def zip[A, B, C](
    dim1: Dimension[A] with Writable with Appendable,
    dim2: Dimension[B] with Writable with Appendable,
    f: C => (A, B)) = {
    new Dimension[C] with Writable with Appendable {
      override val model = NoDimension
      override val growable = new Growable[C] {
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
      dim1: Dimension[A] with Writable with IndexWritable[Ia, IIa],
      dim2: Dimension[B] with Writable with IndexWritable[Ib, IIb],
      i: Ic => (IIa[Ia], IIb[Ib]),
      f: C => (A, B)) = {
    new Dimension[C] with Writable with IndexWritable[Ic, Index] {
      override val model = NoDimension
      override def setAt(ic: Index[Ic], c: C) = {
        val (ia, ib) = i(ic.value)
        val (a, b) = f(c)
        dim1.setAt(ia, a)
        dim2.setAt(ib, b)
      }
    }
  }
  
  def zip[Ia, Ib, Ic, IIa[_] <: Index[_], IIb[_] <: Index[_], A, B, C](
    dim1: Dimension[A] with Readable with IndexReadable[Ia, IIa],
    dim2: Dimension[B] with Readable with IndexReadable[Ib, IIb],
    i: (Ic) => (IIa[Ia], IIb[Ib]), f: (A, B) => C) = {
    new Dimension[C] with Readable with IndexReadable[Ic, Index] {
      override val model = NoDimension
      override def getAt(ic: Index[Ic]) = {
        val (ia, ib) = i(ic.value)
        f(dim1.getAt(ia), dim2.getAt(ib))
      }
    }
  }
}