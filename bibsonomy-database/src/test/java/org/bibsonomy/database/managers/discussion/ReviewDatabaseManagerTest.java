package org.bibsonomy.database.managers.discussion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.discussion.DiscussionDatabaseManager;
import org.bibsonomy.database.managers.discussion.ReviewDatabaseManager;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Review;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.testutil.TestDatabaseManager;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author dzo
 * @version $Id$
 */
public class ReviewDatabaseManagerTest extends AbstractDatabaseManagerTest {

	protected static final String USERNAME_1 = "testuser1";
	protected static final String USERNAME_2 = "testuser2";
	protected static final String USERNAME_3 = "testuser3";
	protected static final String SPAMMER_1 = "testspammer";
	
	protected static final Group TESTGROUP_1 = new Group("testgroup1");
	protected static final Group TESTGROUP_2 = new Group("testgroup2");
	
	static {
		// set group ids
		TESTGROUP_1.setGroupId(TESTGROUP1_ID);
		TESTGROUP_2.setGroupId(TESTGROUP2_ID);
	}
	
	protected static final String HASH = "e2fb0763068b21639c3e36101f64aefe";

	private static ReviewDatabaseManager reviewManager;
	private static DiscussionDatabaseManager discussionDatabaseManager;
	private static TestDatabaseManager testManager;

	@BeforeClass
	public static void setupManager() {
		reviewManager = ReviewDatabaseManager.getInstance();
		discussionDatabaseManager = DiscussionDatabaseManager.getInstance();
		testManager = new TestDatabaseManager();
	}

	@Test
	public void testInsertReview() {
		final double rating = 5.0;
		this.insertReview(USERNAME_2, HASH, rating, null, null);
		final Review review = reviewManager.getReviewForPostAndUser(HASH, USERNAME_2, this.dbSession);

		assertNotNull(review);
		assertNotNull(review.getDate());
		assertEquals(rating, review.getRating(), 0);
		assertEquals(null, review.getText());
		
		// try to insert a new review for the same resource
		assertNull(this.insertReview(USERNAME_2, HASH, rating, null, null));

		this.deleteReview(USERNAME_2, HASH, review.getHash());
		
		// try to update a deleted review
		assertFalse(reviewManager.updateDiscussionItemForResource(HASH, USERNAME_2, review, this.dbSession));
	}
	
	@Test
	public void testAnonym() {
		List<DiscussionItem> items = discussionDatabaseManager.getDiscussionSpaceForResource(DiscussionDatabaseManagerTest.HASH_WITH_RATING, USERNAME_2, DiscussionDatabaseManagerTest.USERNAME_2_VISIBLE_GROUPS, this.dbSession);
		assertEquals(3, items.size());

		// test anonym
		Review review = (Review) items.get(0);
		assertEquals("", review.getUser().getName());
		
		items = discussionDatabaseManager.getDiscussionSpaceForResource(DiscussionDatabaseManagerTest.HASH_WITH_RATING, USERNAME_1, DiscussionDatabaseManagerTest.USERNAME_2_VISIBLE_GROUPS, this.dbSession);
		assertEquals(4, items.size());
		
		review = (Review) items.get(0);
		assertEquals(USERNAME_1, review.getUser().getName());
	}
	
	@Test
	public void testSpammerReview() {
		final String interHash = HASH;
		final String username = SPAMMER_1;
		final double currentRating = testManager.getReviewRatingsArithmeticMean(interHash);
		final String reviewHash = this.insertReview(SPAMMER_1, true, interHash, 1.0, null, null);
		assertNotNull(reviewHash);
		final double afterInsert = testManager.getReviewRatingsArithmeticMean(interHash);
		
		assertEquals(currentRating, afterInsert);
		
		this.deleteReview(username, interHash, reviewHash, true);
	}
	
	@Test
	public void testGroupInsertReview() {
		final double rating = 3.5;
		final String userName = USERNAME_1;
		final String interHash = HASH;
		assertNotNull(this.insertReview(userName, interHash, rating, null, new HashSet<Group>(Arrays.<Group>asList(TESTGROUP_1, TESTGROUP_2)))); // successful?
		
		final int discussionItemsSize = discussionDatabaseManager.getDiscussionSpaceForResource(HASH, null, DiscussionDatabaseManagerTest.USER_NOT_LOGGED_IN_VISIBLE_GROUPS, this.dbSession).size();
		
		final Review review = reviewManager.getReviewForPostAndUser(interHash, userName, this.dbSession);

		assertNotNull(review);
		
		final int notLoggedInSize = discussionDatabaseManager.getDiscussionSpaceForResource(HASH, null, DiscussionDatabaseManagerTest.USER_NOT_LOGGED_IN_VISIBLE_GROUPS, this.dbSession).size();
		final int ownSize = discussionDatabaseManager.getDiscussionSpaceForResource(HASH, userName, DiscussionDatabaseManagerTest.USERNAME_1_VISIBLE_GROUPS, this.dbSession).size();
		final int groupMemeberSize = discussionDatabaseManager.getDiscussionSpaceForResource(HASH, USERNAME_2, DiscussionDatabaseManagerTest.USERNAME_2_VISIBLE_GROUPS, this.dbSession).size();
		
		assertEquals(discussionItemsSize, notLoggedInSize);
		assertEquals(discussionItemsSize + 1, ownSize);
		assertEquals(discussionItemsSize + 1, groupMemeberSize);
	}

