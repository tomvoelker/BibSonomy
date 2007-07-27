package org.bibsonomy.database.plugin.plugins;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.TagRelationDatabaseManagerTest;
import org.bibsonomy.database.params.*;
import org.bibsonomy.database.params.beans.TagRelationParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.testutil.ModelUtils;
import org.bibsonomy.testutil.ParamUtils;
import org.bibsonomy.util.ValidationUtils;
import org.junit.Test;

/**
 * This TestCase tests the syntax of the methods  of the class Logging.java.
 * The SQL Testmethods tests ifthe SQL Statements of the Loggin.java class is
 * working correctly by checking the count of the content id.
 * 
 * @author Anton Wilhelm
 */

public class LoggingTest extends AbstractDatabaseManagerTest {
	
	private DatabasePluginRegistry plugins;
	private Integer anyContentId;

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
	public void onDeleteUserfromGroup() {
		plugins = DatabasePluginRegistry.getInstance();
		anyContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		plugins.onDeleteUserfromGroup("username", 1, dbSession);
	}
	
	/**
	 * Use BibTex with Hash: BIB_TEST_HASH
	 * Get contentId, which is generated when the storePost Methods access
	 * and count this contentId in table log_bibtex. When result = 1, logging was successfull
	 */
	@Test
	public void onBibTexUpdateSQL() {
		final String BIB_TEST_HASH = "00078c9690694eb9a56ca7866b5101c6";
		final BibTexParam param = this.bibtexParam;
		param.setHash(BIB_TEST_HASH);
		param.setSimHash(HashID.INTRA_HASH);
		final Post<BibTex> someBibTexPost = this.bibTexDb.getBibTexByHash(param, this.dbSession).get(0);
		this.bibTexDb.storePost(someBibTexPost.getUser().getName(), someBibTexPost, BIB_TEST_HASH, true, this.dbSession);
		
		Integer currentContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		param.setNewContentId(currentContentId);
		Integer result = this.generalDb.countNewContentIdFromBibTex(param, this.dbSession);
		assertEquals(1, result);
	}
	
	
	@Test
	public void onBibTexDeleteSQL() {

		final String BIB_TEST_HASH = "00078c9690694eb9a56ca7866b5101c6";
		// ContentId of the BibTex with the Hash above
		final int BIB_TEST_CONTENTID = 711342;
		final BibTexParam param = this.bibtexParam;
		param.setRequestedContentId(BIB_TEST_CONTENTID);
		param.setHash(BIB_TEST_HASH);
		param.setSimHash(HashID.INTRA_HASH);
		final Post<BibTex> someBibTexPost = this.bibTexDb.getBibTexByHash(param, this.dbSession).get(0);
		
		Integer result = this.generalDb.countRequestedContentIdFromBibTex(param, this.dbSession);
		assertEquals(0, result);
		this.bibTexDb.deletePost(someBibTexPost.getUser().getName(), BIB_TEST_HASH, this.dbSession);
		result = this.generalDb.countRequestedContentIdFromBibTex(param, this.dbSession);
		assertEquals(1, result);
	}
	
	@Test
	public void onBookmarkUpdateSQL() {
		final String BOOKMARK_TEST_HASH = "0008bae834cc2af4a63fead1fd04b3e1";
		final BookmarkParam param = this.bookmarkParam;
		param.setHash(BOOKMARK_TEST_HASH);
		param.setSimHash(HashID.INTRA_HASH);
		final Post<Bookmark> someBookmarkPost = this.bookmarkDb.getBookmarkByHash(param, this.dbSession).get(0);
		this.bookmarkDb.storePost(someBookmarkPost.getUser().getName(), someBookmarkPost, BOOKMARK_TEST_HASH, true, this.dbSession);
		
		Integer currentContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		param.setNewContentId(currentContentId);
		Integer result = this.generalDb.countNewContentIdFromBookmark(param, this.dbSession);
		assertEquals(1, result);
	}
	
	@Test
	public void onBookmarkDeleteSQL() {
		final String BOOKMARK_TEST_HASH = "00319006d9b0105704533e49661ffab6";
		// ContentId of the Bookmark with the Hash above
		final int BOOKMARK_TEST_CONTENTID = 716849;
		final BookmarkParam param = this.bookmarkParam;
		param.setRequestedContentId(BOOKMARK_TEST_CONTENTID);
		param.setHash(BOOKMARK_TEST_HASH);
		//param.setSimHash(HashID.INTRA_HASH);
		final Post<Bookmark> someBookmarkPost = this.bookmarkDb.getBookmarkByHash(param, this.dbSession).get(0);
		
		this.bookmarkDb.deletePost(someBookmarkPost.getUser().getName(), BOOKMARK_TEST_HASH, this.dbSession);
		
		Integer result = this.generalDb.countRequestedContentIdFromBookmark(param, this.dbSession);
		assertEquals(1, result);
	}
	//@Test
	public void onTagRelationDeleteSQL() {
		/* 
		// this example don't work!
		// TagRelations with relationId 10 will be tested
		final String TEST_USER =  "schmitz";
		final String TEST_LOWER = "plane";
		final String TEST_UPPER = "woodworking";
		
		final int countBefore = bibTexDb.getBibTexByConceptForUser(TEST_USER, TEST_UPPER, TEST_USER, 100, 0, this.dbSession).size();
		this.tagRelDb.deleteRelation(TEST_UPPER, TEST_LOWER, TEST_USER, this.dbSession);
		final int countAfter = bibTexDb.getBibTexByConceptForUser(TEST_USER, TEST_UPPER, TEST_USER, 100, 0, this.dbSession).size();
		assertTrue(countBefore > countAfter);
		*//*
		
		final int countBefore = bibTexDb.getBibTexByConceptForUser("jaeschke", "researcher", "jaeschke", 100, 0, this.dbSession).size();
		final int countLogBefore = this.generalDb.getBibTexByConceptForUser("jaeschke", "researcher", "jaeschke", 100, 0, this.dbSession).size();
		this.tagRelDb.deleteRelation("researcher", "shannon", "jaeschke", this.dbSession);
		final int countAfter = bibTexDb.getBibTexByConceptForUser("jaeschke", "researcher", "jaeschke", 100, 0, this.dbSession).size();
		final int countLogAfter = this.generalDb.getBibTexByConceptForUser("jaeschke", "researcher", "jaeschke", 100, 0, this.dbSession).size();
		assertTrue(countBefore > countAfter);
		assertTrue(countLogBefore < countLogAfter);
		*/
	}
	
	//@Test
	public void onTagDeleteSQL() {
		
	}
	
	//@Test
	public void onDeleteUserfromGroupSQL() {
		
	}
	
}
