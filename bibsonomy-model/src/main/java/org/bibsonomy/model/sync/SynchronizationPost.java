/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.sync;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author wla
 */
public class SynchronizationPost extends SynchronizationResource {

	/**
	 * interHash of this post
	 */
	private String intraHash;

	/**
	 * resource attached from server
	 */
	private Post<? extends Resource> post;

	/**
	 * Checks if two posts are the same
	 * 
	 * FIXME: should we overwrite equals() instead?
	 * 
	 * @see org.bibsonomy.model.sync.SynchronizationResource#isSame(org.bibsonomy.model.sync.SynchronizationResource)
	 * 
	 */
	@Override
	public boolean isSame(final SynchronizationResource post) {
		if (post instanceof SynchronizationPost) {
			final SynchronizationPost p = (SynchronizationPost)post;
			return (
					p.getChangeDate().equals(this.getChangeDate()) && 
					p.getCreateDate().equals(this.getCreateDate()) && 
					p.getIntraHash().equals(this.getIntraHash())
			);
		}
		return false;
	}

	/**
	 * @param intraHash the intraHash to set
	 */
	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}
	
	/**
	 * @return the intraHash
	 */
	public String getIntraHash() {
		return intraHash;
	}

	/**
	 * @param post the post to set
	 */
	public void setPost(Post<? extends Resource> post) {
		this.post = post;
	}

	/**
	 * @return the post
	 */
	public Post<? extends Resource> getPost() {
		return post;
	}

	@Override
	public String toString() {
		return super.toString() + " " + intraHash;
	}

}
