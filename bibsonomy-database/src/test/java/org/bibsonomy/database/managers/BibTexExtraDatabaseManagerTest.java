package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.model.extra.ExtendedFields;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
@Ignore // FIXME adapt to new test db
public class BibTexExtraDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	private final String BIB_TEST_HASH = "2313536a09d3af706469e3d2523fe7ca"; // INTRA-hash
	private final String TEST_USER = "thomi";
	private final String TEST_URL = "http://www.example.com/";
	private final String TEST_TXT = "This is a test...";
	
	private static BibTexExtraDatabaseManager bibTexExtraDb;
	
	/**
	 * sets up the used managers
	 */
	@BeforeClass
	public static void setupDatabaseManager() {
		bibTexExtraDb = BibTexExtraDatabaseManager.getInstance();
	}

	@Test
	public void getURL() {
		final List<BibTexExtra> extras = bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(2, extras.size());

		assertEquals("http://localhost/mywiki/literature/BG98.pdf", extras.get(0).getUrl().toString());
		assertEquals("Local", extras.get(0).getText());

		assertEquals("http://members.pingnet.ch/gamma/junit.htm", extras.get(1).getUrl().toString());
		assertEquals("Online Version", extras.get(1).getText());
	}

	@Test
	public void createURL() {
		bibTexExtraDb.createURL(this.BIB_TEST_HASH, this.TEST_USER, this.TEST_URL, TEST_TXT, this.dbSession);
		final List<BibTexExtra> extras = bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(3, extras.size());
		assertEquals(this.TEST_URL, extras.get(0).getUrl().toString());
		assertEquals(TEST_TXT, extras.get(0).getText());
	}

	@Test
	public void deleteURL() {
		bibTexExtraDb.deleteURL(this.BIB_TEST_HASH, this.TEST_USER, "http://localhost/mywiki/literature/BG98.pdf", this.dbSession);
		final List<BibTexExtra> extras = bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(1, extras.size());
		assertEquals("http://members.pingnet.ch/gamma/junit.htm", extras.get(0).getUrl().toString());
	}

	@Test
	public void deleteAllURLs() {
		// 925724 is the contentId for the hash b6c9a44d411bf8101abdf809d5df1431
		bibTexExtraDb.deleteAllURLs(925724, this.dbSession);
		final List<BibTexExtra> extras = bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(0, extras.size());
	}

	@Test
	public void updateURL() {
		bibTexExtraDb.updateURL(925724, 12345678, this.dbSession);
	}

	@Test
	public void getBibTexPrivnoteForUser() {
		final String note = bibTexExtraDb.getBibTexPrivnoteForUser("6e955a315951954a8030b79cece1e314", "siko", this.dbSession);
		assertEquals("test", note);
	}

	@Test
	public void updateBibTexPrivnoteForUser() {
		bibTexExtraDb.updateBibTexPrivnoteForUser(this.BIB_TEST_HASH, this.TEST_USER, this.TEST_TXT, this.dbSession);
		final String note = bibTexExtraDb.getBibTexPrivnoteForUser(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(this.TEST_TXT, note);
	}

	@Test
	public void updateDocument() {
		bibTexExtraDb.updateDocument(813954, 12345678, this.dbSession);
	}

	@Test
	public void getExtendedFields() {
		final List<ExtendedFields> extendedFields = bibTexExtraDb.getExtendedFields("d6f94bd4bebd899dd38cab0873dbcb64", "xamde", this.dbSession);
		assertEquals(7, extendedFields.size());
		for (final ExtendedFields extendedField : extendedFields) {
			assertEquals(10, extendedField.getGroupId());
			assertTrue(extendedField.getOrder() >= 3 && extendedField.getOrder() <= 9);
			assertNotNull(extendedField.getKey());
			assertNotNull(extendedField.getValue());
		}
	}

	@Test
	public void deleteExtendedFieldsData() {
		bibTexExtraDb.deleteExtendedFieldsData(783786, this.dbSession);
		final List<ExtendedFields> extendedFields = bibTexExtraDb.getExtendedFields("d6f94bd4bebd899dd38cab0873dbcb64", "xamde", this.dbSession);
		assertEquals(0, extendedFields.size());
	}

	@Test
	public void updateExtendedFieldsData() {
		bibTexExtraDb.updateExtendedFieldsData(783786, 12345678, this.dbSession);
	}
}