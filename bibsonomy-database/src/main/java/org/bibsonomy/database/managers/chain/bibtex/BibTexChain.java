package org.bibsonomy.database.managers.chain.bibtex;

import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByConceptForUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByHash;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByHashForUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByTagNames;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByTagNamesAndUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForGroup;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForGroupAndTag;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForHomePage;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexOfFriendsByTags;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexOfFriendsByUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexPopular;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexViewable;
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

/**
 * @author mgr
 */
public class BibTexChain implements FirstChainElement {

	private final ChainElement getBibTexByHash;
	private final ChainElement getBibTexByHashForUser;
	private final ChainElement getBibTexByTagNames;
	private final ChainElement getBibTexByTagNamesAndUser;
	private final ChainElement getBibTexForGroup;
	private final ChainElement getBibTexForGroupAndTag;
	private final ChainElement getBibTexForHomePage;
	private final ChainElement getBibTexForPopular;
	private final ChainElement getBibTexViewable;
	private final ChainElement getBibTexForUser;
    private final ChainElement getBibTexByConceptForUser;
    private final ChainElement getBibTexByUserFriends;
    private final ChainElement getBibTexByUserAndTagsFriends;
	
	public BibTexChain() {
		
		this.getBibTexByHash = new GetBibtexByHash();
		this.getBibTexByHashForUser = new GetBibtexByHashForUser();
		this.getBibTexByTagNames = new GetBibtexByTagNames();
		this.getBibTexByTagNamesAndUser = new GetBibtexByTagNamesAndUser();
		this.getBibTexForGroup = new GetBibtexForGroup();
		this.getBibTexForGroupAndTag = new GetBibtexForGroupAndTag();
		this.getBibTexForHomePage = new GetBibtexForHomePage();
		this.getBibTexForPopular = new GetBibtexPopular();
		this.getBibTexViewable = new GetBibtexViewable();
		this.getBibTexForUser = new GetBibtexForUser();
        this.getBibTexByConceptForUser= new GetBibtexByConceptForUser();
        this.getBibTexByUserFriends=new GetBibtexOfFriendsByUser();
        this.getBibTexByUserAndTagsFriends=new GetBibtexOfFriendsByTags();
        
		this.getBibTexForHomePage.setNext(this.getBibTexForPopular);
		this.getBibTexForPopular.setNext(this.getBibTexForUser);
		this.getBibTexForUser.setNext(this.getBibTexByTagNames);
		this.getBibTexByTagNames.setNext(this.getBibTexByHashForUser);
		this.getBibTexByHashForUser.setNext(this.getBibTexByHash);
		this.getBibTexByHash.setNext(this.getBibTexByTagNamesAndUser);
		this.getBibTexByTagNamesAndUser.setNext(this.getBibTexForGroup);
		this.getBibTexForGroup.setNext(this.getBibTexForGroupAndTag);
		this.getBibTexForGroupAndTag.setNext(this.getBibTexViewable);
		this.getBibTexViewable.setNext(this.getBibTexByConceptForUser);
        this.getBibTexByConceptForUser.setNext(this.getBibTexByUserFriends);
		this.getBibTexByUserFriends.setNext(this.getBibTexByUserAndTagsFriends);
	}

	public ChainElement getFirstElement() {
		return this.getBibTexForHomePage;
	}
}