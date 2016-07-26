/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.ModelUtils;
import org.bibsonomy.util.UrlUtils;
import org.junit.Test;

/**
 * @author rja
 */
public class URLGeneratorTest {

	private static String projectHome = "http://www.bibsonomy.org/";
	private static URLGenerator ug = new URLGenerator(projectHome);
	
	@Test
	public void testGetAbsoluteUrl() throws Exception{
		final String ext = "user/jaescke";
		final String expected = projectHome + ext;
		assertEquals(expected, ug.getAbsoluteUrl(ext));
	}

	@Test
	public void testGetAdminUrlByString() throws Exception{
		//test without username
		String expected = projectHome + "admin";
		assertEquals(expected, ug.getAdminUrlByName(""));
		
		//with username
		expected += "/jaeschke";
		assertEquals(expected, ug.getAdminUrlByName("jaeschke"));
	}

	@Test
	public void testGetAuthorUrlByName() throws Exception{
		final String expected = projectHome + "author/jaeschke";
		assertEquals(expected, ug.getAuthorUrlByName("jaeschke"));
	}
	
	@Test
	public void testGetClipboardUrl() throws Exception{
		final String expected = projectHome + "clipboard";
		assertEquals(expected, ug.getClipboardUrl());
	}
	
	@Test
	public void testGetBookmarkUrl() throws Exception{
		final Post<Bookmark> post = ModelUtils.generatePost(Bookmark.class);
		final Bookmark bm = post.getResource();
		final User user = post.getUser();
		
		//Test without user
		String expected = projectHome + "url/" + bm.getInterHash();
		assertEquals(expected, ug.getBookmarkUrl(bm, (User) null));
		
		//Test with user
		expected = projectHome + "url/" + bm.getIntraHash() + "/" + UrlUtils.safeURIEncode(user.getName());
		assertEquals(expected, ug.getBookmarkUrl(bm, user));
		
	}
	
	@Test
	public void testGetPublicationCommunityUrlByInterHash() {
		String publicationCommunityUrlByInterHash = ug.getPublicationCommunityUrlByInterHash("testinterhash");
		assertEquals(projectHome + "bibtex/testinterhash", publicationCommunityUrlByInterHash);
	}
	
	@Test
	public void testGetBookmarkUrlByIntraHash() throws Exception{
		final Bookmark bm = ModelUtils.generatePost(Bookmark.class).getResource();
		
		final String expected = projectHome + "url/" + bm.getIntraHash();
		assertEquals(expected, ug.getBookmarkUrl(bm, (User) null));
	}

	@Test
	public void testGetBookmarkUrlByIntraHashAndUsername() throws Exception{
		final Post<Bookmark> post = ModelUtils.generatePost(Bookmark.class);
		final Bookmark bm = post.getResource();
		final String userName = post.getUser().getName();
		final String intraHash = bm.getIntraHash();
		String url = projectHome + "url/" + intraHash + "/" +
				     UrlUtils.safeURIEncode(userName);
		assertEquals(url, ug.getBookmarkUrlByIntraHashAndUsername(intraHash, 
															      userName));
	}

	@Test
	public void testGetConceptsUrlByString() throws Exception{
		//test without username
		String expected = projectHome + "concepts";
		assertEquals(expected, ug.getConceptsUrlByString(""));
		
		//test with username
		expected += "/jaescke";
		assertEquals(expected, ug.getConceptsUrlByString("jaescke"));
	}

	@Test
	public void testGetConceptUrlByUserNameAndTagName() throws Exception{
		final String expected = projectHome + "concept/user/jaescke/kde";
		assertEquals(expected, ug.getConceptUrlByUserNameAndTagName("jaescke", "kde"));
	}

	@Test
	public void testGetFollowersUrl() throws Exception{
		final String expected = projectHome + "followers";
		assertEquals(expected, ug.getFollowersUrl());
	}

	@Test
	public void testGetFriendUrlByUserName() throws Exception{
		final String expected = projectHome + "friend/jaescke";
		assertEquals(expected, ug.getFriendUrlByUserName("jaescke"));
	}

	@Test
	public void testGetFriendUrlByUserNameAndTagName() throws Exception{
		final String expected = projectHome + "friend/jaescke/kde";
		assertEquals(expected, ug.getFriendUrlByUserNameAndTagName("jaescke", "kde"));
	}
	
