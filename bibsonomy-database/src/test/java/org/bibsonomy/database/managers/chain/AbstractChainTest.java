package org.bibsonomy.database.managers.chain;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChain;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChain;
import org.bibsonomy.database.managers.chain.concept.ConceptChain;
import org.bibsonomy.database.managers.chain.statistic.post.PostStatisticChain;
import org.bibsonomy.database.managers.chain.tag.TagChain;
import org.bibsonomy.database.managers.chain.user.UserChain;
import org.junit.After;
import org.junit.Before;

/**
 * Abstract class to test the chains. It reuses the database connection setup/clean methods
 * of {@link AbstractDatabaseManagerTest}. 
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public abstract class AbstractChainTest extends AbstractDatabaseManagerTest {

	protected BookmarkChain bookmarkChain;
	protected BibTexChain bibtexChain;
	protected PostStatisticChain postStatisticsChain;
	protected TagChain tagChain;
	protected ConceptChain conceptChain;
	protected UserChain userChain;
	protected ChainStatus chainStatus;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		this.bookmarkChain = new BookmarkChain();
		this.bibtexChain = new BibTexChain();
		this.postStatisticsChain = new PostStatisticChain();
		this.tagChain = new TagChain();
		this.conceptChain = new ConceptChain();
		this.userChain = new UserChain();
		this.chainStatus = new ChainStatus();
	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
		this.bookmarkChain = null;
		this.bibtexChain = null;
		this.postStatisticsChain = null;
		this.tagChain = null;
		this.conceptChain = null;
		this.chainStatus = null;
	}
}