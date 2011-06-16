package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.enums.ClassifierMode;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.database.managers.discussion.DiscussionDatabaseManager;
import org.bibsonomy.database.managers.discussion.DiscussionDatabaseManagerTest;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.TestDatabaseManager;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Robert Jäschke
 * @author Stefan Stützer
 * @version $Id$
 */
public class AdminDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	private static AdminDatabaseManager adminDb;
	private static BookmarkDatabaseManager bookmarkDb;
	private static BibTexDatabaseManager publicationDb;
	private static DiscussionDatabaseManager discussionDatabaseManager;
	private static TestDatabaseManager testDatabaseManager;
	
	/**
	 * sets up required managers
	 */
	@BeforeClass
	public static void setupManager() {
		adminDb = AdminDatabaseManager.getInstance();
		bookmarkDb = BookmarkDatabaseManager.getInstance();
		publicationDb = BibTexDatabaseManager.getInstance();
		discussionDatabaseManager = DiscussionDatabaseManager.getInstance();
		testDatabaseManager = new TestDatabaseManager();
	}

	/**
	 * tests getInetAddressStatus
	 */
	@Test
	public void getInetAddressStatus() {
		try {
			final InetAddress address = InetAddress.getByName("192.168.0.1");
			assertEquals(InetAddressStatus.UNKNOWN, adminDb.getInetAddressStatus(address, this.dbSession));
		} catch (final UnknownHostException ignore) {
			// ignore, we don't need host name resolution
		}
	}

	/**
	 * tests addInetAdressStatus
	 */
	@Test
	public void addInetAdressStatus() {
		try {
			final InetAddress address = InetAddress.getByName("192.168.1.1");
			final InetAddressStatus status = InetAddressStatus.WRITEBLOCKED;
			// write
			adminDb.addInetAddressStatus(address, status, this.dbSession);
			// read
			final InetAddressStatus writtenStatus = adminDb.getInetAddressStatus(address, this.dbSession);
			// check
			assertEquals(status, writtenStatus);
		} catch (final UnknownHostException ignore) {
			// ignore, we don't need host name resolution
		}
	}

	/**
	 * tests deleteInetAdressStatus
	 */
	@Test
	public void deleteInetAdressStatus() {
		try {
			final InetAddress address = InetAddress.getByName("192.168.1.1");
			// read
			InetAddressStatus status = adminDb.getInetAddressStatus(address, this.dbSession);
			// delete
			adminDb.deleteInetAdressStatus(address, this.dbSession);
			// read
			status = adminDb.getInetAddressStatus(address, this.dbSession);
			// check
			assertEquals(InetAddressStatus.UNKNOWN, status);
		} catch (final UnknownHostException ex) {
			// ignore, we don't need host name resolution
		}
	}

	/**
	 * tests getClassifierSettings
	 */
	@Test
	public void getClassifierSettings() {
		final ClassifierSettings settingsKey = ClassifierSettings.ALGORITHM;
		final String value = adminDb.getClassifierSettings(settingsKey, this.dbSession);
		assertEquals("weka.classifiers.lazy.IBk", value);
	}

	/**
	 * tests updateClassifierSettings
	 */
	@Test
	public void updateClassifierSettings() {
		final ClassifierSettings settingsKey = ClassifierSettings.MODE;
		final String value = ClassifierMode.NIGHT.getAbbreviation();

		adminDb.updateClassifierSettings(settingsKey, value, this.dbSession);

		final String result = adminDb.getClassifierSettings(settingsKey, this.dbSession);
		assertEquals(value, result);
	}

	/**
	 * tests logging when flagging and unflagging spammers
	 */
	@Test
	public void updatePredictionLogs() {
		final User user = new User();
		user.setName("testspammer");
		user.setSpammer(true);
		user.setToClassify(0);
		user.setPrediction(1);
		user.setConfidence(0.2);
		user.setMode("D");
		user.setAlgorithm("testlogging");
		//flag spammer (flagging does not change: user is no spammer)
		final String result = adminDb.flagSpammer(user, "classifier", "off", this.dbSession);
		assertEquals(user.getName(), result);
	}
	
	/**
	 * flags and unflags an user as spammer
	 */
	@Test
	public void flagUnflagSpammer() {
		final User user = new User();
		final String userName = "testuser1";
		user.setName(userName);
		
		final List<Integer> visibleGroupsUser2 = Collections.singletonList(PUBLIC_GROUP_ID);
		final String loginUserName2 = "testuser2";
		List<Post<Bookmark>> publicBookmarkUserPosts = bookmarkDb.getPostsForUser(loginUserName2, userName, HashID.INTRA_HASH, PUBLIC_GROUP_ID, visibleGroupsUser2, null, 10, 0, null, this.dbSession);
		List<Post<BibTex>> publicPublicationUserPosts = publicationDb.getPostsForUser(loginUserName2, userName, HashID.INTRA_HASH, PUBLIC_GROUP_ID, visibleGroupsUser2, null, 10, 0, null, this.dbSession);
		assertEquals(1, publicPublicationUserPosts.size());
		assertEquals(1, publicBookmarkUserPosts.size());
		
		user.setSpammer(true);
		user.setToClassify(0);
		user.setPrediction(1);
		user.setConfidence(1.0);
		user.setMode("D");
		user.setAlgorithm("test");
		adminDb.flagSpammer(user, "not-classifier", "off", this.dbSession);
		
		/*
		 * after the user is marked as spammer testuser2 shouldn't see any posts of testuser1
		 */
		publicBookmarkUserPosts = bookmarkDb.getPostsForUser(loginUserName2, userName, HashID.INTRA_HASH, PUBLIC_GROUP_ID, visibleGroupsUser2, null, 10, 0, null, this.dbSession);
		publicPublicationUserPosts = publicationDb.getPostsForUser(loginUserName2, userName, HashID.INTRA_HASH, PUBLIC_GROUP_ID, visibleGroupsUser2, null, 10, 0, null, this.dbSession);
		assertEquals(0, publicPublicationUserPosts.size());
		assertEquals(0, publicBookmarkUserPosts.size());
		
		// TODO: user should see his own posts
		
		// TODO: check tas and grouptas table
		
		/*
		 * check if discussion items are invisible to non-spammers
		 */
		assertEquals(0, discussionDatabaseManager.getDiscussionSpaceForResource(DiscussionDatabaseManagerTest.HASH_WITH_RATING, loginUserName2, visibleGroupsUser2, this.dbSession).size());
		
		/*
		 * check if the review ratings cache was updated
		 */
		assertEquals(0, testDatabaseManager.getReviewRatingsArithmeticMean(DiscussionDatabaseManagerTest.HASH_WITH_RATING), 0);
		
		/*
		 * now unflag testuser1 again
		 */
		user.setSpammer(false);
		adminDb.flagSpammer(user, "admin", "off", this.dbSession);
		
		publicBookmarkUserPosts = bookmarkDb.getPostsForUser(loginUserName2, userName, HashID.INTRA_HASH, PUBLIC_GROUP_ID, visibleGroupsUser2, null, 10, 0, null, this.dbSession);
		publicPublicationUserPosts = publicationDb.getPostsForUser(loginUserName2, userName, HashID.INTRA_HASH, PUBLIC_GROUP_ID, visibleGroupsUser2, null, 10, 0, null, this.dbSession);
		assertEquals(1, publicPublicationUserPosts.size());
		assertEquals(1, publicBookmarkUserPosts.size());
		
		// TODO: check tas and grouptas table
		assertEquals(2, discussionDatabaseManager.getDiscussionSpaceForResource(DiscussionDatabaseManagerTest.HASH_WITH_RATING, loginUserName2, visibleGroupsUser2, this.dbSession).size());
		
		/*
		 * check if the review ratings cache was updated
		 */
		assertEquals(4.0, testDatabaseManager.getReviewRatingsArithmeticMean(DiscussionDatabaseManagerTest.HASH_WITH_RATING), 0);
	}
	
	/**
	 * tests populating given user object with spam flags
	 */
	@Test
	public void getClassifierUserDetails() {
		final User user = new User();
		user.setName("testspammer2");
		user.setEmail("testMail@nomail.com");
		user.setHobbies("spamming, flaming");
		user.setSpammer(true);
		user.setToClassify(1);
		user.setPrediction(1);
		user.setConfidence(0.2);
		user.setMode("D");
		user.setAlgorithm("testlogging");
		// flag spammer (flagging does not change: user is no spammer)
		adminDb.flagSpammer(user, "fei", "off", this.dbSession);
		
		// remove spam informations
		user.setSpammer(null);
		user.setToClassify(null);
		user.setPrediction(null);
		user.setConfidence(null);
		user.setMode(null);
		
		// populate user with spam informations
		final User userRead = adminDb.getClassifierUserDetails(user, this.dbSession);
		
		// assure, that spam data is read but no other informations lost
		assertEquals(user, userRead);
		assertEquals("testMail@nomail.com", user.getEmail());
		assertEquals("spamming, flaming", user.getHobbies());
		// assertEquals(true, user.isSpammer());
		// assertEquals(0, user.getToClassify());
		assertEquals(1, user.getPrediction());
		assertEquals(0.2, user.getConfidence());
		assertEquals("D", user.getMode());
		assertEquals("testlogging", user.getAlgorithm());
	}
}