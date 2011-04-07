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
  import org.w3c.dom.{
      Node,
      Document
  }
  val builder = DocumentBuilderFactory.newInstance.newDocumentBuilder
  
  //****************************************************
  "A document node" must "be accessible by Node" in {
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
  
  it must "be accessible as child when added to a document" in {
    val doc = builder.newDocument
    val sDoc:DOMDocument = DOMNode(doc)
    val root = sDoc.createElement("root")
    val elements = (0 to 10).map{ i => sDoc.createElement("test" + i) }
    root.children = elements
    root.children must be (elements)
    
    root.children = elements.filter{ e => elements.indexOf(e) % 2 == 0 }
    root.children must be (elements.filter{ e => elements.indexOf(e) % 2 == 0 })
  }
  
  "An element node with NS" must "be acessible by Node" in {
    import org.w3c.dom.Node
    val doc = builder.newDocument
    val node: Node = doc.createElementNS("namespace", "test")
    val sNode = DOMNode(node)
    sNode.prefix = Some("tst")
    sNode.namespace must be(Some(new java.net.URI("namespace")))
    sNode.prefix must be(Some("tst"))
    sNode.parentNode must be(None)
  }
}