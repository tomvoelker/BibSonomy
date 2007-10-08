package org.bibsonomy.database.managers.chain.bibtex;

import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibTexByAuthor;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibTexByAuthorAndTag;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByConceptForUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByFriends;
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
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexSearch;
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
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexSearch;	
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByAuthor;
	private final ChainElement<Post<BibTex>, BibTexParam> getBibTexByAuthorAndTag;

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
		this.getBibTexSearch = new GetBibtexSearch();
		
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
		this.getBibTexByFriends.setNext(this.getBibTexSearch);
		this.getBibTexSearch.setNext(getBibTexByAuthor);
		this.getBibTexByAuthor.setNext(this.getBibTexByAuthorAndTag);
		
	}

	public ChainElement<Post<BibTex>, BibTexParam> getFirstElement() {
		return this.getBibTexForHomePage;
	}
}