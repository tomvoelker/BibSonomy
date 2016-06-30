/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.params;

import java.util.Date;

import org.bibsonomy.model.User;

/**
 *
 * @author dzo
 * @author tni
 */
public class LoggingParam {
	private String oldTag;
	private String newTag;

	private String oldHash;
	private String newHash;

	private int oldContentId;
	private int newContentId;
	private int contentType;

	private User postOwner;
	private User postEditor;

	private Date date;

	/**
	 * @return the oldTag
	 */
	public String getOldTag() {
		return this.oldTag;
	}

	/**
	 * @param oldTag
	 *            the oldTag to set
	 */
	public void setOldTag(final String oldTag) {
		this.oldTag = oldTag;
	}

	/**
	 * @return the newTag
	 */
	public String getNewTag() {
		return this.newTag;
	}

	/**
	 * @param newTag
	 *            the newTag to set
	 */
	public void setNewTag(final String newTag) {
		this.newTag = newTag;
	}

	/**
	 * @return the oldContentId
	 */
	public int getOldContentId() {
		return this.oldContentId;
	}

	/**
	 * @param oldContentId
	 *            the oldContentId to set
	 */
	public void setOldContentId(final int oldContentId) {
		this.oldContentId = oldContentId;
	}

	/**
	 * @return the newContentId
	 */
	public int getNewContentId() {
		return this.newContentId;
	}

	/**
	 * @param newContentId the newContentId to set
	 */
	public void setNewContentId(final int newContentId) {
		this.newContentId = newContentId;
	}

	/**
	 * @return the contentType
	 */
	public int getContentType() {
		return this.contentType;
	}

	/**
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(final int contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the postOwner
	 */
	public User getPostOwner() {
		return this.postOwner;
	}

	/**
	 * @param postOwner
	 *            the postOwner to set
	 */
	public void setPostOwner(final User postOwner) {
		this.postOwner = postOwner;
	}

	/**
	 * @return the postEditor
	 */
	public User getPostEditor() {
		return this.postEditor;
	}

	/**
	 * @param postEditor
	 *            the postEditor to set
	 */
	public void setPostEditor(final User postEditor) {
		this.postEditor = postEditor;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(final Date date) {
		this.date = date;
	}

	/**
	 * @return the oldHash
	 */
	public String getOldHash() {
		return this.oldHash;
	}

	/**
	 * @param oldHash
	 *            the oldHash to set
	 */
	public void setOldHash(final String oldHash) {
		this.oldHash = oldHash;
	}

	/**
	 * @return the newHash
	 */
	public String getNewHash() {
		return this.newHash;
	}

	/**
	 * @param newHash
	 *            the newHash to set
	 */
	public void setNewHash(final String newHash) {
		this.newHash = newHash;
	}
}