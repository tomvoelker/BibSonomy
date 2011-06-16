package org.bibsonomy.model.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.Date;

import org.bibsonomy.model.Comment;
import org.bibsonomy.model.Review;
import org.bibsonomy.model.User;
import org.junit.Test;


/**
 * @author dzo
 * @version $Id$
 */
public class DiscussionItemUtilsTest {

	private static final String FIRST_HASH = "d38ac3127809563a311e6b3179de8ea3";
	private static final String SECOND_HASH = "5184fea16c12321902e9028dc0094fa6";
	private static final String THIRD_HASH = "33d0f38502aed387d2f3055e422a64f1";
	private static final String FOURTH_HASH = "09813773888645fad25a1c0f22ce56c8";
	
	@Test
	public void testRecalculateHashComment() {
		final Comment comment = new Comment();
		comment.setDate(new Date(10));
		comment.setText("This is a Text");
		final User user = new User("testuser1");
		comment.setUser(user);
		String hash;
		
		hash = DiscussionItemUtils.recalculateHash(comment);
		
		assertEquals(FIRST_HASH, hash);
		
		// user name should change hash
		user.setName("testuser2");
		hash = DiscussionItemUtils.recalculateHash(comment);
		
		assertNotSame(FIRST_HASH, hash);
		assertEquals(SECOND_HASH, hash);
		
		// text change should change hash
		comment.setText("another Text");
		hash = DiscussionItemUtils.recalculateHash(comment);
		
		assertNotSame(SECOND_HASH, hash);
		assertEquals(THIRD_HASH, hash);
		
		// date change should change hash
		comment.setDate(new Date(1000));
		hash = DiscussionItemUtils.recalculateHash(comment);
		
		assertNotSame(THIRD_HASH, hash);
		assertEquals(FOURTH_HASH, hash);
	}
	
	@Test
	public void testRecalculateHashReview() {
		final Review review = new Review();
		review.setDate(new Date(10));
		review.setText("This is a Text");
		final User user = new User("testuser1");
		review.setUser(user);
		String hash;
		
		hash = DiscussionItemUtils.recalculateHash(review);
		
		assertEquals(FIRST_HASH, hash);
	}
}
