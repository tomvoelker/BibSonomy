package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.systemstags.search.NotTagSystemTag;
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
		final PermissionDatabaseManager pdm = PermissionDatabaseManager.getInstance();
		final List<TagIndex> tagIndex = param.getTagIndex();

		
		/*
		 * Are there Negation tags?
		 */
		boolean existsNegatedTags = false;
		for (SystemTag sysTag: param.getSystemTags()) {
			if (sysTag instanceof NotTagSystemTag) {
				existsNegatedTags = true;
				break;
			}				
		}
		
		/*
		 * Handle the request when:
		 * 1. There are TAGS in the query AND the lucene should be uses for the amount of tags
		 * OR
		 * 2. There are negated tags
		 */
		if ((present(tagIndex) && pdm.useResourceSearchForTagQuery(tagIndex.size())) ||
			existsNegatedTags) {
			return true;
		}
		if ((param.getGrouping() == GroupingEntity.ALL) && (param.getNumSimpleConcepts() > 0)) {
			return true;
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
		
		/*
		 * Get the negated Tags
		 */
		List<String> negatedTags = new LinkedList<String>();;

		for (final SystemTag systemTag : param.getSystemTags()) {
			if (systemTag instanceof YearSystemTag) {
				final YearSystemTag yearTag = (YearSystemTag) systemTag;
				year = yearTag.getYear();
				firstYear = yearTag.getFirstYear();
				lastYear = yearTag.getLastYear();
			}
			else if (systemTag instanceof NotTagSystemTag) {
				negatedTags.add(((NotTagSystemTag) systemTag).getTagName());
			}
		}

		// query the resource searcher
		return this.databaseManager.getPostsByResourceSearch(param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getGroupNames(), param.getRawSearch(), param.getTitle(), param.getAuthor(), tagIndex, year, firstYear, lastYear, negatedTags, param.getLimit(), param.getOffset());
	}
}
