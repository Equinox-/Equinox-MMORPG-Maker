package com.pi.common.database.io.xml;

import java.io.File;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class XMLReader {
    public static SAXParserFactory factory = SAXParserFactory.newInstance();
    public static SAXParser saxParser;

    public static void main(String[] args) {
	XMLElement[] elements = readEntries(new File(
		"/home/westin/Desktop/metadata.opf"), "package");
	for (XMLElement e : elements)
	    System.out.println(e);
    }

    public static XMLElement[] readEntries(File file, String eleName) {
	try {
	    if (saxParser == null)
		saxParser = factory.newSAXParser();
	    ElementTree handler = new ElementTree(eleName);
	    saxParser.parse(file, handler);
	    return handler.results.toArray(new XMLElement[handler.results
		    .size()]);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static class XMLElement {
	public String content = "";
	public String address = "";
	public Attributes attribs;

	@Override
	public String toString() {
	    String res = "XMLElement(Address: " + address;
	    if (attribs != null) {
		res += "  Attribs(";
		for (int i = 0; i < attribs.getLength(); i++) {
		    res += (i > 0 ? " " : "") + attribs.getQName(i)
			    + (attribs.getType(i).equals("CDATA") ? "=" : ": ")
			    + attribs.getValue(i);
		}
		res += ")";
	    }
	    if (content.length() > 0) {
		res += "  Content: " + content;
	    }
	    res += ")";
	    return res;
	}
    }

    private static class ElementTree extends DefaultHandler {
	String[] eleList;
	boolean[] startEle;
	int currEle = 0;
	public LinkedList<XMLElement> results = new LinkedList<XMLElement>();
	String address;
	boolean outsideGoodElement = false;

	public ElementTree(String eleName) {
	    address = eleName;
	    eleList = eleName.split("\\.");
	    startEle = new boolean[eleList.length];
	}

	private XMLElement createElement() {
	    XMLElement e = new XMLElement();
	    e.address = address;
	    return e;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
		Attributes attributes) throws SAXException {
	    if (currEle < eleList.length
		    && qName.equalsIgnoreCase(eleList[currEle])) {
		startEle[currEle] = true;
		currEle++;
		if (startEle[startEle.length - 1]) {
		    results.addLast(createElement());
		    results.getLast().attribs = new AttributesImpl(attributes);
		}
	    } else {
		outsideGoodElement = true;
	    }
	}

	@Override
	public void endElement(String uri, String localName, String qName)
		throws SAXException {
	    if (qName.equalsIgnoreCase(eleList[currEle - 1])) {
		startEle[currEle - 1] = false;
		currEle--;
	    }
	    outsideGoodElement = false;
	}

	@Override
	public void characters(char ch[], int start, int length)
		throws SAXException {
	    if (!outsideGoodElement
		    && startEle[startEle.length - 1]) {
		results.getLast().content += new String(ch, start, length).trim();
	    }
	}
    }
}
