package me.home.util

object Converter {
  import scala.annotation.tailrec
  def toInteger(str: String, base: Int = 10) = {
    def fromChar(c: Char) = {
      (c match {
        case c if '0' <= c && c <= '9' => Some(c.toInt - '0'.toInt)
        case c if 'a' <= c && c <= 'z' => Some(c.toInt - 'a'.toInt + 10)
        case c if 'A' <= c && c <= 'Z' => Some(c.toInt - 'A'.toInt + 10)
        case c => None
      }) filter { _ < base }
    }
    @tailrec
    def toInt(l: List[Char], value: Option[Int]): Option[Int] = l match {
      case x :: Nil => for (v <- value; i <- fromChar(x)) yield i + v * base
      case x :: xs => toInt(xs, for (v <- value; i <- fromChar(x)) yield v * base + i)
      case _ => None
    }
    toInt(str.toList, Some(0))
  }
}