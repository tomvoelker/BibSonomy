package org.bibsonomy.database.plugin.plugins;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.params.*;
import org.bibsonomy.database.params.beans.TagRelationParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

import org.junit.Test;

/**
 * The first methods test the syntax of the methods of the class: Logging.java.
 * The SQL methods test if the SQL Statements of the Logging.java class work
 * semantically correct
 * 
 * @author Anton Wilhelm
 */

public class LoggingTest extends AbstractDatabaseManagerTest {
	
	private DatabasePluginRegistry plugins;
	private Integer anyContentId;

	@Test
	public void addLoggingPlugin() {
		plugins = DatabasePluginRegistry.getInstance();
		plugins.clearPlugins();
		plugins.add( new Logging());
	}
	
	@Test
	public void onBibTexDelete() {
		plugins = DatabasePluginRegistry.getInstance();
		anyContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		plugins.onBibTexDelete(anyContentId, dbSession);
	}
	
	@Test
	public void onBibTexUpdate() {
		plugins = DatabasePluginRegistry.getInstance();
		anyContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		plugins.onBibTexUpdate(anyContentId, anyContentId-1, dbSession);
	}
	
	@Test
	public void onBookmarkDelete() {
		plugins = DatabasePluginRegistry.getInstance();
		anyContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		plugins.onBookmarkDelete(anyContentId, dbSession);
	}
	
	@Test
	public void onBookmarkUpdate() {
		plugins = DatabasePluginRegistry.getInstance();
		anyContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		plugins.onBookmarkUpdate(anyContentId, anyContentId-1, dbSession);
	}
	
	@Test
	public void onTagRelationDelete() {
		plugins = DatabasePluginRegistry.getInstance();
		anyContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		plugins.onTagRelationDelete("upperTagName", "lowerTagName", "userName", dbSession);
	}
	
	@Test
	public void onTagDelete() {
		plugins = DatabasePluginRegistry.getInstance();
		anyContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		plugins.onTagDelete(anyContentId, dbSession);
	}
	
	@Test
	public void removeUserFromGroup() {
		plugins = DatabasePluginRegistry.getInstance();
		anyContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		plugins.onRemoveUserFromGroup("username", 1, dbSession);
	}
	
	/**
	 * 
	 *  SQL - methods ------------------------------------------------------------------------------
	 * 
	 * 
	 * The procedure of all following methods can be describes in the following way
	 * 
	 * 1) Search for each Method any Object (BibTex, Bookmark, etc.) with parameter:
	 *    ContentID, Name, Hash, etc.
	 * 2) Build a param for this Object ans set the parameter
	 * 3) Count in the log_<OBJECT> table for the choosen Object, it must be 0
	 * 4) Do the Logging ( for example: this.bibTexDb.storePost(...); )
	 * 5) Count it again in the log_<OBJECT> table, it must 1
	 * 
	 * All* methods which are calling by the generalDb access to the log_<OBJECT> table
	 * Example: countNewContentIdFromBibTex(...) access to the log_bibtex table
	 * 
	 * * only "countTasIds()" is special
	 */
	
	
	/**
	 * After building a param, you need to build a Post for using the storePost() method
	 */
	
	@Test
	public void onBibTexUpdateSQL() {
		final String HASH = "00078c9690694eb9a56ca7866b5101c6"; // this is an INTER-hash
		final BibTexParam param = this.bibtexParam;
		param.setHash(HASH);
		param.setRequestedSimHash(HashID.INTER_HASH);
		final Post<BibTex> someBibTexPost = this.bibTexDb.getBibTexByHash(param, this.dbSession).get(0);
		
		Integer currentContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		param.setNewContentId(currentContentId+1); // +1 for the future contentId
		Integer result = this.generalDb.countNewContentIdFromBibTex(param, this.dbSession);
		assertEquals(0, result);
		this.bibTexDb.storePost(someBibTexPost.getUser().getName(), someBibTexPost, someBibTexPost.getResource().getIntraHash(), true, this.dbSession);
		
		currentContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		param.setNewContentId(currentContentId);
		result = this.generalDb.countNewContentIdFromBibTex(param, this.dbSession);
		assertEquals(1, result);
	}
	
	
	@Test
	public void onBibTexDeleteSQL() {
		// final String HASH = "00078c9690694eb9a56ca7866b5101c6"; INTERHASH
		final String HASH = "a0bda74e39a8f4c286a81fc66e77f69d"; // INTRAHASH
		// ContentId of the BibTex with the Hash above
		final int CONTENTID = 711342;
		final BibTexParam param = this.bibtexParam;
		param.setRequestedContentId(CONTENTID);
		param.setHash(HASH);
		param.setRequestedSimHash(HashID.INTRA_HASH);
		final Post<BibTex> someBibTexPost = this.bibTexDb.getBibTexByHash(param, this.dbSession).get(0);
		
		Integer result = this.generalDb.countRequestedContentIdFromBibTex(param, this.dbSession);
		assertEquals(0, result);
		
		this.bibTexDb.deletePost(someBibTexPost.getUser().getName(), HASH, this.dbSession);
		
		result = this.generalDb.countRequestedContentIdFromBibTex(param, this.dbSession);
		assertEquals(1, result);
	}
	
