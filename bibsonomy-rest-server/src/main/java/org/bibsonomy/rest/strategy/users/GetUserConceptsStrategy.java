package org.bibsonomy.rest.strategy.users;

import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.ResourceUtils;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.renderer.xml.tools.EscapingPrintWriter;
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
	
	private Writer writer;
	
	/**
	 * @param context
	 * @param userName
	 */
	public GetUserConceptsStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
		this.resourceType = ResourceUtils.getResource(context.getStringAttribute("resourcetype", "all"));				
		this.status = ConceptStatus.getConceptStatus(context.getStringAttribute("status", "all"));
		this.regex = context.getStringAttribute("filter", null);
		this.tags = context.getTags("tags");
	}

	@Override
	public void perform(ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException {
		writer = new EscapingPrintWriter(outStream);
		List<Tag> concepts = this.getLogic().getConcepts(resourceType, GroupingEntity.USER, userName, regex, tags, status, 0, Integer.MAX_VALUE);
		this.getRenderer().serializeTags(writer, concepts, new ViewModel());			
	}
	
	@Override
	protected String getContentType() {
		return "tags";
	}	
}