package org.bibsonomy.database.managers.chain.tag;

import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.tag.get.GetAllTags;
import org.bibsonomy.database.managers.chain.tag.get.GetPopularTags;
import org.bibsonomy.database.managers.chain.tag.get.GetRelatedTags;
import org.bibsonomy.database.managers.chain.tag.get.GetRelatedTagsByAuthorAndTags;
import org.bibsonomy.database.managers.chain.tag.get.GetRelatedTagsForGroup;
import org.bibsonomy.database.managers.chain.tag.get.GetSimilarTags;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByAuthor;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByBibtexkey;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByExpression;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByFriendOfUser;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByGroup;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByHash;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByHashForUser;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByResourceSearch;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsBySearchString;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByUser;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsViewable;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.Tag;

/**
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class TagChain implements FirstChainElement<Tag, TagParam> {

	private final ChainElement<Tag, TagParam> getTagsByUser;
	private final ChainElement<Tag, TagParam> getTagsByGroup;
	private final ChainElement<Tag, TagParam> getTagsViewable;
	private final ChainElement<Tag, TagParam> getTagsByRegularExpression;
	private final ChainElement<Tag, TagParam> getAllTags;
	private final ChainElement<Tag, TagParam> getTagsByResourceSearch;
	private final ChainElement<Tag, TagParam> getTagsByAuthor;	
	private final ChainElement<Tag, TagParam> getTagsBySearchString;	
	private final ChainElement<Tag, TagParam> getRelatedTagsForGroup;
	private final ChainElement<Tag, TagParam> getRelatedTags;
	private final ChainElement<Tag, TagParam> getSimilarTags;
	private final ChainElement<Tag, TagParam> getTagsByHash;
	private final ChainElement<Tag, TagParam> getTagsByHashForUser;
	private final ChainElement<Tag, TagParam> getPopularTags;
	private final ChainElement<Tag, TagParam> getTagsByFriendOfUser;
	private final ChainElement<Tag, TagParam> getTagsByBibtexkey;
	private final ChainElement<Tag, TagParam> getRelatedTagsByAuthorAndTag;
	

	
	/**
	 * Constructs the chain
	 */
	public TagChain() {
		this.getTagsByUser = new GetTagsByUser();
		this.getTagsByGroup = new GetTagsByGroup();
		this.getTagsViewable = new GetTagsViewable();
		this.getAllTags = new GetAllTags();
		this.getTagsByRegularExpression = new GetTagsByExpression();
		this.getTagsByResourceSearch = new GetTagsByResourceSearch();
		this.getTagsByAuthor=new GetTagsByAuthor();
		this.getTagsBySearchString = new GetTagsBySearchString();
		this.getRelatedTagsForGroup = new GetRelatedTagsForGroup();
		this.getRelatedTags = new GetRelatedTags();
		this.getTagsByHash = new GetTagsByHash();
		this.getTagsByHashForUser = new GetTagsByHashForUser();
		this.getSimilarTags = new GetSimilarTags();
		this.getPopularTags = new GetPopularTags();
		this.getTagsByFriendOfUser = new GetTagsByFriendOfUser();
		this.getTagsByBibtexkey = new GetTagsByBibtexkey();
		this.getRelatedTagsByAuthorAndTag = new GetRelatedTagsByAuthorAndTags();

		
		this.getTagsByUser.setNext(this.getTagsByGroup);
		this.getTagsByGroup.setNext(this.getTagsByFriendOfUser);
		this.getTagsByFriendOfUser.setNext(this.getAllTags);
		this.getAllTags.setNext(this.getSimilarTags);
		this.getSimilarTags.setNext(this.getRelatedTags);
		this.getRelatedTags.setNext(this.getTagsBySearchString);
		this.getTagsBySearchString.setNext(this.getTagsByResourceSearch);
		this.getTagsByResourceSearch.setNext(this.getTagsByAuthor);
		this.getTagsByAuthor.setNext(this.getRelatedTagsByAuthorAndTag);
		this.getRelatedTagsByAuthorAndTag.setNext(getTagsViewable);
		this.getTagsViewable.setNext(this.getTagsByRegularExpression);
		this.getTagsByRegularExpression.setNext(this.getRelatedTagsForGroup);
		this.getRelatedTagsForGroup.setNext(this.getTagsByHash);
		this.getTagsByHash.setNext(this.getTagsByHashForUser);
		this.getTagsByHashForUser.setNext(this.getPopularTags);
		this.getPopularTags.setNext(this.getTagsByBibtexkey);
		
	}

	public ChainElement<Tag, TagParam> getFirstElement() {
		return this.getTagsByUser;
	}
}