package org.bibsonomy.database.managers.hash.bibtex;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.database.managers.hash.HashElement;
import org.bibsonomy.database.managers.hash.HashingManager;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibTexByAuthor;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibTexByAuthorAndTag;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibTexByConceptByTag;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexByConceptForGroup;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexByConceptForUser;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexByFriends;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexByHash;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexByHashForUser;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexByKey;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexByTagNames;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexByTagNamesAndUser;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexForGroup;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexForGroupAndTag;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexForHomePageOrPopular;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexForUser;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexOfFriendsByTags;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexOfFriendsByUser;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexSearch;
import org.bibsonomy.database.managers.hash.bibtex.get.GetBibtexViewable;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author Andreas Koch
 * @version $Id$
 */
public class BibTexHashingManager extends HashingManager {

	private HashMap<String, HashElement<? extends Post<? extends Resource>, ? extends GenericParam>> bibtexMap;

	private final HashElement<Post<BibTex>, BibTexParam> getBibTexByHash;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexByHashForUser;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexByTagNames;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexByTagNamesAndUser;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexForGroup;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexForGroupAndTag;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexForHomePageOrPopular;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexViewable;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexForUser;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexByConceptForUser;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexByUserFriends;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexByUserAndTagsFriends;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexByFriends;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexSearch;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexByAuthor;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexByAuthorAndTag;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexByConceptByTag;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexByConceptForGroup;
	private final HashElement<Post<BibTex>, BibTexParam> getBibTexByKey;

	private GetBibtexOfFriendsByTags getBibTexOfFriendsByTags;

	private GetBibtexOfFriendsByUser getBibTexOfFriendsByUser;

	public BibTexHashingManager() {
		this.getBibTexByHash = new GetBibtexByHash();
		this.getBibTexByHashForUser = new GetBibtexByHashForUser();
		this.getBibTexByTagNames = new GetBibtexByTagNames();
		this.getBibTexByTagNamesAndUser = new GetBibtexByTagNamesAndUser();
		this.getBibTexForGroup = new GetBibtexForGroup();
		this.getBibTexForGroupAndTag = new GetBibtexForGroupAndTag();
		this.getBibTexForHomePageOrPopular = new GetBibtexForHomePageOrPopular();
		this.getBibTexViewable = new GetBibtexViewable();
		this.getBibTexForUser = new GetBibtexForUser();
		this.getBibTexByConceptForUser = new GetBibtexByConceptForUser();
		this.getBibTexByUserFriends = new GetBibtexOfFriendsByUser();
		this.getBibTexByUserAndTagsFriends = new GetBibtexOfFriendsByTags();
		this.getBibTexByFriends = new GetBibtexByFriends();
		this.getBibTexByAuthor = new GetBibTexByAuthor();
		this.getBibTexByAuthorAndTag = new GetBibTexByAuthorAndTag();
		this.getBibTexSearch = new GetBibtexSearch();
		this.getBibTexByConceptByTag = new GetBibTexByConceptByTag();
		this.getBibTexByConceptForGroup = new GetBibtexByConceptForGroup();
		this.getBibTexByKey = new GetBibtexByKey();
		this.getBibTexOfFriendsByTags = new GetBibtexOfFriendsByTags();
		this.getBibTexOfFriendsByUser = new GetBibtexOfFriendsByUser();
	}

	@Override
	protected StringBuilder additionalElementToHash(HashElement element) {
		StringBuilder sb = new StringBuilder();
		if (element instanceof BibTexHashElement) {
			addBoolean(sb, ((BibTexHashElement) element).isBibtexKey());
		}

		return sb;
	}

	@Override
	protected StringBuilder additionalParamToHash(GenericParam param) {
		StringBuilder sb = new StringBuilder();

		if (param instanceof BibTexParam) {
			addBoolean(sb, ValidationUtils.present(((BibTexParam) param).getBibtexKey()));
		}

		return sb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.database.managers.hash.HashingManager#getMap()
	 */
	@Override
	protected Map<String, HashElement<? extends Post<? extends Resource>, ? extends GenericParam>> getMap() {
		if (bibtexMap == null) {
			bibtexMap = new HashMap<String, HashElement<? extends Post<? extends Resource>, ? extends GenericParam>>();

			// fill the map with all existing elements 
			bibtexMap.put(elementToHashString(this.getBibTexByHash), this.getBibTexByHash);
			bibtexMap.put(elementToHashString(this.getBibTexByHashForUser), this.getBibTexByHashForUser);
			bibtexMap.put(elementToHashString(this.getBibTexByTagNames), this.getBibTexByTagNames);
			bibtexMap.put(elementToHashString(this.getBibTexByTagNamesAndUser), this.getBibTexByTagNamesAndUser);
			bibtexMap.put(elementToHashString(this.getBibTexForGroup), this.getBibTexForGroup);
			bibtexMap.put(elementToHashString(this.getBibTexForGroupAndTag), this.getBibTexForGroupAndTag);
			bibtexMap.put(elementToHashString(this.getBibTexForHomePageOrPopular), this.getBibTexForHomePageOrPopular);
			bibtexMap.put(elementToHashString(this.getBibTexViewable), this.getBibTexViewable);
			bibtexMap.put(elementToHashString(this.getBibTexForUser), this.getBibTexForUser);
			bibtexMap.put(elementToHashString(this.getBibTexByConceptForUser), this.getBibTexByConceptForUser);
			bibtexMap.put(elementToHashString(this.getBibTexByUserFriends), this.getBibTexByUserFriends);
			bibtexMap.put(elementToHashString(this.getBibTexByUserAndTagsFriends), this.getBibTexByUserAndTagsFriends);
			bibtexMap.put(elementToHashString(this.getBibTexByFriends), this.getBibTexByFriends);
			bibtexMap.put(elementToHashString(this.getBibTexByAuthor), this.getBibTexByAuthor);
			bibtexMap.put(elementToHashString(this.getBibTexByAuthorAndTag), this.getBibTexByAuthorAndTag);
			bibtexMap.put(elementToHashString(this.getBibTexSearch), this.getBibTexSearch);
			bibtexMap.put(elementToHashString(this.getBibTexByConceptByTag), this.getBibTexByConceptByTag);
			bibtexMap.put(elementToHashString(this.getBibTexByConceptForGroup), this.getBibTexByConceptForGroup);
			bibtexMap.put(elementToHashString(this.getBibTexByKey), this.getBibTexByKey);
			bibtexMap.put(elementToHashString(this.getBibTexOfFriendsByTags), this.getBibTexOfFriendsByTags);
			bibtexMap.put(elementToHashString(this.getBibTexOfFriendsByUser), this.getBibTexOfFriendsByUser);

		}
		return bibtexMap;
	}

}
