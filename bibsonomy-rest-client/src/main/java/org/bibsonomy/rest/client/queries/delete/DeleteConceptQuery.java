package org.bibsonomy.rest.client.queries.delete;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Status;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;

/**
 * Use this Class to delete a concept or a single relation.
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class DeleteConceptQuery extends AbstractQuery<String> {

	private final String conceptName;
	private final GroupingEntity grouping;
	private final String groupingName;
	
	/** if is set only the relation <em>conceptName <- subTag </em> will be deleted */
	private String subTag;
	
	public DeleteConceptQuery(final String conceptName, final GroupingEntity grouping, final String groupingName) {
		this.conceptName = conceptName;
		this.grouping = grouping;
		this.groupingName = groupingName;
		this.downloadedDocument = null;
	}
	
	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		String url;
		
		switch (grouping) {
		case USER:	
			url = URL_USERS; 
			break;
		case GROUP:
			url = URL_GROUPS;
			break;
		default:
			throw new UnsupportedOperationException("Grouping " + grouping + " is not available for concept delete query");
		}

		url += "/" + this.groupingName + "/" + URL_CONCEPTS + "/" + this.conceptName;
		
		if (subTag != null) {
			url += "?subtag=" + this.subTag;
		}
		
		this.downloadedDocument = performRequest(HttpMethod.DELETE, url, null);
		return null;	
	}

	@Override
	public String getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess())
			return Status.OK.getMessage();
		return this.getError();
	}

	public void setSubTag(String subTag) {
		this.subTag = subTag;
	}	
}