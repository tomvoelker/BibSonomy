package org.bibsonomy.rest.client.queries.put;

import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.model.Person;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

import java.io.StringWriter;

/**
 * query to update a {@link Person}
 *
 * @author pda
 */
public class UpdatePersonQuery extends AbstractQuery<String> {
	private final Person person;
	private final PersonUpdateOperation operation;

	public UpdatePersonQuery(Person person, PersonUpdateOperation operation) {
		this.person = person;
		this.operation = operation;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializePerson(sw, this.person, null);
		final String personUrl = this.getUrlRenderer().
						createUrlBuilderForPersons(person.getPersonId(), operation).asString();
		this.downloadedDocument = performRequest(HttpMethod.PUT, personUrl, sw.toString());
	}

	@Override
	protected String getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess()) {
			return this.getRenderer().parsePersonId(this.downloadedDocument);
		}
		return this.getError();
	}
}
