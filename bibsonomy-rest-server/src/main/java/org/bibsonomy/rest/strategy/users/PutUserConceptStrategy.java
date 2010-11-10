package org.bibsonomy.rest.strategy.users;

import java.io.Writer;

import org.bibsonomy.common.enums.ConceptUpdateOperation;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * Handle a concept update request
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class PutUserConceptStrategy extends AbstractUpdateStrategy {

	private final String userName; 
	
	/**
	 * @param context - the context
	 * @param userName - the owner of the concept
	 */
	public PutUserConceptStrategy(Context context, String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	protected String update() {
		final Tag concept = this.getRenderer().parseTag(this.doc);
		return this.getLogic().updateConcept(concept, GroupingEntity.USER, this.userName, ConceptUpdateOperation.UPDATE);		
	}

	@Override
	protected void render(Writer writer, String resourceID) {
		this.getRenderer().serializeResourceHash(writer, resourceID);		
	}
}