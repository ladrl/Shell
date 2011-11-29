package me.home.shell.command

import org.scalatest.matchers.MustMatchers
import org.scalatest.FlatSpec
import me.home.tools.afrp._
import scala.collection.mutable.ListBuffer

class OutputTest extends FlatSpec with MustMatchers {
  import EF._
/*  "An output" must "respond to queries according to the response list" in {
    val console = new ListBuffer[String]()

    val queryResponses = (Some("yes") :: None :: Some("perhaps") :: Nil)

    val OutputAsString = EF.arr {
      val queryResponse = queryResponses.toIterator
      (_: Output) match {
        case Message(msg) => msg
        case Query(msg, e) => queryResponse.next.map { e(_) }; msg
      }
    }

    val e = OutputAsString(accept { s: String => console += s })

    val answers = ListBuffer[String]()
    val e_answer = accept { answers += (_: String) }
    val input = Query("say yes", e_answer) :: Query("dont respond", e_answer) :: Query("say perhaps", e_answer) :: Nil

    for (in <- input)
      e(in)

    console must be("say yes" :: "dont respond" :: "say perhaps" :: Nil)
    answers must be("yes" :: "perhaps" :: Nil)
  }
  */
  
  "An output processor" must "allow to answer the last query" in {
    val outputs = new ListBuffer[String]()
    val answers = new ListBuffer[String]()
    val (op, answer) = Tools.outputProcessor[String]
    
    def acceptWith(msg: String) = accept { answers += msg + (_:String) }
    val e = op(accept { outputs += (_:String)})
    
    import Tools._
    e(Query("say nothing", acceptWith("1:")))
    e(Query("say yes", acceptWith("2:")))
    answer("yes")
    e(Message("say no"))
    answer("no")
    
    answers must be ("2:yes" :: Nil)
  }
}