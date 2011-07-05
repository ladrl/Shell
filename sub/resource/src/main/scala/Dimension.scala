package me.home.tools.resource

trait Index[T] { // T is the base type for the index
  
}

trait Discrete[T] extends Index[T] {
  def contains(t: T): Boolean
}

trait Inductive[T] extends Index[T] {
  val value: T
  def nextValue(from: T): Inductive[T]
  def next: Inductive[T] = nextValue(value)
}

trait Continuous[T] extends Index[T] {
  
}

// A dimension of elements of A
trait Dimension[+A] {
  type DimensionType = A
}

trait Indexed[T] { self: Dimension[_] =>
  type I <: Index[T]
}


trait Readable { self: Dimension[_] => }

trait Iterable { self: Dimension[_] with Readable =>
  def iterator: Iterator[self.DimensionType]
}

trait IndexReadable[T] extends Indexed[T] { self: Dimension[_] with Readable =>
  def getAt(i: I): T
}


trait Writable { self: Dimension[_] => }

trait Growable { self: Dimension[_] with Writable =>
  def growable: scala.collection.generic.Growable[self.DimensionType]
}

trait IndexWritable[T] extends Indexed[T] { self: Dimension[_] with Writable =>
  def setAt(i: I, t: T): Unit
}
