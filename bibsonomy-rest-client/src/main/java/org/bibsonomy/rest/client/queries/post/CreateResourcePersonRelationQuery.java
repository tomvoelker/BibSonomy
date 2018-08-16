package org.bibsonomy.rest.client.queries.post;

import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.StringUtils;

import java.io.StringWriter;

public class CreateResourcePersonRelationQuery extends AbstractQuery<ResourcePersonRelation> {
    private final ResourcePersonRelation resourcePersonRelation;

    public CreateResourcePersonRelationQuery(ResourcePersonRelation resourcePersonRelation) {
        this.resourcePersonRelation = resourcePersonRelation;
    }

    @Override
    protected void doExecute() throws ErrorPerformingRequestException {
        final StringWriter sw = new StringWriter(100);
        getRenderer().serializeResourcePersonRelation(sw, resourcePersonRelation, null);
        downloadedDocument = performRequest(HttpMethod.POST,
                getUrlRenderer().createUrlBuilderForResourcePersonRelations(
                        resourcePersonRelation.getPerson().getPersonId()).asString(),
                StringUtils.toDefaultCharset(sw.toString()));
    }

    @Override
    protected ResourcePersonRelation getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
        return null;
    }
}
