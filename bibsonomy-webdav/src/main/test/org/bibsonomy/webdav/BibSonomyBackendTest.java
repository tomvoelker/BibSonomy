package org.bibsonomy.webdav;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bibsonomy.testutil.JNDITestDatabaseBinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.atlassian.confluence.extra.webdav.servlet.resource.CollectionResource;
import com.atlassian.confluence.extra.webdav.servlet.resource.InsufficientAuthorizationException;

/**
 * @author Christian Schenk
 * @version $Id$
 */
@Ignore
public class BibSonomyBackendTest {

	private BibSonomyBackend backend;

	/**
	 * setup
	 */
	@Before
	public void setUp() {
		JNDITestDatabaseBinder.bind();
		this.backend = new BibSonomyBackend();
	}

	/**
	 * tear down
	 */
	@After
	public void tearDown() {
		JNDITestDatabaseBinder.unbind();
	}

	/**
	 * tests authenticateUser, getCurrentUser, isUserAuthenticated and
	 * clearUserAuthentication
	 */
	@Test
	public void authentication() {
		for (final String user[] : new String[][] { { "testuser1", "test123", "11111111111111111111111111111111" }, { "testuser2", "test123", "22222222222222222222222222222222" }, { "testuser3", "test123", "33333333333333333333333333333333" } }) {
			// passwords work
			assertTrue(this.backend.authenticateUser(user[0], user[1]));
			assertNotNull(this.backend.getCurrentUser());
			assertEquals(user[0], this.backend.getCurrentUser().getFullName());
			assertTrue(this.backend.isUserAuthenticated());
			assertTrue(this.backend.clearUserAuthentication());
			assertFalse(this.backend.isUserAuthenticated());

			// API keys don't work
			assertFalse(this.backend.authenticateUser(user[0], user[2]));
		}

		// invalid
		for (final String username : new String[] { "", " ", null, "anonymous" }) {
			for (final String password : new String[] { "", " ", null, "test123" }) {
				assertFalse(this.backend.authenticateUser(username, password));
			}
		}
	}

	/**
	 * tests getLogicInterface
	 */
	@Test
	public void getLogicInterface() {
		try {
			this.backend.getLogicInterface();
			fail("expected exception");
		} catch (InsufficientAuthorizationException ignore) {
		}

		this.backend.authenticateUser("testuser1", "test123");
		assertNotNull(this.backend.getLogicInterface());
	}

	/**
	 * tests getRootResource, getChildren and getChild
	 */
	@Test
	public void getRootResource() {
		assertTrue(this.backend.getRootResource() instanceof CollectionResource);
		final CollectionResource root = (CollectionResource) this.backend.getRootResource();
		assertEquals(2, root.getChildren().size());
		assertNull(root.getChild("nonexistent"));

		// authenticate first
		this.backend.authenticateUser("testuser1", "test123");

		// test bookmark resources
		assertTrue(root.getChild("Bookmarks") instanceof CollectionResource);
		// TODO: implement BookmarkCollectionResource

		// test publication resources
		assertTrue(root.getChild("Publications") instanceof CollectionResource);
		final CollectionResource publications = (CollectionResource) root.getChild("Publications");
		assertEquals(2, publications.getChildren().size());
		assertNotNull(publications.getChild("BibSonomy: A Social Bookmark and Publication Sharing System.txt"));
	}
}