package org.bibsonomy.rest.strategy.posts.community.references;

import java.io.Writer;
import java.util.Set;

import org.bibsonomy.model.enums.GoldStandardRelation;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * adds references to a gold standard post
 * 
 * @author dzo
 */
public class PostRelationsStrategy extends AbstractCreateStrategy {

	private final String hash;
	private final GoldStandardRelation relation;
	
	/**
	 * @param context
	 * @param hash 
	 * @param relation
	 */
	public PostRelationsStrategy(final Context context, final String hash, final GoldStandardRelation relation) {
		super(context);
		this.hash = hash;
		this.relation = relation;
	}

	@Override
	protected String create() {
		final Set<String> references = this.getRenderer().parseReferences(this.doc);
		this.getLogic().createRelations(this.hash, references, relation);
		
		return this.hash;
	}

	@Override
	protected void render(final Writer writer, final String resourceID) {
		this.getRenderer().serializeResourceHash(this.writer, resourceID);
	}

}
