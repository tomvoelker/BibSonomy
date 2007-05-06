package org.bibsonomy.database.managers.chain.bookmark;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.junit.Test;

public class BookmarkChainTest {

	List<FirstChainElement> chains = new LinkedList<FirstChainElement>();
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