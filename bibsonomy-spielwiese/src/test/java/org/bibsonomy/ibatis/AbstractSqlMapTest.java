package org.bibsonomy.ibatis;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

import org.bibsonomy.ibatis.params.BibTexParam;
import org.bibsonomy.ibatis.params.BookmarkParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Tag;
import org.junit.After;
import org.junit.Before;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * This class provides iBATIS-tests with the necessary SqlMapClient.
 * 
 * @author Christian Schenk
 */
public abstract class AbstractSqlMapTest {

	/** Communication with the database is done with the sqlMap */
	protected SqlMapClient sqlMap;
	protected BookmarkParam bookmarkParam;
	protected BibTexParam bibtexParam;
	protected boolean print;

	private enum QueryTemplate {
		BOOKMARK, BIBTEX
	};

	@Before
	public void setUp() throws IOException {
		final String resource = "SqlMapConfig.xml";
		final Reader reader = Resources.getResourceAsReader(resource);
		this.sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
		this.bookmarkParam = TestHelper.getDefaultBookmarkParam();
		this.bibtexParam = TestHelper.getDefaultBibTexParam();
		this.print = false;
	}

	@After
	public void tearDown() {
		this.sqlMap = null;
		this.bookmarkParam = null;
		this.bibtexParam = null;
	}

	/**
	 * Can be used to start a query that retrieves a list of bookmarks.
	 */
	protected void bookmarkTemplate(final String query) {
		this.template(query, QueryTemplate.BOOKMARK);
	}

	/**
	 * Can be used to start a query that retrieves a list of BibTexs.
	 */
	protected void bibtexTemplate(final String query) {
		this.template(query, QueryTemplate.BIBTEX);
	}

	@SuppressWarnings("unchecked")
	private void template(final String query, final QueryTemplate template) {
		try {
			switch (template) {
			case BOOKMARK:
				final List<Bookmark> bookmarks = this.sqlMap.queryForList(query, this.bookmarkParam);
				printBookmarks(bookmarks);
				break;
			case BIBTEX:
				final List<BibTex> bibtexs = this.sqlMap.queryForList(query, this.bibtexParam);
				printBibTex(bibtexs);
				break;
			}
		} catch (final SQLException ex) {
			ex.printStackTrace();
			fail("SQLException");
		}
	}

	private void printBookmarks(final List<Bookmark> bookmarks) {
		if (!this.print) return;
		for (final Bookmark bookmark : bookmarks) {
			System.out.println("ContentId   : " + bookmark.getContentId());
			System.out.println("Description : " + bookmark.getDescription());
			System.out.println("Extended    : " + bookmark.getExtended());
			System.out.println("Date        : " + bookmark.getDate());
			System.out.println("URL         : " + bookmark.getUrl());
			System.out.println("URLHash     : " + bookmark.getUrlHash());
			System.out.println("UserName    : " + bookmark.getUserName());
			System.out.print("Tags        : ");
			for (final Tag tag : bookmark.getTags()) {
				System.out.print(tag.getName() + " ");
			}
			System.out.println("\n");
		}
	}

	private void printBibTex(final List<BibTex> bibtexs) {
		if (!this.print) return;
		for (final BibTex bibtex : bibtexs) {
			System.out.println("Address          : " + bibtex.getAddress());
			System.out.println("Annote           : " + bibtex.getAnnote());
			System.out.println("Author           : " + bibtex.getAuthor());
			System.out.println("BibTexAbstract   : " + bibtex.getBibtexAbstract());
			System.out.println("BibTexKey        : " + bibtex.getBibtexKey());
			System.out.println("Bkey             : " + bibtex.getBkey());
			System.out.println("Booktitle        : " + bibtex.getBooktitle());
			System.out.println("Chapter          : " + bibtex.getChapter());
			System.out.println("Crossref         : " + bibtex.getCrossref());
			System.out.println("Day              : " + bibtex.getDay());
			System.out.println("Description      : " + bibtex.getDescription());
			System.out.println("Edition          : " + bibtex.getEdition());
			System.out.println("Editor           : " + bibtex.getEditor());
			System.out.println("Entrytype        : " + bibtex.getEntrytype());
			System.out.println("Group            : " + bibtex.getGroup());
			System.out.println("HowPublished     : " + bibtex.getHowpublished());
			System.out.println("Instution        : " + bibtex.getInstitution());
			System.out.println("Journal          : " + bibtex.getJournal());
			System.out.println("Misc             : " + bibtex.getMisc());
			System.out.println("Month            : " + bibtex.getMonth());
			System.out.println("Note             : " + bibtex.getNote());
			System.out.println("Number           : " + bibtex.getNumber());
			System.out.println("Organization     : " + bibtex.getOrganization());
			System.out.println("Pages            : " + bibtex.getPages());
			System.out.println("Publisher        : " + bibtex.getPublisher());
			System.out.println("School           : " + bibtex.getSchool());
			System.out.println("Series           : " + bibtex.getSeries());
			System.out.println("Title            : " + bibtex.getTitle());
			System.out.println("UserName         : " + bibtex.getUserName());
			System.out.println("Volume           : " + bibtex.getVolume());
			System.out.println("Year             : " + bibtex.getYear());
			System.out.println("Url              : " + bibtex.getUrl());
			System.out.println("Ctr              : " + bibtex.getCtr());
			System.out.print("Tags             : ");
			for (final Tag tag : bibtex.getTags()) {
				System.out.print(tag.getName() + " ");
			}
			System.out.println("\n");
		}
	}
}