package org.bibsonomy.rest.client.queries.post;

import org.bibsonomy.model.Person;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.StringUtils;

import java.io.StringWriter;

import static org.bibsonomy.util.ValidationUtils.present;

public class CreatePersonQuery extends AbstractQuery<String> {
    private final Person person;

    public CreatePersonQuery(Person person) {
        if (!present(person)) throw new IllegalArgumentException("no person specified");
        if (!present(person.getPersonId())) throw new IllegalArgumentException("no userid specified");
        this.person = person;
    }

    @Override
    protected void doExecute() throws ErrorPerformingRequestException {
        final StringWriter sw = new StringWriter(100);
        getRenderer().serializePerson(sw, person, null);
        downloadedDocument = performRequest(HttpMethod.POST,
                getUrlRenderer().createUrlBuilderForPersons().asString(),
                StringUtils.toDefaultCharset(sw.toString()));
    }

    @Override
    protected String getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
        return isSuccess() ? getRenderer().parsePerson(downloadedDocument).getPersonId() : getError();
    }
}
