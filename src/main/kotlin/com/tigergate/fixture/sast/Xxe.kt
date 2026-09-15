package com.tigergate.fixture.sast

import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.SAXParserFactory
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamSource
import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.xml.sax.helpers.DefaultHandler

object Xxe {
    // CWE-611: parsers created with default settings — DTDs and external entities enabled.
    fun parseDom(xml: String): Document =
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(InputSource(StringReader(xml)))

    fun parseSax(xml: String) =
        SAXParserFactory.newInstance().newSAXParser().parse(InputSource(StringReader(xml)), DefaultHandler())

    fun parseStax(xml: String): XMLStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(StringReader(xml))

    fun transformer(xslt: String): Transformer = TransformerFactory.newInstance().newTransformer(StreamSource(StringReader(xslt)))
}
