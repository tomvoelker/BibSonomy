package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author cvo
 * @version $Id$
 */
public class SettingsCommand extends BaseCommand {

	private int logLevel;
	
	private String defaultLanguage;
	
	private int itemcount;
	
	private int tagboxTooltip;
	
	private int tagboxMinfreq;
	
	private int tagSort;
	
	private int tagboxStyle;

	private boolean confirmDelete;
	
	public int getLogLevel() {
		return this.logLevel;
	}

	public String getDefaultLanguage() {
		return this.defaultLanguage;
	}

	public int getItemcount() {
		return this.itemcount;
	}

	public int getTagboxTooltip() {
		return this.tagboxTooltip;
	}

	public int getTagboxMinfreq() {
		return this.tagboxMinfreq;
	}

	public int getTagSort() {
		return this.tagSort;
	}

	public int getTagboxStyle() {
		return this.tagboxStyle;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public void setItemcount(int itemcount) {
		this.itemcount = itemcount;
	}

	public void setTagboxTooltip(int tagboxTooltip) {
		this.tagboxTooltip = tagboxTooltip;
	}

	public void setTagboxMinfreq(int tagboxMinfreq) {
		this.tagboxMinfreq = tagboxMinfreq;
	}

	public void setTagSort(int tagSort) {
		this.tagSort = tagSort;
	}

	public void setTagboxStyle(int tagboxStyle) {
		this.tagboxStyle = tagboxStyle;
	}

	public boolean isConfirmDelete() {
		return this.confirmDelete;
	}

	public void setConfirmDelete(boolean confirmDelete) {
		this.confirmDelete = confirmDelete;
	}
}
