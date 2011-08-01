package org.bibsonomy.database.managers;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.FirstListChainElement;
import org.bibsonomy.database.managers.chain.goldstandard.publication.GoldStandardPublicationChain;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;

/**
 * Used to create, read, update and delete gold standard publications from the database.
 * 
 * @author dzo
 * @version $Id$
 */
public final class GoldStandardPublicationDatabaseManager extends GoldStandardDatabaseManager<BibTex, GoldStandardPublication, BibTexParam> {
	private static final GoldStandardPublicationDatabaseManager INSTANCE = new GoldStandardPublicationDatabaseManager();
	
	
	private static final GoldStandardPublicationChain chain = new GoldStandardPublicationChain();
	
	/**
	 * @return the @{link:GoldStandardPublicationDatabaseManager} instance
	 */
	public static GoldStandardPublicationDatabaseManager getInstance() {
		return INSTANCE;
	}
	
	private GoldStandardPublicationDatabaseManager() {}
	
	@Override
	protected void onGoldStandardReferenceDelete(final String userName, final String interHash, final String interHashRef, final DBSession session) {
		this.plugins.onGoldStandardPublicationReferenceDelete(userName, interHash, interHashRef, session);		
	}

	@Override
	protected FirstListChainElement<Post<GoldStandardPublication>, BibTexParam> getChain() {
	    return chain;
	}

	@Override
	protected BibTexParam createNewParam() {
		return new BibTexParam();
	}
}