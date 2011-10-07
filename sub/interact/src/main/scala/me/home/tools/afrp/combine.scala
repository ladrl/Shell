package me.home.tools.afrp

import EF._
import SF._
class SampleAndHold[A](implicit ef_ops: EFops, sf_ops: SFops) {
  private var sample: A = _
  val event = accept { sample = (_: A) }(ef_ops)
  val signal = `return` { sample }(sf_ops)
}

// TODO: Would it be wise to use not inheritance but delegation?
class Poll[A](val e: E[A], s: S[A])(implicit ef_ops: EFops, sf_ops: SFops) extends E[Any] {
  def apply(a: Any) = { // ignore a, event is only a trigger
    e(s())
  }
}

class PollStamped[A, B](val e: E[(A, B)], s: S[A])(implicit ef_ops: EFops, sf_ops: SFops) extends E[B] {
  def apply(b: B) = {
    // TODO: Could it be a good idea to use a to construct the value of the event, 
    //       stamping the sample
    e((s(), b))
  }
}