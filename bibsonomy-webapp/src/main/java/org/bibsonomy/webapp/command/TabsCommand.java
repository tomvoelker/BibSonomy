package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean for arbitrary content in a tabbed context
 * 
 * @author Stefan Stützer
 * @version $Id$
 * @param <T> type of the tab content
 */
public class TabsCommand<T> extends ResourceViewCommand {

	/** id of current selected tab */
	protected Integer selTab = 1;	

	/** list of defined tabs */
	protected List<TabCommand> tabs = new ArrayList<TabCommand>();

	/** the content of the current selected tab */
	protected List<T> content;

	/**
	 * @return content of tab
	 */
	public List<T> getContent() {
		return this.content;
	}

	/**
	 * Sets the content of the current selected tab
	 * @param content
	 */
	public void setContent(List<T> content) {
		this.content = content;
	}

	/**
	 * @return ID of current selected tab
	 */
	public Integer getSelTab() {
		return this.selTab;
	}

	/**
	 * Sets the id of the current selected tab
	 * @param selectedTab The tab ID
	 */
	public void setSelTab(Integer selectedTab) {
		this.selTab = selectedTab;
	}

	/**
	 * @return List of defined tabs 
	 */
	public List<TabCommand> getTabs() {
		return this.tabs;
	}

	/**
	 * @param tabs Sets the tabs 
	 */
	public void setTabs(List<TabCommand> tabs) {
		this.tabs = tabs;
	}

	/**
	 * Adds a single tab with the the given id and title.
	 * 
	 * @param id 
	 * @param title 
	 */
	public void addTab(final Integer id, final String title) {
		tabs.add(new TabCommand(id, title));
	}

	/**
	 * Adds for each title a tab.
	 * 
	 * @param titles
	 */
	public void addTabs(final String[] titles) {
		for(int i=0; i<titles.length; i++) {
			addTab(i, titles[i]);
		}
	}

}