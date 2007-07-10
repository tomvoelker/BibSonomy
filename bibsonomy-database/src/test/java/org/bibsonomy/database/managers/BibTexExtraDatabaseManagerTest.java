package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.util.List;

import org.bibsonomy.model.BibTexExtra;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexExtraDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private final String TEST_HASH = "b6c9a44d411bf8101abdf809d5df1431";
	private final String TEST_USER = "thomi";
	private final String TEST_URL = "http://www.example.com/";
	private final String TEST_TXT = "This is a test...";

	@Test
	public void getURL() {
		final List<BibTexExtra> extras = this.bibTexExtraDb.getURL(this.TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(2, extras.size());

		assertEquals("http://localhost/mywiki/literature/BG98.pdf", extras.get(0).getUrl().toString());
		assertEquals("Local", extras.get(0).getText());
		assertEquals("Fri Dec 22 09:49:10 CET 2006", extras.get(0).getDate().toString());

		assertEquals("http://members.pingnet.ch/gamma/junit.htm", extras.get(1).getUrl().toString());
		assertEquals("Online Version", extras.get(1).getText());
		assertEquals("Fri Dec 22 09:49:10 CET 2006", extras.get(1).getDate().toString());
	}

	@Test
	public void createURL() throws MalformedURLException {
		this.bibTexExtraDb.createURL(this.TEST_HASH, this.TEST_USER, this.TEST_URL, TEST_TXT, this.dbSession);
		final List<BibTexExtra> extras = this.bibTexExtraDb.getURL("b6c9a44d411bf8101abdf809d5df1431", this.TEST_USER, this.dbSession);
		assertEquals(3, extras.size());
		assertEquals(this.TEST_URL, extras.get(0).getUrl().toString());
		assertEquals(TEST_TXT, extras.get(0).getText());
	}

	@Test
	public void deleteURL() throws MalformedURLException {
		this.bibTexExtraDb.deleteURL(this.TEST_HASH, this.TEST_USER, "http://localhost/mywiki/literature/BG98.pdf", this.dbSession);
		final List<BibTexExtra> extras = this.bibTexExtraDb.getURL("b6c9a44d411bf8101abdf809d5df1431", this.TEST_USER, this.dbSession);
		assertEquals(1, extras.size());
		assertEquals("http://members.pingnet.ch/gamma/junit.htm", extras.get(0).getUrl().toString());
	}

	@Test
	public void getBibTexPrivnoteForUser() {
		final String note = this.bibTexExtraDb.getBibTexPrivnoteForUser("628119945ae766f2e938731644847382", "siko", this.dbSession);
		assertEquals("test", note);
	}

	@Test
	public void updateBibTexPrivnoteForUser() {
		this.bibTexExtraDb.updateBibTexPrivnoteForUser(this.TEST_HASH, this.TEST_USER, this.TEST_TXT, this.dbSession);
		final String note = this.bibTexExtraDb.getBibTexPrivnoteForUser(this.TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(this.TEST_TXT, note);
	}
}