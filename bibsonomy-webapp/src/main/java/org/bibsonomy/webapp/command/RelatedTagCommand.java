/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.Tag;

/**
 * Bean for related tags of a single tag or a list
 * of tags
 * 
 * @author Stefan Stuetzer
 */
public class RelatedTagCommand extends BaseCommand {

	/** the requested tag(s) for whose to find related tags*/
	private String requestedTags;	
	
	/**  the related tags of the requested tag(s) */
	private List<Tag> relatedTags = new ArrayList<Tag>();
	
	/** the global count of the tag these tags are related to */
	private Integer tagGlobalCount = 1;

	/**
	 * @return the requestedTags
	 */
	public String getRequestedTags() {
		return this.requestedTags;
	}

	/**
	 * @param requestedTags the requestedTags to set
	 */
	public void setRequestedTags(String requestedTags) {
		this.requestedTags = requestedTags;
	}

	/**
	 * @return the relatedTags
	 */
	public List<Tag> getRelatedTags() {
		return this.relatedTags;
	}

	/**
	 * @param relatedTags the relatedTags to set
	 */
	public void setRelatedTags(List<Tag> relatedTags) {
		this.relatedTags = relatedTags;
	}

	/**
	 * @return the tagGlobalCount
	 */
	public Integer getTagGlobalCount() {
		return this.tagGlobalCount;
	}

	/**
	 * @param tagGlobalCount the tagGlobalCount to set
	 */
	public void setTagGlobalCount(Integer tagGlobalCount) {
		this.tagGlobalCount = tagGlobalCount;
	}
}
