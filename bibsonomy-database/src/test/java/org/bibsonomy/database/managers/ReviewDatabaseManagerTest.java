package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Review;
import org.bibsonomy.model.User;
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
	private static final String HASH = "e2fb0763068b21639c3e36101f64aefe";
	private static final String HASH_WITH_RATING = "d9eea4aa159d70ecfabafa0c91bbc9f0";
	private static final String RATING_USERNAME = "testuser1";
	
	private static ReviewDatabaseManager reviewManager;
	private static TestDatabaseManager testManager;
	
	@BeforeClass
	public static void setupManager() {
		reviewManager = ReviewDatabaseManager.getInstance();
		testManager = new TestDatabaseManager();
	}
	
	@Test
	public void testGetReviewsForPost() {
		List<Review> reviews = reviewManager.getReviewsForResource(HASH_WITH_RATING, this.dbSession);
		assertEquals(1, reviews.size());
		
		reviews = reviewManager.getReviewsForResource(HASH, this.dbSession);
		assertEquals(0, reviews.size());
	}
	
	@Test
	public void testInsertReview() {
		this.insertReview(USERNAME_2, HASH, 5.0, null);
		final Review review = reviewManager.getReviewForPostAndUser(HASH, USERNAME_2, this.dbSession);
		assertNotNull(review);
		assertNotNull(review.getDate());
		assertEquals(5.0, review.getRating(), 0);
		assertEquals(null, review.getText());
		
		this.deleteReview(USERNAME_2, HASH);
	}
	
	@Test
	public void testUpdateReview() {
		this.insertReview(USERNAME_2, HASH, 4.0, "Great job!");
		final Review newReview = new Review();
		newReview.setRating(1.5);
		final String newText = "humbug!";
		newReview.setText(newText);
		newReview.setUser(new User(USERNAME_2));
		reviewManager.updateReview(HASH, newReview, this.dbSession);
		final Review review = reviewManager.getReviewForPostAndUser(HASH, USERNAME_2, this.dbSession);
		assertNotNull(review);
		assertNotNull(review.getDate());
		assertNotNull(review.getChangeDate());
		assertEquals(1.5, review.getRating(), 0);
		assertEquals(newText, review.getText());
		this.deleteReview(USERNAME_2, HASH);
	}
	
	@Test
	public void testCache() {
		final double average = testManager.getReviewRatingsAverage(HASH);
		int numberOfReviews = testManager.getReviewCount(HASH);
		this.insertReview(USERNAME_2, HASH, 4.5, "Great job!");
		final double average2 = calcNewAvarage(average, 4.5, numberOfReviews);
		numberOfReviews++;
		assertEquals(average2, testManager.getReviewRatingsAverage(HASH), 0.000000001);
		
		this.insertReview(USERNAME_3, HASH, 4, "Great job! You're awesome!");
		
		final double average3 = calcNewAvarage(average2, 4, numberOfReviews);
		numberOfReviews++;
		
		assertEquals(average3, testManager.getReviewRatingsAverage(HASH), 0.000000001);
		
		this.deleteReview(USERNAME_3, HASH);
		assertEquals(average2, testManager.getReviewRatingsAverage(HASH), 0.000000001);
		
		this.deleteReview(USERNAME_2, HASH);
	}
	
	@Test
	public void testMarkAsHelpful() {
		reviewManager.markReview(USERNAME_2, HASH_WITH_RATING, RATING_USERNAME, true, this.dbSession);
		final Review review = reviewManager.getReviewForPostAndUser(HASH_WITH_RATING, RATING_USERNAME, this.dbSession);
		
		assertEquals(2, review.getNotHelpful());
		assertEquals(1, review.getHelpful());
	}
	
	@Test
	public void testLogMarkAsHelpful() {
		this.insertReview(USERNAME_2, HASH, 4.5, "Great job!");
		reviewManager.markReview(USERNAME_2, HASH, USERNAME_2, true, this.dbSession);
		reviewManager.markReview(USERNAME_3, HASH, USERNAME_2, true, this.dbSession);
		this.deleteReview(USERNAME_2, HASH);
	}
	
	@Test(expected = ValidationException.class)
	public void testAlreadyMarkedReview() {
		// testuser3 has already marked the review as not helpful
		reviewManager.markReview(USERNAME_3, HASH_WITH_RATING, RATING_USERNAME, true, this.dbSession);
	}
	
	private double calcNewAvarage(double old, double newValue, int count) {
		return (old * count + newValue) / (count + 1);
	}
	
	private void deleteReview(final String username, final String interHash) {
		final int countReviewLog = testManager.countReviewLogs();
		final int expectedCount = testManager.countReviewHelpfulLogs() + testManager.countReviewHelpful(interHash);
		
		reviewManager.deleteReview(interHash, username, this.dbSession);
		final Review review = reviewManager.getReviewForPostAndUser(interHash, username, this.dbSession);
		assertNull(review);
		assertEquals(countReviewLog + 1, testManager.countReviewLogs());
		
		assertEquals(expectedCount, testManager.countReviewHelpfulLogs());
		assertEquals(0, testManager.countReviewHelpful(interHash));
	}
	
	private void insertReview(final String username, final String interHash, final double rating, String text) {
		final Review review = new Review();
		review.setRating(rating);
		review.setText(text);
		review.setUser(new User(username));
		reviewManager.createReviewForPost(interHash, review , this.dbSession);
	}
	
	@Test(expected = ValidationException.class)
	public void invalidReviewMaxRating() {
		this.insertReview(USERNAME_2, HASH, 5.0000000000001, "Great job!");
	}
	
	@Test(expected = ValidationException.class)
	public void invalidReviewMinRating() {
		this.insertReview(USERNAME_2, HASH, -1.0, "Great job!");
	}
	
	@Test(expected = ValidationException.class)
	public void invalidReviewNotHalfRating() {
		this.insertReview(USERNAME_2, HASH, 2.7, "Great job!");
	}
}
