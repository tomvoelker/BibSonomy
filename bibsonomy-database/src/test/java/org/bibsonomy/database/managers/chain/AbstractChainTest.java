package org.bibsonomy.database.managers.chain;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.chain.tag.TagChain;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChain;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChain;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.params.GroupParam;
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
	
	protected TagChain tagChain;
	protected BookmarkChain bookmarkChain;
	protected BibTexChain bibtexChain;
			
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.AbstractDatabaseManagerTest#setUp()
	 */
	@Before
	public void setUp() {
		super.setUp();
		this.tagChain = new TagChain();
		this.bookmarkChain = new BookmarkChain();
		this.bibtexChain = new BibTexChain();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.AbstractDatabaseManagerTest#tearDown()
	 */
	@After
	public void tearDown() {
		super.tearDown();
		this.tagChain = null;
		this.bibtexChain = null;
		this.bookmarkChain = null;			
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.AbstractDatabaseManagerTest#resetParameters()
	 * 
	 * In order to check if the chain elements are called correctly, we need to completely
	 * reset the parameters.
	 * 
	 * @author dbe
	 */
	protected void resetParameters() {
				
		this.generalParam = new BookmarkParam();
		this.bookmarkParam = new BookmarkParam();
		this.bibtexParam = new BibTexParam();
		this.userParam = new UserParam();
		this.tagParam = new TagParam();
		this.groupParam = new GroupParam();;
		
	}
	
}