	@Test
		public void testGetCommunityPublicationUrlByInterHash() throws Exception{
			final GoldStandardPublication gst = ModelUtils.generatePost(GoldStandardPublication.class).getResource();
			final String expected = projectHome + "bibtex/" + gst.getInterHash();
			assertEquals(expected, ug.getCommunityPublicationUrlByInterHash(gst.getInterHash()));
		}
	
	@Test
		public void testGetCommunityPublicationUrlByInterHashAndUsername() throws Exception{
			final Post<GoldStandardPublication> post = ModelUtils.generatePost(GoldStandardPublication.class);
			final BibTex gst = post.getResource();
			final String userName = post.getUser().getName();
			
			String expected = projectHome + "bibtex/" + gst.getInterHash() + "/" +
							  userName;
			assertEquals(expected, ug.getCommunityPublicationUrlByInterHashUsernameAndSysUrl(gst.getInterHash(), userName, projectHome));
		}

	@Test
	public void testGetGroupUrlByGroupName() throws Exception{
		final String expected = projectHome + "group/kde";
		assertEquals(expected, ug.getGroupUrlByGroupName("kde"));
	}

	@Test
	public void testGetGroupUrlByGroupNameAndTagName() throws Exception{
		final String expected = projectHome + "group/kde/kde";
		assertEquals(expected, ug.getGroupUrlByGroupNameAndTagName("kde", "kde"));
	}

	@Test
	public void testGetLoginUrl() throws Exception{
		final String expected = projectHome + "login";
		assertEquals(expected, ug.getLoginUrl());
	}

	@Test
	public void testGetMyBibTexUrl() throws Exception{
		final String expected = projectHome + "myBibTex";
		assertEquals(expected, ug.getMyBibTexUrl());
	}

	@Test
	public void testGetMyDocumentsUrl() throws Exception{
		final String expected = projectHome + "myDocuments";
		assertEquals(expected, ug.getMyDocumentsUrl());
	}

	@Test
	public void testGetMyDuplicatesUrl() throws Exception{
		final String expected = projectHome + "myDuplicates";
		assertEquals(expected, ug.getMyDuplicatesUrl());
	}

	@Test
	public void testGetMyHomeUrl() throws Exception{
		final String expected = projectHome + "myHome";
		assertEquals(expected, ug.getMyHomeUrl());
	}

	@Test
	public void testGetMyRelationsUrl() throws Exception{
		final String expected = projectHome + "myRelations";
		assertEquals(expected, ug.getMyRelationsUrl());
	}

	@Test
	public void testGetMySearchUrl() throws Exception{
		final String expected = projectHome + "mySearch";
		assertEquals(expected, ug.getMySearchUrl());
	}

	@Test
	public void testGetPostUrl() {
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		assertEquals(projectHome + "bibtex/" + HashID.INTRA_HASH.getId() + post.getResource().getIntraHash() + "/" + post.getUser().getName(), ug.getPublicationUrl(post.getResource(), post.getUser()));
		final Post<Bookmark> bPost = ModelUtils.generatePost(Bookmark.class);
		assertEquals(projectHome + "url/" + bPost.getResource().getIntraHash() + "/" + bPost.getUser().getName(), ug.getPostUrl(bPost));

	}

	@Test
	public void testGetProjectHome() throws Exception{
		assertEquals(projectHome, ug.getProjectHome());
	}

	@Test
	public void testGetPublicationsAsBibtexUrl() throws Exception{
		final String expected = projectHome + "bib";
		assertEquals(expected, ug.getPublicationsAsBibtexUrl());
	}

	@Test
	public void testGetPublicationsAsBibtexUrlByUserName() throws Exception{
		final String expected = projectHome + "bib/user/jaescke";
		assertEquals(expected, ug.getPublicationsAsBibtexUrlByUserName("jaescke"));
	}

	@Test
	public void testGetPublicationUrl() {
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		assertEquals(projectHome + "bibtex/" + HashID.INTRA_HASH.getId() + post.getResource().getIntraHash() + "/" + post.getUser().getName(), ug.getPublicationUrl(post.getResource(), post.getUser()));
	}

