package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.ChainUtils;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.systemstags.search.NotTagSystemTag;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.Tag;

/**
 * Returns a list of tags for a given author.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
  */
public class GetTagsByResourceSearch extends TagChainElement {

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		final Collection<String> tags = present(param.getTagIndex()) ? DatabaseUtils.extractTagNames(param) : null;
		/*
		 * Check System tags for negated and year tags
		 */
		final List<String> negatedTags = new LinkedList<String>();
		if (present(param.getSystemTags())) {
			for (final SystemTag systemTag : param.getSystemTags()) {
				if (systemTag instanceof NotTagSystemTag) {
					negatedTags.add(((NotTagSystemTag) systemTag).getTagName());
				}
			}
		}
		return this.db.getTagsByResourceSearch(param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getGroupNames(), param.getSearch(), param.getTitle(), param.getAuthor(), tags, null, null, null, negatedTags, param.getLimit(), param.getOffset());
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (!present(param.getBibtexKey()) && 
				!present(param.getRegex()) && 
				!present(param.getHash()) && 
				!present(param.getTagRelationType()) && 
				(present(param.getSearch()) || present(param.getTitle()) || present(param.getAuthor()) || ChainUtils.useResourceSearch(param)));
	}
}