package org.bibsonomy.database.plugin.plugins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.TagRelationDatabaseManager;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * The first methods test the syntax of the methods of the class: Logging.java.
 * The SQL methods test if the SQL statements of the Logging.java class are
 * semantically correct.
 * 
 * @author Anton Wilhelm
 * @version $Id$
 */
@Ignore // FIXME adapt to new test db
public class LoggingTest extends AbstractDatabaseManagerTest {
	
	private static GeneralDatabaseManager generalDb;
	private static BookmarkDatabaseManager bookmarkDb;
	private static BibTexDatabaseManager publicationDb;
	private static GroupDatabaseManager groupDb;
	private static TagRelationDatabaseManager tagRelDb;
	
	/**
	 * sets up the used managers
	 */
	@BeforeClass
	public static void setupDatabaseManager() {
		generalDb = GeneralDatabaseManager.getInstance();
		bookmarkDb = BookmarkDatabaseManager.getInstance();
		groupDb = GroupDatabaseManager.getInstance();
		publicationDb = BibTexDatabaseManager.getInstance();
		tagRelDb = TagRelationDatabaseManager.getInstance();
	}

	/**
	 * tests whether we can add this plugin to the registry
	 */
	@Test
	public void addLoggingPlugin() {
		pluginRegistry.clearPlugins();
		pluginRegistry.add(new Logging());
	}

	/**
	 * tests onBibTexDelete
	 */
	@Test
	public void onBibTexDelete() {
		final int anyContentId = generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		pluginRegistry.onBibTexDelete(anyContentId, dbSession);
	}

	/**
	 * tests onBibTexUpdate
	 */
	@Test
	public void onBibTexUpdate() {
		final int anyContentId = generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		pluginRegistry.onBibTexUpdate(anyContentId, anyContentId - 1, dbSession);
	}

	/**
	 * tests onBookmarkDelete
	 */
	@Test
	public void onBookmarkDelete() {
		final int anyContentId = generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		pluginRegistry.onBookmarkDelete(anyContentId, dbSession);
	}

	/**
	 * tests onBookmarkUpdate
	 */
	@Test
	public void onBookmarkUpdate() {
		final int anyContentId = generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		pluginRegistry.onBookmarkUpdate(anyContentId, anyContentId - 1, dbSession);
	}

	/**
	 * tests onTagRelationDelete
	 */
	@Test
	public void onTagRelationDelete() {
		generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		pluginRegistry.onTagRelationDelete("upperTagName", "lowerTagName", "userName", dbSession);
	}

	/**
	 * tests onTagDelete
	 */
	@Test
	public void onTagDelete() {
		final int anyContentId = generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		pluginRegistry.onTagDelete(anyContentId, dbSession);
	}

	/**
	 * tests removeUserFromGroup
	 */
	@Test
	public void removeUserFromGroup() {
		generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		pluginRegistry.onRemoveUserFromGroup("username", 1, dbSession);
	}

	/**
	 * 
	 * SQL - methods
	 * ------------------------------------------------------------------------------
	 * 
	 * 
	 * The procedure of all following methods can be describes in the following
	 * way
	 * 
	 * 1) Search for each Method any Object (BibTex, Bookmark, etc.) with
	 * parameter: ContentID, Name, Hash, etc. 2) Build a param for this Object
	 * ans set the parameter 3) Count in the log_<OBJECT> table for the choosen
	 * Object, it must be 0 4) Do the Logging ( for example:
	 * this.bibTexDb.storePost(...); ) 5) Count it again in the log_<OBJECT>
	 * table, it must 1
	 * 
	 * All* methods which are calling by the generalDb access to the log_<OBJECT>
	 * table Example: countNewContentIdFromBibTex(...) access to the log_bibtex
	 * table * only "countTasIds()" is special
	 */

	/**
	 * After building a param, you need to build a Post for using the
	 * storePost() method
	 */

