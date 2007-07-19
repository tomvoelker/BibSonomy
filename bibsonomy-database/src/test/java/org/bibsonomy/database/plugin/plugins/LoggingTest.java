package org.bibsonomy.database.plugin.plugins;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.testutil.ModelUtils;
import org.bibsonomy.testutil.ParamUtils;
import org.bibsonomy.util.ValidationUtils;
import org.junit.Test;



public class LoggingTest extends AbstractDatabaseManagerTest {
	
	private DatabasePluginRegistry plugins;
	private Integer anyContentId;
	
	@Test
	public void onBibTexInsert() {
		plugins = DatabasePluginRegistry.getInstance();
		anyContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		plugins.onBibTexInsert(anyContentId, dbSession);
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
	public void onBookmarkInsert() {
		plugins = DatabasePluginRegistry.getInstance();
		anyContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		plugins.onBookmarkInsert(anyContentId, dbSession);
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
	
	
	//@Test
	public void onBibTexInsertSQL() {
		plugins = DatabasePluginRegistry.getInstance();
		anyContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		final Post<BibTex> toInsert = ModelUtils.generatePost(BibTex.class);
		String hash = toInsert.getResource().getIntraHash();
		this.bibTexDb.storePost(toInsert.getUser().getName(), toInsert, hash, false, this.dbSession);
		Integer currentContentId = this.generalDb.getCurrentContentId(ConstantID.IDS_CONTENT_ID, this.dbSession);
		
		final BibTexParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, toInsert.getUser().getName(), GroupingEntity.USER, toInsert.getUser().getName(), Arrays.asList(new String[] { ModelUtils.class.getName(), "hurz" }), "", null, 0, 50);
		param.setRequestedContentId(currentContentId);
		Integer result = this.generalDb.onBibTexInsertSQL(param, this.dbSession);
		assertEquals(1, result);
	}
	
	
	

}
