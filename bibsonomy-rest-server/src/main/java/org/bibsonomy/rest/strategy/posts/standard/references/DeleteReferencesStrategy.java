package org.bibsonomy.rest.strategy.posts.standard.references;

import java.io.Reader;
import java.util.Set;

import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author dzo
 * @version $Id$
 */
public class DeleteReferencesStrategy extends AbstractDeleteStrategy {
	
	private final String hash;
	private final Reader doc;

	/**
	 * @param context
	 * @param hash
	 */
	public DeleteReferencesStrategy(final Context context, final String hash) {
		super(context);
		
		this.hash = hash;
		this.doc = context.getDocument();
	}

	@Override
	protected boolean delete() {
		final Set<String> references = this.getRenderer().parseReferences(this.doc);
		this.getLogic().deleteReferences(this.hash, references);
		
		// no exception => delete successful
		return true;
	}

}