	/**
	 * tests onBibTexUpdateSQL
	 */
	@Test
	public void onBibTexUpdateSQL() {
		final String HASH = "00078c9690694eb9a56ca7866b5101c6"; // this is an INTER-hash
		final BibTexParam param = ParamUtils.getDefaultBibTexParam();
		param.setHash(HASH);
		param.setSimHash(HashID.INTER_HASH);
		final Post<BibTex> someBibTexPost = publicationDb.getPostsByHash(HASH, HashID.INTER_HASH, PUBLIC_GROUP_ID, 50, 0, this.dbSession).get(0);

		Integer currentContentId = generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		// +1 for the future contentId
		param.setNewContentId(currentContentId + 1);
		Integer result = generalDb.countNewContentIdFromBibTex(param, this.dbSession);
		assertEquals(0, result);
		publicationDb.updatePost(someBibTexPost, someBibTexPost.getResource().getIntraHash(), PostUpdateOperation.UPDATE_ALL, this.dbSession);

		currentContentId = generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		param.setNewContentId(currentContentId);
		result = generalDb.countNewContentIdFromBibTex(param, this.dbSession);
		assertEquals(1, result);
	}

	/**
	 * tests onBibTexDeleteSQL
	 */
	@Test
	public void onBibTexDeleteSQL() {
		final String HASH = "a0bda74e39a8f4c286a81fc66e77f69d"; // INTRAHASH
		// ContentId of the BibTex with the Hash above
		final int contentId = 711342;
		final BibTexParam param = ParamUtils.getDefaultBibTexParam();
		param.setRequestedContentId(contentId);
		param.setHash(HASH);
		param.setSimHash(HashID.INTRA_HASH);
		final Post<BibTex> someBibTexPost = publicationDb.getPostsByHash(HASH, HashID.INTER_HASH, PUBLIC_GROUP_ID, 50, 0, this.dbSession).get(0);

		int result = generalDb.countRequestedContentIdFromBibTex(param, this.dbSession);
		assertEquals(0, result);

		publicationDb.deletePost(someBibTexPost.getUser().getName(), HASH, this.dbSession);

		result = generalDb.countRequestedContentIdFromBibTex(param, this.dbSession);
		assertEquals(1, result);
	}

	/**
	 * tests onBookmarkUpdateSQL
	 */
	@Test
	public void onBookmarkUpdateSQL() {
		final String HASH = "0008bae834cc2af4a63fead1fd04b3e1";
		final BookmarkParam param = ParamUtils.getDefaultBookmarkParam();
		param.setHash(HASH);
		final Post<Bookmark> someBookmarkPost = bookmarkDb.getPostsByHash(HASH, HashID.INTRA_HASH, PUBLIC_GROUP_ID, 10, 0, this.dbSession).get(0);

		Integer currentContentId = generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		param.setNewContentId(currentContentId + 1); // +1, next content_id
		Integer result = generalDb.countNewContentIdFromBookmark(param, this.dbSession);
		assertEquals(0, result);

		bookmarkDb.updatePost(someBookmarkPost, HASH, PostUpdateOperation.UPDATE_ALL, this.dbSession);

		currentContentId = generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		param.setNewContentId(currentContentId);
		result = generalDb.countNewContentIdFromBookmark(param, this.dbSession);
		assertEquals(1, result);
	}

	/**
	 * tests onBookmarkDeleteSQL
	 */
	@Test
	public void onBookmarkDeleteSQL() {
		final String HASH = "00319006d9b0105704533e49661ffab6";
		// ContentId of the Bookmark with the Hash above
		final int contentId = 716849;
		final Post<Bookmark> someBookmarkPost = bookmarkDb.getPostsByHash(HASH, HashID.INTRA_HASH, PUBLIC_GROUP_ID, 10, 0, this.dbSession).get(0);

		final BookmarkParam param = ParamUtils.getDefaultBookmarkParam();
		param.setRequestedContentId(contentId);
		param.setHash(HASH);
		Integer result = generalDb.countRequestedContentIdFromBookmark(param, this.dbSession);
		assertEquals(0, result);

		bookmarkDb.deletePost(someBookmarkPost.getUser().getName(), HASH, this.dbSession);

		result = generalDb.countRequestedContentIdFromBookmark(param, this.dbSession);
		assertEquals(1, result);
	}

