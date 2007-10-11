package org.bibsonomy.database.util;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.logic.Order;

/**
 * Supplies methods to adapt the LogicInterface to the database layer.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class LogicInterfaceHelper {

	/**
	 * Builds a param object for the given parameters from the LogicInterface.
	 * @param search TODO
	 */
	public static <T extends GenericParam> T buildParam(final Class<T> type, final String authUser, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final Order order, final int start, final int end, String search) {
		final T param = getParam(type);
		param.setUserName(authUser);
		param.setGrouping(grouping);
		if (search != null) {
			param.setSearch(search);
		}
		if (grouping != GroupingEntity.GROUP) {
			param.setRequestedUserName(groupingName);
		}
		param.setRequestedGroupName(groupingName);
		param.setHash(hash);
		param.setOrder(order);
		param.setOffset(start);
		if (end - start < 0 ) {
			param.setLimit(0);
		}
		else {
			param.setLimit(end - start);
		}
		if (tags != null) {
			for (String tag : tags) {
				tag = tag.trim();
				
				if (tag.length() > 2) {
					if (tag.charAt(0) != '-' && tag.charAt(0) != '<' && tag.charAt(tag.length() - 1) != '>') {
						param.addTagName(tag);
						continue;
					}
					if (tag.substring(0, 2).equals("->")) {
						param.addSimpleConceptName(tag.substring(2).trim());
						continue;
					}
					if (tag.substring(0, 3).equals("-->")) {
						if (tag.length() > 3) 
							param.addTransitiveConceptName(tag.substring(3).trim());
						else
							param.addTagName(tag);						
						continue;
					}
					if (tag.substring(tag.length() - 3, tag.length()).equals("-->")) {
						if (tag.length() > 3) 
							param.addSimpleConceptWithParentName(tag.substring(0,tag.length() - 3).trim());
						else
							param.addTagName(tag);						
						continue;
					}					
					if (tag.substring(tag.length() - 2, tag.length()).equals("->")) {
						param.addSimpleConceptWithParentName(tag.substring(0,tag.length() - 2).trim());
						continue;
					}
					if (tag.substring(0,3).equals("<->")) {
						if (tag.length() > 3) 
							param.addCorrelatedConceptName(tag.substring(3).trim());
						else
							param.addTagName(tag);
						continue;
					}															
				}
				
				// if none of the above was applicable, we add a simple tag
				param.addTagName(tag);
				
			} // end for
			
		} // end if (tags != null)
		
		return param;
	}

	/**
	 * Instatiates a param object for the given class.
	 */
	@SuppressWarnings("unchecked")
	private static <T extends GenericParam> T getParam(final Class<T> type) {
		if (type == BookmarkParam.class) {
			return (T) new BookmarkParam();
		} else if (type == BibTexParam.class) {
			return (T) new BibTexParam();
		} else if (type == TagParam.class) {
			return (T) new TagParam();
		} else if (type == UserParam.class) {
			return (T) new UserParam();
		} else if (type == GroupParam.class) {
			return (T) new GroupParam();
		} else {
			throw new RuntimeException("Can't instantiate param: " + type.getName());
		}
	}
}