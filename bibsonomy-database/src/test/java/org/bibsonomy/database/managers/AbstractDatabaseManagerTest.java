package org.bibsonomy.database.managers;

import java.io.IOException;
import java.util.List;

import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.managers.GenericChainHandler;
import org.bibsonomy.database.managers.TagDatabaseManager;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.testutil.ParamUtils;
import org.junit.After;
import org.junit.Before;

/**
 * This class provides a connection to the database and convenience methods to
 * print both bookmarks and BibTexs. Every class that implements tests for
 * methods which interact with the database should be derived from this class.
 * 
 * @author Christian Schenk
 */
public abstract class AbstractDatabaseManagerTest {

	/** The database manager for general queries */
	protected GeneralDatabaseManager generalDb;
	/** The database manager for Bookmarks */
	protected BookmarkDatabaseManager bookmarkDb;
	/** The database manager for BibTexs */
	protected BibTexDatabaseManager bibTexDb;
	/** The database manager for Tags */
	protected TagDatabaseManager tagDb;
	/** The chain handler */
	protected GenericChainHandler chainHandler;
	/**
	 * This param can be used both for queries about bookmarks and all other
	 * queries
	 */
	protected BookmarkParam bookmarkParam;
	/** This param can be used for queries about BibTexs */
	protected BibTexParam bibtexParam;

	@Before
	public void setUp() throws IOException {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.bookmarkDb = BookmarkDatabaseManager.getInstance();
		this.bibTexDb = BibTexDatabaseManager.getInstance();
		this.tagDb = TagDatabaseManager.getInstance();
		this.chainHandler = GenericChainHandler.getInstance();
		this.resetParameters();
		// testcases shouldn't write to the db
		this.generalDb.setReadonly();
		this.bookmarkDb.setReadonly();
		this.bibTexDb.setReadonly();
		this.tagDb.setReadonly();
	}

	@After
	public void tearDown() {
		this.generalDb = null;
		this.bookmarkDb = null;
		this.bibTexDb = null;
		this.tagDb = null;
		this.chainHandler = null;
		this.bookmarkParam = null;
		this.bibtexParam = null;
	}

	/**
	 * Resets the parameter objects, which can be useful inside one method of a
	 * testcase. On some occasions we need to do this, e.g. when more than one
	 * query is involved and the results from one query are stored in the
	 * parameter object so they can be used in the next query: in this case the
	 * parameter object is altered which can lead to side effects in the
	 * following queries.<br/>
	 * 
	 * This is done before running a testcase method.
	 */
	protected void resetParameters() {
		this.bookmarkParam = ParamUtils.getDefaultBookmarkParam();
		this.bibtexParam = ParamUtils.getDefaultBibTexParam();
	}

	/**
	 * Convenience method to print a list of bookmarks.
	 */
	protected void printBookmarks(final List<Bookmark> bookmarks) {
		// TODO doesn't fit the model anymore
//		for (final Bookmark bookmark : bookmarks) {
//			System.out.println("ContentId   : " + bookmark.getContentId());
//			System.out.println("Description : " + bookmark.getDescription());
//			System.out.println("Extended    : " + bookmark.getExtended());
//			System.out.println("Date        : " + bookmark.getDate());
//			System.out.println("URL         : " + bookmark.getUrl());
//			System.out.println("URLHash     : " + bookmark.getHash());
//			System.out.println("UserName    : " + bookmark.getUserName());
//			System.out.print("Tags        : ");
//			for (final Tag tag : bookmark.getTags()) {
//				System.out.print(tag.getName() + " ");
//			}
//			System.out.println("\n");
//		}

//		for (final Post<Resource> post : list) {
//			final Bookmark resource = (Bookmark) post.getResource();
//			System.out.println(resource.getContentId());
//			System.out.println(resource.getUrlHash());
//			System.out.println("Tags: " + post.getTags());
//			System.out.println("User: " + post.getUser().getName());
//			System.out.println("-----------------------------");
//		}
	}

	/**
	 * Convenience method to print a list of BibTexs.
	 */
	protected void printBibTex(final List<BibTex> bibtexs) {
		// TODO doesn't fit the model anymore
//		for (final BibTex bibtex : bibtexs) {
//			System.out.println("Address          : " + bibtex.getAddress());
//			System.out.println("Annote           : " + bibtex.getAnnote());
//			System.out.println("Author           : " + bibtex.getAuthor());
//			System.out.println("BibTexAbstract   : " + bibtex.getBibtexAbstract());
//			System.out.println("BibTexKey        : " + bibtex.getBibtexKey());
//			System.out.println("BKey             : " + bibtex.getBKey());
//			System.out.println("Booktitle        : " + bibtex.getBooktitle());
//			System.out.println("Chapter          : " + bibtex.getChapter());
//			System.out.println("Crossref         : " + bibtex.getCrossref());
//			System.out.println("Day              : " + bibtex.getDay());
//			System.out.println("Description      : " + bibtex.getDescription());
//			System.out.println("Edition          : " + bibtex.getEdition());
//			System.out.println("Editor           : " + bibtex.getEditor());
//			System.out.println("Entrytype        : " + bibtex.getEntrytype());
//			System.out.println("HowPublished     : " + bibtex.getHowpublished());
//			System.out.println("Instution        : " + bibtex.getInstitution());
//			System.out.println("Journal          : " + bibtex.getJournal());
//			System.out.println("Misc             : " + bibtex.getMisc());
//			System.out.println("Month            : " + bibtex.getMonth());
//			System.out.println("Note             : " + bibtex.getNote());
//			System.out.println("Number           : " + bibtex.getNumber());
//			System.out.println("Organization     : " + bibtex.getOrganization());
//			System.out.println("Pages            : " + bibtex.getPages());
//			System.out.println("Publisher        : " + bibtex.getPublisher());
//			System.out.println("School           : " + bibtex.getSchool());
//			System.out.println("Series           : " + bibtex.getSeries());
//			System.out.println("Title            : " + bibtex.getTitle());
//			System.out.println("UserName         : " + bibtex.getUserName());
//			System.out.println("Volume           : " + bibtex.getVolume());
//			System.out.println("Year             : " + bibtex.getYear());
//			System.out.println("Url              : " + bibtex.getUrl());
//			System.out.print("Tags             : ");
//			for (final Tag tag : bibtex.getTags()) {
//				System.out.print(tag.getName() + " ");
//			}
//			System.out.println("\n");
//		}
	}
}