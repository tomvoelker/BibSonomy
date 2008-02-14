package org.bibsonomy.rest.client.queries.put;

import java.io.StringWriter;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to update an existing concept
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class ChangeConceptQuery extends AbstractQuery<String> {

	private final Tag concept;	
	private final String conceptName;
	private final GroupingEntity grouping;
	private final String groupingName;
	
	public ChangeConceptQuery(final Tag concept, final String conceptName, final GroupingEntity grouping, final String groupingName) {
		this.concept = concept;
		this.conceptName = conceptName;
		this.grouping = grouping;
		this.groupingName = groupingName;
	}
	
	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		String url;
		final StringWriter sw = new StringWriter(100);
		RendererFactory.getRenderer(getRenderingFormat()).serializeTag(sw, concept, null);
		
		switch (grouping) {
		case USER:
			url = URL_USERS;			
			break;
		case GROUP:
			url = URL_GROUPS;
			break;
		default:
			throw new UnsupportedOperationException("Grouping " + grouping + " is not available for concept change query");
		}		
		
		url += "/" + this.groupingName + "/" + URL_CONCEPTS + "/" + this.conceptName;
		
		this.downloadedDocument = performRequest(HttpMethod.PUT, url + "?format=" + getRenderingFormat().toString().toLowerCase(), sw.toString());
		return null;
	}

	@Override
	public String getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess())
			return RendererFactory.getRenderer(getRenderingFormat()).parseResourceHash(this.downloadedDocument); 
		return this.getError();
	}
}