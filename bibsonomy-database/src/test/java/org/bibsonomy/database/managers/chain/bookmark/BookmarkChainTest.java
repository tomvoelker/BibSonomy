package org.bibsonomy.database.managers.chain.bookmark;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test related to the Bookmark Chain
 * 
 * @author Dominik Benz
 * @version $Id$
 */
@Ignore
public class BookmarkChainTest extends AbstractChainTest {

	List<FirstChainElement<Bookmark, BookmarkParam>> chains = new LinkedList<FirstChainElement<Bookmark, BookmarkParam>>();
	String authUser="grahl";
	GroupingEntity grouping=GroupingEntity.USER;
	String groupingName="grahl";
	List<String> tags; 
	String hash; 
	boolean popular=false; 
	boolean added=false;
	int start=0; 
	int end=19;

	@Test
	public void chainTest() {
		/*for (final FirstChainElement chain : this.chains) {
			
			final List<Post<? extends Resource>> list =chain.getFirstElement().perform(authUser, grouping, groupingName, tags, hash, popular, added, start, end);
             
			if (list != null){
				
			String e="Warnung";
			System.out.println(e);
				
			}
		}*/
	}
}