	/**
	 * For Testing the onTagDelete() method you must first build a BibTex and
	 * then delete it, the Tags will be deleted automatically by the delete
	 * methode of the BibTexDatabaseManager
	 * 
	 * 2nd assertion: countTasIds() count the number of TAS with the choosen
	 * ContentID in the original table: bibtex countLoggedTasIds() count it in
	 * the logging table: log_bibtex At the end it will be comparing
	 * (res_original, res_logging)
	 */
	@Test
	public void onTagDeleteSQL() {
		// final String HASH = "00078c9690694eb9a56ca7866b5101c6"; INTERHASH
		final String HASH = "a0bda74e39a8f4c286a81fc66e77f69d"; // INTRAHASH
		// ContentId of the BibTex with the Hash above
		final int contentId = 711342;
		final BibTexParam param = ParamUtils.getDefaultBibTexParam();
		param.setRequestedContentId(contentId);
		param.setHash(HASH);
		param.setSimHash(HashID.INTRA_HASH);
		final TagParam tagparam = ParamUtils.getDefaultTagParam();
		tagparam.setRequestedContentId(contentId);
		final Post<BibTex> someBibTexPost = publicationDb.getPostsByHash(HASH, HashID.INTRA_HASH, PUBLIC_GROUP_ID, 50, 0, this.dbSession).get(0);

		Integer res_original = generalDb.countTasIds(tagparam, this.dbSession);
		Integer result = generalDb.countRequestedContentIdFromBibTex(param, this.dbSession);
		assertEquals(0, result);

		publicationDb.deletePost(someBibTexPost.getUser().getName(), HASH, this.dbSession);

		result = generalDb.countRequestedContentIdFromBibTex(param, this.dbSession);
		assertEquals(1, result);
		Integer res_logging = generalDb.countLoggedTasIds(tagparam, this.dbSession);
		assertEquals(res_original, res_logging);
	}

	/**
	 * 2nd assertion: It is like in the onTagDeleteSQL() method
	 * getBibTexByConceptForUser() will be access before and after logging (in
	 * the original table!) At the end the tests checks if the TagRelation
	 * decreases in the orignial table
	 * 
	 */
	@Test
	public void onTagRelationDeleteSQL() {
		final List<TagIndex> tagIndex = new ArrayList<TagIndex>();
		final String user = "jaeschke", lower = "shannon", upper = "researcher";
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		tagIndex.add(new TagIndex("researcher", 1));
		final int countBefore = publicationDb.getPostsByConceptForUser(user, user, visibleGroupIDs, tagIndex, false, 100, 0, null, this.dbSession).size();
		final TagRelationParam trp = new TagRelationParam();
		trp.setOwnerUserName(user);
		trp.setLowerTagName(lower);
		trp.setUpperTagName(upper);
		Integer result = generalDb.countTagRelation(trp, this.dbSession);
		assertEquals(0, result);
		tagRelDb.deleteRelation(upper, lower, user, this.dbSession);
		final int countAfter = publicationDb.getPostsByConceptForUser(user, user, visibleGroupIDs, tagIndex, false, 100, 0, null, this.dbSession).size();
		result = generalDb.countTagRelation(trp, this.dbSession);
		assertTrue(countBefore > countAfter);
		assertEquals(1, result);
	}

	/**
	 * tests onRemoveUserFromGroupSQL
	 */
	@Test
	public void onRemoveUserFromGroupSQL() {
		final String user = "jaeschke", groupname = "kde";
		final GroupParam param = new GroupParam();
		param.setUserName(user);
		param.setGroupId(TESTGROUP1_ID);

		Integer result = generalDb.countGroup(param, this.dbSession);
		assertEquals(0, result);
		groupDb.removeUserFromGroup(groupname, user, this.dbSession);
		result = generalDb.countGroup(param, this.dbSession);
		assertEquals(1, result);
	}
}