package org.bibsonomy.database.managers.hash.bibtex;

import java.util.HashMap;
import java.util.Map;

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
import org.bibsonomy.util.ValidationUtils;

/**
 * @author Andreas Koch
 * @version $Id$
 */
public class BibTexHashingManager extends HashingManager<BibTexHashElement> {

	private HashMap<String, BibTexHashElement> bibtexMap;

	private final BibTexHashElement getBibTexByHash;
	private final BibTexHashElement getBibTexByHashForUser;
	private final BibTexHashElement getBibTexByTagNames;
	private final BibTexHashElement getBibTexByTagNamesAndUser;
	private final BibTexHashElement getBibTexForGroup;
	private final BibTexHashElement getBibTexForGroupAndTag;
	private final BibTexHashElement getBibTexForHomePageOrPopular;
	private final BibTexHashElement getBibTexViewable;
	private final BibTexHashElement getBibTexForUser;
	private final BibTexHashElement getBibTexByConceptForUser;
	private final BibTexHashElement getBibTexByUserFriends;
	private final BibTexHashElement getBibTexByUserAndTagsFriends;
	private final BibTexHashElement getBibTexByFriends;
	private final BibTexHashElement getBibTexSearch;
	private final BibTexHashElement getBibTexByAuthor;
	private final BibTexHashElement getBibTexByAuthorAndTag;
	private final BibTexHashElement getBibTexByConceptByTag;
	private final BibTexHashElement getBibTexByConceptForGroup;
	private final BibTexHashElement getBibTexByKey;

	private GetBibtexOfFriendsByTags getBibTexOfFriendsByTags;

	private GetBibtexOfFriendsByUser getBibTexOfFriendsByUser;

	/**
	 * 
	 */
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

	@SuppressWarnings("cast")
	@Override
	protected StringBuilder additionalElementToHash(BibTexHashElement element) {
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
	protected Map<String, BibTexHashElement> getMap() {
		if (bibtexMap == null) {
			bibtexMap = new HashMap<String, BibTexHashElement>();

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
