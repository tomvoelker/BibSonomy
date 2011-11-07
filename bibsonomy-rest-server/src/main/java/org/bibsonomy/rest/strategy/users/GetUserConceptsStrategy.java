package org.bibsonomy.rest.strategy.users;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * Handle a user concepts request
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class GetUserConceptsStrategy extends Strategy {
	protected final Class<? extends Resource> resourceType;
	private final String userName;
	private final ConceptStatus status;
	private final String regex;	
	private final List<String> tags;
	
	/**
	 * @param context
	 * @param userName
	 */
	public GetUserConceptsStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
		this.resourceType = ResourceFactory.getResourceClass(context.getStringAttribute(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.RESOURCE_CLASS_NAME));		
		this.status = ConceptStatus.getConceptStatus(context.getStringAttribute(RESTConfig.CONCEPT_STATUS_PARAM, "all"));
		this.regex = context.getStringAttribute(RESTConfig.FILTER_PARAM, null);
		this.tags = context.getTags(RESTConfig.TAGS_PARAM);
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException {
		final List<Tag> concepts = this.getLogic().getConcepts(resourceType, GroupingEntity.USER, userName, regex, tags, status, 0, Integer.MAX_VALUE);
		this.getRenderer().serializeTags(writer, concepts, new ViewModel());			
	}
	
	@Override
	protected String getContentType() {
		return "tags";
	}
}