	@Test
	public void testGetPublicationUrlByBibTexKey() throws Exception{
		final String expected = projectHome + "bibtexkey/testBibtexKey/jaescke";
		assertEquals(expected, ug.getPublicationUrlByBibTexKeyAndUserName("testBibtexKey", "jaescke"));		
	}

	@Test
	public void testGetPublicationUrlByBibTexKeyAndUserName() throws Exception{
		final String expected = projectHome + "bibtexkey/testBibtexKey";
		assertEquals(expected, ug.getPublicationUrlByBibTexKey("testBibtexKey"));
	}

	@Test
	public void testGetPublicationUrlByInterHash() throws Exception{
		final BibTex bt = ModelUtils.generatePost(BibTex.class).getResource();
		final String expected = projectHome + "bibtex/1" + bt.getInterHash();
		assertEquals(expected, ug.getPublicationUrlByInterHash(bt.getInterHash()));
	}
	
	@Test
	public void testGetPublicationUrlByInterHashAndUsername() throws Exception{
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		final BibTex bt = post.getResource();
		final String userName = post.getUser().getName();
		
		String expected = projectHome + "bibtex/1" + bt.getInterHash() + "/" +
						  userName;
		assertEquals(expected, ug.getPublicationUrlByInterHashAndUsername(bt.getInterHash(), userName));
	}

	@Test
	public void testGetPublicationUrlByIntraHash() throws Exception{
		final BibTex bt = ModelUtils.generatePost(BibTex.class).getResource();
		final String expected = projectHome + "bibtex/2" + bt.getIntraHash();
		assertEquals(expected, ug.getPublicationUrlByIntraHash(bt.getIntraHash()));
	}

	@Test
	public void testGetPublicationUrlByIntraHashAndUsername() throws Exception{
		final Post<BibTex> post = ModelUtils.generatePost(BibTex.class);
		final BibTex bt = post.getResource();
		final String userName = post.getUser().getName();
		
		String expected = projectHome + "bibtex/2" + bt.getIntraHash() + "/" +
						  userName;
		assertEquals(expected, ug.getPublicationUrlByIntraHashAndUsername(bt.getIntraHash(), userName));
	}

	@Test
	public void testGetRelevantForUrlByGroupName() throws Exception{
		String expected = projectHome + "relevantfor/group/kde";
		assertEquals(expected, ug.getRelevantForUrlByGroupName("kde"));
	}

	@Test
	public void testGetSearchUrl() throws Exception{
		String expected = projectHome + "search/testSearch";
		assertEquals(expected, ug.getSearchUrl("testSearch"));
	}

	@Test
	public void testGetTagUrlByTagName() throws Exception{
		String expected = projectHome + "tag/kde";
		assertEquals(expected, ug.getTagUrlByTagName("kde"));
	}

	@Test
	public void testGetUrl() throws Exception{
		//TODO
	}

	@Test
	public void testGetUserPictureUrlByUsername() throws Exception{
		String expected = projectHome + "picture/user/jaeschke";
		assertEquals(expected, ug.getUserPictureUrlByUsername("jaeschke"));
	}

	@Test
	public void testGetUserUrl() {
		assertEquals(projectHome + "user/jaeschke", ug.getUserUrl(new User("jaeschke")));
	}

	@Test
	public void testGetUserUrlByUserName() throws Exception{
		String expected = projectHome + "user/jaeschke";
		assertEquals(expected, ug.getUserUrlByUserName("jaeschke"));
	}

	@Test
	public void testGetUserUrlByUserNameAndTagName() throws Exception{
		String expected = projectHome + "user/jaeschke/kde";
		assertEquals(expected, ug.getUserUrlByUserNameAndTagName("jaeschke", "kde"));
	}

	@Test
	public void testGetViewableFriendsUrl() throws Exception{
		String expected = projectHome + "viewable/friends";
		assertEquals(expected, ug.getViewableFriendsUrl());
	}

	@Test
	public void testGetViewableFriendsUrlByTagName() throws Exception{
		String expected = projectHome + "viewable/friends/kde";
		assertEquals(expected, ug.getViewableFriendsUrlByTagName("kde"));
	}

	@Test
	public void testGetViewablePrivateUrl() throws Exception{
		String expected = projectHome + "viewable/private";
		assertEquals(expected, ug.getViewablePrivateUrl());
	}

