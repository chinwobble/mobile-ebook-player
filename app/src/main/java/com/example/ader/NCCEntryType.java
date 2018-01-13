package com.ader;

/**
 * NCCEntryType represents the type of each NCC entry and is intended to
 * capture the type of the individual elements of the book such as page-number
 * level, etc.
 * 
 * <p>[NOTE: This is an experiment to see if the current design can be tweaked to
 * cope with page-numbers correctly. We may end up restructuring the code e.g.
 * to use a DOM to represent the book in which case this enum may be
 * superfluous.]</p>
 * 
 * @author jharty
 *
 */
public enum NCCEntryType {
	UNKNOWN, LEVEL, PAGENUMBER;
}
