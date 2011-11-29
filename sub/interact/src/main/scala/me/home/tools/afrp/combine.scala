package me.home.tools.afrp

import EF.accept
import SF.`return`

class SampleAndHold[A](implicit ef_ops: EFops, sf_ops: SFops) {
  private var sample: A = _
  val event = accept { sample = (_: A) }
  val signal = `return` { sample }
}


object Poll {
  def apply[A](e: E[A])(s: S[A])(implicit ef_ops: EFops, sf_ops: SFops) = 
    accept[Any] { _ => e(s()) }
  
  def stamped[A, B](e: E[(A, B)])(s: S[A])(implicit ef_ops: EFops, sf_ops: SFops) = 
    accept { (b:B) => e((s(), b)) }
}
