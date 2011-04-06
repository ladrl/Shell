package me.scalatoys.shell

// Converter from DOM to scala.xml (anti-xml) and vice versa


package xml {
  import java.net.{
    URI
  }
  
  trait Node {
    def children: Seq[Node]
//    def attributes: Map[String, Node]
    def parentNode: Option[Node]
    
    def `type`: Short
    def name: String
    def localName: String
    def prefix: Option[String]
    def namespace: Option[URI]
  }
  
  package dom {
    import org.w3c.dom.{
      Node => W3CNode
    }
    
    class DOMNode(val peer: W3CNode) extends Node {
      override def children: Seq[Node] = {
        if(peer.hasChildNodes) {
          new IndexedSeq[DOMNode] {
            override def length: Int = peer.getChildNodes.getLength
            override def apply(idx: Int): DOMNode = new DOMNode(peer.getChildNodes.item(idx))
          }
        }
        else
          Nil
      }
//      def attributes: Map[String, DOMNode] = new Map[String, DOMNode] {
//        
//      }
      override def parentNode: Option[Node] = Option(peer.getParentNode).map { new DOMNode(_) }
      
      override def `type`: Short = peer.getNodeType      
      override def name: String = peer.getNodeName
      override def localName: String = peer.getLocalName
      override def prefix: Option[String] = Option(peer.getPrefix)
      override def namespace: Option[URI] = Option(peer.getNamespaceURI).map { new URI(_) }
    }
    
    
  }
}


/*
class Node(val peer: W3CNode) extends ScalaNode {
   def child: Seq[ScalaNode] = new Seq[ScalaNode] {
//     def apply(idx: Int): ScalaNode = peer.get
    
   }
}
*/
object DOM2Scala {
}