package me.scalatoys.shell

// Converter from DOM to scala.xml (anti-xml) and vice versa


package xml {
  import java.net.{
    URI
  }
  
  trait Node {
    def children: Seq[Node]
    def children_=(update: Seq[Node]): Unit
    def attributes: Map[(String, Option[URI]), Node]
    def attributes_=(attrs: Map[(String, Option[URI]), Node]): Unit
    def attrsFromString(attrs: Map[String, String]): Unit = {
      owner.map { doc =>
        attributes = for((name, value) <- attrs) yield {
          (name, namespace) -> doc.createAttribute(name, namespace)
        }
      }
    }
    
    def parentNode: Option[Node]
    def owner: Option[Document]
    
    def `type`: Short
    def name: String
    def localName: String
    def prefix: Option[String]
    def prefix_=(newPrefix: Option[String]): Unit
    def namespace: Option[URI]
    def value: Option[String] = None
    def value_=(newValue: Option[String]): Unit = {}
    def value_=(newValue: String):Unit = this.value = Option(newValue)
    override def toString = ("Node %s (local: %s)"  format (name, localName)) + namespace.map { " in %s" format _ }.getOrElse("")
  }
  
  trait Document { self: Node =>
    def createElement(name: String, namespace: Option[URI] = None): Node
    def createAttribute(name: String, namespace: Option[URI] = None): Node
    def createText(value: String): Node
  }
  
  package dom {
    import org.w3c.{ dom => w3c }
    
    trait TextNode { self: DOMNode =>
      override def value: Option[String] = Option(peer.getNodeValue)
      override def value_=(newValue: Option[String]): Unit = peer.setNodeValue(newValue.getOrElse(null))
    }
    
    case class DOMElement(override val peer: w3c.Element)                             extends DOMNode(peer, w3c.Node.ELEMENT_NODE)
    case class DOMAttribute(override val peer: w3c.Attr)                              extends DOMNode(peer, w3c.Node.ATTRIBUTE_NODE) with TextNode
    case class DOMCDataSection(override val peer: w3c.CDATASection)                   extends DOMNode(peer, w3c.Node.CDATA_SECTION_NODE)
    case class DOMComment(override val peer: w3c.Comment)                             extends DOMNode(peer, w3c.Node.COMMENT_NODE) 
    case class DOMDocumentType(override val peer: w3c.DocumentType)                   extends DOMNode(peer, w3c.Node.DOCUMENT_TYPE_NODE)
    case class DOMEntity(override val peer: w3c.Entity)                               extends DOMNode(peer, w3c.Node.ENTITY_NODE)
    case class DOMEntityReference(override val peer: w3c.EntityReference)             extends DOMNode(peer, w3c.Node.ENTITY_REFERENCE_NODE)
    case class DOMNotation(override val peer: w3c.Notation)                           extends DOMNode(peer, w3c.Node.NOTATION_NODE)
    case class DOMProcessingInstruction(override val peer: w3c.ProcessingInstruction) extends DOMNode(peer, w3c.Node.PROCESSING_INSTRUCTION_NODE)
    case class DOMText(override val peer: w3c.Text)                                   extends DOMNode(peer, w3c.Node.TEXT_NODE) with TextNode
    case class DOMDocument(override val peer: w3c.Document)                           extends DOMNode(peer, w3c.Node.DOCUMENT_NODE) with Document {
      override def createAttribute(name: String, ns: Option[URI] = None): Node = DOMNode(ns match {
        case Some(ns) => peer.createAttributeNS(ns.toString, name)
        case None =>     peer.createAttribute(name)
      })
      override def createElement(name: String, ns: Option[URI] = None): Node = DOMNode(ns match {
        case Some(ns) => peer.createElementNS(ns.toString, name)
        case None =>     peer.createElement(name)
      })
      override def createText(value: String): Node = DOMNode(peer.createTextNode(value))
    }
    
    object DOMNode {
      def unapply(node: DOMNode) = Option(node.peer)
      def apply(peer: w3c.Document): DOMDocument = {
        (peer.getNodeType, peer) match {
          case (w3c.Node.DOCUMENT_NODE, peer: w3c.Document)                            => DOMDocument(peer)
        }
      }
      def apply(peer: w3c.Node): DOMNode = {
        (peer.getNodeType, peer) match {
          case (w3c.Node.ELEMENT_NODE, peer: w3c.Element)                              => DOMElement(peer)
          case (w3c.Node.ATTRIBUTE_NODE, peer: w3c.Attr)                               => DOMAttribute(peer)
          case (w3c.Node.CDATA_SECTION_NODE, peer: w3c.CDATASection)                   => DOMCDataSection(peer)
          case (w3c.Node.COMMENT_NODE, peer: w3c.Comment)                              => DOMComment(peer)
          case (w3c.Node.DOCUMENT_TYPE_NODE, peer: w3c.DocumentType)                   => DOMDocumentType(peer)
          case (w3c.Node.DOCUMENT_NODE, peer: w3c.Document)                            => DOMDocument(peer)
          case (w3c.Node.ENTITY_NODE, peer: w3c.Entity)                                => DOMEntity(peer)
          case (w3c.Node.ENTITY_REFERENCE_NODE, peer: w3c.EntityReference)             => DOMEntityReference(peer)
          case (w3c.Node.NOTATION_NODE, peer: w3c.Notation)                            => DOMNotation(peer)
          case (w3c.Node.PROCESSING_INSTRUCTION_NODE, peer: w3c.ProcessingInstruction) => DOMProcessingInstruction(peer)
          case (w3c.Node.TEXT_NODE, peer: w3c.Text)                                    => DOMText(peer)
        }
      }
    }
    
    abstract class DOMNode(val peer: w3c.Node, override val `type`: Short) extends Node {
      override def children: Seq[Node] = {
        if(peer.hasChildNodes)
          new IndexedSeq[DOMNode] {
            override def length: Int = peer.getChildNodes.getLength
            override def apply(idx: Int): DOMNode = DOMNode(peer.getChildNodes.item(idx))
          }
        else
          Nil
      }
      
      override def children_=(update: Seq[Node]): Unit = {
        val childs = children
        for(DOMNode(c) <- childs)
          peer.removeChild(c)
        for(DOMNode(u) <- update)
          peer.appendChild(u)
      }
      
      override def attributes: Map[(String, Option[URI]), Node] = {
        val attrMap = peer.getAttributes
        val list = for(i <- 0 until attrMap.getLength) yield {
          val node = attrMap.item(i)
          ((node.getNodeName, Option(node.getNamespaceURI).map{ new URI(_) }), DOMNode(node))
        }
        Map(list:_*)
      }
      override def attributes_=(attrs: Map[(String, Option[URI]), Node]): Unit = {
        val curAttrs = attributes
        val newAttrs = curAttrs ++ attrs
        val delAttrs = for((nameNs, node) <- curAttrs if(!newAttrs.contains(nameNs))) yield nameNs
        val peerAttrs = peer.getAttributes
        newAttrs.foreach { 
          case ((name, None), node) => {
            if(name != node.name) error("Parameter name doesn't match content of node: %s instead of %s" format (name, node.name))
            val DOMNode(n) = node
            peerAttrs.setNamedItem(n)
          }
          case ((name, Some(ns)), node) => {
            if(name != node.localName || Some(ns) != node.namespace) error("Parameters name and namespace don't match content of node: %s in %s expected instead %s in %s found" format (node.localName, node.namespace, name, ns))
            val DOMNode(n) = node
            peerAttrs.setNamedItemNS(n)
          }
        }
        delAttrs.foreach { 
          case (name, None) => peerAttrs.removeNamedItem(name)
          case (name, Some(ns)) => peerAttrs.removeNamedItemNS(ns.toString, name)
        }
      }
      
      override def parentNode: Option[Node] = Option(peer.getParentNode).map { DOMNode(_) }
      override def owner: Option[Document] = Option(peer.getOwnerDocument).map { DOMNode(_) }
      override def name: String = peer.getNodeName
      override def localName: String = peer.getLocalName
      override def prefix: Option[String] = Option(peer.getPrefix)
      override def prefix_=(prefix: Option[String]): Unit = peer.setPrefix(prefix.getOrElse(null))
      override def namespace: Option[URI] = Option(peer.getNamespaceURI).map { new URI(_) }
    }
  }
}