/**
 * BibSonomy-Rest-Client - The REST-client.
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
package org.bibsonomy.rest.client.queries.delete;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.ValidationUtils;

import java.util.Collections;

/**
 * query to delete a CRIS link
 *
 * @author pda
 */
public class DeleteCRISLinkQuery extends AbstractQuery<JobResult> {
	private final Linkable source;
	private final Linkable target;

	public DeleteCRISLinkQuery(Linkable source, Linkable target) {
		this.source = ValidationUtils.requirePresent(source, "No source given.");
		this.target = ValidationUtils.requirePresent(target, "No target given.");
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		downloadedDocument = performRequest(HttpMethod.DELETE,
						getUrlRenderer().createUrlBuilderForCRISLinks(source.getLinkableId(),
										target.getLinkableId()).asString(), null);
	}

	@Override
	protected JobResult getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (!isSuccess()) {
			return JobResult.buildFailure(
							Collections.singletonList(new ErrorMessage(getError(), "" + getStatusCode())));
		}
		return JobResult.buildSuccess();
	}
}
