package org.bibsonomy.rest.strategy.concepts;

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
 * Handles a global concepts request
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class GetConceptsStrategy extends Strategy {
	protected final Class<? extends Resource> resourceType;
	private final String regex;	
	private final List<String> tags;
	
	/**
	 * @param context
	 */
	public GetConceptsStrategy(Context context) {
		super(context);
		this.resourceType = ResourceFactory.getResourceClass(context.getStringAttribute(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.RESOURCE_CLASS_NAME));
		this.regex = context.getStringAttribute(RESTConfig.REGEX_PARAM, null);
		this.tags = context.getTags(RESTConfig.TAGS_PARAM);
	}
	
	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException {
		final List<Tag> concepts = this.getLogic().getConcepts(resourceType, GroupingEntity.ALL, null, regex, tags, ConceptStatus.ALL, 0, Integer.MAX_VALUE);
		this.getRenderer().serializeTags(writer, concepts, new ViewModel());
	}

	@Override
	protected String getContentType() {
		return "tags";
	}
}