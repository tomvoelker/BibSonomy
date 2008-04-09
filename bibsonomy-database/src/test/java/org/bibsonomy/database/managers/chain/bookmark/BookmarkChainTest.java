package org.bibsonomy.database.managers.chain.bookmark;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.AbstractChainTest;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test related to the Bookmark Chain
 * 
 * @author miranda
 * @version $Id$
 */

public class BookmarkChainTest extends AbstractChainTest {

	@Test
	public void getBookmarkByConceptForUser() {
		this.bookmarkParam.setGrouping(GroupingEntity.USER);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setRequestedUserName("hotho");
		this.bookmarkParam.setNumSimpleConcepts(3);
		this.bookmarkParam.setNumSimpleTags(0);
		this.bookmarkParam.setNumTransitiveConcepts(0);
		final List<Post<Bookmark>> posts=this.bookmarkChain.getFirstElement().perform(bookmarkParam,dbSession);
	
	}
	
	@Test
	public void getBookmarkByFriends() {
		this.bookmarkParam.setGrouping(GroupingEntity.FRIEND);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		final List<Post<Bookmark>> posts=this.bookmarkChain.getFirstElement().perform(bookmarkParam,dbSession);	
		
	}
	@Test
	public void getBookmarkByHash() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		
		final List<Post<Bookmark>> posts=this.bookmarkChain.getFirstElement().perform(bookmarkParam,dbSession);
	}
	
	@Test
	public void getBookmarkByHashForUser() {
		this.bookmarkParam.setGrouping(GroupingEntity.USER);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		
		final List<Post<Bookmark>> posts=this.bookmarkChain.getFirstElement().perform(bookmarkParam,dbSession);
	
	}
	@Test
	public void getBookmarkByTagNames() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		
		final List<Post<Bookmark>> posts=this.bookmarkChain.getFirstElement().perform(bookmarkParam,dbSession);
	}
	@Test
	public void getBookmarkByTagNamesAndUser() {
		this.bookmarkParam.setUserName("grahl");
		this.bookmarkParam.setGrouping(GroupingEntity.USER);
		this.bookmarkParam.setRequestedUserName("grahl");
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		
		final List<Post<Bookmark>> posts=this.bookmarkChain.getFirstElement().perform(bookmarkParam,dbSession);
	}
	@Test
	public void getBookmarkForGroup() {
		this.bookmarkParam.setGrouping(GroupingEntity.GROUP);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		
		final List<Post<Bookmark>> posts=this.bookmarkChain.getFirstElement().perform(bookmarkParam,dbSession);
	}
	@Test
	public void getBookmarkForGroupAndTag() {
		this.bookmarkParam.setGrouping(GroupingEntity.GROUP);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		
		final List<Post<Bookmark>> posts=this.bookmarkChain.getFirstElement().perform(bookmarkParam,dbSession);
	
	}
	@Test
	public void getBookmarkForHomePage() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		
		final List<Post<Bookmark>> posts=this.bookmarkChain.getFirstElement().perform(bookmarkParam,dbSession);
	
	
	}
	@Test
	public void getBookmarkForUser() {
		this.bookmarkParam.setGrouping(GroupingEntity.USER);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		final List<Post<Bookmark>> posts=this.bookmarkChain.getFirstElement().perform(bookmarkParam,dbSession);
	}
	@Test
	public void getBookmarkofFriendsByTags() {
		this.bookmarkParam.setGrouping(GroupingEntity.FRIEND);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setRequestedUserName(null);
		final List<Post<Bookmark>> posts=this.bookmarkChain.getFirstElement().perform(bookmarkParam,dbSession);
	}
	@Test
	public void getBookmarkofFriendsByUser() {
		this.bookmarkParam.setGrouping(GroupingEntity.FRIEND);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		this.bookmarkParam.setRequestedUserName(null);
		final List<Post<Bookmark>> posts=this.bookmarkChain.getFirstElement().perform(bookmarkParam,dbSession);
	}
	@Test
	public void getBookmarkPopular() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(Order.POPULAR);
		this.bookmarkParam.setTagIndex(null);
		
		final List<Post<Bookmark>> posts=this.bookmarkChain.getFirstElement().perform(bookmarkParam,dbSession);
	}
	@Test
	public void getBookmarkViewable() {
		this.bookmarkParam.setGrouping(GroupingEntity.VIEWABLE);
		this.bookmarkParam.setHash(null);
		this.bookmarkParam.setOrder(null);
		this.bookmarkParam.setTagIndex(null);
		
		final List<Post<Bookmark>> posts=this.bookmarkChain.getFirstElement().perform(bookmarkParam,dbSession);
	}
	
	/**
	 * TODO: adapt to new test DB
	 */
	@Ignore
	public void getBookmarkSearch() {
		this.bookmarkParam.setGrouping(GroupingEntity.ALL);
		this.bookmarkParam.setRequestedUserName(null);
		this.bookmarkParam.setSearch("\"www.ubuntuusers.de\"");		
		final List<Post<Bookmark>> posts = this.bookmarkChain.getFirstElement().perform(bookmarkParam,dbSession);
		assertEquals(4, posts.size());
	}	

}