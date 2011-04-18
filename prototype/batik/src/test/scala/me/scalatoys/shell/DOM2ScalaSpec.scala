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
  import java.net.URI
  val builder = DocumentBuilderFactory.newInstance.newDocumentBuilder
  
  //****************************************************
  val doc: Document = builder.newDocument
  val sDoc = DOMNode(doc)
  
  "A document node" must "be accessible by Node" in {
    sDoc.`type` must be (Node.DOCUMENT_NODE)
    sDoc.parentNode must be(None)
  }
  
  it must "produce elements and attributes" in {
    val elem = sDoc.createElement("name1")
    val attr = sDoc.createAttribute("name2")
    
    elem.namespace must be (None)
    elem.name must be ("name1")
    attr.namespace must be (None)
    attr.name must be ("name2")
  }
  it must "produce elements and attributes with namespaces" in {
    val ns = new URI("namespace")
    val elemNS = sDoc.createElement("name3", Some(ns))
    val attrNS = sDoc.createAttribute("name4", Some(ns))
    
    elemNS.prefix = Some("tst")
    elemNS.namespace must be (Some(ns))
    elemNS.localName must be ("name3")
    elemNS.name must be ("tst:name3")
    attrNS.prefix = Some("tst")
    attrNS.namespace must be (Some(ns))
    attrNS.localName must be ("name4")
    attrNS.name must be ("tst:name4")
  }
  
  "An element node" must "be accessible by Node" in {
    val node = doc.createElement("test")
    
    val sNode = DOMNode(node)
    sNode.`type` must be (Node.ELEMENT_NODE)
    sNode.name must be("test")
    sNode.namespace must be (None)
    sNode.prefix must be (None)
  }
  
  it must "be accessible as child when added to a document" in {
    val root = sDoc.createElement("root")
    val elements = (0 to 10).map{ i => sDoc.createElement("test" + i) }
    
    root.children = elements
    root.children must be (elements)
    
    root.children = elements.filter{ e => elements.indexOf(e) % 2 == 0 }
    root.children must be (elements.filter{ e => elements.indexOf(e) % 2 == 0 })
  }
  
  it must "allow access to its attributes" in {
    val root = sDoc.createElement("root")
    val attr = sDoc.createAttribute("test")
    val ns = new URI("namespace")
    val attrNS = sDoc.createAttribute("test", Some(ns))
    
    root.attributes = Map(("test", None) -> attr, ("test", Some(ns)) -> attrNS)
    root.attributes(("test"), None) must be (attr)
  }
  
  "An element node with NS" must "be acessible by Node" in {
    val node = doc.createElementNS("namespace", "test")
    val sNode = DOMNode(node)
    
    sNode.prefix = Some("tst")
    sNode.namespace must be(Some(new java.net.URI("namespace")))
    sNode.prefix must be(Some("tst"))
    sNode.parentNode must be(None)
  }
  
  "A text node" must "allow content" in {
    val node = sDoc.createText("InitialText")
    node.value must be (Some("InitialText"))
    node.value = "TestText"
    node.value must be (Some("TestText"))
    node.value = None
    node.value must be (None)
    val str: String = null
    node.value = str
    node.value must be (None)
  }
}