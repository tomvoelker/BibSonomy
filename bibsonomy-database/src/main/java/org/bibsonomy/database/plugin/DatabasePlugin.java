package org.bibsonomy.database.plugin;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BasketParam;
import org.bibsonomy.database.params.UserParam;

/**
 * This interface supplies hooks which can be implemented by plugins. This way
 * the code for basic operations, like updating a bookmark or publication, can
 * be kept concise and is easier to maintain.<br/>
 * 
 * If a method returns <code>null</code> its execution will be skipped.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @author Stefan Stützer
 * @author Anton Wilhelm
 * @version $Id$
 */
public interface DatabasePlugin {

	/**
	 * Called when a BibTex is inserted.
	 * 
	 * @param contentId
	 * @param session
	 * @return runnable
	 */
	public Runnable onBibTexInsert(int contentId, DBSession session);

	/**
	 * Called when a BibTex is deleted.
	 * 
	 * @param contentId
	 * @param session
	 * @return runnable
	 */
	public Runnable onBibTexDelete(int contentId, DBSession session);

	/**
	 * Called when a BibTex is updated.
	 * 
	 * @param newContentId
	 * @param contentId
	 * @param session
	 * @return runnable
	 */
	public Runnable onBibTexUpdate(int newContentId, int contentId, DBSession session);

	/**
	 * Called when a gold standard publication is created.
	 * 
	 * @param interhash
	 * @param session
	 * @return runnable
	 */
	public Runnable onGoldStandardPublicationCreate(String interhash, DBSession session);

	/**
	 * Called when a gold standard publication will be updated.
	 * 
	 * @param newInterhash
	 * @param interhash
	 * @param session
	 * @return runnable
	 */
	public Runnable onGoldStandardPublicationUpdate(String newInterhash, String interhash, DBSession session);
	
	/**
	 * Called when a reference of a gold standard publication will be created
	 * @param userName
	 * @param interHash_publication
	 * @param interHash_reference
	 * @return runnable
	 */
	public Runnable onGoldStandardPublicationReferenceCreate(String userName, String interHash_publication, String interHash_reference);
	
	/**
	 * Called when a reference of a gold standard publication will be deleted
	 * 
	 * @param userName
	 * @param interHash_publication
	 * @param interHash_reference
	 * @param session
	 * @return runnable
	 */
	public Runnable onGoldStandardPublicationReferenceDelete(String userName, String interHash_publication, String interHash_reference, DBSession session);
	
	/**
	 * Called when a gold standard publication is deleted.
	 * 
	 * @param interhash
	 * @param session
	 * @return runnable
	 */
	public Runnable onGoldStandardPublicationDelete(String interhash, DBSession session);
	
	/**
	 * Called when a Bookmark is inserted.
	 * 
	 * @param contentId
	 * @param session
	 * @return runnable
	 */
	public Runnable onBookmarkInsert(int contentId, DBSession session);

	/**
	 * Called when a Bookmark is deleted.
	 * 
	 * @param contentId
	 * @param session
	 * @return runnable
	 */
	public Runnable onBookmarkDelete(int contentId, DBSession session);

	/**
	 * Called when a Bookmark is updated.
	 * 
	 * @param newContentId
	 * @param contentId
	 * @param session
	 * @return runnable
	 */
	public Runnable onBookmarkUpdate(int newContentId, int contentId, DBSession session);
	
	/**
	 * Called when a TagRelation is deleted.
	 * 
	 * @param upperTagName
	 * @param lowerTagName
	 * @param userName
	 * @param session
	 * @return runnable
	 */
	public Runnable onTagRelationDelete(String upperTagName, String lowerTagName, String userName, DBSession session);
	
	/**
	 * Called when a Concept is deleted.
	 * 
	 * @param conceptName
	 * @param userName
	 * @param session
	 * @return runnable
	 */
	public Runnable onConceptDelete(String conceptName, String userName, DBSession session);
	
	/**
	 * Called when a Tag is deleted.
	 * 
	 * @param contentId
	 * @param session
	 * @return runnable
	 */
	public Runnable onTagDelete(int contentId, DBSession session);
	
	/**
	 * Called when a User is inserted.
	 * 
	 * @param userName
	 * @param session
	 * @return runnable
	 */
	public Runnable onUserInsert(String userName, DBSession session);

	/**
	 * Called when a User is deleted.
	 * 
	 * @param userName
	 * @param session
	 * @return runnable
	 */
	public Runnable onUserDelete(String userName, DBSession session);

	/**
	 * Called when a User is updated.
	 * 
	 * @param userName
	 * @param session
	 * @return runnable
	 */
	public Runnable onUserUpdate(String userName, DBSession session);	

	/**
	 * Called when a user is removed from a group.
	 * 
	 * @param userName
	 * @param groupId
	 * @param session
	 * @return runnable
	 */
	public Runnable onRemoveUserFromGroup(String userName, int groupId, DBSession session);
	
	/**
	 * Called when a fellowship will be deleted
	 * 
	 * @param param
	 * @param session
	 * @return runnable
	 */
	public Runnable onDeleteFellowship(final UserParam param, final DBSession session);
	
	/**
	 * Called when a friendship will be deleted
	 * 
	 * @param param
	 * @param session
	 * @return runnable
	 */
	public Runnable onDeleteFriendship(final UserParam param, final DBSession session);
	
	/**
	 * Called when a basket item will be deleted
	 * 
	 * @param param
	 * @param session
	 * @return runnable
	 */
	public Runnable onDeleteBasketItem(final BasketParam param, final DBSession session);
	
	/**
	 * Called when all basket items will be deleted
	 * 
	 * @param userName 
	 * @param session 
	 * @return runnable
	 * 
	 */
	public Runnable onDeleteAllBasketItems(final String userName, final DBSession session);
}