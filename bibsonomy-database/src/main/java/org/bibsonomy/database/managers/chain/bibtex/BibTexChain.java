package org.bibsonomy.database.managers.chain.bibtex;

import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByHash;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByHashForUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByTagNames;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByTagNamesAndUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForGroup;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForGroupAndTag;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForHomePage;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexForUser;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexPopular;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexViewable;

/**
 * @author mgr
 */
public class BibTexChain implements FirstChainElement {

	private ChainElement getBibTexByHash;
	private ChainElement getBibTexByHashForUser;
	private ChainElement getBibTexByTagNames;
	private ChainElement getBibTexByTagNamesAndUser;
	private ChainElement getBibTexForGroup;
	private ChainElement getBibTexForGroupAndTag;
	private ChainElement getBibTexForHomePage;
	private ChainElement getBibTexForPopular;
	private ChainElement getBibTexViewable;
	private ChainElement getBibTexForUser;

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

		this.getBibTexForHomePage.setNext(this.getBibTexForPopular);
		this.getBibTexForPopular.setNext(this.getBibTexForUser);
		this.getBibTexForUser.setNext(this.getBibTexByTagNames);
		this.getBibTexByTagNames.setNext(this.getBibTexByHashForUser);
		this.getBibTexByHashForUser.setNext(this.getBibTexByHash);
		this.getBibTexByHash.setNext(this.getBibTexByTagNamesAndUser);
		this.getBibTexByTagNamesAndUser.setNext(this.getBibTexForGroup);
		this.getBibTexForGroup.setNext(this.getBibTexForGroupAndTag);
		this.getBibTexForGroupAndTag.setNext(this.getBibTexViewable);

		/*
		 * getBoomarksViewable.setNext(getBookmarksByUserFriends);
		 * getBookmarksByUserFriends.setNext(getBookmarksConcept);
		 */
	}

	public ChainElement getFirstElement() {
		return this.getBibTexForHomePage;
	}
}