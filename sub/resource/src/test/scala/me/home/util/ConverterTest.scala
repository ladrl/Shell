package me.home.util
import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers
import me.hom.util.Converter._

class ConverterTest extends FlatSpec with MustMatchers {
  "toInteger" must "convert a 10-base int" in {
    toInteger("12345") must be (Some(12345))
  }
  it must "convert a 16-base int" in {
    toInteger("ff", 16) must be (Some(0xFF))
  }
  it must "convert a 8-base int" in {
    toInteger("1234567", 8) must be (Some(01234567))
  }
  it must "convert a 2-base int" in {
    toInteger("0111", 2) must be (Some(7))
  }
  it must "reject all digits exceeding the base" in {
    pending
    toInteger("0123", 2) must be (None)
    toInteger("01", 2) must be (Some(123))
  }
}