	@Test
	public void onBookmarkUpdateSQL() {
		final String HASH = "0008bae834cc2af4a63fead1fd04b3e1";
		final BookmarkParam param = this.bookmarkParam;
		param.setHash(HASH);
		final Post<Bookmark> someBookmarkPost = this.bookmarkDb.getBookmarkByHash(param, this.dbSession).get(0);
		
		Integer currentContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		param.setNewContentId(currentContentId+1); // +1, next content_id
		Integer result = this.generalDb.countNewContentIdFromBookmark(param, this.dbSession);
		assertEquals(0, result);
		
		this.bookmarkDb.storePost(someBookmarkPost.getUser().getName(), someBookmarkPost, HASH, true, this.dbSession);
		
		currentContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		param.setNewContentId(currentContentId);
		result = this.generalDb.countNewContentIdFromBookmark(param, this.dbSession);
		assertEquals(1, result);
	}
	
	@Test
	public void onBookmarkDeleteSQL() {
		final String HASH = "00319006d9b0105704533e49661ffab6";
		// ContentId of the Bookmark with the Hash above
		final int CONTENTID = 716849;
		final BookmarkParam param = this.bookmarkParam;
		param.setRequestedContentId(CONTENTID);
		param.setHash(HASH);
		final Post<Bookmark> someBookmarkPost = this.bookmarkDb.getBookmarkByHash(param, this.dbSession).get(0);
		
		Integer result = this.generalDb.countRequestedContentIdFromBookmark(param, this.dbSession);
		assertEquals(0, result);
		
		this.bookmarkDb.deletePost(someBookmarkPost.getUser().getName(), HASH, this.dbSession);
		
		result = this.generalDb.countRequestedContentIdFromBookmark(param, this.dbSession);
		assertEquals(1, result);
	}
	
	/**
	 * For Testing the onTagDelete() method you must first build a 
	 * BibTex and then delete it, the Tags will be deleted automatically
	 * by the delete methode of the BibTexDatabaseManager
	 * 
	 * 2nd assertion:
	 * countTasIds() count the number of TAS with the choosen ContentID
	 * in the original table: bibtex
	 * countLoggedTasIds() count it in the logging table: log_bibtex
	 * At the end it will be comparing (res_original, res_logging)
	 */
	@Test
	public void onTagDeleteSQL() {
		// final String HASH = "00078c9690694eb9a56ca7866b5101c6"; INTERHASH
		final String HASH = "a0bda74e39a8f4c286a81fc66e77f69d"; // INTRAHASH
		// ContentId of the BibTex with the Hash above
		final int CONTENTID = 711342;
		final BibTexParam param = this.bibtexParam;
		param.setRequestedContentId(CONTENTID);
		param.setHash(HASH);
		param.setRequestedSimHash(HashID.INTRA_HASH);
		final TagParam tagparam = this.tagParam;
		tagparam.setRequestedContentId(CONTENTID);	
		final Post<BibTex> someBibTexPost = this.bibTexDb.getBibTexByHash(param, this.dbSession).get(0);
		
		Integer res_original = this.generalDb.countTasIds(tagparam, this.dbSession);
		Integer result = this.generalDb.countRequestedContentIdFromBibTex(param, this.dbSession);
		assertEquals(0, result);
		
		this.bibTexDb.deletePost(someBibTexPost.getUser().getName(), HASH, this.dbSession);
		
		result = this.generalDb.countRequestedContentIdFromBibTex(param, this.dbSession);
		assertEquals(1, result);
		Integer res_logging = this.generalDb.countLoggedTasIds(tagparam, this.dbSession);
		assertEquals(res_original, res_logging);
		
	}
	
	
	/**
	 * 2nd assertion:
	 * It is like in the onTagDeleteSQL() method
	 * getBibTexByConceptForUser() will be access before and after logging (in the original table!)
	 * At the end the tests checks if the TagRelation decreases in the orignial table
	 * 
	 */
	@Test
	public void onTagRelationDeleteSQL() {
		final String USER = "jaeschke", LOWER = "shannon", UPPER = "researcher";
		final int countBefore = bibTexDb.getBibTexByConceptForUser(USER, UPPER, USER, 100, 0, this.dbSession).size();
		final TagRelationParam trp = new TagRelationParam();
		trp.setOwnerUserName(USER);
		trp.setLowerTagName(LOWER);
		trp.setUpperTagName(UPPER);
		Integer result = this.generalDb.countTagRelation(trp, this.dbSession);
		assertEquals(0, result);
		this.tagRelDb.deleteRelation(UPPER, LOWER, USER, this.dbSession);
		final int countAfter = bibTexDb.getBibTexByConceptForUser(USER, UPPER, USER, 100, 0, this.dbSession).size();
		result = this.generalDb.countTagRelation(trp, this.dbSession);
		assertTrue(countBefore > countAfter);
		assertEquals(1, result);
	}
	
	@Test
	public void onRemoveUserFromGroupSQL() {
		final String USER = "jaeschke", GROUPNAME = "kde";
		final int GROUPID = 3;
		final GroupParam param = new GroupParam();
		param.setUserName(USER);
		param.setGroupId(GROUPID);
		
		Integer result = this.generalDb.countGroup(param, this.dbSession);
		assertEquals(0, result);
		this.groupDb.removeUserFromGroup(GROUPNAME, USER, this.dbSession);
		result = this.generalDb.countGroup(param, this.dbSession);
		assertEquals(1, result);
	}
	
}
