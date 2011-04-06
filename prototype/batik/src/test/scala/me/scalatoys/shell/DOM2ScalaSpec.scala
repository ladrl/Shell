package me.scalatoys.shell


import org.scalatest._
import org.scalatest.matchers._

import xml._
import xml.dom._

class DOM2ScalaSpec extends FlatSpec with MustMatchers {
  import javax.xml.parsers.{
    DocumentBuilder,
    DocumentBuilderFactory
  }
  val builder = DocumentBuilderFactory.newInstance.newDocumentBuilder
  
  //****************************************************
  "A document node" must "be accessible by Node" in {
    import org.w3c.dom.{
      Node,
      Document
    }
    val doc: Document = builder.newDocument
    
    val sDoc = DOMNode(doc)
    sDoc.`type` must be (Node.DOCUMENT_NODE)
    sDoc.parentNode must be(None)
  }
  
  "An element node" must "be accessible by Node" in {
    import org.w3c.dom.Node
    val doc = builder.newDocument
    val node: Node = doc.createElement("test")
    
    val sNode = DOMNode(node)
    sNode.`type` must be (Node.ELEMENT_NODE)
    sNode.name must be("test")
    sNode.namespace must be (None)
    sNode.prefix must be (None)
  }
  
  "An element node with NS" must "be acessible by Node" in {
    import org.w3c.dom.Node
    val doc = builder.newDocument
    val node: Node = doc.createElementNS("namespace", "test")
    node.setPrefix("tst")
    val sNode = DOMNode(node)
    sNode.namespace must be(Some(new java.net.URI("namespace")))
    sNode.prefix must be(Some("tst"))
    sNode.parentNode must be(None)
  }
}