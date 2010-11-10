package org.bibsonomy.rest.strategy.posts.standard.references;

import java.io.Writer;
import java.util.Set;

import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * adds references to a gold standard post
 * 
 * @author dzo
 * @version $Id$
 */
public class PostReferencesStrategy extends AbstractCreateStrategy {

	private final String hash;
	
	/**
	 * @param context
	 * @param hash 
	 */
	public PostReferencesStrategy(final Context context, final String hash) {
		super(context);
		
		this.hash = hash;
	}

	@Override
	protected String create() {
		final Set<String> references = this.getRenderer().parseReferences(this.doc);
		this.getLogic().createReferences(this.hash, references);
		
		return this.hash;
	}

	@Override
	protected void render (final Writer writer, final String resourceID) {
		this.getRenderer().serializeResourceHash(this.writer, resourceID);
	}

}
