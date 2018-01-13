package com.example.ader;

import java.io.Serializable;

public class NCCEntry implements Serializable, DaisyItem {

    private static final long serialVersionUID = 2L;
    private String smil;
	private String smilRef;
	private String text;
	private int level;
	private DaisyItemType type;

	/**
	 * Retained for now to enable easier migration. Will be deprecated once the
	 * new XMLParser works.
	 * @param element the DaisyElement to process
	 * @param type the type of the entry
	 * @param level the level of the element on the DAISY book hierarchy
	 */
	public NCCEntry(DaisyElement element, DaisyItemType type, int level) {
		this(element.getText(), element.getAttributes().getValue("", "href"), type, level);
	}

	/**
	 * Creates an NCC Entry.
	 *
	 * This signature helps with the migration from the old DaisyParser to the
	 * new XMLParser (that uses DAISY3 constructs).
	 * @param text the text content
	 * @param smil the smil reference to the content
	 * @param type the type of NCC Entry e.g. HEADING
	 * @param level the level of the element in the DAISY book hierarchy
	 */
	public NCCEntry(String text, String smil, DaisyItemType type, int level) {
		// I discovered the parser (or something) added newline and tab characters
		// to the last page-number for light-man. This is a crude workaround until
		// I get to the bottom of the issue. Since I want to retire this, old
		// parser, I'm fairly relaxed about the problem, but am curious to learn
		// the cause so I make sure the new parser doesn't suffer from similar
		// problems.
		this.text = text.trim();  // Strips off extraneous new line characters, etc.
		this.type = type;
		this.level = level;
		int hashPosition = smil.indexOf('#');
		smilRef = smil.substring(hashPosition + 1);
		String tempStr = smil.substring(0, hashPosition);
		this.smil = tempStr;

	}

	/* (non-Javadoc)
	 * @see com.ader.DaisyItem#getType()
	 */
	public DaisyItemType getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see com.ader.DaisyItem#getLevel()
	 */
	public int getLevel() {
		return level;
	}

	/* (non-Javadoc)
	 * @see com.ader.DaisyItem#getSmil()
	 */
	public String getSmil() {
		return smil;
	}

	/* (non-Javadoc)
	 * @see com.ader.DaisyItem#getText()
	 */
	public String getText() {
		return text;
	}

	/* (non-Javadoc)
	 * @see com.ader.DaisyItem#toString()
	 */
	@Override
	public String toString() {
		return "smil:" + smil + " text: " + text;
	}

	/* (non-Javadoc)
	 * @see com.ader.DaisyItem#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
	    if (!(obj instanceof NCCEntry)) {
	        return false;
	    }
	    NCCEntry rhs = (NCCEntry) obj;
        return obj != null && smil.equals(rhs.smil) && smilRef.equals(rhs.smilRef)
            && text.equals(rhs.text);
	}

    /* (non-Javadoc)
	 * @see com.ader.DaisyItem#hashCode()
	 */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + level;
        result = prime * result + ((smil == null) ? 0 : smil.hashCode());
        result = prime * result + ((smilRef == null) ? 0 : smilRef.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }
}
