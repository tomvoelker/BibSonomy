package org.bibsonomy.database.managers.chain.bookmark;

import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByConceptByTag;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByConceptForGroup;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByConceptForUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByFollowedUsers;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByFriends;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByHash;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByHashForUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByTagNames;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByTagNamesAndUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForGroup;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForGroupAndTag;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForHomePage;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksFromInbox;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksOfFriendsByTags;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksOfFriendsByUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksPopular;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksSearch;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksSearchForGroup;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksViewable;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class BookmarkChain implements FirstChainElement<Post<Bookmark>, BookmarkParam> {

	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksForUser;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksByHash;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksByHashForUser;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksByTagNames;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksByTagNamesAndUser;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksForGroup;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksForGroupAndTag;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksForHomePage;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksForPopular;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksViewable;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksByConcept;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksByUserFriends;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksByUserAndTagsFriends;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksByFriends;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksSearch;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksSearchForGroup;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksByConceptByTag;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksByConceptForGroup;	
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksByFollowedUsers;
	private final ChainElement<Post<Bookmark>, BookmarkParam> getBookmarksFromInbox;
	
	/**
	 * Constructs the chain
	 */
	public BookmarkChain() {
		this.getBookmarksForUser = new GetBookmarksForUser();
		this.getBookmarksByHash = new GetBookmarksByHash();
		this.getBookmarksByHashForUser = new GetBookmarksByHashForUser();
		this.getBookmarksByTagNames = new GetBookmarksByTagNames();
		this.getBookmarksByTagNamesAndUser = new GetBookmarksByTagNamesAndUser();
		this.getBookmarksForGroup = new GetBookmarksForGroup();
		this.getBookmarksForGroupAndTag = new GetBookmarksForGroupAndTag();
		this.getBookmarksForHomePage = new GetBookmarksForHomePage();
		this.getBookmarksForPopular = new GetBookmarksPopular();
		this.getBookmarksViewable = new GetBookmarksViewable();
		this.getBookmarksByConcept = new GetBookmarksByConceptForUser();
		this.getBookmarksByUserFriends = new GetBookmarksOfFriendsByUser();
		this.getBookmarksByUserAndTagsFriends = new GetBookmarksOfFriendsByTags();
		this.getBookmarksByFriends = new GetBookmarksByFriends();
		this.getBookmarksByFollowedUsers = new GetBookmarksByFollowedUsers();
		this.getBookmarksSearch = new GetBookmarksSearch();
		this.getBookmarksSearchForGroup = new GetBookmarksSearchForGroup();
		this.getBookmarksByConceptByTag = new GetBookmarksByConceptByTag();
		this.getBookmarksByConceptForGroup = new GetBookmarksByConceptForGroup();
		this.getBookmarksFromInbox = new GetBookmarksFromInbox();
		
		this.getBookmarksForHomePage.setNext(this.getBookmarksForPopular);
		this.getBookmarksForPopular.setNext(this.getBookmarksForUser);
		this.getBookmarksForUser.setNext(this.getBookmarksByTagNames);
		this.getBookmarksByTagNames.setNext(this.getBookmarksByHashForUser);
		this.getBookmarksByHashForUser.setNext(this.getBookmarksByHash);
		this.getBookmarksByHash.setNext(this.getBookmarksByTagNamesAndUser);
		this.getBookmarksByTagNamesAndUser.setNext(this.getBookmarksForGroup);
		this.getBookmarksForGroup.setNext(this.getBookmarksForGroupAndTag);
		this.getBookmarksForGroupAndTag.setNext(this.getBookmarksViewable);
		this.getBookmarksViewable.setNext(this.getBookmarksByConcept);
		this.getBookmarksByConcept.setNext(this.getBookmarksByUserFriends);
		this.getBookmarksByUserFriends.setNext(this.getBookmarksByUserAndTagsFriends);
		this.getBookmarksByUserAndTagsFriends.setNext(this.getBookmarksByFriends);
		this.getBookmarksByFriends.setNext(this.getBookmarksByFollowedUsers);
		this.getBookmarksByFollowedUsers.setNext(this.getBookmarksSearch);
		this.getBookmarksSearch.setNext(this.getBookmarksSearchForGroup);
		this.getBookmarksSearchForGroup.setNext(this.getBookmarksByConceptByTag);
		this.getBookmarksByConceptByTag.setNext(this.getBookmarksByConceptForGroup);
		this.getBookmarksByConceptForGroup.setNext(this.getBookmarksFromInbox);
	}

	public ChainElement<Post<Bookmark>, BookmarkParam> getFirstElement() {
		return this.getBookmarksForHomePage;
	}
}