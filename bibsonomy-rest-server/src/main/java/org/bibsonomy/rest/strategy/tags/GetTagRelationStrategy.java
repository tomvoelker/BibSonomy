/**
 * BibSonomy-Rest-Server - The REST-server.
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
package org.bibsonomy.rest.strategy.tags;

import java.util.List;

import org.bibsonomy.common.enums.TagRelation;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.SortUtils;

/**
 * Strategy for handling a request for related tags.
 * @author niebler
 */
public class GetTagRelationStrategy extends GetListOfTagsStrategy {
	
	/** The list of requested tags. */
	protected List<String> tags;
	/** The requested relation. */
	protected TagRelation relation;

	/**
	 * Creates the strategy object for handling a request for related tags.
	 * @param context the REST context.
	 * @param tags a list of requested tag names.
	 * @param relation the requested relation. This must not be null.
	 */
	public GetTagRelationStrategy(final Context context, final List<String> tags, final TagRelation relation) {
		super(context);
		
		this.tags = tags;
		if (relation == null) {
			throw new BadRequestOrResponseException("relation unknown!");
		}
		this.relation = relation;
	}
	
	/**
	 * Returns a list of tags according to the requested relation. If <tt>relation</tt>
	 * is something else than RELATED, SIMILAR, all tags are returned.
	 * @return 
	 */
	@Override
	protected final List<Tag> getList() {
		switch (this.relation) {
			case RELATED:
				return this.getTags(TagSimilarity.COOC);
			case SIMILAR:
				if (this.tags.size() != 1) {
					return null;
				}
				return this.getTags(TagSimilarity.COSINE);
			default:
				return this.getTags(null);
		}
	}

	private List<Tag> getTags(final TagSimilarity tagSimilarity) {
		return this.getLogic().getTags(resourceType, grouping, groupingValue, tags,
						hash, null, regex, tagSimilarity, SortUtils.getFirstSortKey(this.getView().getSortCriteria()), null, null,
						this.getView().getStartValue(), this.getView().getEndValue());
	}
}
