package org.bibsonomy.webapp.command;

import java.util.List;

/**
 * @author rja
 * @version $Id$
 * @param <T> 
 */
public interface TabsCommandInterface<T> {

	/**
	 * @return content of tab
	 */
	public abstract List<T> getContent();

	/**
	 * Sets the content of the current selected tab
	 * @param content
	 */
	public abstract void setContent(List<T> content);

	/**
	 * @return ID of current selected tab
	 */
	public abstract Integer getSelTab();

	/**
	 * Sets the id of the current selected tab
	 * @param selectedTab The tab ID
	 */
	public abstract void setSelTab(Integer selectedTab);

	/**
	 * @return List of defined tabs 
	 */
	public abstract List<TabCommand> getTabs();

	/**
	 * @param tabs Sets the tabs 
	 */
	public abstract void setTabs(List<TabCommand> tabs);


}