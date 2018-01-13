package com.example.ader;
/**
 * This parser processes DAISY 2.02 specification, albeit incompletely.
 * 
 * We intend to add support for DAISY 3 and epub formats.
 * 
 * Currently the parsing is limited and does not detect malformed books. We
 * want to add logic to detect malformed books. The reference will be the DAISY
 * 2.02 Specification http://www.daisy.org/z3986/specifications/daisy_202.html
 * 
 * TODO 20111215 (jharty) Consider separating the two concerns in this class
 * namely the utility wrapper methods for creating daisy elements. Storing the
 * daisy elements internally implies this is also a container for the book meta
 * data. This should really be separated out.
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.example.ader.io.ExtractXMLEncoding;
import com.example.ader.utilities.Logging;

public class DaisyParser extends DefaultHandler {
	private static final String TAG = DaisyParser.class.getSimpleName();
	private static final int EMPTY = 0;
	
	private List<DaisyElement> daisyElements = new ArrayList<DaisyElement>();
	private DaisyElement current;
	private StringBuilder builder = new StringBuilder();

	public List<DaisyElement> parse(String content) {
		return this.parse(new ByteArrayInputStream(content.getBytes()));
	}

	public List<DaisyElement> parse(InputStream stream) {
		
		EntityResolver der = dummyEntityResolver();
		// TODO (jharty): Extract the encoding from the stream
		return parseNccContents(stream, der, "UTF-8");
	}
	
	public List<DaisyElement> openAndParseFromFile(final String xmlFile) throws IOException {
		Logging.logInfo(TAG, "XMLFILE " + xmlFile);
		String encoding = ExtractXMLEncoding.obtainEncodingStringFromFile(xmlFile); 
		encoding = ExtractXMLEncoding.mapUnsupportedEncoding(encoding);

		FileInputStream fis = new FileInputStream(xmlFile);
		BufferedInputStream bis = new BufferedInputStream(fis);
		EntityResolver er = entityResolverForExternalFile(xmlFile);
		return parseNccContents(bis, er, encoding);
	}


	// Xml handling implementation.
	/**
	 * Implementation of the start event listener overriding the default.
	 * 
	 * TODO 20111215 (jharty) Consider retaining the structure and using nested
	 * handlers for separation of the different sections of the book metadata.
	 */
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		builder.setLength(EMPTY);
		current = new DaisyElement();
		// Possible bug between Android and Java...
		// On Android the element name is returned in localName, on the
		// workstation it's returned in 'name'
		// Adding a temporary workaround until I understand what's happening!
		// also use isEmpty if using java 6
		if (localName.length() > EMPTY) {
			current.setName(localName);
		} else {
			current.setName(name);
		}
		current.setAttributes(attributes);
		daisyElements.add(current);
	}
	
	/**
	 * Handle tag content.
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}
	
	@Override
	public void endElement(String uri, String localName, String name)
	throws SAXException {
		current.setText(builder.toString());
	}
	
	/** 
	 * Parse the contents of the DAISY 2.02 ncc.html file.
	 * 
	 * I've had to change the call to accept an InputStream rather than an
	 * InputSource in order to have the encoding take effect. I'm not sure
	 * quite why this makes a difference, however I noticed the parser now has
	 * a buffered input stream as the byteStream (which was null before). 
	 * 
	 * Note: this code is modelled on the code in SmilParser.java which
	 * correctly supports the encoding.
	 */
	private List<DaisyElement> parseNccContents(InputStream stream, EntityResolver er, String encoding) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			XMLReader saxParser = factory.newSAXParser().getXMLReader();

			// The EntityResolver ensures the SAX parser can correctly locate
			// referenced entities e.g. xhtml1-strict.dtd from
        	// the referenced URI when the files are in relative paths. 
			// 
        	// Thanks to: http://forums.sun.com/thread.jspa?threadID=413937 and
			// http://www.jdom.org/pipermail/jdom-interest/2001-August/007129.html
        	// which helped us to resolve the problem.
			
        	saxParser.setEntityResolver(er);
        	saxParser.setContentHandler(this);
        	org.xml.sax.InputSource input = new InputSource(stream);
        	input.setEncoding(encoding);
        	saxParser.parse(input);
        	
        	return daisyElements;
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	private EntityResolver entityResolverForExternalFile(final String xmlFile) {
		// TODO: We may need to limit this handler to file:// references and
		// find another way of resolving other references e.g. http://
		
		EntityResolver er = new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId)
			throws java.io.IOException
			{
				String directory = xmlFile.substring(0, xmlFile.lastIndexOf('/') + 1);
				Logging.logInfo(TAG, "xml directory:" + directory);
				String resourcePath =
					directory + systemId.substring(systemId.lastIndexOf('/') + 1);
				return new InputSource(new BufferedReader(new FileReader(resourcePath)));
			}
		};
		return er;
	}
	
	private EntityResolver dummyEntityResolver() {
		// Thanks to http://www.junlu.com/msg/202604.html 
		EntityResolver er = new EntityResolver() {
	    public InputSource resolveEntity(String publicId, String systemId)
	    {
	        return new InputSource(new StringReader(" "));
	    }
	};
	return er;
	}

}