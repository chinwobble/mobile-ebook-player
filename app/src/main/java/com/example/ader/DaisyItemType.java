package com.example.ader;

/**
 * DaisyItemType represents the type of each DaisyItem and is intended to
 * capture the type of the individual elements of the book such as page-number
 * level, etc.
 * 
 * <p>[NOTE: This is an experiment to see if the current design can be tweaked to
 * cope with page-numbers correctly. We may end up restructuring the code e.g.
 * to use a DOM to represent the book in which case this enum may be
 * superfluous.]</p>
 * 
 * TODO 20111215 (jharty) Consider restructuring with polymorphism rather than
 * type enum.
 * 
 * @author jharty
 *
 */
public enum DaisyItemType {
	UNKNOWN, LEVEL, PAGENUMBER;
}
