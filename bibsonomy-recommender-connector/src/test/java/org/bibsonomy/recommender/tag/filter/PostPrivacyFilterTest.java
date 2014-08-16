package org.bibsonomy.recommender.tag.filter;

import java.util.Date;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.recommender.item.filter.UserPrivacyFilter;
import org.junit.Assert;
import org.junit.Test;

import recommender.core.interfaces.filter.PrivacyFilter;

/**
 * This class tests the implementation of the {@link PrivacyFilter} interface.
 * The test cases cover {@link PostPrivacyFilter} and {@link UserPrivacyFilter} implementation.
 * 
 * @author lukas
 *
 */
public class PostPrivacyFilterTest {
	
	/**
	 * tests whether private publication posts and private attributes of public
	 * posts are removed
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testPostPrivacyFilterWithBibTexPost() {
		Post<BibTex> bibtexEntity = this.createBibTexPost(true);
		
		final PrivacyFilter<Post<? extends Resource>> filter = new PostPrivacyFilter();
		
		Post<BibTex> filteredBibTexEntity = (Post<BibTex>) filter.filterEntity(bibtexEntity);
		
		Assert.assertNotNull(filteredBibTexEntity);
		
		// check if these values are still accessible, they should be
		Assert.assertEquals(bibtexEntity.getContentId(), filteredBibTexEntity.getContentId());
		Assert.assertEquals(bibtexEntity.getResource().getTitle(), filteredBibTexEntity.getResource().getTitle());
		Assert.assertEquals(bibtexEntity.getResource().getUrl(), filteredBibTexEntity.getResource().getUrl());
		Assert.assertEquals(bibtexEntity.getUser().getName(), filteredBibTexEntity.getUser().getName());

		
		// only public attributes should have been copied
		Assert.assertNull(filteredBibTexEntity.getHiddenSystemTags());
		
		bibtexEntity = this.createBibTexPost(false);
		
		filteredBibTexEntity = (Post<BibTex>) filter.filterEntity(bibtexEntity);
		
		// no public post -> filter returns null
		Assert.assertNull(filteredBibTexEntity);
	}
	
	/**
	 * tests whether private bookmark posts and public posts private attributes are
	 * removed
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testPostPrivacyFilterWithBookmarkPost() {
		Post<Bookmark> bookmarkEntity = this.createBookmarkPost(true);
		
		final PrivacyFilter<Post<? extends Resource>> filter = new PostPrivacyFilter();
		
		Post<Bookmark> filteredBookmarkEntity = (Post<Bookmark>) filter.filterEntity(bookmarkEntity);
		
		Assert.assertNotNull(filteredBookmarkEntity);
		
		Bookmark bookmark = bookmarkEntity.getResource();
		
		Bookmark filteredBookmark = filteredBookmarkEntity.getResource();
		
		// check if these values are still accessible, they should be
		Assert.assertEquals(bookmarkEntity.getContentId(), filteredBookmarkEntity.getContentId());
		Assert.assertEquals(bookmark.getTitle(), filteredBookmark.getTitle());
		Assert.assertEquals(bookmark.getUrl(), filteredBookmark.getUrl());
		Assert.assertEquals(bookmarkEntity.getUser().getName(), filteredBookmarkEntity.getUser().getName());
		
		// only public attributes should have been copied
		Assert.assertNull(filteredBookmarkEntity.getHiddenSystemTags());
		
		bookmarkEntity = this.createBookmarkPost(false);
		
		filteredBookmarkEntity = (Post<Bookmark>) filter.filterEntity(bookmarkEntity);
		
		// no public post -> filter returns null
		Assert.assertNull(filteredBookmarkEntity);
	}
	
	/**
	 * Create an public or private mockup post with a bibtex resource
	 * 
	 * @param publicPost true for retrieving a public, false for a private post
	 */
	private static Post<BibTex> createBibTexPost(final boolean publicPost) {
		final Post<BibTex> post = new Post<BibTex>();
		
		setUpPost(post, publicPost);
		
		final BibTex bibtex = new BibTex();
		bibtex.setTitle("foo and bar");
		bibtex.setIntraHash("abc");
		bibtex.setInterHash("abc");
		bibtex.setYear("2009");
		bibtex.setBibtexKey("test");
		bibtex.setEntrytype("twse");
		post.setResource(bibtex);
		
		return post;
	}

	private static void setUpPost(final Post<? extends Resource> post, boolean publicPost) {
		final User user = new User("foo");
		final Tag tag = new Tag("foobar");
		post.setUser(user);
		post.getTags().add(tag);
		post.setDate(new Date(System.currentTimeMillis()));
		post.setContentId(Integer.valueOf(0));
		
		if(publicPost) {
			post.addGroup(GroupUtils.PUBLIC_GROUP_NAME);
		} else {
			post.addGroup(GroupUtils.PRIVATE_GROUP_NAME);
		}
		
		post.addHiddenSystemTag(new Tag("HiddenSystemTag"));
	}
	
	/**
	 * Create an public or private mockup post with a bookmark resource
	 * 
	 * @param publicPost true for retrieving a public, false for a private post
	 */
	private static Post<Bookmark> createBookmarkPost(final boolean publicPost) {
		final Post<Bookmark> post = new Post<Bookmark>();
		
		setUpPost(post, publicPost);
		
		final Bookmark bibtex = new Bookmark();
		bibtex.setTitle("foo and bar");
		bibtex.setIntraHash("abc");
		bibtex.setInterHash("abc");
		post.setResource(bibtex);
		return post;
	}
	
}
