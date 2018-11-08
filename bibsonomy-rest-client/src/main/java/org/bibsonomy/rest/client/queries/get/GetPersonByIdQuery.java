package org.bibsonomy.rest.client.queries.get;

import org.apache.http.HttpStatus;
import org.bibsonomy.model.Person;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * retrieves a person by its personId
 *
 * @author pda
 */
public class GetPersonByIdQuery extends AbstractQuery<Person> {
    private final String id;

    public GetPersonByIdQuery(final String id) {
        if (!present(id)) throw new IllegalArgumentException("no id given");
        this.id = id;
    }

    @Override
    protected void doExecute() throws ErrorPerformingRequestException {
        final String url = getUrlRenderer().createUrlBuilderForPersons(this.id).asString();
        this.downloadedDocument = performGetRequest(url);
    }

    @Override
    protected Person getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
        if (this.getHttpStatusCode() == HttpStatus.SC_NOT_FOUND) {
            return null;
        }
        return this.getRenderer().parsePerson(this.downloadedDocument);
    }
}
