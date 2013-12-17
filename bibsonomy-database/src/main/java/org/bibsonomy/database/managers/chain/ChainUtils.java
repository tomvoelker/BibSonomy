package org.bibsonomy.database.managers.chain;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.systemstags.search.NotTagSystemTag;

/**
 * @author mba
 */
public class ChainUtils {

	public static boolean useResourceSearch(final GenericParam param) {
		final PermissionDatabaseManager pdm = PermissionDatabaseManager.getInstance();
		final List<TagIndex> tagIndex = param.getTagIndex();
	
		
		/*
		 * Are there Negation tags?
		 */
		boolean existsNegatedTags = false;
		for (final SystemTag sysTag: param.getSystemTags()) {
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
		if ((present(tagIndex) && pdm.useResourceSearchForTagQuery(tagIndex.size())) ||	existsNegatedTags) {
			return true;
		}
		if ((param.getGrouping() == GroupingEntity.ALL) && (param.getNumSimpleConcepts() > 0)) {
			return true;
		}
		
		return false;
	}

}
