package me.scalatoys.shell.prototype.interactionModel


trait EventModel {
  trait Sink[-T, E[+_]] {
    def accept(e: E[T])
  }
  
  trait Source[-T, E[+_]] {
    def sink: Sink[T, E]
  }
  
  case class Event[+T](val content:T)
  
  trait EventSink[T] extends Sink[T, Event] {
    def f(t: T): Unit
    def accept(e: Event[T]): Unit = {
      val Event(content:T) = e
      f(content)
    }
  }
  
  trait EventSource[T] extends Source[T, Event]{
    def sink: EventSink[T]
  }
  
  trait EventTransformer[A, B] extends Sink[A, Event] with EventSource[B] {
    def process(a: A) : B
    def accept(e: Event[A]): Unit = {
      val Event(content) = e
      val result = process(content)
      sink.accept(Event(result))
    }
  }
}

trait SignalModel {
  trait Signal[+T] {
    def sample: T
  }
  
  trait Sink[+T, S[+_]] {
    def source: S[T]
  }
  
  trait SignalTransformer[+A, +B] extends Sink[A, Signal] with Signal[B] {
    override def source: Signal[A]
    def process(s: this.type): B
    override def sample: B = process(this)
  }
  
  class ConcreteSignal[+T](val sampleFunc: () => T) extends Signal[T] {
    override def sample = sampleFunc()
  }
  /* FIXME: C must allow map 
  trait SignalCollector[+A, C[+_] <: Seq[_]] extends Signal[C[A]] {
    override def source: C[Signal[A]]
    override def sample: C[A] = source.map { a: Signal[A] => a.sample }
  }*/
}