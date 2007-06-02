package org.bibsonomy.database.managers.chain;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChain;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChain;
import org.bibsonomy.database.managers.chain.tag.TagChain;
import org.junit.After;
import org.junit.Before;

/**
 * Abstract class to test the chains. It reuses the database connection setup/clean methods
 * of AbstractDatabaseManagerTest. 
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class AbstractChainTest extends AbstractDatabaseManagerTest {

	protected BookmarkChain bookmarkChain;
	protected BibTexChain bibtexChain;
	protected TagChain tagChain;

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.AbstractDatabaseManagerTest#setUp()
	 */
	@Before
	public void setUp() {
		super.setUp();
		this.bookmarkChain = new BookmarkChain();
		this.bibtexChain = new BibTexChain();
		this.tagChain = new TagChain();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.AbstractDatabaseManagerTest#tearDown()
	 */
	@After
	public void tearDown() {
		super.tearDown();
		this.bookmarkChain = null;
		this.bibtexChain = null;
		this.tagChain = null;
	}
}