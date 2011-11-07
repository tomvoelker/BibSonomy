package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
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
	
	/**
	 * @return the @{link:GoldStandardPublicationDatabaseManager} instance
	 */
	public static GoldStandardPublicationDatabaseManager getInstance() {
		return INSTANCE;
	}
	
	private GoldStandardPublicationDatabaseManager() {}
	
	@Override
	public Post<GoldStandardPublication> getPostDetails(final String loginUserName, final String resourceHash, final String userName, final List<Integer> visibleGroupIDs, final DBSession session) {
		final Post<GoldStandardPublication> post = super.getPostDetails(loginUserName, resourceHash, userName, visibleGroupIDs, session);
		
		if (present(post)) {
			/*
			 * before the resource leaves the logic parse the misc field
			 */
			post.getResource().parseMiscField();
		}
		
		return post;
	}
	
	@Override
	protected void onGoldStandardReferenceDelete(final String userName, final String interHash, final String interHashRef, final DBSession session) {
		this.plugins.onGoldStandardPublicationReferenceDelete(userName, interHash, interHashRef, session);		
	}

	@Override
	protected BibTexParam createNewParam() {
		return new BibTexParam();
	}
}