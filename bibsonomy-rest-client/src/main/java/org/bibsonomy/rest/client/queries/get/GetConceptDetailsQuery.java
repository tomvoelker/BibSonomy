package org.bibsonomy.rest.client.queries.get;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to get information about the specified concept
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class GetConceptDetailsQuery extends AbstractQuery<Tag> {

	private final String conceptname;
	private String groupingName;
	private GroupingEntity grouping = GroupingEntity.ALL;
	
	public GetConceptDetailsQuery(final String conceptName) {
		this.conceptname = conceptName;
		this.downloadedDocument = null;
	}
	
	@Override
	protected Tag doExecute() throws ErrorPerformingRequestException {
		String url = null;
		
		switch (this.grouping) {
		case USER:
			url = URL_USERS + "/" + this.groupingName + "/" + URL_CONCEPTS + "/" + this.conceptname;
			break;
		case GROUP:
			throw new UnsupportedOperationException("Grouping " + grouping + " is not implemented yet");
			//url = URL_GROUPS + "/" + this.groupingName + "/" + URL_CONCEPTS + "/" + this.conceptname;
			//break;
		case ALL:
			url = URL_TAGS + "/" + "-%3E" + this.conceptname;  
			break;			
		default:
			throw new UnsupportedOperationException("Grouping " + grouping + " is not available for concept details query");
		}
		
		this.downloadedDocument = performGetRequest(url + "?format=" + getRenderingFormat().toString().toLowerCase());
		return null;
	}

	@Override
	public Tag getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.downloadedDocument == null) throw new IllegalStateException("Execute the query first.");
		return RendererFactory.getRenderer(getRenderingFormat()).parseTag(this.downloadedDocument);
	}
	
	public void setUserName(final String userName) {
		this.groupingName = userName;
		this.grouping = GroupingEntity.USER;
	}
	
	public void setGroupName(final String groupName) {
		this.groupingName = groupName;
		this.grouping = GroupingEntity.GROUP;
	}	
}