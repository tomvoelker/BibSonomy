package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.model.extra.ExtendedFields;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
@Ignore
// FIXME adapt to new test db
public class BibTexExtraDatabaseManagerTest extends AbstractDatabaseManagerTest {

	// private final String BIB_TEST_HASH = "b6c9a44d411bf8101abdf809d5df1431"; // INTER-hash
	private final String BIB_TEST_HASH = "2313536a09d3af706469e3d2523fe7ca"; // INTRA-hash
	private final String TEST_USER = "thomi";
	private final String TEST_URL = "http://www.example.com/";
	private final String TEST_TXT = "This is a test...";
	private final String DOC_TEST_HASH = "04337cc3ad69663c4a4636ddade8221b";
	private final String DOC_TEST_USER = "cupodmac";

	@Test
	public void getURL() {
		final List<BibTexExtra> extras = this.bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(2, extras.size());

		assertEquals("http://localhost/mywiki/literature/BG98.pdf", extras.get(0).getUrl().toString());
		assertEquals("Local", extras.get(0).getText());
		// assertEquals("Fri Dec 22 09:49:10 CET 2006", extras.get(0).getDate().toString());

		assertEquals("http://members.pingnet.ch/gamma/junit.htm", extras.get(1).getUrl().toString());
		assertEquals("Online Version", extras.get(1).getText());
		// assertEquals("Fri Dec 22 09:49:10 CET 2006", extras.get(1).getDate().toString());
	}

	@Test
	public void createURL() {
		this.bibTexExtraDb.createURL(this.BIB_TEST_HASH, this.TEST_USER, this.TEST_URL, TEST_TXT, this.dbSession);
		final List<BibTexExtra> extras = this.bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(3, extras.size());
		assertEquals(this.TEST_URL, extras.get(0).getUrl().toString());
		assertEquals(TEST_TXT, extras.get(0).getText());
	}

	@Test
	public void deleteURL() {
		this.bibTexExtraDb.deleteURL(this.BIB_TEST_HASH, this.TEST_USER, "http://localhost/mywiki/literature/BG98.pdf", this.dbSession);
		final List<BibTexExtra> extras = this.bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(1, extras.size());
		assertEquals("http://members.pingnet.ch/gamma/junit.htm", extras.get(0).getUrl().toString());
	}

	@Test
	public void deleteAllURLs() {
		// 925724 is the contentId for the hash b6c9a44d411bf8101abdf809d5df1431
		this.bibTexExtraDb.deleteAllURLs(925724, this.dbSession);
		final List<BibTexExtra> extras = this.bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(0, extras.size());
	}

	@Test
	public void updateURL() {
		this.bibTexExtraDb.updateURL(925724, 12345678, this.dbSession);
	}

	@Test
	public void getBibTexPrivnoteForUser() {
		final String note = this.bibTexExtraDb.getBibTexPrivnoteForUser("6e955a315951954a8030b79cece1e314", "siko", this.dbSession);
		assertEquals("test", note);
	}

	@Test
	public void updateBibTexPrivnoteForUser() {
		this.bibTexExtraDb.updateBibTexPrivnoteForUser(this.BIB_TEST_HASH, this.TEST_USER, this.TEST_TXT, this.dbSession);
		final String note = this.bibTexExtraDb.getBibTexPrivnoteForUser(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(this.TEST_TXT, note);
	}

	@Test
	public void getDocumentByHash() {
		for (final String hash : new String[] { "", null, this.DOC_TEST_HASH }) {
			try {
				this.bibTexExtraDb.getDocumentByHash(hash, this.dbSession);
				fail("Should throw exception");
			} catch (final RuntimeException ex) {
			}
		}
	}

	@Test
	public void getDocumentByHashAndUser() {
		final String filename = this.bibTexExtraDb.getDocumentByHashAndUser(this.DOC_TEST_HASH, this.DOC_TEST_USER, this.dbSession);
		assertEquals("Latex_Suendenregister.pdf", filename);
	}

	@Test
	public void deleteDocument() {
		// 813954 is the contentId for the hash 04337cc3ad69663c4a4636ddade8221b
		this.bibTexExtraDb.deleteDocument(813954, this.dbSession);
		assertEquals(null, this.bibTexExtraDb.getDocumentByHashAndUser(this.DOC_TEST_HASH, this.DOC_TEST_USER, this.dbSession));
	}

	@Test
	public void updateDocument() {
		this.bibTexExtraDb.updateDocument(813954, 12345678, this.dbSession);
	}

	@Test
	public void getExtendedFields() {
		final List<ExtendedFields> extendedFields = this.bibTexExtraDb.getExtendedFields("d6f94bd4bebd899dd38cab0873dbcb64", "xamde", this.dbSession);
		assertEquals(7, extendedFields.size());
		for (final ExtendedFields extendedField : extendedFields) {
			assertEquals(10, extendedField.getGroupId());
			assertTrue(extendedField.getOrder() >= 3 && extendedField.getOrder() <= 9);
			assertNotNull(extendedField.getKey());
			assertNotNull(extendedField.getValue());
			// assertEquals("Wed Nov 22 10:47:19 CET 2006", extendedField.getCreated().toString());
			// assertEquals("Wed Nov 22 10:47:19 CET 2006", extendedField.getLastModified().toString());
		}
	}

	@Test
	public void deleteExtendedFieldsData() {
		this.bibTexExtraDb.deleteExtendedFieldsData(783786, this.dbSession);
		final List<ExtendedFields> extendedFields = this.bibTexExtraDb.getExtendedFields("d6f94bd4bebd899dd38cab0873dbcb64", "xamde", this.dbSession);
		assertEquals(0, extendedFields.size());
	}

	@Test
	public void updateExtendedFieldsData() {
		this.bibTexExtraDb.updateExtendedFieldsData(783786, 12345678, this.dbSession);
	}

}