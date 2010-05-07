package org.bibsonomy.database.managers.chain.bibtex;

import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibTexByAuthor;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibTexByAuthorAndTag;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibTexByConceptByTag;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibTexByTitle;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByConceptForGroup;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByConceptForUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByFollowedUsers;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByFriends;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByHash;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByHashForUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByKey;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByResourceSearch;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByTagNames;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByTagNamesAndUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForGroup;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForGroupAndTag;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForHomePage;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexFromBasketForUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexFromInbox;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexOfFriendsByTags;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexOfFriendsByUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexPopular;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexSearch;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexSearchForGroup;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexViewable;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class BibTexChain implements FirstChainElement<Post<BibTex>, BibTexParam> {

	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByHash;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByHashForUser;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByKey;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByTagNames;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByTagNamesAndUser;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexForGroup;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexForGroupAndTag;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexForHomePage;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexForPopular;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexViewable;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexForUser;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByConceptForUser;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByUserFriends;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByUserAndTagsFriends;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByFriends;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByResourceSearch;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexSearch;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexSearchForGroup;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByAuthor;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByAuthorAndTag;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByConceptByTag;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByConceptForGroup;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexFromBasketForUser;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByFollowedUsers;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexFromInbox;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibtexByTitle;

	/**
	 * Constructs the chain
	 */
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
		this.getBibTexByConceptForUser = new GetBibtexByConceptForUser();
		this.getBibTexByUserFriends = new GetBibtexOfFriendsByUser();
		this.getBibTexByUserAndTagsFriends = new GetBibtexOfFriendsByTags();
		this.getBibTexByFriends = new GetBibtexByFriends();
		this.getBibTexByAuthor = new GetBibTexByAuthor();
		this.getBibTexByAuthorAndTag = new GetBibTexByAuthorAndTag();
		this.getBibTexByResourceSearch = new GetBibtexByResourceSearch();
		this.getBibTexSearch = new GetBibtexSearch();
		this.getBibTexSearchForGroup = new GetBibtexSearchForGroup();
		this.getBibTexByConceptByTag = new GetBibTexByConceptByTag();
		this.getBibTexByConceptForGroup = new GetBibtexByConceptForGroup();
		this.getBibTexByKey = new GetBibtexByKey();
		this.getBibTexFromBasketForUser = new GetBibtexFromBasketForUser();
		this.getBibTexByFollowedUsers = new GetBibtexByFollowedUsers();
		this.getBibTexFromInbox = new GetBibtexFromInbox();
		this.getBibtexByTitle = new GetBibTexByTitle();

		this.getBibtexByTitle.setNext(this.getBibTexForHomePage);
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
		this.getBibTexByUserAndTagsFriends.setNext(this.getBibTexByFriends);
		this.getBibTexByFriends.setNext(this.getBibTexByFollowedUsers);
		this.getBibTexByFollowedUsers.setNext(this.getBibTexByResourceSearch);
		this.getBibTexByResourceSearch.setNext(this.getBibTexSearch);
		this.getBibTexSearch.setNext(getBibTexSearchForGroup);
		this.getBibTexSearchForGroup.setNext(getBibTexByAuthor);
		this.getBibTexByAuthor.setNext(this.getBibTexByAuthorAndTag);
		this.getBibTexByAuthorAndTag.setNext(getBibTexByConceptByTag);
		this.getBibTexByConceptByTag.setNext(this.getBibTexByConceptForGroup);
		this.getBibTexByConceptForGroup.setNext(this.getBibTexByKey);
		this.getBibTexByKey.setNext(this.getBibTexFromBasketForUser);
		this.getBibTexFromBasketForUser.setNext(this.getBibTexFromInbox);
	}

	public ChainElement<Post<BibTex>, BibTexParam> getFirstElement() {
		return this.getBibtexByTitle;
	}
}