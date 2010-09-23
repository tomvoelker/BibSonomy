package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.systemstags.search.YearSystemTag;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for a given search.
 * 
 * @author fei
 * @version $Id$
 */
public class GetBibtexByResourceSearch extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
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
		return this.db.getPostsByResourceSearch(param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(),  param.getGroupNames(), param.getRawSearch(), param.getTitle(), param.getAuthor(), tagIndex, year, firstYear, lastYear,  param.getLimit(), param.getOffset());
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (!present(param.getBibtexKey()) &&
				(present(param.getSearch()) || present(param.getAuthor()) || present(param.getTitle()))
				); 
	}	
}