package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
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
	protected BibTexParam getInsertParam(final Post<GoldStandardPublication> post) {
		final BibTexParam insert = new BibTexParam();
		insert.setResource(post.getResource());
		insert.setDescription(post.getDescription());
		insert.setDate(post.getDate());
		insert.setUserName((present(post.getUser()) ? post.getUser().getName() : ""));
		insert.setGroupId(GroupID.PUBLIC); // gold standards are public
		
		return insert;
	}
	
	@Override
	protected void onGoldStandardCreate(String resourceHash, DBSession session) {
		this.plugins.onGoldStandardPublicationCreate(resourceHash, session);
	}
	
	@Override
	protected void onGoldStandardUpdate(String oldHash, String newResourceHash, DBSession session) {
		this.plugins.onGoldStandardPublicationUpdate(newResourceHash, oldHash, session);		
	}
	
	@Override
	protected void onGoldStandardDelete(String resourceHash, DBSession session) {
		this.plugins.onGoldStandardPublicationDelete(resourceHash, session);
	}
	
	@Override
	protected void onGoldStandardReferenceDelete(String userName, String interHash, String interHashRef, DBSession session) {
		this.plugins.onGoldStandardPublicationReferenceDelete(userName, interHash, interHashRef, session);		
	}
}