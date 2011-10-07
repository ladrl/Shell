package me.home.tools.afrp

import org.scalatest.FreeSpec
import org.scalatest.matchers.MustMatchers

class CombineTest extends FreeSpec with MustMatchers {
  implicit val sf_ops = SimpleSF
  implicit val ef_ops = SimpleEF

  "A sample and hold object" - {
    val sah = new SampleAndHold[String]
    "must update it's value to the value of an event" - {
      sah.event("test")
      sah.signal() must be("test")
    }
    "must be a normal event" - {
      val ef = EF.arr { (_: Int).toString }
      val e = ef(sah.event)
      e(1000)
      sah.signal() must be("1000")
    }
    "must be a normal signal" - {
      val sf = SF.arr { (s: String) => "'%s'" format s }
      val s = sf(sah.signal)
      s() must be("'1000'")
    }
  }

  "A Poll" - {
    var a = 0
    val trigger_e = EF.accept { a = (_: Int) }
    var b = 0
    val signal = SF.`return` { b }
    "must sample a signal upon an event and send out an update event" - {
      val p = new Poll(trigger_e, signal)
      
      a must be (0)
      b = 1101
      p.event(())
      a must be (1101)
    }
  }
  
  "A stamped Poll" - {
    var a = (0, "")
    val trigger_e = EF.accept { t: ((Int, String)) => a = t }
    var b = 0
    val signal = SF.`return` { b }
    "must sample a signal and stamp it with the value of the trigger event" - {
      val p = new PollStamped(trigger_e, signal)
      
      a must be ((0, ""))
      b = 1234
      p.event("stamp")
      a must be ((1234, "stamp"))
      b = 4321
      p.event("stamp again")
      a must be ((4321, "stamp again"))
    }
  }
}