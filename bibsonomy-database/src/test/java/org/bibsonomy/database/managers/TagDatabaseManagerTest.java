/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.managers;

import static org.bibsonomy.testutil.Assert.assertTagsByName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.testutil.ModelUtils;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests related to tags.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Kramer
 * @author Christian Schenk
 * @author Clemens Baier
 * 
 */
public class TagDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	private static final TagDatabaseManager tagDb = TagDatabaseManager.getInstance();
	
	/**
	 * tests {@link TagDatabaseManager#getAllTags(TagParam, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	@Ignore // TODO: insert test data for populartags table
	public void getAllTags() {
		final List<Tag> tags = tagDb.getAllTags(ParamUtils.getDefaultTagParam(), this.dbSession);
		assertEquals(10, tags.size());
	}
	
	/**
	 * tests {@link TagDatabaseManager#getTagsViewable(ConstantID, String, int, Order, int, int, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void getTagsViewable() {
		final List<Tag> tags = tagDb.getTagsViewable(ConstantID.ALL_CONTENT_TYPE, "testuser1", 4, Order.FREQUENCY, 10, 0, this.dbSession);
		assertEquals(3, tags.size());
	}
	
	/**
	 * tests {@link TagDatabaseManager#getRelatedTagsViewable(ConstantID, String, int, List, Order, int, int, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testGetTagsViewableRelated() {
		final List<TagIndex> tagIndex = new LinkedList<>();
		tagIndex.add(new TagIndex("finetune", 1));
		final List<Tag> relatedTags = tagDb.getRelatedTagsViewable(ConstantID.ALL_CONTENT_TYPE, "testuser1", 4, tagIndex, Order.ADDED, 10, 0, this.dbSession);
		assertEquals(1, relatedTags.size());
		
		tagIndex.add(new TagIndex("radio", 2));
		
		final List<Tag> relatedRelatedTag = tagDb.getRelatedTagsViewable(ConstantID.ALL_CONTENT_TYPE, "testuser1", 4, tagIndex, Order.ADDED, 10, 0, this.dbSession);
		assertEquals(0, relatedRelatedTag.size());
	}
	
	/**
	 * tests {@link TagDatabaseManager#insertTag(Tag, org.bibsonomy.database.common.DBSession)}
	 * with <code>null</code>
	 */
	@Test(expected = IllegalArgumentException.class)
	public void insertTagEmpty() {
		tagDb.insertTag(new Tag(), this.dbSession);
	}
	
	/**
	 * tests {@link TagDatabaseManager#insertTag(Tag, org.bibsonomy.database.common.DBSession)}
	 * with empty tag
	 */
	@Test(expected = IllegalArgumentException.class)
	public void insertTagEmpty2() {
		tagDb.insertTag(new Tag(""), this.dbSession);
	}
	
	/**
	 * tests {@link TagDatabaseManager#insertTag(Tag, org.bibsonomy.database.common.DBSession)}
	 * with whitespace
	 */
	@Test(expected = IllegalArgumentException.class)
	public void insertTagWhitespace() {
		tagDb.insertTag(new Tag("this taghasawhitepace"), this.dbSession);
	}

	/**
	 * tests getTagsByUser with order by Order.FREQUENCY
	 */
	@Test
	public void getTagsByUserOrderedByFrequency() {
		final TagParam tagParam = ParamUtils.getDefaultTagParam();
		tagParam.setRequestedUserName("testuser1");
		tagParam.setGroupId(0);
		tagParam.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
		tagParam.setOrder(Order.FREQUENCY);
		final List<Tag> tags = tagDb.getTagsByUser(tagParam, this.dbSession);
		int count = tags.get(0).getUsercount();
		for (final Tag tag : tags) {
			assertTrue(count + " is smaller than " + tag.getUsercount(), count >= tag.getUsercount());
			count = tag.getUsercount();
		}
	}
	
	/**
	 * tests {@link TagDatabaseManager#getTagsByUser(TagParam, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void getTagsByUser() {
		final TagParam param = new TagParam();
		param.setRequestedUserName("testuser3");
		param.setLimit(10);
		param.setOffset(0);
		param.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
		final List<Tag> bookmarkTags = tagDb.getTagsByUser(param, this.dbSession);
		assertEquals(3, bookmarkTags.size());
		
		param.setContentType(ConstantID.BIBTEX_CONTENT_TYPE);
		final List<Tag> publicationTags = tagDb.getTagsByUser(param, this.dbSession);
		assertEquals(0, publicationTags.size());
	}
	
	/**
	 * tests {@link TagDatabaseManager#getTagsByGroup(TagParam, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testGetTagsByGroup() {
		final TagParam param = new TagParam();
		param.setRequestedGroupName("testgroup2");
		param.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
		param.setLimit(10);
		param.setOffset(0);
		param.setGroupId(4);
		final List<Tag> tags = tagDb.getTagsByGroup(param, this.dbSession);
		assertEquals(4, tags.size());
		
		param.setContentType(ConstantID.BIBTEX_CONTENT_TYPE);
		final List<Tag> publTags = tagDb.getTagsByGroup(param, this.dbSession);
		assertEquals(2, publTags.size());
	}

	/**
	 * tests {@link TagDatabaseManager#getTagsByExpression(TagParam, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testGetTagsByExpression() {
		final TagParam tagParam = ParamUtils.getDefaultTagParam();
		tagParam.setLimit(1000);
		tagParam.setRegex("such%");
		tagParam.setRequestedUserName("testuser1");
		final List<Tag> tags = tagDb.getTagsByExpression(tagParam, this.dbSession);
		assertEquals(1, tags.size());
		assertEquals("suchmaschine", tags.get(0).getName());
	}

	/**
	 * tests {@link TagDatabaseManager#getTagDetails(User, String, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testGetTagDetails() {
		final Tag tag = tagDb.getTagDetails(new User("testuser1"), "google", this.dbSession);
		assertNotNull(tag);
		assertEquals(1, tag.getGlobalcount());
		assertEquals(1, tag.getUsercount());
	}
	
	/**
	 * tests {@link TagDatabaseManager#getTagsByBibtexHash(String, String, HashID, List, Order, int, int, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testGetTagsByPublicationHash() {
		final String loginUserName = "testuser1";
		final String hash = "097248439469d8f5a1e7fad6b02cbfcd";
		final List<Integer> visibleGroups = Collections.singletonList(Integer.valueOf(PUBLIC_GROUP_ID));
		final List<Tag> tags = tagDb.getTagsByPublicationHash(loginUserName, hash, HashID.INTER_HASH, visibleGroups, Order.ALPH, 10, 0, this.dbSession);
		assertEquals(2, tags.size());
	}

	/**
	 * tests {@link TagDatabaseManager#getTagsByPublicationHashForUser(String, String, String, HashID, List, int, int, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testGetTagsByPublicationHashForUser() {
		final String loginUserName = "testuser1";
		final String requestedUserName = "testuser1";
		final String hash = "097248439469d8f5a1e7fad6b02cbfcd";
		final List<Integer> visibleGroups = Collections.singletonList(Integer.valueOf(PUBLIC_GROUP_ID));
		final List<Tag> tags = tagDb.getTagsByPublicationHashForUser(loginUserName, requestedUserName, hash, HashID.INTER_HASH, visibleGroups, 10, 0, this.dbSession);
		assertEquals(3, tags.size());
	}

	/**
	 * tests {@link TagDatabaseManager#getTagsByBookmarkHash(String, String, List, Order, int, int, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testGetTagsByBookmarkHash() {
		final String loginUserName = "testuser2";
		final String hash = "7eda282d1d604c702597600a06f8a6b0";
		final List<Integer> visibleGroups = Collections.singletonList(Integer.valueOf(PUBLIC_GROUP_ID));
		final List<Tag> tags = tagDb.getTagsByBookmarkHash(loginUserName, hash, visibleGroups, Order.FREQUENCY, 10, 0, this.dbSession);
		assertEquals(2, tags.size());
	}

	/**
	 * tests {@link TagDatabaseManager#getTagsByBookmarkHashForUser(String, String, String, List, int, int, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testGetTagsByBookmarkHashForUser() {
		final String loginUserName = "testuser2";
		final String requestedUserName = loginUserName;
		final String hash = "7eda282d1d604c702597600a06f8a6b0";
		final List<Integer> visibleGroups = Collections.singletonList(Integer.valueOf(PUBLIC_GROUP_ID));
		final List<Tag> tags = tagDb.getTagsByBookmarkHashForUser(loginUserName, requestedUserName, hash, visibleGroups, 10, 0, this.dbSession);
		assertEquals(2, tags.size());
	}
	
	/**
	 * tests {@link TagDatabaseManager#getRelatedTags(TagParam, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	@Ignore // TODO: insert test data for tagtag table
	public void testGetRelatedTags() {
		final TagParam param = new TagParam();
		param.addTagName("suchmaschine");
		param.addGroup(Integer.valueOf(PUBLIC_GROUP_ID));
		param.setLimit(10);
		param.setOffset(0);
		final List<Tag> tags = tagDb.getRelatedTags(param, this.dbSession);
		assertEquals(3, tags.size());
	}
	
	/**
	 * tests {@link TagDatabaseManager#getRelatedTagsForUser(String, String, List, List, int, int, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testGetRelatedTagsForUser() {
		final TagParam param = new TagParam();
		param.addTagName("google");
		final List<Integer> visibleGroupIDs = Collections.singletonList(Integer.valueOf(PUBLIC_GROUP_ID));
		final List<Tag> tags = tagDb.getRelatedTagsForUser(null, "testuser1", param.getTagIndex(), visibleGroupIDs, 10, 0, this.dbSession);
		assertEquals(2, tags.size());
	}
	
	/**
	 * tests {@link TagDatabaseManager#getRelatedTagsForGroup(TagParam, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void testGetRelatedTagsForGroup() {
		final TagParam param = new TagParam();
		param.addTagName("suchmaschine");
		param.setRequestedGroupName("testgroup1");
		param.setLimit(100);
		final List<Tag> relatedTagsForGroup = tagDb.getRelatedTagsForGroup(param, this.dbSession);
		assertEquals(3, relatedTagsForGroup.size());
		
		param.addTagName("google");
		final List<Tag> relatedTagsForGroup2 = tagDb.getRelatedTagsForGroup(param, this.dbSession);
		assertEquals(1, relatedTagsForGroup2.size());
	}
	
	/**
	 * tests {@link TagDatabaseManager#updateTags(User, List, List, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void updateTags() {
		final User user = new User("testuser1");
		final List<Tag> tagsToReplace = new LinkedList<Tag>();
		tagsToReplace.add(new Tag("suchmaschine"));
		final List<Tag> replacementTags = new LinkedList<Tag>(ModelUtils.getTagSet("search", "engine"));

		tagDb.updateTags(user, tagsToReplace, replacementTags, this.dbSession);
		
		// TODO: implement test
	}

	/**
	 * tests {@link TagDatabaseManager#getTagsByBibtexkey(String, List, String, String, int, int, org.bibsonomy.database.common.DBSession)}
	 */
	@Test
	public void getTagsByBibtexkey() {
		/*
		 * fetch tags from public publication entries with the key "test bibtexkey"
		 * (should be 3)
		 */
		List<Integer> visibleGroups = Collections.singletonList(PUBLIC_GROUP_ID);
		
		String requestedUserName = null;
		String loginUserName = null;
		List<Tag> tags = tagDb.getTagsByBibtexkey("test bibtexKey", visibleGroups, requestedUserName, loginUserName, 10, 0, this.dbSession);
		assertTagsByName(ModelUtils.getTagSet("spam", "testbibtex", "testtag"), tags);

		/*
		 * fetch tags from public publication entries of testuser1 with the key
		 * "test bibtexkey" (should be 2)
		 */
		requestedUserName = "testuser1";
		tags = tagDb.getTagsByBibtexkey("test bibtexKey", visibleGroups, requestedUserName, loginUserName, 10, 0, this.dbSession);
		assertTagsByName(ModelUtils.getTagSet("testbibtex", "testtag"), tags);

		/*
		 * fetch tags from private publication of testuser1 with the key
		 * "test bibtexkey" (should be 1)
		 */
		requestedUserName = null;
		loginUserName = "testuser1";
		visibleGroups = Collections.singletonList(PRIVATE_GROUP_ID);
		tags = tagDb.getTagsByBibtexkey("test bibtexKey", visibleGroups, requestedUserName, loginUserName, 10, 0, this.dbSession);
		assertEquals(1, tags.size());
		assertEquals("privatebibtex", tags.get(0).getName());
	}
}