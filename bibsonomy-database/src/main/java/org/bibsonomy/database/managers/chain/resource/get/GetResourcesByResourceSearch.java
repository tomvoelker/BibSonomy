package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.database.systemstags.search.YearSystemTag;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author claus
 * @version $Id: GetResourcesByResourceSearch.java,v 1.7 2012-06-01 14:03:27
 *          telekoma Exp $
 * @param <R>
 *            the resource
 * @param <P>
 *            the param
 */
public abstract class GetResourcesByResourceSearch<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected boolean canHandle(final P param) {
		PermissionDatabaseManager pdm = PermissionDatabaseManager.getInstance();
		List<TagIndex> tagIndex = param.getTagIndex();
		// TODO should we check the present of negated tag here? / Would it be
		// better of have a boolean param that indicates
		// whether there are negated tags in the list?
		if (present(tagIndex) && (pdm.useResourceSearchForTagQuery(tagIndex.size()) || hasNegatedTags(tagIndex))) {
			return true;
		}
		if (param.getGrouping() == GroupingEntity.ALL && param.getNumSimpleConcepts() > 0) {
			return true;
		} else
			return false;
	}

	private boolean hasNegatedTags(List<TagIndex> tagIndex) {
		for (TagIndex tag : tagIndex) {
			if (tag.getTagName().startsWith("!")) return true;
		}
		return false;
	}

	@Override
	protected List<Post<R>> handle(final P param, final DBSession session) {
		// convert tag index to tag list
		List<String> tagIndex = null;
		if (present(param.getTagIndex())) {
			tagIndex = DatabaseUtils.extractTagNames(param);
		}

		/*
		 * extract first-, last- and year from the system tag if present
		 */
		String year = null;
		String firstYear = null;
		String lastYear = null;

		final YearSystemTag yearTag = (YearSystemTag) param.getSystemTags().get(YearSystemTag.NAME);
		if (present(yearTag)) {
			year = yearTag.getYear();
			firstYear = yearTag.getFirstYear();
			lastYear = yearTag.getLastYear();
		}

		// query the resource searcher
		return this.databaseManager.getPostsByResourceSearch(param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getGroupNames(), param.getRawSearch(), param.getTitle(), param.getAuthor(), tagIndex, year, firstYear, lastYear, param.getLimit(), param.getOffset());
	}
}
