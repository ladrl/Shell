package me.home.tools.resource
import scala.collection.generic.Growable


// Index classes
trait Index[T] { // T is the base type for the index
  def value: T
}

object Index {
  def apply[T](t: T) = new Index[T] { val value = t }
}

trait Discrete[T] extends Index[T] {
  def contains(t: T): Boolean
}

trait Inductive[T] extends Index[T] {
  val value: T
  def next: Inductive[T]
}

trait Continuous[T] extends Index[T] {
}

// A dimension of elements of A
trait Dimension[+A] {
  def model: DimensionModel // Reference to the creating model
  type DimensionType = A    // Stored parameter for self types
}

// A dimension with some sort of index
trait Indexed[T, I[_] <: Index[_]] { self: Dimension[_] =>
  type IndexType = I[T]
}

// A readable dimension
trait Readable { self: Dimension[_] => }

// An iterable dimension (requires readable)
trait Iterable { self: Dimension[_] with Readable =>
  def iterator: Iterator[self.DimensionType]
}

// An indexable dimension (requires readable)
trait IndexReadable[T, I[_] <: Index[_]] extends Indexed[T, I] { self: Dimension[_] with Readable =>
  def getAt(i: I[T]): self.DimensionType
}

// A writable dimension
trait Writable { self: Dimension[_] => }

// A growable dimension which can be written elemnt by element (requires writable)
trait Appendable { self: Dimension[_] with Writable =>
  def growable: Growable[self.DimensionType]
}

// An indexable dimension which can be written with random access (requires writable)
trait IndexWritable[T, I[_] <: Index[_]] extends Indexed[T, I] { self: Dimension[_] with Writable =>
  def setAt(i: I[T], v: DimensionType): Unit
}
