package me.home.tools.resource
import scala.collection.generic.Growable

/**
 *  A resource
 *  It owns a chunk of data and employs a set of dimensions providing projections on
 *  this data. These projections can be readable, writable or both.
 *  The data is not necessarily a real data block. It could be a (possibly cached) projection
 *  onto other resources, its base.
 */
trait Resource {
  import Resource._

  def read[A]: DataRead[A]
  def write[A]: DataWrite[A]
  /**
   *  Required operations:
   *  - Construct (from some sort of template some data block)
   *   * Requires access to the data block for the dimensions
   *  - Access dimensions
   *  - Provide info about dimensions (i.e. orthogonality)
   */
  def base: Iterable[(Resource, Dimension)] // This iterable will be empty when there is no parent
  def dimensions: Dimension
}

object Resource {
  def apply[A, D[-_]](template: ResourceTemplate[A, D]): Resource = {
    sys.error("nyi")
  }

  /**
   *  The data access trait must provide a way to access the internal
   *  data store of a resource
   */

  // Indexing is a problem - leave of the time being. Iterating first, then indexing
  trait DataRead[+A /*, I*/ ] {
    def iterable: Option[Iterable[A]]
    def iterator: Option[Iterator[A]]
    // def getter: Option[I => A]

    def reiterate[B](f: Iterable[A] => B) = iterable.map { f(_) }
    def iterate[B](f: Iterator[A] => B) = iterator.map { f(_) }
    // def index[B](f: (I => A) => B) = getter.map { f(_) }
  }
  trait DataWrite[-A /*, I */ ] {
    def builder: Option[Growable[A]]
    // def setter: Option[(I, A) => Unit]

    def build[B](f: (B, Growable[A]) => Unit) = (b: B) => builder.map { f(b, _) }
    // def set[B](f: (B, (I, A) => Unit) => Unit) = (b: B) => setter.foreach { f(b, _) } 
  }

  /**
   *  A dimension
   *  It is a view onto the data of a resource. As such it is bound to a resource.
   *  Basically it is a function from the internal data block to some more abstract
   *  representation.
   */
  class Dimension(val context: Resource)

  trait Readable[+A] extends Dimension {
  }
}

/**
 *  A template for a resource
 *  Describes a dimension. Since a dimension is essentially defined by its
 *  functions and the its context, the template defines the functions which
 *  are then bound to a resource as its context.
 *
 *  The individual functions:
 *  - matcher: A function which identifies a dimension in the resource data block. It returns true only if the data block represents a valid instance of this dimension
 *  - usage: A function which generates a bitmap of the data block. For each element the bitmap indicates if this element is involved in the dimension. It is used
 *           to judge if two dimensions are orthogonal or not. It can be seen as some sort of 'stencil' on the data. Access to data clipped by this function will be
 *           prohibited.
 *  - project: The actual function of the dimension, projecting the data block onto the more abstract dimension. It allows to read the resource as this dimension.
 *  - inverse: The inversion of the project function. It allows to write the resource as this dimension. Orthogonal dimensions get invalidated by this operation.
 *
 */
object DimensionTemplate {
  type Matcher[A] = Iterable[A] => Boolean
  type ImpactMap[I] = I => Boolean
  type Projection[-A, B, +D[_]] = Function1[A, D[B]]
  type ReverseProjection[+A, B, -D[_]] = Function1[D[B], A]
}

class DimensionTemplate[+A, -B, D[-_]](	
  matcher: Iterable[A] => Boolean,
  usage: Int => Boolean,
  project: Iterable[A] => D[B],
  inverse: (D[B], Growable[A]) => Unit)

/**
 *  A template for a resource
 *  Describes a resource. It is used to construct the described resource => it needs
 *  to contain _all_ info necessary for this construction.
 *  It must include:
 *  - The memory block to use (for concrete resources)
 */
abstract class ResourceTemplate[+A, D[-_]](val dimensions: List[DimensionTemplate[A, _, D]])

//class ConcreteResourceTemplate[+A, D[-_]] extends ResourceTemplate[A, D]
//class CachingResourceTemplate extends ResourceTemplate
//class VirtualResourceTemplate extends ResourceTemplate

