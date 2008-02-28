package org.bibsonomy.database.managers.chain.tag;

import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.tag.get.GetAllTags;
import org.bibsonomy.database.managers.chain.tag.get.GetRelatedTags;
import org.bibsonomy.database.managers.chain.tag.get.GetRelatedTagsForGroup;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByAuthor;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByExpression;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByGroup;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByUser;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsViewable;
import org.bibsonomy.database.managers.chain.tag.get.getTagsByHash;
import org.bibsonomy.database.managers.chain.tag.get.getTagsByHashForUser;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.Tag;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class TagChain implements FirstChainElement<Tag, TagParam> {

	private final ChainElement<Tag, TagParam> getTagsByUser;
	private final ChainElement<Tag, TagParam> getTagsByGroup;
	private final ChainElement<Tag, TagParam> getTagsViewable;
	private final ChainElement<Tag, TagParam> getTagsByRegularExpression;
	private final ChainElement<Tag, TagParam> getAllTags;
	private final ChainElement<Tag, TagParam> getTagsByAuthor;	
	private final ChainElement<Tag, TagParam> getRelatedTagsForGroup;
	private final ChainElement<Tag, TagParam> getRelatedTags;
	private final ChainElement<Tag, TagParam> getTagsByHash;
	private final ChainElement<Tag, TagParam> getTagsByHashForUser;

	/**
	 * Constructs the chain
	 */
	public TagChain() {
		this.getTagsByUser = new GetTagsByUser();
		this.getTagsByGroup = new GetTagsByGroup();
		this.getTagsViewable = new GetTagsViewable();
		this.getAllTags = new GetAllTags();
		this.getTagsByRegularExpression = new GetTagsByExpression();
		this.getTagsByAuthor=new GetTagsByAuthor();
		this.getRelatedTagsForGroup = new GetRelatedTagsForGroup();
		this.getRelatedTags = new GetRelatedTags();
		this.getTagsByHash = new getTagsByHash();
		this.getTagsByHashForUser = new getTagsByHashForUser();

		this.getTagsByUser.setNext(this.getTagsByGroup);
		this.getTagsByGroup.setNext(this.getAllTags);
		this.getAllTags.setNext(this.getRelatedTags);
		this.getRelatedTags.setNext(this.getTagsByAuthor);
		this.getTagsByAuthor.setNext(getTagsViewable);
		this.getTagsViewable.setNext(this.getTagsByRegularExpression);
		this.getTagsByRegularExpression.setNext(this.getRelatedTagsForGroup);
		this.getRelatedTagsForGroup.setNext(this.getTagsByHash);
		this.getTagsByHash.setNext(this.getTagsByHashForUser);
	}

	public ChainElement<Tag, TagParam> getFirstElement() {
		return this.getTagsByUser;
	}
}