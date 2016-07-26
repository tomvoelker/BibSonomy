/**
 * BibSonomy-Rest-Client - The REST-client.
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
package org.bibsonomy.rest.client.queries.post;

import java.io.StringWriter;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.StringUtils;

/**
 * use this query to create a new concept
 * 
 * @author Stefan Stützer
 */
public class CreateConceptQuery extends AbstractQuery<String> {
	private final Tag concept;
	private final String conceptName;
	private final GroupingEntity grouping;
	private final String groupingName;
	
	/**
	 * @param concept
	 * @param conceptName
	 * @param grouping
	 * @param groupingName
	 */
	public CreateConceptQuery(final Tag concept,final String conceptName, final GroupingEntity grouping, final String groupingName) {
		this.concept = concept;
		this.conceptName = conceptName;
		this.grouping = grouping;
		this.groupingName = groupingName;
		if (!(GroupingEntity.GROUP == grouping) && !(GroupingEntity.USER == grouping)) {
			throw new UnsupportedOperationException("Grouping " + grouping + " is not available for concept change query");
		}
	}
	
	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializeTag(sw, this.concept, null);
		
		final String conceptUrl = this.getUrlRenderer().createHrefForConcept(this.grouping, this.groupingName, this.conceptName);
		this.downloadedDocument = performRequest(HttpMethod.POST, conceptUrl, StringUtils.toDefaultCharset(sw.toString()));
	}

	@Override
	protected String getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess()) {
			return this.getRenderer().parseResourceHash(this.downloadedDocument);
		}
		return this.getError();
	}
}