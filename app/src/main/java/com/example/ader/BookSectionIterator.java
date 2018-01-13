/**
 * Allows a caller to iterate through DaisyItems.
 * 
 * Code based on example from Chapter 9 of Head First Design Patterns book.
 */
package com.example.ader;

import java.util.Iterator;

/**
 * Iterates through Daisy Items.
 * 
 * @author Julian Harty
 */
public class BookSectionIterator implements Iterator<DaisyItem> {
	private DaisyItem[] items;
	private int position = 0;

	public BookSectionIterator(final DaisyItem[] items) {
		// Currently I'm willing to leave Sonar's 'critical' warning unfixed as
		// this code isn't actually used and may well be reworked anyway. The
		// warning acts as a useful reminder.
		this.items = items;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if(position >= items.length) { return false; }
		if (items[position] == null) { return false; }
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public DaisyItem next() {
		DaisyItem daisyItem = items[position];
		position = position + 1;
		return daisyItem;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException("Removing items from a Daisy Book is not supported.");

	}
}
