/*
 * Created on 08.10.2007
 */
package org.bibsonomy.model;

public class UserSettings {
	/**
	 * tagbox style
	 */
	private int tagboxStyle     = 0;  // 0 = cloud, 1 = list
	
	/**
	 * sorting of tag box
	 */
	private int tagboxSort      = 0;  // 0 = alph, 1 = freq\
	
	/**
	 * minimum frequency for tags to be displayed in tag box
	 */
	private int tagboxMinfreq   = 0;  // minimal freq a tag must have to be shown\
	
	/**
	 * tooltip
	 */
	private int tagboxTooltip   = 0;  // 0 = don't show, 1 = show (TODO: what does this mean?)
	
	/**
	 * number of list items per page
	 */
	private int listItemcount       = 10; // how many posts to show in post lists? 	

	public int getTagboxStyle() {
		return this.tagboxStyle;
	}

	public void setTagboxStyle(int tagboxStyle) {
		this.tagboxStyle = tagboxStyle;
	}

	public int getTagboxSort() {
		return this.tagboxSort;
	}

	public void setTagboxSort(int tagboxSort) {
		this.tagboxSort = tagboxSort;
	}

	public int getTagboxMinfreq() {
		return this.tagboxMinfreq;
	}

	public void setTagboxMinfreq(int tagboxMinfreq) {
		this.tagboxMinfreq = tagboxMinfreq;
	}

	public int getTagboxTooltip() {
		return this.tagboxTooltip;
	}

	public void setTagboxTooltip(int tagboxTooltip) {
		this.tagboxTooltip = tagboxTooltip;
	}

	public int getListItemcount() {
		return this.listItemcount;
	}

	public void setListItemcount(int listItemcount) {
		this.listItemcount = listItemcount;
	}
}
