package org.bibsonomy.model;

/**
 * Holds settings for a user.
 * 
 * @version $Id$
 */
public class UserSettings {
	/**
	 * tagbox style; 0 = cloud, 1 = list
	 */
	private int tagboxStyle = 0;

	/**
	 * sorting of tag box; 0 = alph, 1 = freq
	 */
	private int tagboxSort = 0;

	/**
	 * minimum frequency for tags to be displayed in tag box
	 */
	private int tagboxMinfreq = 0;

	/**
	 * tooltip; 0 = don't show, 1 = show TODO: what does this mean?
	 */
	private int tagboxTooltip = 0;

	/**
	 * number of list items per page; how many posts to show in post lists
	 */
	private int listItemcount = 10;
	
	/**
	 * the default language for i18n
	 */
	private String defaultLanguage = "en";
	/**
	 * How much data about the user behaviour (clicking, etc.) is logged.
	 */
	private int logLevel;



	/**
	 * @return tagboxStyle
	 */
	public int getTagboxStyle() {
		return this.tagboxStyle;
	}

	/**
	 * @param tagboxStyle
	 */
	public void setTagboxStyle(int tagboxStyle) {
		this.tagboxStyle = tagboxStyle;
	}

	/**
	 * @return tagboxSort
	 */
	public int getTagboxSort() {
		return this.tagboxSort;
	}

	/**
	 * @param tagboxSort
	 */
	public void setTagboxSort(int tagboxSort) {
		this.tagboxSort = tagboxSort;
	}

	/**
	 * @return tagboxMinfreq
	 */
	public int getTagboxMinfreq() {
		return this.tagboxMinfreq;
	}

	/**
	 * @param tagboxMinfreq
	 */
	public void setTagboxMinfreq(int tagboxMinfreq) {
		this.tagboxMinfreq = tagboxMinfreq;
	}

	/**
	 * @return tagboxTooltip
	 */
	public int getTagboxTooltip() {
		return this.tagboxTooltip;
	}

	/**
	 * @param tagboxTooltip
	 */
	public void setTagboxTooltip(int tagboxTooltip) {
		this.tagboxTooltip = tagboxTooltip;
	}

	/**
	 * @return listItemcount
	 */
	public int getListItemcount() {
		return this.listItemcount;
	}

	/**
	 * @param listItemcount
	 */
	public void setListItemcount(int listItemcount) {
		this.listItemcount = listItemcount;
	}

	/**
	 * @return the default language
	 */
	public String getDefaultLanguage() {
		return this.defaultLanguage;
	}

	/**
	 * @param defaultLanguage the default language
	 */
	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public int getLogLevel() {
		return this.logLevel;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}	
}