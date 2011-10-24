package me.home.tools.afrp

import akka.actor._

/*
 * General stuff:
 * 
 * It seems that the usage of actors of signal processing requires the whole chain 
 * to be an actor... 
 * 
 * Questions
 * 
 * How many actors are necesary to form a complete signal chain?
 * 3? Source -> Processor -> Sink
 * What are the types of the individual ports of the signal chain?
 * Source: S[A] / Processor: SF[A, B] / Sink: S[B]
 * --> Sink = Processor ( Source )
 *     Actor1 = Actor2 ( Actor3 )
 * How to best write down this type of chain?
 * 
 * How are the actors started/stopped?
 * 
 * 
 */


object AkkaEF {
  case class Event[A](val a: A)(implicit val mf: Manifest[A])
  
  def async[A, B](ef: EF[A, B])(implicit mfa: Manifest[A], mfb: Manifest[B]): EF[A, B] = new EF[A, B] {
    def apply(e: E[B]): E[A] = {
    	val ea:E[A] = ef(e)
    	val a = Actor.actorOf(new EventClient[A](ea)).start
    	new EventServer[A](a)
    }
  }
  
  class EventClient[A](val e: E[A])(implicit val mf: Manifest[A]) extends Actor {
    def receive = {
      case Event(a) => e(a.asInstanceOf[A]) // Execute the side effects of the event
    }
  }
  
  class EventServer[A](val actor: ActorRef)(implicit val mf: Manifest[A]) extends E[A] {
    def apply(a: A) = actor ! Event(a)
  }
} 
 
object AkkaSF {
  sealed trait SampleCommand
  case object SampleRequest extends SampleCommand
  case class SampleResponse[A](val sample: A) extends SampleCommand

  def async[A, B](sf: SF[A, B])(implicit mf: Manifest[B]): SF[A, B] = new SF[A, B] {
    def apply(s: S[A]): S[B] = {
      // construct an actor which executes the processing of s
      val a = Actor.actorOf(new SignalServer[B](sf(s))).start
      new SignalClient[B](a)
    }
  }
  
  class SignalServer[A](val s: S[A]) extends Actor {
    override def receive = {
      case SampleRequest => {
        val sample = s() // This sampling is the operation which can be time consuming
        self reply (SampleResponse(sample))
      }
    }
  }
  
  class SignalClient[A](val actor: ActorRef)(implicit val mf: Manifest[A]) extends S[A] {
    override def apply() = {
      val future = actor ? SampleRequest
      val Some(SampleResponse(sample)) = future.as[SampleResponse[A]]
      sample
    }
  }
}