/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.model;

import java.util.List;

/**
 * The Inbox, that stores posts sent by friends
 * 
 *  
 * @author sdo
 */
public class Inbox {

	private List<Post<Bookmark>> bookmarks;
	private List<Post<BibTex>> publications;

	private int numBookmarks;
	private int numPublications;
	private int numPosts;

	/**
	 * @return numBookmarks
	 */
	public int getNumBookmarks() {
		return this.numBookmarks;
	}

	/**
	 * @param numBookmarks
	 */
	public void setNumBookmarks(int numBookmarks) {
		this.numBookmarks = numBookmarks;
	}

	/**
	 * @return numPublications
	 */
	public int getNumPublications() {
		return this.numPublications;
	}

	public int getNumPosts() {
		return this.numPosts;
	}
	
	public void setNumPosts(int numPosts) {
		this.numPosts=numPosts;
	}
	/**
	 * @param numPublications
	 */
	public void setNumPublications(int numPublications) {
		this.numPublications = numPublications;
	}
}