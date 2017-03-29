/**
 * BibSonomy - A blue social bookmark and publication sharing system.
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
package org.bibsonomy.lucene.index;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.apache.lucene.document.Document;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.lucene.index.converter.LuceneResourceConverter;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.search.LucenePost;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.testutil.CommonModelUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author fei
 */
public class LuceneResourceIndexTest {
	private static LuceneResourceConverter<BibTex> bibTexConverter;
	private static LuceneResourceConverter<Bookmark> bookmarkConverter;
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUp() {		
		// initialize
		bibTexConverter = (LuceneResourceConverter<BibTex>) LuceneSpringContextWrapper.getBeanFactory().getBean("lucenePublicationConverter");
		bookmarkConverter = (LuceneResourceConverter<Bookmark>) LuceneSpringContextWrapper.getBeanFactory().getBean("luceneBookmarkConverter");
	}
	
	@Test
	public void testCache() throws PersonListParserException {
		final LucenePost<Bookmark> bmPost = generateBookmarkDatabaseManagerTestPost();
		final LucenePost<BibTex> bibPost = generateBibTexDatabaseManagerTestPost(GroupID.PUBLIC);
		
		bmPost.setContentId(0);
		bibPost.setContentId(0);
		
		final Document bmDoc = (Document) bookmarkConverter.readPost(bmPost);
		final Document bibDoc = (Document) bibTexConverter.readPost(bibPost);
		
		final LuceneResourceIndex<Bookmark> bmIndex = new LuceneResourceIndex<Bookmark>();
		bmIndex.setIndexId(0);
		bmIndex.setResourceClass(Bookmark.class);
		final LuceneResourceIndex<BibTex> bibIndex  = new LuceneResourceIndex<BibTex>();
		bibIndex.setIndexId(0);
		bibIndex.setResourceClass(BibTex.class);
		
		bmIndex.insertDocument(bmDoc);
		bmIndex.insertDocument(bmDoc);
		bibIndex.insertDocument(bibDoc);
		bibIndex.insertDocument(bibDoc);
		
		assertEquals(1, bmIndex.getPostsToInsert().size());
		assertEquals(1, bibIndex.getPostsToInsert().size());
		
		final int postSize = 50;
		for (int i = 1; i < postSize; i++) {
			bmPost.setContentId(i);
			bibPost.setContentId(i);

			bmIndex.insertDocument((Document) bookmarkConverter.readPost(bmPost));
			bibIndex.insertDocument((Document) bibTexConverter.readPost(bibPost));
		}

		assertEquals(postSize, bmIndex.getPostsToInsert().size());
		assertEquals(postSize, bibIndex.getPostsToInsert().size());
	}
	
	/**
	 * generate a BibTex Post, can't call setBeanPropertiesOn() because private
	 * so copy & paste the setBeanPropertiesOn() into this method
	 * @throws PersonListParserException 
	 */
	private LucenePost <BibTex> generateBibTexDatabaseManagerTestPost(final GroupID groupID) throws PersonListParserException {
		
		final LucenePost<BibTex> LucenePost = new LucenePost<BibTex>();

		final Group group = new Group(groupID);
	
		LucenePost.getGroups().add(group);

		Tag tag = new Tag();
		tag.setName("tag1");
		LucenePost.getTags().add(tag);
		tag = new Tag();
		tag.setName("tag2");
		LucenePost.getTags().add(tag);
		tag = new Tag();
		tag.setName("testTag");
		LucenePost.getTags().add(tag);

		LucenePost.setContentId(null); // will be set in storePost()
		LucenePost.setDescription("luceneTestPost");
		LucenePost.setDate(new Date(System.currentTimeMillis()));
		final User user = new User();
		CommonModelUtils.setBeanPropertiesOn(user);
		user.setName("testuser1");
		user.setRole(Role.NOBODY);
		LucenePost.setUser(user);
		final BibTex resource;

		
		final BibTex bibtex = new BibTex();
		CommonModelUtils.setBeanPropertiesOn(bibtex);
		bibtex.setCount(0);		
		bibtex.setEntrytype("inproceedings");
		bibtex.setAuthor(PersonNameUtils.discoverPersonNames("MegaMan and Lucene GigaWoman"));
		bibtex.setEditor(PersonNameUtils.discoverPersonNames("Peter Silie"));
		bibtex.setTitle("bibtex insertpost test");
		resource = bibtex;
		
		String title, year, journal, booktitle, volume, number = null;
		title = "title "+ (Math.round(Math.random()*Integer.MAX_VALUE));
		year = "test year";
		journal = "test journal";
		booktitle = "test booktitle";
		volume = "test volume";
		number = "test number";
		bibtex.setTitle(title);
		bibtex.setYear(year);
		bibtex.setJournal(journal);
		bibtex.setBooktitle(booktitle);
		bibtex.setVolume(volume);
		bibtex.setNumber(number);
		bibtex.setScraperId(-1);
		bibtex.setType("2");
		bibtex.recalculateHashes();
		LucenePost.setResource(resource);
		LucenePost.setLastLogDate(new Date());
		return LucenePost;
	}
	
	/**
	 * generate a Bookmark LucenePost, can't call setBeanPropertiesOn() because private
	 * so copy & paste the setBeanPropertiesOn() into this method
	 */
	private LucenePost <Bookmark> generateBookmarkDatabaseManagerTestPost() {
		
		final LucenePost<Bookmark> LucenePost = new LucenePost<Bookmark>();

		final Group group = new Group();
		group.setDescription(null);
		group.setName("public");
		group.setGroupId(GroupID.PUBLIC.getId());
		LucenePost.getGroups().add(group);

		Tag tag = new Tag();
		tag.setName("tag1");
		LucenePost.getTags().add(tag);
		tag = new Tag();
		tag.setName("tag2");
		LucenePost.getTags().add(tag);

		LucenePost.setContentId(null); // will be set in storePost()
		LucenePost.setDescription("Some description");
		LucenePost.setDate(new Date());
		final User user = new User();
		CommonModelUtils.setBeanPropertiesOn(user);
		user.setName("testuser1");
		user.setRole(Role.NOBODY);
		LucenePost.setUser(user);
		final Bookmark resource;

		
		final Bookmark bookmark = new Bookmark();
		bookmark.setCount(0);
		//bookmark.setIntraHash("e44a7a8fac3a70901329214fcc1525aa");
		bookmark.setTitle("test" + (Math.round(Math.random() * Integer.MAX_VALUE)));
		bookmark.setUrl("http://www.testurl.orgg");
		bookmark.recalculateHashes();
		resource = bookmark;
		
		LucenePost.setResource(resource);
		LucenePost.setLastLogDate(new Date());
		return LucenePost;
	}
}
