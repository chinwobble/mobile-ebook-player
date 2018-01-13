package com.example.ader;

import java.io.IOException;
import java.util.List;

import android.widget.TextView;

/**
 * This interface names the methods of a top level daisy item (a book) and
 * also encapsulates the current singleton 'current' book, the depth in the
 * book.
 * 
 * @todo(jharty) Update the documentation.
 * @author jharty
 *
 */
public interface DaisyBook {

	/**
	 * @return the current DaisyItem being used in the Daisy Book.
	 */
	DaisyItem current();
	
	int decrementSelectedLevel();

	/**
	 * Returns the current depth in the book.
	 */
	int getCurrentDepthInDaisyBook();

	/**
	 * FIXME: Another temporary getter until I cleanup the implementation of
	 * bookmark(s). This allows the extracted openSmil() code to function for
	 * the moment.
	 * @return the current index into the set of DaisyItems
	 */
	//TODO 20110818 (jharty): Remove once bookmark.java no longer stores the NCC Index.
	int getCurrentIndex();

	/**
	 * TODO 20110818 (jharty): Cleanup the design as the code improves.
	 * Temporary getter to help with restructuring the classes.
	 * @return the current SmilFile in the Daisy Book
	 */
	String getCurrentSmilFilename();

	int getDisplayPosition();
	
	/**
	 * gets the maximum depth of the book
	 * @return the depth of the book in sections.
	 */
	int getMaximumDepthInDaisyBook();
	
	List<DaisyItem> getNavigationDisplay();
	
	/**
	 * FIXME: Currently returns the path for the book. This is no longer needed 
	 * by the book, rather, the player needs it to manage bookmarks. However I
	 * need to change the design so DaisyReader (which *knows* the path) can 
	 * pass the path to DaisyPlayer. Rather than make the change in this 
	 * refactoring; I'll do so later.
	 * @return
	 */
	@Deprecated
	String getPath();
	
	/**
	 * Go to the specified item in the book.
	 * @param item
	 */
	void goTo(DaisyItem nccEntry);
	
	/**
	 * Update the internal state of the book to Go To the NCC Index provided. 
	 * @param nccIndex the NCC Index to use.
	 */
	// TODO 20110818 (jharty): We can remove this once bookmark doesn't contain the NCC Index.
	public void goTo(int nccIndex);

	int incrementSelectedLevel();

	/**
	 * Go to the next section in the book
	 * @param includeLevels - when true, pick the next section at a level
	 * equal or higher than the level selected by the user, else simply go to
	 * the next section.
	 * @return true if the book has a next section and navigated successfully
	 * to that section. If there is no next section, false is returned.
	 */
	boolean nextSection(Boolean includeLevels);

	/**
	 * Opens a Daisy Book from a full path and filename.
	 * 
	 * @param nccFullPathAndFilename The ncc file
	 * @throws InvalidDaisyStructureException if there are serious problems in
	 * the book structure.
	 * @throws IOException 
	 * 
	 * TODO 20111202 (jharty) This should be a named load format factory.
	 */
	void openFromFile(String nccFullPathAndFilename)
			throws InvalidDaisyStructureException, IOException;
	
	/**
	 * Go to the previous section in the book.
	 * @return true when the book has a previous section and navigated
	 * successfully to that section. If there is no previous section available,
	 * false is returned.
	 */
	boolean previousSection();

	/**
	 * Processes the Daisy Elements, e.g. from DaisyParser()
	 * @param elements The Daisy Book Elements
	 * @throws NumberFormatException
	 */
	List<DaisyItem> processDaisyElements(List<DaisyElement> elements);

	int setSelectedLevel(int level);
	
	/**
	 * Get the title of the book.
	 * 
	 * @return The title string of the book.
	 */
	public String getTitle();
}
