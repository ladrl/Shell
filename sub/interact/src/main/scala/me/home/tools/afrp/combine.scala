package me.home.tools.afrp

import EF.accept
import SF.`return`

class SampleAndHold[A](implicit ef_ops: EFops, sf_ops: SFops) {
  private var sample: A = _
  val event = accept { sample = (_: A) }(ef_ops)
  val signal = `return` { sample }(sf_ops)
}

class Poll[A](val e: E[A], s: S[A])(implicit ef_ops: EFops, sf_ops: SFops) {
  val event = accept[Any] { _ => e(s()) }
}

class PollStamped[A, B](val e: E[(A, B)], s: S[A])(implicit ef_ops: EFops, sf_ops: SFops) {
  val event = accept { (b:B) => e((s(), b)) }
}