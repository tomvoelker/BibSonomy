package org.bibsonomy.database.managers.chain.bibtex;

import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByKey;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByResourceSearch;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexFromBasketForUser;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByConceptByTag;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByConceptForGroup;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByConceptForUser;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByFollowedUsers;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByFriends;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByHash;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByHashForUser;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByTagNames;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByTagNamesAndUser;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesForGroup;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesForGroupAndTag;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesForHomepage;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesForUser;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesFromInbox;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesOfFriendsByTags;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesOfFriendsByUser;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesPopular;
import org.bibsonomy.database.managers.chain.resource.get.GetResourcesViewable;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class BibTexChain implements FirstChainElement<Post<BibTex>, BibTexParam> {

	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsByHash;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsByHashForUser;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsByKey;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsByTagNames;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsByTagNamesAndUser;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsForGroup;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsForGroupAndTag;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsForHomepage;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsForPopular;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsViewable;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsForUser;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsByConceptForUser;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsByUserFriends;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsByUserAndTagsFriends;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsByFriends;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsByResourceSearch;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsByConceptByTag;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsByConceptForGroup;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsFromBasketForUser;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsByFollowedUsers;
	private final ChainElement<Post<BibTex>, BibTexParam> getPublicationsFromInbox;

	/**
	 * Constructs the chain
	 */
	public BibTexChain() {
		this.getPublicationsByHash = new GetResourcesByHash<BibTex, BibTexParam>();
		this.getPublicationsByHashForUser = new GetResourcesByHashForUser<BibTex, BibTexParam>();
		this.getPublicationsByTagNames = new GetResourcesByTagNames<BibTex, BibTexParam>();
		this.getPublicationsByTagNamesAndUser = new GetResourcesByTagNamesAndUser<BibTex, BibTexParam>();
		this.getPublicationsForGroup = new GetResourcesForGroup<BibTex, BibTexParam>();
		this.getPublicationsForGroupAndTag = new GetResourcesForGroupAndTag<BibTex, BibTexParam>();
		this.getPublicationsForHomepage = new GetResourcesForHomepage<BibTex, BibTexParam>();
		this.getPublicationsForPopular = new GetResourcesPopular<BibTex, BibTexParam>();
		this.getPublicationsViewable = new GetResourcesViewable<BibTex, BibTexParam>();
		this.getPublicationsForUser = new GetResourcesForUser<BibTex, BibTexParam>();
		this.getPublicationsByConceptForUser = new GetResourcesByConceptForUser<BibTex, BibTexParam>();
		this.getPublicationsByUserFriends = new GetResourcesOfFriendsByUser<BibTex, BibTexParam>();
		this.getPublicationsByUserAndTagsFriends = new GetResourcesOfFriendsByTags<BibTex, BibTexParam>();
		this.getPublicationsByFriends = new GetResourcesByFriends<BibTex, BibTexParam>();	
		this.getPublicationsByResourceSearch = new GetBibtexByResourceSearch();
		this.getPublicationsByConceptByTag = new GetResourcesByConceptByTag<BibTex, BibTexParam>();
		this.getPublicationsByConceptForGroup = new GetResourcesByConceptForGroup<BibTex, BibTexParam>();
		this.getPublicationsByKey = new GetBibtexByKey();
		this.getPublicationsFromBasketForUser = new GetBibtexFromBasketForUser();
		this.getPublicationsByFollowedUsers = new GetResourcesByFollowedUsers<BibTex, BibTexParam>();
		this.getPublicationsFromInbox = new GetResourcesFromInbox<BibTex, BibTexParam>();
		
		this.getPublicationsForHomepage.setNext(this.getPublicationsForPopular);
		this.getPublicationsForPopular.setNext(this.getPublicationsForUser);
		this.getPublicationsForUser.setNext(this.getPublicationsByTagNames);
		this.getPublicationsByTagNames.setNext(this.getPublicationsByHashForUser);
		this.getPublicationsByHashForUser.setNext(this.getPublicationsByHash);
		this.getPublicationsByHash.setNext(this.getPublicationsByTagNamesAndUser);
		this.getPublicationsByTagNamesAndUser.setNext(this.getPublicationsForGroup);
		this.getPublicationsForGroup.setNext(this.getPublicationsForGroupAndTag);
		this.getPublicationsForGroupAndTag.setNext(this.getPublicationsViewable);
		this.getPublicationsViewable.setNext(this.getPublicationsByConceptForUser);
		this.getPublicationsByConceptForUser.setNext(this.getPublicationsByUserFriends);
		this.getPublicationsByUserFriends.setNext(this.getPublicationsByUserAndTagsFriends);
		this.getPublicationsByUserAndTagsFriends.setNext(this.getPublicationsByFriends);
		this.getPublicationsByFriends.setNext(this.getPublicationsByFollowedUsers);
		this.getPublicationsByFollowedUsers.setNext(this.getPublicationsByResourceSearch);
		this.getPublicationsByResourceSearch.setNext(this.getPublicationsByConceptByTag);
		this.getPublicationsByConceptByTag.setNext(this.getPublicationsByConceptForGroup);
		this.getPublicationsByConceptForGroup.setNext(this.getPublicationsByKey);
		this.getPublicationsByKey.setNext(this.getPublicationsFromBasketForUser);
		this.getPublicationsFromBasketForUser.setNext(this.getPublicationsFromInbox);
	}

	public ChainElement<Post<BibTex>, BibTexParam> getFirstElement() {
		return this.getPublicationsForHomepage;
	}
}