package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.bibsonomy.model.extra.BibTexExtra;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
// FIXME adapt to new test db
public class BibTexExtraDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	private final String BIB_TEST_HASH = "b77ddd8087ad8856d77c740c8dc2864a"; // INTRA-hash
	private final String TEST_USER = "testuser1";
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

	@Ignore @Test
	public void getURL() {
		final List<BibTexExtra> extras = bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(2, extras.size());

		assertEquals("http://localhost/mywiki/literature/BG98.pdf", extras.get(0).getUrl().toString());
		assertEquals("Local", extras.get(0).getText());

		assertEquals("http://members.pingnet.ch/gamma/junit.htm", extras.get(1).getUrl().toString());
		assertEquals("Online Version", extras.get(1).getText());
	}

	@Ignore @Test
	public void createURL() {
		bibTexExtraDb.createURL(this.BIB_TEST_HASH, this.TEST_USER, this.TEST_URL, TEST_TXT, this.dbSession);
		final List<BibTexExtra> extras = bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(3, extras.size());
		assertEquals(this.TEST_URL, extras.get(0).getUrl().toString());
		assertEquals(TEST_TXT, extras.get(0).getText());
	}

	@Ignore @Test
	public void deleteURL() {
		bibTexExtraDb.deleteURL(this.BIB_TEST_HASH, this.TEST_USER, "http://localhost/mywiki/literature/BG98.pdf", this.dbSession);
		final List<BibTexExtra> extras = bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(1, extras.size());
		assertEquals("http://members.pingnet.ch/gamma/junit.htm", extras.get(0).getUrl().toString());
	}

	@Ignore @Test
	public void deleteAllURLs() {
		// 925724 is the contentId for the hash b6c9a44d411bf8101abdf809d5df1431
		bibTexExtraDb.deleteAllURLs(925724, this.dbSession);
		final List<BibTexExtra> extras = bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(0, extras.size());
	}

	@Ignore @Test
	public void updateURL() {
		bibTexExtraDb.updateURL(925724, 12345678, this.dbSession);
	}

	@Ignore @Test
	public void getBibTexPrivnoteForUser() {
		final String note = bibTexExtraDb.getBibTexPrivnoteForUser("6e955a315951954a8030b79cece1e314", "siko", this.dbSession);
		assertEquals("test", note);
	}

	@Ignore @Test
	public void updateBibTexPrivnoteForUser() {
		bibTexExtraDb.updateBibTexPrivnoteForUser(this.BIB_TEST_HASH, this.TEST_USER, this.TEST_TXT, this.dbSession);
		final String note = bibTexExtraDb.getBibTexPrivnoteForUser(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
		assertEquals(this.TEST_TXT, note);
	}

	@Ignore @Test
	public void updateDocument() {
		bibTexExtraDb.updateDocument(813954, 12345678, this.dbSession);
	}
	
	@Test 
	public void insertExtendedField() {
	    Map<String, List<String>> extendedFieldList = bibTexExtraDb.getExtendedFields("testuser1", "b77ddd8087ad8856d77c740c8dc2864a", this.dbSession);
	    assertEquals(1, extendedFieldList.size());
	    
	    List<String> keys = extendedFieldList.get(extendedFieldList.keySet().iterator().next());
	    
	    assertEquals(3, keys.size());
	    
	    bibTexExtraDb.createExtendedField("testuser1", "b77ddd8087ad8856d77c740c8dc2864a", "ACM", "TEST", this.dbSession);
	    
	    extendedFieldList = bibTexExtraDb.getExtendedFields("testuser1", "b77ddd8087ad8856d77c740c8dc2864a", this.dbSession);
	    assertEquals(2, extendedFieldList.size());
	    
	}
	
	@Test
	public void getExtendedFieldByKey() {
	    Map<String, List<String>> exFields = bibTexExtraDb.getExtendedFieldsByKey("testuser2", "1b298f199d487bc527a62326573892b8", "JEL", this.dbSession);
	    
	    List<String> keys = exFields.get(exFields.keySet().iterator().next());
	    
	    assertEquals(3, keys.size());
	
	}

	/**
	 * Tested in {@link #insertExtendedField()}
	 */
	@Ignore @Test
	public void getExtendedFields() {
	}

	@Test
	public void deleteExtendedFieldsData() {		
	    	Map<String, List<String>> extendedFields = bibTexExtraDb.getExtendedFields("testuser1", "b77ddd8087ad8856d77c740c8dc2864a", this.dbSession);

		assertEquals(2, extendedFields.size());
		extendedFields = bibTexExtraDb.getExtendedFieldsByKey("testuser1", "b77ddd8087ad8856d77c740c8dc2864a","ACM", this.dbSession);

		assertEquals(1, extendedFields.size());
		
		bibTexExtraDb.deleteExtendedFieldByKeyValue("testuser1", "b77ddd8087ad8856d77c740c8dc2864a", "ACM", "TEST", this.dbSession);
		extendedFields = bibTexExtraDb.getExtendedFields("testuser1", "b77ddd8087ad8856d77c740c8dc2864a", this.dbSession);
		
		assertEquals(1, extendedFields.size());
		
		extendedFields = bibTexExtraDb.getExtendedFieldsByKey("testuser1", "b77ddd8087ad8856d77c740c8dc2864a","ACM", this.dbSession);

		assertEquals(0, extendedFields.size());
		
	}
	
	@Ignore @Test
	public void updateExtendedFieldsData() {
		bibTexExtraDb.updateExtendedFieldsData(783786, 12345678, this.dbSession);
	}
}