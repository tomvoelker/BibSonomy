package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.model.Tag;
import org.bibsonomy.testutil.DatabasePluginMock;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class TagRelationDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private static final Logger log = Logger.getLogger(TagRelationDatabaseManagerTest.class);
	private Tag tag;
	private Tag subTag;
	private Tag superTag;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		this.tag = new Tag();
		this.tag.setName(this.getClass().getName());
		this.subTag = new Tag();
		this.subTag.setName(this.getClass().getName() + "-sub");
		this.superTag = new Tag();
		this.superTag.setName(this.getClass().getName() + "-super");
		this.tag.setSubTags(Arrays.asList(new Tag[] { this.subTag }));
		this.subTag.setSuperTags(Arrays.asList(new Tag[] { this.tag }));
		this.tag.setSuperTags(Arrays.asList(new Tag[] { this.superTag }));
		this.superTag.setSubTags(Arrays.asList(new Tag[] { this.tag }));
	}

	@Test
	public void testInsertNewRelations() {
		final Integer newId1 = this.generalDb.getNewContentId(ConstantID.IDS_TAGREL_ID, this.dbSession);
		this.tagRelDb.insertRelations(tag, this.getClass().getName() + "-user", this.dbSession);
		final Integer newId2 = this.generalDb.getNewContentId(ConstantID.IDS_TAGREL_ID, this.dbSession);
		assertEquals(newId1 + 3, newId2);
	}

	@Test
	public void testInsertExistingRelations() {
		final Integer newId1 = this.generalDb.getNewContentId(ConstantID.IDS_TAGREL_ID, this.dbSession);
		this.tagRelDb.insertRelations(tag, this.getClass().getName() + "-user", this.dbSession);
		this.tagRelDb.insertRelations(tag, this.getClass().getName() + "-user", this.dbSession);
		final Integer newId2 = this.generalDb.getNewContentId(ConstantID.IDS_TAGREL_ID, this.dbSession);
		assertEquals(newId1 + 3, newId2);
	}

	@Test
	public void testDeleteRelation() {
		// FIXME: this boilerplate code could be removed with a DI-framework (i.e. next three lines)
		final DatabasePluginMock plugin = new DatabasePluginMock();
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(plugin);
		assertFalse(plugin.isOnTagRelationDelete());

		final int countBefore = bibTexDb.getBibTexByConceptForUser("jaeschke", "researcher", "jaeschke", 100, 0, this.dbSession).size();
		this.tagRelDb.deleteRelation("researcher", "shannon", "jaeschke", this.dbSession);
		final int countAfter = bibTexDb.getBibTexByConceptForUser("jaeschke", "researcher", "jaeschke", 100, 0, this.dbSession).size();
		log.debug("before: " + countBefore);
		log.debug("after: " + countAfter);
		assertTrue(countBefore > countAfter);

		assertTrue(plugin.isOnTagRelationDelete());
	}
}