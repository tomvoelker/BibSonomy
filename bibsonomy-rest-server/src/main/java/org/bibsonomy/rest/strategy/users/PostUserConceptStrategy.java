package org.bibsonomy.rest.strategy.users;

import java.io.Writer;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * Handle a concept creation request
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class PostUserConceptStrategy extends AbstractCreateStrategy {

	private final String userName;
	
	/**
	 * @param context - the context
	 * @param userName -  the owner of the new concept
	 */
	public PostUserConceptStrategy(Context context, String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	protected String create() {
		final Tag concept = this.getRenderer().parseTag(this.doc);
		return this.getLogic().createConcept(concept, GroupingEntity.USER, userName);				
	}

	@Override
	protected void render(Writer writer, String resourceHash) {
		this.getRenderer().serializeResourceHash(writer, resourceHash);	
	}

	@Override
	protected String getContentType() {
		return null;
	}
}