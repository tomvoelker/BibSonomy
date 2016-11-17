/**
 * BibSonomy-Layout - Layout engine for the webapp.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.layout.csl;

/**
 * 
 * @author jp
 */
public class CSLStyle extends org.bibsonomy.model.Layout {
	
	private String id;
	private String content;
	private String aliasedTo;
	private boolean userLayout;

	/**
	 * @param id 
	 * @param displayName 
	 * @param content 
	 */
	public CSLStyle(String id, String displayName, String content) {
		super(id);
		this.id = id;
		this.displayName = displayName;
		this.content = content;
	}
	
	/**
	 * @param name
	 * @param id
	 * @param displayName
	 * @param content
	 * @param aliasedTo
	 */
	public CSLStyle(String id, String displayName, String content, String aliasedTo) {
		this(id, displayName, content);
		this.setAliasedTo(aliasedTo);
	}

	//TODO eigtl nur vorrübergehend.. vielleicht
	/**
	 * @param id 
	 */
	public CSLStyle(String id) {
		super(id);
		this.id = id;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.Layout#hasEmbeddedLayout()
	 */
	@Override
	public boolean hasEmbeddedLayout() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * @return the userLayout
	 */
	public boolean isUserLayout() {
		return this.userLayout;
	}

	/**
	 * @param userLayout the userLayout to set
	 */
	public void setUserLayout(boolean userLayout) {
		this.userLayout = userLayout;
	}

	/**
	 * @return the aliasedTo
	 */
	public String getAliasedTo() {
		return this.aliasedTo;
	}

	/**
	 * @param aliasedTo the aliasedTo to set
	 */
	public void setAliasedTo(String aliasedTo) {
		this.aliasedTo = aliasedTo;
	}
}