	@Test
	public void testGetViewablePrivateUrlByTagName() throws Exception{
		String expected = projectHome + "viewable/private/kde";
		assertEquals(expected, ug.getViewablePrivateUrlByTagName("kde"));
	}

	@Test
	public void testGetViewablePublicUrl() throws Exception{
		String expected = projectHome + "viewable/public";
		assertEquals(expected, ug.getViewablePublicUrl());
	}

	@Test
	public void testGetViewablePublicUrlByTagName() throws Exception{
		String expected = projectHome + "viewable/public/kde";
		assertEquals(expected, ug.getViewablePublicUrlByTagName("kde"));
	}

	@Test
	public void testGetViewableUrlByGroupName() throws Exception{
		String expected = projectHome + "viewable/kde";
		assertEquals(expected, ug.getViewableUrlByGroupName("kde"));
	}

	@Test
	public void testGetViewableUrlByGroupNameAndTagName() throws Exception{
		String expected = projectHome + "viewable/kde/aTag";
		assertEquals(expected, ug.getViewableUrlByGroupNameAndTagName("kde", "aTag"));
	}

	@Test
	public void testMatch() {
		assertTrue(ug.matchesPage(projectHome + "inbox", URLGenerator.Page.INBOX));
		assertTrue(ug.matchesPage(projectHome + "clipboard", URLGenerator.Page.CLIPBOARD));
		assertTrue(ug.matchesPage(projectHome + "clipboard?start=0", URLGenerator.Page.CLIPBOARD));
		
		assertFalse(ug.matchesPage(projectHome + "clipboard", URLGenerator.Page.INBOX));
		assertFalse(ug.matchesPage(projectHome + "foo/clipboard", URLGenerator.Page.CLIPBOARD));
		assertFalse(ug.matchesPage("/clipboard", URLGenerator.Page.CLIPBOARD));
	}

	@Test
	public void testMatchesResourcePage() throws Exception {
		assertTrue(ug.matchesResourcePage("http://my.biblicious.org/bibtex/24778fe29bb578a70f0536f2351bbee13/jaeschke", "jaeschke", "4778fe29bb578a70f0536f2351bbee13"));
		assertTrue(ug.matchesResourcePage("http://my.biblicious.org/bibtex/4778fe29bb578a70f0536f2351bbee13/jaeschke", "jaeschke", "4778fe29bb578a70f0536f2351bbee13"));
		assertFalse(ug.matchesResourcePage("http://my.biblicious.org/bibtex/4778fe29bb578a70f0536f2351bbee13", "jaeschke", "4778fe29bb578a70f0536f2351bbee13"));
		assertFalse(ug.matchesResourcePage("http://my.biblicious.org/url/a68693ed0faaaff909bb1f73a2dcc784", "jaeschke", "a68693ed0faaaff909bb1f73a2dcc784"));	
	}
	
	@Test
	public void testPrefix() throws Exception {
		URLGenerator urlg = new URLGenerator(projectHome);
		String expected = projectHome + "export/user/jaeschke";
		
		assertEquals(expected, urlg.prefix("export/").getUserUrlByUserName("jaeschke"));
	}
	
	@Test
	public void testGetGroupSettingsUrlByGroupName() {
		String expected = projectHome + "settings/group/franzosengruppe";
		assertEquals(expected, ug.getGroupSettingsUrlByGroupName("franzosengruppe", null));
	}

	@Test
	public void testGetHistoryUrlForPost() throws Exception {
		final Post<GoldStandardPublication> post = new Post<GoldStandardPublication>();
		final GoldStandardPublication resource = new GoldStandardPublication();
		resource.setInterHash("hash");
		post.setResource(resource);
		
		assertEquals(projectHome + "history/bibtex/hash" , ug.getHistoryUrlForPost(post));
		
		final Post<GoldStandardBookmark> post2 = new Post<GoldStandardBookmark>();
		final GoldStandardBookmark resource2 = new GoldStandardBookmark();
		resource2.setInterHash("hash");
		resource2.setIntraHash("hash");
		post2.setResource(resource2);
		assertEquals(projectHome + "history/url/hash" , ug.getHistoryUrlForPost(post2));
	}

}
