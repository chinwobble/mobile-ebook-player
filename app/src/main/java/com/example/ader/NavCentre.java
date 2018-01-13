package com.example.ader;

import java.util.ArrayList;
import java.util.List;

/**
 * NavCenter stores and represents the navigable sections in an eBook.
 * 
 * It's based on the structures defined for DAISY 3 books, e.g. it contains a
 * NavMap, etc. See http://www.daisy.org/z3986/2005/Z3986-2005.html#NCX for
 * more information on the DAISY 3 specification. 
 * 
 * Currently it is limited to sections in the eBook e.g. page-numbers aren't
 * included - this is a short-term limitation we expect to address.
 */
public class NavCentre {
	private List<NavPoint> navMap = new ArrayList<NavPoint>();
	private List<PageTarget> pageList = new ArrayList<PageTarget>();

	/**
	 * add a Navigation Point e.g. a Heading in a DAISY 2.02 document
	 * @param navPoint the structure containing the Navigation Point
	 */
	public void addNavPoint(NavPoint navPoint) {
		this.navMap.add(navPoint);
	}
	
	/**
	 * Add a Page Target e.g. a Page Number in a DAISY 2.02 document
	 * @param target the structure containing the Page Number
	 */
	public void addPageTarget(PageTarget target) {
		this.pageList.add(target);
		
	}
	
	/**
	 * Returns the specified NavPoint. 
	 * 
	 * The index matches the order that navigation points were found in the
	 * source document.
	 * @param index between 0 and count() items
	 * @return the specified NavPoint, or null if index is outside the valid
	 * range.
	 */
	// TODO(gary): I've modified the behaviour to return null, what do you think?
	public NavPoint getNavPoint(int index) {
		if (index >= 0 && index < navMap.size()) {
			return navMap.get(index);
		} else {
			return null;
		}
	}
	
	/**
	 * Return the PageTarget that matches the index.
	 * 
	 * The index matches the order that navigation points were found in the
	 * source document.
	 * @param index between 0 and count() items
	 * @return the specified PageTarget, or null if the index is outside the
	 * valid range.
	 */
	public PageTarget getPageTarget(int index) {
		if (index >= 0 && index < pageList.size()) {
			return pageList.get(index);
		} else {
			return null;
		}
	}
	/**
	 * A NavCenter contains NavPoints and PageTargets, get the count of items.
	 * @return the number of items in the NavCenter.
	 */
	public int count() {
		return navMap.size() + pageList.size();
	}
}
