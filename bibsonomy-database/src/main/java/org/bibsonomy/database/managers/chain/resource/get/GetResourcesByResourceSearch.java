package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.systemstags.search.YearSystemTag;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

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
		final PermissionDatabaseManager pdm = PermissionDatabaseManager.getInstance();
		final List<TagIndex> tagIndex = param.getTagIndex();
		/*
		 * TODO should we check the present of negated tag here? / Would it be
		 * better of have a boolean param that indicates
		 * whether there are negated tags in the list?
		 */
		if (present(tagIndex) && (pdm.useResourceSearchForTagQuery(tagIndex.size()) || hasNegatedTags(tagIndex))) {
			return true;
		}
		if ((param.getGrouping() == GroupingEntity.ALL) && (param.getNumSimpleConcepts() > 0)) {
			return true;
		}
		
		return false;
	}

	private static boolean hasNegatedTags(final List<TagIndex> tagIndex) {
		for (final TagIndex tag : tagIndex) {
			if (tag.getTagName().startsWith(Tag.NEGATION_PREFIX)) {
				return true;
			}
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

		for (final SystemTag systemTag : param.getSystemTags()) {
			if (systemTag instanceof YearSystemTag) {
				final YearSystemTag yearTag = (YearSystemTag) systemTag;
				year = yearTag.getYear();
				firstYear = yearTag.getFirstYear();
				lastYear = yearTag.getLastYear();
			}
		}

		// query the resource searcher
		return this.databaseManager.getPostsByResourceSearch(param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getGroupNames(), param.getRawSearch(), param.getTitle(), param.getAuthor(), tagIndex, year, firstYear, lastYear, param.getLimit(), param.getOffset());
	}
}
