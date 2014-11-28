/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.resource.ResourcePageCommand;

/**
 * @author pba
 */
public class DiffPublicationCommand extends ResourcePageCommand<BibTex>{
	
	private Post<BibTex> post;
	private Post<BibTex> postDiff;
	private String user;
	private String intraHashToUpdate;
	/**
	 * stores an id, e.g. for mapping recommendations to posts
	 */
	private int postID;
	
	/**
	 * stores version (index of publication in history list)
	 */
	private int compareVersion;
	

	/**
	 * @return Index from postDiff in PostHistoryList
	 */
	public int getCompareVersion() {
		return compareVersion;
	}

	/**
	 * @param compareVersion The index of the post which should be compared to current post
	 */
	public void setCompareVersion(int compareVersion) {
		this.compareVersion = compareVersion;
	}

	/**
	 * @return post which should compared to current post
	 */
	public Post<BibTex> getPostDiff() {
		return postDiff;
	}

	/**
	 * @param postDiff The post which should be compared to current post
	 */
	public void setPostDiff(Post<BibTex> postDiff) {
		this.postDiff = postDiff;
	}

	/**
	 * @return current post
	 */
	public Post<BibTex> getPost() {
		return post;
	}

	/**
	 * @param post The current post
	 */
	public void setPost(Post<BibTex> post) {
		this.post = post;
	}

	/**
	 * @return The name of the user whose post should be copied.
	 */
	public String getUser() {
		return this.user;
	}

	/** 
	 * @param user The name of the user whose post should be copied.
	 */
	public void setUser(final String user) {
		this.user = user;
	}
	
	/**
	 * @return The intra hash of the post which should be copied. Must be used 
	 * together with the name of the user.
	 */
	public String getIntraHashToUpdate() {
		return this.intraHashToUpdate;
	}

	/**
	 * Sets the intra hash of the post which should be copied. Must be used 
	 * together with the name of the user.
	 * 
	 * @param intraHashToUpdate
	 */
	public void setIntraHashToUpdate(final String intraHashToUpdate) {
		this.intraHashToUpdate = intraHashToUpdate;
	}

	/**
	 * The post id is used to uniquely identify a post until it is stored in the
	 * database. The recommender service needs this to assign recommenders to 
	 * posting processes.
	 *  
	 * @param postID
	 */
	public void setPostID(final int postID) {
		this.postID = postID;
	}

	/**
	 * @see #setPostID(int)
	 * @return the postID used by the recommenders
	 */
	public int getPostID() {
		return postID;
	}

}
