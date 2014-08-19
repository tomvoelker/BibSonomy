package org.bibsonomy.recommender.item.content;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.testutil.RecommenderTestContext;
import org.bibsonomy.recommender.item.db.DBLogConfigItemAccess;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;
import org.bibsonomy.recommender.item.service.RecommenderMainItemAccess;
import org.bibsonomy.recommender.item.testutil.DummyCollaborativeMainAccess;
import org.bibsonomy.recommender.item.testutil.DummyMainItemAccess;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.core.database.DBLogic;

/**
 * This class tests the {@link AdaptedContentBasedItemRecommender} extension of
 * the library's version {@link ContentBasedItemRecommender}.
 * It checks whether additional bibtex and bookmark information is used.
 * 
 * @author lukas
 *
 */
public class AdaptedContentBasedItemRecommenderTest {
	public static final String REQUESTING_USER_NAME = "requestUser";
	public static final int RECOMMENDATIONS_TO_CALCULATE = 4;
	public static final String WINNER_TITLE = "recommender systems";
	public static final String[] USER_NAMES = {"cfuser1", "cfuser2"}; 
	
	private static int id_generator = 0;
	
	private static DBLogic<RecommendationUser, RecommendedPost<BibTex>> bibtexDBLogic;
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUp() {
		bibtexDBLogic = RecommenderTestContext.getBeanFactory().getBean("bibtexRecommenderLogic", DBLogConfigItemAccess.class);
	}
	
	/**
	 * Checks the correct count of results and the handling of Bookmark and
	 * BibTex resources.
	 */
	@Test
	public void testAdaptedContentBasedItemRecommender() {
		RecommenderMainItemAccess<BibTex> dbAccess = new DummyMainItemAccess<BibTex>() {
			@Override
			protected BibTex createResource() {
				return new BibTex();
			}
		};
		
		AdaptedContentBasedItemRecommender<BibTex> reco = new AdaptedContentBasedItemRecommender<BibTex>();
		reco.setDbAccess(dbAccess);
		reco.setDbLogic(bibtexDBLogic);
		reco.setNumberOfItemsToRecommend(RECOMMENDATIONS_TO_CALCULATE);
		
		User u = new User(ContentBasedItemRecommenderTest.DUMMY_CF_USER_NAME);
		final RecommendationUser user = new RecommendationUser();
		user.setUserName(u.getName());
		
		SortedSet<RecommendedPost<BibTex>> recommendations = reco.getRecommendation(user);
		
		// checks if the count of items is correct
		assertEquals(RECOMMENDATIONS_TO_CALCULATE, recommendations.size());
		
		// new dbAccess to make the database results non random
		dbAccess = new DummyCollaborativeMainAccess<BibTex>() {
			@Override
			protected BibTex createResource() {
				return new BibTex();
			}
		};
		reco.setDbAccess(dbAccess);
		
		recommendations = reco.getRecommendation(user);
		
		// this makes sure, for requesting user his bibtex and bookmark resources
		// are used for getting his vocabulary
		// also it ensures, that abstracts and descriptions get used
		assertEquals(WINNER_TITLE, recommendations.first().getTitle());
	}
	
	/**
	 * helper method for creation of recommendation items
	 * 
	 * @return a list with fix specified attributes like given below
	 */
	public static List<Post<? extends Resource>>createItemsForCfUsers() {
		final List<Post<? extends Resource>> posts = new ArrayList<Post<? extends Resource>>();
		posts.add(createBibTexPost(WINNER_TITLE, "recommender systems", "recommender systems", USER_NAMES[0]));
		posts.add(createBibTexPost("failed", "recommender systems", "unknown description", USER_NAMES[1]));
		return posts;
	}
	
	/**
	 * Creates a bibtex Post
	 * 
	 * @param title the post's title
	 * @param abstractString the bibtex's abstract
	 * @param description the post's description
	 * @param username the username of the owner
	 * @return an instance of a bibtex post
	 */
	public static Post<BibTex> createBibTexPost(final String title, final String abstractString, final String description, final String username) {
		final Post<BibTex> post = new Post<BibTex>();
		final BibTex bibtex = new BibTex();
		bibtex.setTitle(title);
		bibtex.setAbstract(abstractString);
		post.setDescription(description);
		post.setContentId(id_generator);
		id_generator++;
		post.setResource(bibtex);
		post.setUser(new User(username));
		return post;
	}
	
	/**
	 * Creates a bookmark post
	 * 
	 * @param title the post's title
	 * @param description the post's description
	 * @param username the username of the owder
	 * @return an instance of a bookmark post
	 */
	public static Post<Bookmark> createBookmarkPost(final String title, final String description, final String username) {
		final Post<Bookmark> post = new Post<Bookmark>();
		final Bookmark bookmark = new Bookmark();
		bookmark.setTitle(title);
		post.setDescription(description);
		post.setContentId(id_generator);
		id_generator++;
		post.setResource(bookmark);
		post.setUser(new User(username));
		return post;
	}
}
