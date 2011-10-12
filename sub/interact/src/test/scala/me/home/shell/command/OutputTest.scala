package me.home.shell.command

import org.scalatest.matchers.MustMatchers
import org.scalatest.FlatSpec
import me.home.tools.afrp._
import scala.collection.mutable.ListBuffer

class OutputTest extends FlatSpec with MustMatchers {
  implicit val efops = SimpleEF
  import EF._
  "An output" must "generate a list of strings" in {
    var console = new ListBuffer[String]()

    val OutputAsString = arr { o: Output =>
      o match {
        case Message(msg) => msg
        case Query(msg, _) => msg + "?"
      }
    }

    val e = OutputAsString(EF.accept { console += (_: String) })

    val input: List[Output] = Message("test") :: Message("test2") :: Query("test3", null) :: Nil
    for (in <- input)
      e(in)

    console must be("test" :: "test2" :: "test3?" :: Nil)
  }

  "An output with a loop" must "hold the queries" in {
    val console = new ListBuffer[String]()

    val queryResponses = (Some("yes") :: None :: Some("perhaps") :: Nil)
    
    val OutputAsString = EF.arr {
      val queryResponse = queryResponses.toIterator
      (_: Output) match {
        case Message(msg) => msg
        case Query(msg, e) => queryResponse.next.map { e(_) }; msg
      }
    }
    
    val e = OutputAsString(accept { s:String => console += s } )
    
    val answers = ListBuffer[String]()
    val e_answer = accept { answers += (_:String) }
    val input = Query("say yes", e_answer) :: Query("dont respond", e_answer) :: Query("say perhaps", e_answer) :: Nil
    
    for (in <- input)
      e(in)
      
    console must be ("say yes" :: "dont respond" :: "say perhaps" :: Nil )
    answers must be ("yes" :: "perhaps" :: Nil)
  }
}