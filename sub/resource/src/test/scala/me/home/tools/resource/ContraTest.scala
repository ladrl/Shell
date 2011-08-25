package me.home.tools.resource

import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers

class CanExtractTest extends FlatSpec with MustMatchers {
  "A CanExtract" must "act like a constant" in {
    val ce = new CanExtract[String] {
      override def extract = "test"
    }
    ce.extract must be ("test")
  }
  
  "A CanExtractFrom" must "act like a pattern matching" in {
    val cef = new CanExtractFrom[Option, Int, String] {
      def from(o: Option[Int]) = new CanExtract[String] {
        override def extract = o map { _.toString } getOrElse "No Int" 
      }
    }
    
    cef.from(None).extract must be ("No Int")
    cef.from(Some(1)).extract must be ("1")
  }
}

class ContraTest extends FlatSpec with MustMatchers {
}