package org.bibsonomy.database.managers.chain.bookmark;

import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByHash;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByHashForUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByTagNames;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByTagNamesAndUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForGroup;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForGroupAndTag;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForHomePage;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForUser;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksPopular;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksViewable;

/**
 * @author mgr
 */
public class BookmarkChain implements FirstChainElement {

	private final ChainElement getBookmarksForUser;
	private final ChainElement getBookmarksByHash;
	private final ChainElement getBookmarksByHashForUser;
	private final ChainElement getBookmarksByTagNames;
	private final ChainElement getBookmarksByTagNamesAndUser;
	private final ChainElement getBookmarksForGroup;
	private final ChainElement getBookmarksForGroupAndTag;
	private final ChainElement getBookmarksForHomePage;
	private final ChainElement getBookmarksForPopular;
	private final ChainElement getBookmarksViewable;

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

		this.getBookmarksForHomePage.setNext(this.getBookmarksForPopular);
		this.getBookmarksForPopular.setNext(this.getBookmarksForUser);
		this.getBookmarksForUser.setNext(this.getBookmarksByTagNames);
		this.getBookmarksByTagNames.setNext(this.getBookmarksByHashForUser);
		this.getBookmarksByHashForUser.setNext(this.getBookmarksByHash);
		this.getBookmarksByHash.setNext(this.getBookmarksByTagNamesAndUser);
		this.getBookmarksByTagNamesAndUser.setNext(this.getBookmarksForGroup);
		this.getBookmarksForGroup.setNext(this.getBookmarksForGroupAndTag);
		this.getBookmarksForGroupAndTag.setNext(this.getBookmarksViewable);

		/*
		 * getBookmarksViewable.setNext(getBookmarksByUserFriends);
		 * getBookmarksByUserFriends.setNext(getBookmarksConcept);
		 */
	}

	public ChainElement getFirstElement() {
		return this.getBookmarksForHomePage;
	}
}