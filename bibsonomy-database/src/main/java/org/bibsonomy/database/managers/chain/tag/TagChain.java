package org.bibsonomy.database.managers.chain.tag;

import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.FirstChainElement;
import org.bibsonomy.database.managers.chain.tag.get.GetAllTags;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByAuthor;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByExpression;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByGroup;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByUser;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsViewable;
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

		this.getTagsByUser.setNext(this.getTagsByGroup);
		this.getTagsByGroup.setNext(this.getAllTags);
		this.getAllTags.setNext(this.getTagsByAuthor);
		this.getTagsByAuthor.setNext(getTagsViewable);
		this.getTagsViewable.setNext(this.getTagsByRegularExpression);
	}

	public ChainElement<Tag, TagParam> getFirstElement() {
		return this.getTagsByUser;
	}
}