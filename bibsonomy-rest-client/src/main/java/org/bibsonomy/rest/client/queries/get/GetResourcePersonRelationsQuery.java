package org.bibsonomy.rest.client.queries.get;

import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

import java.util.List;

/**
 * @author dzo
 */
public class GetResourcePersonRelationsQuery extends AbstractQuery<List<ResourcePersonRelation>> {
	private final String personId;

	public GetResourcePersonRelationsQuery(String personId) {
		this.personId = personId;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		this.downloadedDocument = this.performGetRequest(this.getUrlRenderer().createUrlBuilderForResourcePersonRelations(this.personId).asString());
	}

	@Override
	protected List<ResourcePersonRelation> getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		return this.getRenderer().parseResourcePersonRelations(this.downloadedDocument);
	}
}
