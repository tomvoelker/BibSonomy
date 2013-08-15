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
 * @version $Id: PostUserConceptStrategy.java,v 1.2 2013-02-13 21:03:21
 *          nosebrain Exp $
 */
public class PostUserConceptStrategy extends AbstractCreateStrategy {
	private final String userName;

	/**
	 * @param context - the context
	 * @param userName - the owner of the new concept
	 */
	public PostUserConceptStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	protected String create() {
		final Tag concept = this.getRenderer().parseTag(this.doc);
		return this.getLogic().createConcept(concept, GroupingEntity.USER, this.userName);
	}

	@Override
	protected void render(final Writer writer, final String resourceHash) {
		this.getRenderer().serializeResourceHash(writer, resourceHash);
	}
}