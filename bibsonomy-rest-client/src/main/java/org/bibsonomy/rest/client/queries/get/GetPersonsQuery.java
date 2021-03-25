package org.bibsonomy.rest.client.queries.get;

import java.util.List;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * @author dzo
 */
public class GetPersonsQuery extends AbstractQuery<List<Person>> {

	private final PersonQuery personQuery;

	/**
	 * default constructor
	 * @param personQuery
	 */
	public GetPersonsQuery(PersonQuery personQuery) {
		this.personQuery = personQuery;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String url = this.getUrlRenderer().createUrlBuilderForPersons(this.personQuery.getUserName()).asString();
		this.downloadedDocument = performGetRequest(url);
	}

	@Override
	protected List<Person> getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		return this.getRenderer().parsePersons(this.downloadedDocument);
	}
}
