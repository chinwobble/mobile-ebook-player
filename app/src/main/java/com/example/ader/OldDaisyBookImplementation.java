package com.ader;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import com.example.ader.utilities.*;
import com.example.ader.*;

@SuppressWarnings("serial")
public class OldDaisyBookImplementation implements Serializable, DaisyBook {
	// public static final long serialVersionUID = 1;
	
	private static final String IS_HEADING = "h[123456]",
			IS_META = "meta";
	
	private static final String META_NAME_KEY = "name",
			META_VALUE_KEY = "content";
	
	private static final String TITLE_PREFIX = "Title: ",
			PATH_PREFIX = "Directory: ",
			SPACER = "\t";

	private static final String TAG = OldDaisyBookImplementation.class.getSimpleName();
	private int currentnccIndex = 0; // FIXME: Was -1 Temporary change during restructuring
	private int NCCDepth = 0;
	private int selectedLevel = 1;
	private List<DaisyItem> items = new ArrayList<DaisyItem>();
	private String path;
	private String title = null; // the title of the book from one of the source
	

	/* (non-Javadoc)
	 * @see com.ader.DaisyBook#getDisplayPosition()
	 */
	public int getDisplayPosition() {
		if (current().getLevel() <= selectedLevel) {
			return getNavigationDisplay().indexOf(current());
		}
		else {
			// find the position of the current item in the whole book
			int i = items.indexOf(current());

			// go backward through the book till we find an item in the
			// navigation display
			while (items.get(i).getLevel() > selectedLevel) {
				i--;
			}

			// return the position of the found item in the nav display
			return getNavigationDisplay().indexOf(items.get(i));
		}
	}

	/* (non-Javadoc)
	 * @see com.ader.DaisyBook#setSelectedLevel(int)
	 */
	public int setSelectedLevel(int level) {
		if (level >= 1 && level <= NCCDepth) {
			this.selectedLevel = level;
		}
		return this.selectedLevel;
	}

	/* (non-Javadoc)
	 * @see com.ader.DaisyBook#incrementSelectedLevel()
	 */
	public int incrementSelectedLevel() {
		if (this.selectedLevel < NCCDepth) {
			this.selectedLevel++;
		}
		return this.selectedLevel;
	}

	/* (non-Javadoc)
	 * @see com.ader.DaisyBook#decrementSelectedLevel()
	 */
	public int decrementSelectedLevel() {
		if (this.selectedLevel > 1) {
			this.selectedLevel--;
		}
		return this.selectedLevel;
	}

	/* (non-Javadoc)
	 * @see com.ader.DaisyBook#getCurrentDepthInDaisyBook()
	 */
	public int getCurrentDepthInDaisyBook() {
		return selectedLevel;
	}
	
	/* (non-Javadoc)
	 * @see com.ader.DaisyBook#getMaximumDepthInDaisyBook()
	 */
	public int getMaximumDepthInDaisyBook() {
		return NCCDepth;
	}
	
	/* (non-Javadoc)
	 * @see com.ader.DaisyBook#getPath()
	 */
	public String getPath() {
		return path;
	}

