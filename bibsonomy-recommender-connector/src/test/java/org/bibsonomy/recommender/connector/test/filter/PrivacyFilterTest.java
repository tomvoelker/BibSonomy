package org.bibsonomy.recommender.connector.test.filter;

import java.util.Date;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.recommender.connector.model.PostWrapper;
import org.bibsonomy.recommender.connector.model.UserWrapper;
import org.bibsonomy.recommender.connector.testutil.DummyMainItemAccess;
import org.bibsonomy.recommender.item.filter.UserPrivacyFilter;
import org.bibsonomy.recommender.tag.filter.PostPrivacyFilter;
import org.junit.Assert;
import org.junit.Test;

import recommender.core.interfaces.filter.PrivacyFilter;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.TagRecommendationEntity;

/**
 * This class tests the implementation of the {@link PrivacyFilter} interface.
 * The test cases cover {@link PostPrivacyFilter} and {@link UserPrivacyFilter} implementation.
 * 
 * @author lukas
 *
 */
public class PrivacyFilterTest {

	/**
	 * tests whether usernames are mapped to valid Long ids
	 */
	@Test
	public void testUserPrivacyFilter() {
		final User user = new User("testuser");
		final ItemRecommendationEntity entity = new UserWrapper(user);
		
		final UserPrivacyFilter filter = new UserPrivacyFilter();
		filter.setDbAccess(new DummyMainItemAccess());
		
		final ItemRecommendationEntity filteredEntity = filter.filterEntity(entity);
		Long parsed = 0L;
		try {
			parsed = Long.parseLong(filteredEntity.getRecommendationId());
		} catch (NumberFormatException e) {
			Assert.fail("Id was not a valid Long value!");
		}
		
		Assert.assertEquals(filteredEntity.getRecommendationId(), parsed.toString());
	}
	
	/**
	 * tests whether private bibtex posts and public posts private attributes are
	 * removed
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testPostPrivacyFilterWithBibTexPost() {
		TagRecommendationEntity bibtexEntity = this.createBibTexPost(true);
		
		final PrivacyFilter<TagRecommendationEntity> filter = new PostPrivacyFilter();
		
		TagRecommendationEntity filteredBibTexEntity = filter.filterEntity(bibtexEntity);
		
		Assert.assertNotNull(filteredBibTexEntity);
		
		final Post<BibTex> filteredWrappedBibTexPost = ((PostWrapper<BibTex>) filteredBibTexEntity).getPost();
		
		// check if these values are still accessible, they should be
		Assert.assertEquals(bibtexEntity.getRecommendationId(), filteredBibTexEntity.getRecommendationId());
		Assert.assertEquals(bibtexEntity.getTitle(), filteredBibTexEntity.getTitle());
		Assert.assertEquals(bibtexEntity.getUrl(), filteredBibTexEntity.getUrl());
		// Assert.assertEquals(bibtexEntity.getUserName(), filteredBibTexEntity.getUserName()); FIXME (refactor)

		
		// only public attributes should have been copied
		Assert.assertNull(filteredWrappedBibTexPost.getHiddenSystemTags());
		
		bibtexEntity = this.createBibTexPost(false);
		
		filteredBibTexEntity = filter.filterEntity(bibtexEntity);
		
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
		TagRecommendationEntity bookmarkEntity = this.createBookmarkPost(true);
		
		final PrivacyFilter<TagRecommendationEntity> filter = new PostPrivacyFilter();
		
		TagRecommendationEntity filteredBookmarkEntity = filter.filterEntity(bookmarkEntity);
		
		Assert.assertNotNull(filteredBookmarkEntity);
		
		final Post<BibTex> filteredWrappedBookmarkPost = ((PostWrapper<BibTex>) filteredBookmarkEntity).getPost();
		
		// check if these values are still accessible, they should be
		Assert.assertEquals(bookmarkEntity.getRecommendationId(), filteredBookmarkEntity.getRecommendationId());
		Assert.assertEquals(bookmarkEntity.getTitle(), filteredBookmarkEntity.getTitle());
		Assert.assertEquals(bookmarkEntity.getUrl(), filteredBookmarkEntity.getUrl());
		// Assert.assertEquals(bookmarkEntity.getUserName(), filteredBookmarkEntity.getUserName()); FIXME (refactor)
		
		// only public attributes should have been copied
		Assert.assertNull(filteredWrappedBookmarkPost.getHiddenSystemTags());
		
		bookmarkEntity = this.createBookmarkPost(false);
		
		filteredBookmarkEntity = filter.filterEntity(bookmarkEntity);
		
		// no public post -> filter returns null
		Assert.assertNull(filteredBookmarkEntity);
	}
	
	/**
	 * Create an public or private mockup post with a bibtex resource
	 * 
	 * @param publicPost true for retrieving a public, false for a private post
	 */
	private TagRecommendationEntity createBibTexPost(final boolean publicPost) {
		final Post<BibTex> post = new Post<BibTex>();
		final User user = new User("foo");
		final Group group = new Group("bar");
		final Tag tag = new Tag("foobar");
		post.setUser(user);
		post.getGroups().add(group);
		post.getTags().add(tag);
		post.setDate(new Date(System.currentTimeMillis()));
		final BibTex bibtex = new BibTex();
		bibtex.setTitle("foo and bar");
		bibtex.setIntraHash("abc");
		bibtex.setInterHash("abc");
		bibtex.setYear("2009");
		bibtex.setBibtexKey("test");
		bibtex.setEntrytype("twse");
		post.setResource(bibtex);
		post.setContentId(0);
		if(publicPost) {
			post.addGroup(GroupUtils.PUBLIC_GROUP_NAME);
		} else {
			post.addGroup(GroupUtils.PRIVATE_GROUP_NAME);
		}
		post.addHiddenSystemTag(new Tag("HiddenSystemTag"));
		
		return new PostWrapper<BibTex>(post);
	}
	
	/**
	 * Create an public or private mockup post with a bookmark resource
	 * 
	 * @param publicPost true for retrieving a public, false for a private post
	 */
	private TagRecommendationEntity createBookmarkPost(final boolean publicPost) {
		final Post<Bookmark> post = new Post<Bookmark>();
		final User user = new User("foo");
		final Group group = new Group("bar");
		final Tag tag = new Tag("foobar");
		post.setUser(user);
		post.getGroups().add(group);
		post.getTags().add(tag);
		post.setDate(new Date(System.currentTimeMillis()));
		final Bookmark bibtex = new Bookmark();
		bibtex.setTitle("foo and bar");
		bibtex.setIntraHash("abc");
		bibtex.setInterHash("abc");
		post.setResource(bibtex);
		post.setContentId(0);
		if(publicPost) {
			post.addGroup(GroupUtils.PUBLIC_GROUP_NAME);
		} else {
			post.addGroup(GroupUtils.PRIVATE_GROUP_NAME);
		}
		post.addHiddenSystemTag(new Tag("HiddenSystemTag"));
		
		return new PostWrapper<Bookmark>(post);
	}
	
}
