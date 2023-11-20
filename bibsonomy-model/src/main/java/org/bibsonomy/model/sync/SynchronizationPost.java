/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author wla
 */
@Getter
@Setter
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

	@Override
	public String toString() {
		return super.toString() + " " + intraHash;
	}

}
