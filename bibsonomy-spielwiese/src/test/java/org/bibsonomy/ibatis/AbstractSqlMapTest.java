package org.bibsonomy.ibatis;

import java.io.IOException;
import java.util.List;

import org.bibsonomy.ibatis.db.impl.DatabaseManager;
import org.bibsonomy.ibatis.params.BibTexParam;
import org.bibsonomy.ibatis.params.BookmarkParam;
import org.bibsonomy.ibatis.util.ParamUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Tag;
import org.junit.After;
import org.junit.Before;

/**
 * This class provides a connection to the database and convenience methods to
 * print both bookmarks and BibTexs.
 * 
 * @author Christian Schenk
 */
public abstract class AbstractSqlMapTest {

	/** Communication with the database is done with this class */
	protected DatabaseManager db;
	/** This param can be used both for queries about bookmarks and all other queries */
	protected BookmarkParam bookmarkParam;
	/** This param can be used for queries about BibTexs */
	protected BibTexParam bibtexParam;

	@Before
	public void setUp() throws IOException {
		this.db = DatabaseManager.getInstance();
		this.bookmarkParam = ParamUtils.getDefaultBookmarkParam();
		this.bibtexParam = ParamUtils.getDefaultBibTexParam();
	}

	@After
	public void tearDown() {
		this.db = null;
		this.bookmarkParam = null;
		this.bibtexParam = null;
	}

	/**
	 * Convenience method to print a list of bookmarks.
	 */
	protected void printBookmarks(final List<Bookmark> bookmarks) {
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

	/**
	 * Convenience method to print a list of BibTexs.
	 */
	protected void printBibTex(final List<BibTex> bibtexs) {
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