package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.database.systemstags.search.YearSystemTag;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author claus
 * @version $Id$
 * @param <R> the resource
 * @param <P> the param
 */
public abstract class GetResourcesByResourceSearch<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected List<Post<R>> handle(P param, DBSession session) {
		// convert tag index to tag list
		List<String> tagIndex = null;
		if (present(param.getTagIndex())) {
			tagIndex = DatabaseUtils.extractTagNames(param.getTagIndex());
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
		return this.getDatabaseManagerForType(param.getClass()).getPostsByResourceSearch(
				param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(), 
				param.getGroupNames(), param.getRawSearch(), param.getTitle(), param.getAuthor(), tagIndex, 
				year, firstYear, lastYear, 
				param.getLimit(), param.getOffset());
	}
}
