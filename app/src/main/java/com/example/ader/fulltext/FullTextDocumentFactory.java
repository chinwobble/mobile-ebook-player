package com.ader.fulltext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jsoup.nodes.Document;

/**
 * This class contains creates a jsoup document for full-text content.
 * 
 * @author jharty
 */
public class FullTextDocumentFactory {
	// TODO 20110904 (jharty): Clean up duplicate constants across the codebase.
	private static final String SEPARATOR = "#";

	Document processedContents = null;

	/**
	 * Factory to create the Document. 
	 * 
	 * It caches the content on the first call to improve performance.
	 * @param filename
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Document createDocument(String filename) throws FileNotFoundException, IOException {
		if (processedContents == null) {
			FullText fullText = new FullText();
			String elements[] = filename.split(SEPARATOR);
			File fileToReadFrom = new File(elements[0]);
			StringBuilder fileContents = fullText.getContentsOfHTMLFile(fileToReadFrom);
			processedContents = fullText.processHTML(fileContents.toString());
		}
		return processedContents;
	}
}
