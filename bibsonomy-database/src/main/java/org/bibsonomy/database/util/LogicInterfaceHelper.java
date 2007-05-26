package org.bibsonomy.database.util;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.Order;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.TagParam;

/**
 * Supplies methods to adapt the LogicInterface to the database layer.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class LogicInterfaceHelper {

	/**
	 * Builds a param object for the given parameters from the LogicInterface.
	 */
	public static <T extends GenericParam> T buildParam(final Class<T> type, final String authUser, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final Order order, final int start, final int end) {
		final T param = getParam(type);
		param.setUserName(authUser);
		param.setGrouping(grouping);
		if (grouping != GroupingEntity.GROUP) {
			param.setRequestedUserName(groupingName);
		}
		param.setRequestedGroupName(groupingName);
		param.setHash(hash);
		param.setOrder(order);
		param.setOffset(start);
		param.setLimit(end - start);
		if (tags != null) {
			for (final String tag : tags) {
				param.addTagName(tag);
			}
		}
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
		} else {
			throw new RuntimeException("Can't instantiate param: " + type.getName());
		}
	}
}