	/* (non-Javadoc)
	 * @see com.ader.DaisyBook#openFromFile(java.lang.String)
	 */
	public void openFromFile(String nccFullPathAndFilename) throws InvalidDaisyStructureException, IOException {
		items.clear();
		String filename = nccFullPathAndFilename;
		this.path = new File(nccFullPathAndFilename).getParent() + "/";
		DaisyParser parser = new DaisyParser();
		try {
			Logging.logInfo(TAG, new File(".").getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<DaisyElement> elements = parser.openAndParseFromFile(filename);
		items = processDaisyElements(elements);
		validateDaisyContents();
	}
	
	/**
	 * Open a Daisy Book using a text stream. 
	 * 
	 * This is intended to facilitate automated tests.
	 * @param contents The text representing the contents of a DAISY 2.02
	 * ncc.html file. 
	 */
	protected void open(String contents) throws InvalidDaisyStructureException {
		DaisyParser parser = new DaisyParser();
		List<DaisyElement> elements = parser.parse(contents);
		items = processDaisyElements(elements);
		validateDaisyContents();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ader.DaisyBook#current()
	 */
	public DaisyItem current() {
		return getDaisyItemFor(currentnccIndex);
	}


	/* (non-Javadoc)
	 * @see com.ader.DaisyBook#getNavigationDisplay()
	 */
	public List<DaisyItem> getNavigationDisplay() {
		ArrayList<DaisyItem> displayItems = new ArrayList<DaisyItem>();

		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getLevel() <= selectedLevel 
				&& items.get(i).getType() == DaisyItemType.LEVEL) {
				displayItems.add(items.get(i));
			}
		}
		return displayItems;
	}

	/* (non-Javadoc)
	 * @see com.ader.DaisyBook#goTo(com.ader.DaisyItem)
	 */
	public void goTo(DaisyItem nccEntry) {
		int index = items.indexOf(nccEntry);
		Logging.logInfo(TAG, "goto " + index);
		currentnccIndex = index;
	}
	
	/* (non-Javadoc)
	 * @see com.ader.DaisyBook#goTo(int)
	 * 
	 */
	// TODO 20110818 (jharty): We can remove this once bookmark doesn't contain the NCC Index.
	public void goTo(int nccIndex) {
		DaisyItem itemToGoTo = getDaisyItemFor(nccIndex);
		goTo(itemToGoTo);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ader.DaisyBook#getCurrentSmilFilename()
	 */
	public String getCurrentSmilFilename() {

		return path + current().getSmil();
	}
	
	/* (non-Javadoc)
	 * @see com.ader.DaisyBook#getCurrentIndex()
	 */
	public int getCurrentIndex() {
		return currentnccIndex;
	}

	/* (non-Javadoc)
	 * @see com.ader.DaisyBook#nextSection(java.lang.Boolean)
	 */
	public boolean nextSection(Boolean includeLevels) {
		Logging.logInfo(TAG, String.format(
				"next called; includelevels: %b selectedLevel: %d, currentnccIndex: %d", 
				includeLevels, selectedLevel, currentnccIndex));
		for (int i = currentnccIndex + 1; i < items.size(); i++) {
			if (items.get(i).getType() != DaisyItemType.LEVEL) {
				continue;
			}
			
			if (items.get(i).getLevel() > selectedLevel && includeLevels) {
				continue;
			}
			
			currentnccIndex = i;
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.ader.DaisyBook#previousSection()
	 */
	public boolean previousSection() {
		Logging.logInfo(TAG, "previous");
		for (int i = currentnccIndex -1; i >= 0; i--) {
			if (items.get(i).getLevel() <= selectedLevel
				&& items.get(i).getType() == DaisyItemType.LEVEL) {
				currentnccIndex = i;
				// TODO (jharty): make sure bookmark is updated by caller. bookmark.setPosition(0);
				return true;
			}
		}
		return false;
	}
	
	protected void validateDaisyContents() throws InvalidDaisyStructureException {
		// Check there is at least one H1 element
		for (int i = 0; i < items.size(); i++) {
			DaisyItem entry = items.get(i);
			if (entry.getType() == DaisyItemType.LEVEL && entry.getLevel() == 1) {
				return;
			}
		}
		throw new InvalidDaisyStructureException("No H1 level in the book");
	}
	
	/* (non-Javadoc)
	 * @see com.ader.DaisyBook#processDaisyElements(java.util.ArrayList)
	 * 
	 * TODO 20111215 (jharty) refactor this to be part of a builder as there
	 * is likely to be a lot of switch casing and this long if list is pretty
	 * nasty so should be buried in the factory that creates the book, probably
	 * in the parser. 
	 */
	public List<DaisyItem> processDaisyElements(List<DaisyElement> elements) {
		int level = 0;
		DaisyItemType type = DaisyItemType.UNKNOWN;
	
		for (DaisyElement element : elements) {
			String elementName = element.getName();
			
			// is it a heading element
			if (elementName.matches(IS_HEADING)) {
				level = Integer.decode(elementName.substring(1));
				type = DaisyItemType.LEVEL;
				if (level > NCCDepth) {
					NCCDepth = level;
				}
				
				continue;
			}
			
			if (elementName.matches(IS_META)) {
				MetaLabel label = determineMetaLabel(element);
				switch (label) {
					// TODO 20111215 (damienkallison) handle conflicts created
					// by duplicate titles.
					case TITLE: title = getMetaValue(element); 
						break;
					default: // skip default cases;
				}
			}
			
			// Note: The following is a hack, we should check the 'class'
			// attribute for a value containing "page-"
			if (elementName.contains("span")
					&& element.getAttributes().getValue(0).contains("page-")) {
				
				type = DaisyItemType.PAGENUMBER;
			}
			
			// is it an anchor element
			if (elementName.equalsIgnoreCase("a")) {
				// TODO (jharty): level should only be set for content, not
				// page-numbers, etc. However let's see where this takes us
				items.add(new NCCEntry(element, type, level));
			}
		}
		return items;
	}
	
	/**
	 * Get the metadata label value.
	 * 
	 * @param element to inspect
	 * @return The value of the metadata label or null if the label can't 
	 * 		be determined.
	 */
	static String getMetaValue(DaisyElement element) {
		if (null == element) {
			return null;
		}
		if (null == element.getName() ||
				!element.getName().matches(IS_META)) {
			return null;
		}
		return getAttributeValue(META_VALUE_KEY, element.getAttributes());
	}
	
	static String getAttributeValue(String key, Attributes attributes) {
		for (int i = 0; i < attributes.getLength(); i++) {
			if (attributes.getLocalName(i).equals(key)) {
				return attributes.getValue(i);
			}
		}
		return null;
	}
	
	/**
	 * Determine the metadata label type for a meta data element.
	 * 
	 * @param element to inspect.
	 * @return The Metadata label type. Note that unknown is returned in all
	 * 		cases where the label can not be determined.
	 * TODO 20111215 (damienkallison) Refactor this along with the parser
	 * factory so that it doesn't use this very expensive search for the node
	 * name.
	 */
	static MetaLabel determineMetaLabel(DaisyElement element) {
		if (null == element) {
			return MetaLabel.UNKNOWN;
		}
		if (null == element.getName() || 
				!element.getName().matches(IS_META)) {
			return MetaLabel.UNKNOWN;
		}
		String metaName = getAttributeValue(META_NAME_KEY,
				element.getAttributes());
		if (null == metaName) {
			return MetaLabel.UNKNOWN;
		}
		for (MetaLabel label : MetaLabel.values()) {
			if (metaName.equals(label.getName())) {
				return label;
			}
		}
		return MetaLabel.UNKNOWN;
	}
	
	enum MetaLabel {
		CHARACTER_SET {
			@Override String getName() {
				return "ncc:charset";
			}
		},
		FORMAT {
			@Override String getName() {
				return "dc:format";
			}
		},
		MULTI_MEDIA_TYPE {
			@Override String getName() {
				return "ncc:multimediaType";
			}
		},
		NARRATOR {
			@Override String getName() {
				return "ncc:narrator";
			}
		},
		CONTENTS_ITEM_COUNT {
			@Override String getName() {
				return "ncc:tocItems";
			}
		},
		/*
		 * Note, there are at least three definitions of the title: The title
		 * element, the dc:title meta data tag and the ncc:sourceTitle
		 */
		TITLE {
			@Override String getName() {
				return "dc:title";
			}
		},
		TOTAL_PLAY_TIME{
			@Override String getName() {
				return "ncc:narrator";
			}
		},
		UNKNOWN {
			@Override String getName() {
				return null;
			}
		};
		abstract String getName();
	}

	/**
	 * @return the DaisyItem for the nccIndex provided.
	 * 
	 * This method is public to enable easier refactoring of the DaisyPlayer
	 * and the rest of this codebase.
	 */
	// TODO 20110818 (jharty): remove this method once the bookmark code no longer stores the NCC Index.
	private DaisyItem getDaisyItemFor(int nccIndex) {
		DaisyItem item = items.get(nccIndex);
		Logging.logInfo(TAG, String.format("DaisyItem is index:%d, ncc:%s", nccIndex, item));
		return item;
	}
	
	/**
	 * Get the title.
	 * 
	 * @see DaisyBook#getTitle
	 */
	public String getTitle() {
		return title;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (null != title) {
			builder.append(TITLE_PREFIX);
			builder.append(title);
			builder.append(SPACER);
		}
		builder.append(PATH_PREFIX);
		builder.append(path);
		return builder.toString();
	}
}