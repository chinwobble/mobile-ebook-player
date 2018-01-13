package com.example.ader;

import org.w3c.dom.Node;

/**
 * NavBase is a concept described in the DAISY 3 standard.
 *
 * @author jharty
 */
public class NavBase {
	private int playOrder;
	private String text;
	private String smil;
	private String id;

	/* non JavaDoc
	 * TODO(jharty): Discuss the following...
	 * Hide default public constructor, to prevent the object being created
	 * with null values - probably not what we want.
	 *
	 * Compare with a Factory design pattern where the default constructor is
	 * also prohibited.
	 */
	@SuppressWarnings("unused")
	private NavBase() {

	}

	/* Non JavaDoc
	 * TODO(jharty): Act on the following note...
	 * Notes: The following code assumes every element is present and correct.
	 * If these assumptions are incorrect, what happens? Let's add some tests
	 * to ascertain the behaviour, then modify the behaviour to what we'd like
	 * it to be :)
	 */
	public NavBase(Node anchor) {
		this.text = anchor.getFirstChild().getNodeValue();
		this.smil = anchor.getAttributes().getNamedItem("href").getNodeValue();
		this.id = smil.substring(smil.indexOf('#') + 1);
		smil = smil.substring(0, smil.indexOf('#') );
	}

	/**
	 * @return the id of the NavBase.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the play order.
	 */
	public int getPlayOrder() {
		return playOrder;
	}

	/**
	 * @return the text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the value for the Smil filename.
	 */
	public String getSmil() {
		return smil;
	}

}
