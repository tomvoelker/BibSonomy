package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean for arbitrary content in a tabbed context
 * 
 * @author Stefan Stützer
 * @version $Id$
 * @param <T>
 *            type of the tab content
 */
public class TabsCommand<T> extends ResourceViewCommand implements TabsCommandInterface<T> {

	/**
	 * tabURL of the current tab
	 */
	private String tabURL = null;

	/** id of current selected tab */
	protected Integer selTab = 1;

	/** list of defined tabs */
	protected List<TabCommand> tabs = new ArrayList<TabCommand>();

	/** the content of the current selected tab */
	protected List<T> content;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.webapp.command.TabsCommandInterface#getContent()
	 */
	@Override
	public List<T> getContent() {
		return this.content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.webapp.command.TabsCommandInterface#setContent(java.util
	 * .List)
	 */
	@Override
	public void setContent(final List<T> content) {
		this.content = content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.webapp.command.TabsCommandInterface#getSelTab()
	 */
	@Override
	public Integer getSelTab() {
		return this.selTab;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.webapp.command.TabsCommandInterface#setSelTab(java.lang
	 * .Integer)
	 */
	@Override
	public void setSelTab(final Integer selectedTab) {
		this.selTab = selectedTab;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.webapp.command.TabsCommandInterface#getTabs()
	 */
	@Override
	public List<TabCommand> getTabs() {
		return this.tabs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.webapp.command.TabsCommandInterface#setTabs(java.util.List)
	 */
	@Override
	public void setTabs(final List<TabCommand> tabs) {
		this.tabs = tabs;
	}

	/**
	 * @param id
	 * @param title
	 */
	protected void addTab(final Integer id, final String title) {
		tabs.add(new TabCommand(id, title));
	}

	/**
	 * @param titles
	 */
	protected void addTabs(final String[] titles) {
		for (int i = 0; i < titles.length; i++) {
			addTab(i, titles[i]);
		}
	}

	/**
	 * @return the tabURL
	 */
	public String getTabURL() {
		return this.tabURL;
	}

	/**
	 * @param tabURL
	 *            the tabURL to set
	 */
	public void setTabURL(final String tabURL) {
		this.tabURL = tabURL;
	}

}