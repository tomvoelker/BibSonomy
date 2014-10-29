package org.bibsonomy.rest.strategy.posts.community.references;

import java.io.Reader;
import java.util.Set;

import org.bibsonomy.model.enums.GoldStandardRelation;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author dzo
 */
public class DeleteRelationsStrategy extends AbstractDeleteStrategy {

	private final String hash;
	private final Reader doc;
	private final GoldStandardRelation relation;

	/**
	 * @param context
	 * @param hash
	 * @param relation
	 */
	public DeleteRelationsStrategy(final Context context, final String hash, final GoldStandardRelation relation) {
		super(context);

		this.hash = hash;
		this.doc = context.getDocument();
		this.relation =  relation;
	}

	@Override
	protected boolean delete() {
		final Set<String> references = this.getRenderer().parseReferences(this.doc);
		this.getLogic().deleteRelations(this.hash, references, relation);

		// no exception => delete successful
		return true;
	}

}
