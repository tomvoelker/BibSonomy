/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
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
package org.bibsonomy.recommender.connector.model;

import org.bibsonomy.model.Tag;

import recommender.core.interfaces.model.RecommendationTag;

/**
 * This class wraps a BibSonomy {@link Tag} to allow injection of those into the framework.
 * 
 * @author lukas
 *
 */
public class TagWrapper implements RecommendationTag {

	private Tag tag;

	public TagWrapper(Tag tag) {
		this.tag = tag;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.tag.getName();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.tag.setName(name);
	}

	/* 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		return (this.tag.equals(obj));
	}

	@Override
	public int compareTo(final recommender.core.interfaces.model.RecommendationTag tag) {
		return this.tag.getName().toLowerCase().compareTo(tag.getName().toLowerCase());
	}
	
	public Tag getTag() {
		return tag;
	}
	
	public void setTag(Tag tag) {
		this.tag = tag;
	}
}
