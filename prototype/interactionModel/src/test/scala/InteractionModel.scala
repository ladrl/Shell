package me.scalatoys.shell.prototype.interactionModel

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers

trait InteractionModel {
  
  type Actor = scala.actors.Actor
  
  trait Flow[F <: Point, T <: Point, Dir <: Direction] {
    type ValueType
    val from: F
    val to: T
  }

  object FromControl extends Direction {
    type Fun[T] = T => Unit
  }
  object ToControl extends Direction {
    type Fun[T] = () => T
  }
  
  trait Event[-T] {
    def fire(t: T): Unit
  }
  
  trait Signal[+T] {
    def sample: T
  }

  trait ControlToReaction[T, Dir <: Direction] extends Flow[PointOfControl[T], PointOfReaction[T], Dir] {
    type ValueType = T
    val from: PointOfControl[T]
    val to: PointOfReaction[T]
    val fun: Dir#Fun[T]
  }
  
  case class ControlToReactionEvent[T](val from: POC[T], val to: POR[T] ) extends ControlToReaction[T, FromControl.type] with Event[T] {
    override def fire(t: T): Unit = { to.fire(t) }
    val fun : (T) => Unit = { t:T => fire(t)  }
  }
  
  case class ControlToReactionSignal[T](val from: POC[T], val to: POR[T] ) extends ControlToReaction[T, ToControl.type] with Signal[T] {
    override def sample: T = error("aiea")
    val fun : () => T = sample _
  }

  trait Point

  trait PointOfControl[T] extends Actor with Point 
  
  type POC[T] = PointOfControl[T]

  trait PointOfReaction[-T] extends Point with Event[T]
  
  object NoReaction extends PointOfReaction[Any] {
    def fire(a: Any): Unit = ()
  }
  
  type POR[T] = PointOfReaction[T]

  trait Direction {
    type Fun[_]
  }
}

class InteractionModelTest extends FlatSpec with MustMatchers with InteractionModel {
  "A flow" must "connect two points" in {
    val poc = new PointOfControl[Int] {
      def act = {
      }
    }
    val por = new PointOfReaction[Int] {
      def fire(i: Int) = println(i)
    }
    val flow = ControlToReactionEvent[Int](poc, por)
    flow.from must be (poc)
    flow.to must be (por)
    
  }
}