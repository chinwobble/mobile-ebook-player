package com.example.ader;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.example.ader.utilities.*;

/*
 * Non JavaDoc
 * TODO(jharty):
 * 1. Somewhere we need to either add preconditions (to assert the basic
 *  structure is acceptable and as expected) or handle problematic content more
 *  robustly. e.g. at the moment we gayly dereference things like:
 *  document.getElementsByTagName("body").item(0).getChildNodes() without
 *  checking for nulls being returned by the intervening calls.
 */
public class XMLParser {
	private static final String PROBLEM_PARSING_XML = "Problem parsing XML";
	private static final String TAG = "XMLParser";
	private Document document;
	private NavCentre navCentre;

	/**
	 * Parses a valid Daisy 2.02 ncc.html document.
	 * 
	 * @param input valid Daisy 2.02 content.
	 */
	public XMLParser(InputStream input) {
		navCentre = new NavCentre();	
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

		try {
			// Many thanks for the discussion at http://code.google.com/p/plist/issues/detail?id=13
			// which provided enough info to add the following workaround for Android.
			if(System.getProperty("java.vendor").toLowerCase().contains("android")) {
				builderFactory.setValidating(false);
			} else {
			builderFactory
					.setFeature(
							"http://apache.org/xml/features/nonvalidating/load-external-dtd",
							false);
			}
			document = builderFactory.newDocumentBuilder().parse(input);
		} catch (ParserConfigurationException e) {
			Logging.logSevereWarning(TAG, PROBLEM_PARSING_XML, e);
		} catch (SAXException e) {
			Logging.logSevereWarning(TAG, PROBLEM_PARSING_XML, e);
		} catch (IOException e) {
			Logging.logSevereWarning(TAG, PROBLEM_PARSING_XML, e);
		}
	}

	/**
	 * Processes the NCC contents to generate a NavCentre object.
	 * 
	 * Currently we only process the h1 through h6 elements & page-numbers.
	 * And we assume the content is valid, otherwise various exceptions, 
	 * including a null pointer may be thrown.
	 * 
	 * @return the NavCentre object.
	 */
	public NavCentre processNCC() {
		// get a list of all the tags inside the body element
		NodeList body = document.getElementsByTagName("body").item(0).getChildNodes();
				
		for (int i = 0; i < body.getLength(); i++) {
			Node currentNode = body.item(i);
			
			// is it a heading tag
			if (currentNode.getNodeName().matches("h[123456]")) { 
				handleNCChTag(currentNode);
			}
			
			// is it a span tag
			 if (currentNode.getNodeName().equalsIgnoreCase("span")) {
				handleNCCspanTag(currentNode);
			 }
		}
		
		return navCentre;
	}
	
	private void handleNCChTag(Node heading) {
		int level = Integer.decode(heading.getNodeName().substring(1));
		NavPoint navPoint = new NavPoint(heading.getFirstChild(), level);

		navCentre.addNavPoint(navPoint);
	}

	private void handleNCCspanTag(Node span) {
		
		Node value = span.getFirstChild();
		Logging.logInfo("XMLParser", "Page No: " + value.getFirstChild().getNodeValue());
		
		// This is still imperfect, however it should be better than the previous code.
		// TODO(jharty): Decide what parameters to pass to PageTarget, etc.
		PageTarget target = new PageTarget(value, "page-normal");
		navCentre.addPageTarget(target);
	}
}










