package org.bibsonomy.database.managers.chain;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.chain.tag.TagChain;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChain;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChain;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.testutil.ParamUtils;
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
	
	
	// parameters
	protected String authUser; 
	protected GroupingEntity grouping; 
	protected String groupingName; 
	protected List<String> tags; 
	protected String hash; 
	protected boolean popular;
	protected boolean added; 
	protected int start; 
	protected int end; 			
	protected String regex;
	protected Boolean subTags;
	protected Boolean superTags;
	protected Boolean subSuperTagsTransitive;
	protected String tagName;	
	
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
		
		this.authUser = null; 
		this.grouping = null; 
		this.groupingName = null; 
		this.tags = null; 
		this.hash = null; 
		this.popular = false;
		this.added = false; 
		this.start = 0; 
		this.end = 0; 			
		this.regex = null;
		this.subTags = null;
		this.superTags = null;
		this.subSuperTagsTransitive = null;
		this.tagName = null;		
	}
	
	protected void resetParameters() {
		super.resetParameters();
		this.authUser = null; 
		this.grouping = null; 
		this.groupingName = null; 
		this.tags = null; 
		this.hash = null; 
		this.popular = false;
		this.added = false; 
		this.start = 0; 
		this.end = 0; 			
		this.regex = null;
		this.subTags = null;
		this.superTags = null;
		this.subSuperTagsTransitive = null;
		this.tagName = null;			
	}
	
}
