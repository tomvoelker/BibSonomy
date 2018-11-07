package org.bibsonomy.rest.client.queries.post;

import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.StringUtils;

import java.io.StringWriter;

public class MergePersonQuery extends AbstractQuery<Boolean> {
    private final PersonMatch match;

    public MergePersonQuery(PersonMatch match) {
        this.match = match;
    }

    @Override
    protected void doExecute() throws ErrorPerformingRequestException {
        final StringWriter sw = new StringWriter(100);
        this.getRenderer().serializePersonMatch(sw, this.match, null);
        this.downloadedDocument = performRequest(HttpMethod.POST,
                getUrlRenderer().createUrlBuilderForPersonMatch(match.getPerson1().getPersonId(),
                        match.getPerson2().getPersonId()).asString(),
                StringUtils.toDefaultCharset(sw.toString()));
    }

    @Override
    protected Boolean getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
        return this.isSuccess();
    }
}
