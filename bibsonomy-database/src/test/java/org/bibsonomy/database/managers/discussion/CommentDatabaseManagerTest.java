package org.bibsonomy.database.managers.discussion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.Collections;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Comment;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author dzo
 * @version $Id$
 */
public class CommentDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	private static DiscussionItemDatabaseManager<Comment> commentDatabaseManager;
	
	@BeforeClass
	public static void initDBMangager() {
		commentDatabaseManager = CommentDatabaseManager.getInstance();
	}
	
	@Test
	public void testCreateUpdateDelete() {
		final String interHash = ReviewDatabaseManagerTest.HASH;
		final String userNameComment = ReviewDatabaseManagerTest.USERNAME_2;
		final String userNameSubComment = ReviewDatabaseManagerTest.USERNAME_1;
		final String commentText = "Good review!";
		final String parentHash = this.insertComment(userNameComment, BibTex.class, ReviewDatabaseManagerTest.HASH, commentText, null);
		
		final Comment comment = commentDatabaseManager.getDiscussionItemByHashForResource(interHash, userNameComment, parentHash, this.dbSession);
		assertNotNull(comment);
		assertNotNull(comment.getDate());
		assertEquals(commentText, comment.getText());
		
		/*
		 * test thread structure
		 */
		final String subCommentHash = this.insertComment(ReviewDatabaseManagerTest.USERNAME_1, BibTex.class, ReviewDatabaseManagerTest.HASH, "correct", parentHash);
		final DiscussionItem subComment = commentDatabaseManager.getDiscussionItemByHashForResource(interHash, userNameSubComment, subCommentHash, this.dbSession);
		assertNotNull(subComment);
		assertNotNull(comment.getDate());
		assertEquals(parentHash, subComment.getParentHash());
		
		comment.setText("Check out this item");
		
		/*
		 * update parent comment (hash changed!)
		 */
		commentDatabaseManager.updateDiscussionItemForResource(interHash, parentHash, comment, this.dbSession);
		final String newHash = comment.getHash();
		assertNotSame(parentHash, newHash); // hash has changed
		
		// subComment parentHash update?!
		final DiscussionItem newSubComment = commentDatabaseManager.getDiscussionItemByHashForResource(interHash, userNameSubComment, subCommentHash, this.dbSession);
		assertEquals(newHash, newSubComment.getParentHash());
	}
	
	@Test(expected = ValidationException.class)
	public void testNoParentCommentFound() {
		this.insertComment("test", BibTex.class, "hah", "jjdfs", "thisisastrangehash");
	}
	
	private String insertComment(final String username, final Class<? extends Resource> resourceType, final String hash, final String text, final String parentHash) {
		final Comment comment = new Comment();
		fillComment(comment, username, text);
		comment.setParentHash(parentHash);
		comment.setResourceType(resourceType);
		comment.setText(text);

		commentDatabaseManager.createDiscussionItemForResource(hash, comment, this.dbSession);
		return comment.getHash();
	}
	
	protected static void fillComment(final DiscussionItem comment, final String username, final String text) {
		fillDiscussionItem(comment, username, false);
	}

	protected static void fillDiscussionItem(final DiscussionItem discussionItem, final String username, final boolean spammer) {
		final User user = new User(username);
		user.setSpammer(spammer);
		discussionItem.setUser(user);
		final Group publicGroup = spammer ? GroupUtils.getPublicSpamGroup() : GroupUtils.getPublicGroup();
		discussionItem.setGroups(Collections.singleton(publicGroup));
	}
}