	@Test
	public void testUpdateReviewForPost() {
		final List<DiscussionItem> beforeInsert = discussionDatabaseManager.getDiscussionSpaceForResource(HASH, null, DiscussionDatabaseManagerTest.USER_NOT_LOGGED_IN_VISIBLE_GROUPS, this.dbSession);
		final String oldHash = this.insertReview(USERNAME_2, HASH, 4.0, "Great job!", null);

		final Review newReview = new Review();
		newReview.setRating(1.5);
		final String newText = "humbug!";
		newReview.setText(newText);
		newReview.setUser(new User(USERNAME_2));
		newReview.setAnonym(true);
		/*
		 * change visibility
		 */
		newReview.setGroups(Collections.singleton(GroupUtils.getPrivateGroup()));

		reviewManager.updateDiscussionItemForResource(HASH, oldHash, newReview, this.dbSession);
		final Review review = reviewManager.getReviewForPostAndUser(HASH, USERNAME_2, this.dbSession);
		assertNotNull(review);
		assertNotNull(review.getDate());
		/*
		 * check change date
		 */
		assertNotNull(review.getChangeDate());
		assertTrue(review.isAnonym());
		assertEquals(1.5, review.getRating(), 0);
		assertEquals(newText, review.getText());
		
		/*
		 * check visibility
		 */
		final List<DiscussionItem> items = discussionDatabaseManager.getDiscussionSpaceForResource(HASH, null, DiscussionDatabaseManagerTest.USER_NOT_LOGGED_IN_VISIBLE_GROUPS, this.dbSession);
		assertEquals(beforeInsert.size(), items.size());
		
		this.deleteReview(USERNAME_2, HASH, review.getHash());
	}

	@Test
	public void testCache() {
		final double average = testManager.getReviewRatingsArithmeticMean(HASH);
		int numberOfReviews = testManager.getReviewCount(HASH);
		final double rating1 = 4.5;
		final String reviewHashUser2 = this.insertReview(USERNAME_2, HASH, rating1, "Great job!", null);
		final double average2 = calcNewAvarage(average, rating1, numberOfReviews);
		numberOfReviews++;
		assertEquals(average2, testManager.getReviewRatingsArithmeticMean(HASH), 0.000000001);

		final int rating2 = 4;
		final String reviewHashUser3 = this.insertReview(USERNAME_3, HASH, rating2, "Great job! You're awesome!", null);

		final double average3 = calcNewAvarage(average2, rating2, numberOfReviews);
		numberOfReviews++;

		assertEquals(average3, testManager.getReviewRatingsArithmeticMean(HASH), 0.000000001);
		
		// TODO: do a update

		this.deleteReview(USERNAME_3, HASH, reviewHashUser3);
		assertEquals(average2, testManager.getReviewRatingsArithmeticMean(HASH), 0.000000001);

		this.deleteReview(USERNAME_2, HASH, reviewHashUser2);
	}

	private double calcNewAvarage(final double old, final double newValue, final int count) {
		return (old * count + newValue) / (count + 1);
	}
	
	private void deleteReview(final String username, final String interHash, final String hash) {
		deleteReview(username, interHash, hash, false);
	}

	private void deleteReview(final String username, final String interHash, final String hash, final boolean spammer) {
		final int countReviewLog = testManager.countReviewLogs();

		/*
		 * delete review
		 */
		final User user = new User(username);
		user.setSpammer(spammer);
		reviewManager.deleteDiscussionItemForResource(interHash, user, hash, this.dbSession);

		/*
		 * check if review was deleted
		 */
		final Review review = reviewManager.getReviewForPostAndUser(interHash, username, this.dbSession);
		assertNull(review);

		/*
		 * check log table
		 */
		assertEquals(countReviewLog + 1, testManager.countReviewLogs());
	}
	
	private String insertReview(final String username, final String interHash, final double rating, final String text, final Set<Group> groups) {
		return insertReview(username, false, interHash, rating, text, groups);
	}
	
	private String insertReview(final String username, final boolean spammer, final String interHash, final double rating, final String text, final Set<Group> groups) {
		final Review review = new Review();
		review.setRating(rating);
		review.setText(text);
		CommentDatabaseManagerTest.fillDiscussionItem(review, username, spammer);
		if (groups != null) {
			review.setGroups(groups);
		}
		final boolean success = reviewManager.createDiscussionItemForResource(interHash, review, this.dbSession);
		return success ? review.getHash() : null;
	}

	@Test(expected = ValidationException.class)
	public void invalidReviewMaxRating() {
		this.insertReview(USERNAME_2, "a", 5.0000000000001, "Great job!", null);
	}

	@Test(expected = ValidationException.class)
	public void invalidReviewMinRating() {
		this.insertReview(USERNAME_2, "a", -1.0, "Great job!", null);
	}

	@Test(expected = ValidationException.class)
	public void invalidReviewNotHalfRating() {
		this.insertReview(USERNAME_2, "a", 2.7, "Great job!", null);
	}
}
