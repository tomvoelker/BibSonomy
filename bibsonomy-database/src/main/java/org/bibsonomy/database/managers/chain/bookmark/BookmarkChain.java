package org.bibsonomy.database.managers.chain.bookmark;

import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByConceptForUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByHash;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByHashForUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByTagNames;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByTagNamesAndUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForGroup;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForGroupAndTag;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForHomePage;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksOfFriendsByTags;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksOfFriendsByUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksPopular;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksViewable;
import org.bibsonomy.model.Bookmark;

/**
 * @author mgr
 */
public class BookmarkChain implements FirstChainElement<Bookmark> {

	private final ChainElement<Bookmark> getBookmarksForUser;
	private final ChainElement<Bookmark> getBookmarksByHash;
	private final ChainElement<Bookmark> getBookmarksByHashForUser;
	private final ChainElement<Bookmark> getBookmarksByTagNames;
	private final ChainElement<Bookmark> getBookmarksByTagNamesAndUser;
	private final ChainElement<Bookmark> getBookmarksForGroup;
	private final ChainElement<Bookmark> getBookmarksForGroupAndTag;
	private final ChainElement<Bookmark> getBookmarksForHomePage;
	private final ChainElement<Bookmark> getBookmarksForPopular;
	private final ChainElement<Bookmark> getBookmarksViewable;
	private final ChainElement<Bookmark> getBookmarksByConcept;
	private final ChainElement<Bookmark> getBookmarksByUserFriends;
	private final ChainElement<Bookmark> getBookmarksByUserAndTagsFriends;

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
	}

	public ChainElement<Bookmark> getFirstElement() {
		return this.getBookmarksForHomePage;
	}
}