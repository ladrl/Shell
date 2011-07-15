package me.home.tools.resource

import scalaz.Validation

sealed trait Access
case object Read extends Access
case object Write extends Access

/**
  Rank
  Defines the rank of a dimension, determining the behavior of dimensions in relation to the _number_ of elements
  The rank can normally not be extended, the only exception would be the
  transition from Finite to Cyclic (algorithmically done by some kind of looping)
*/
trait Rank 

// An Infinite dimension will always be capable of producing a next value. It is by character only iterable
trait Infinite extends Rank

// A finite dimension will reach its last element eventually - however, no claims are made about when exactly. Finite 
// dimension can be both iterable and indexable
trait Finite extends Rank

// A deterministic finite dimension is able to exactly define when the last element is reached. This decision can only 
// be made each time an element is processed (thus, there is no 'length' field available)
// Examples of a deterministic finite dimension could be the XMPP protocol, where a message with a closing tag terminates
// the stream.
case object Deterministic extends Finite

// A nondeterministic finite dimension cannot tell the user that there are no more elements available, getting an element can 
// fail each time
case object Nondeterministic extends Finite

// A dimension which can tell its length
case object Definite extends Finite

// A dimension with no entry
case object NonExisting extends Finite

// An infinite dimension can be either cyclic (which can be created by looping any finite dimension) or acyclic (which must be 
// some kind of mathematical algorithm)
case object Cyclic extends Infinite
case object Acyclic extends Infinite


trait DimensionModel {
  type DimensionType
  val rank: Rank
  def isReadable = access.contains(Read)
  def isWritable = access.contains(Write)
  def access: List[Access] = Nil
  def isIterable: Boolean  = false
  def isIndexable: Boolean = false
}

trait IsCreatableFrom[R] { self: DimensionModel =>
  def create(from: R): Validation[String, Dimension[DimensionType]]
}

trait IsWritable extends DimensionModel { self: DimensionModel =>
  override def access:List[Access] = super.access :+ Write
}

trait IsReadable extends DimensionModel { self: DimensionModel =>
  override def access:List[Access] = super.access :+ Read
}

trait IsIterable { self: DimensionModel =>
  override val isIterable = true
}

trait IsIndexable { self: DimensionModel =>
  override val isIndexable = true
}

