package org.bibsonomy.database.util;

import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.model.util.UserUtils;

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
	 * 
	 * @param <T> the type of param object to be build
	 * @param type the type of param object to be build
	 * @param authUser name of logged in user as specified for {@link PostLogicInterface#getPosts}
	 * @param grouping as specified for {@link PostLogicInterface#getPosts}
	 * @param groupingName as specified for {@link PostLogicInterface#getPosts} 
	 * @param tags as specified for {@link PostLogicInterface#getPosts} 
	 * @param hash as specified for {@link PostLogicInterface#getPosts}
	 * @param order as specified for {@link PostLogicInterface#getPosts}
	 * @param start as specified for {@link PostLogicInterface#getPosts} 
	 * @param end as specified for {@link PostLogicInterface#getPosts}
	 * @param search as specified for {@link PostLogicInterface#getPosts} 
	 * @param filter as specified for {@link PostLogicInterface#getPosts}
	 * @param loginUser TODO
	 * @return the fresh param object 
	 */
	public static <T extends GenericParam> T buildParam(final Class<T> type, final String authUser, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final Order order, final int start, final int end, final String search, final FilterEntity filter, final User loginUser) {
		final T param = getParam(type);

		// if hash length is 33 ,than use the first character as hash type
		if (hash != null && hash.length() == 33) {
			HashID id = HashID.SIM_HASH1;
			try {
				// FIXME: this logic already exists in HashID.getSimHash()
				switch (Integer.valueOf(hash.substring(0, 1))) {
					case 0: id = HashID.SIM_HASH0; break;
					case 2: id = HashID.SIM_HASH2; break;
					case 3: id = HashID.SIM_HASH3; break;
					default: break;
				}
			} catch (final NumberFormatException ex) {
				throw new RuntimeException(ex);
			}
			
			if (param instanceof BibTexParam) {
				((BibTexParam) param).setSimHash(id);
			} else if (param instanceof TagParam) {
				((TagParam) param).setHashId(id);
			}
			param.setHash(hash.substring(1));
		} else {
			param.setHash(hash);
		}
		
		param.setUserName(authUser);
		param.setGrouping(grouping);
		if (search != null) {
			param.setSearch(search);
		}
		if (grouping != GroupingEntity.GROUP) {
			param.setRequestedUserName(groupingName);
		}
		param.setRequestedGroupName(groupingName);
		
		// add filters
		param.setFilter(filter);

		// set the groupIDs the logged-in user may see 
		//  - every user may see public posts - this one is added in the constructor of DBLogic
		//  - groups the logged-in user is explicitely member of
		param.addGroups(UserUtils.getListOfGroupIDs(loginUser));
		//  - private / friends groups are set later on 
		//    (@see org.bibsonomy.database.util.DatabaseUtils.prepareGetPostForUser)

		param.setOrder(order);
		param.setOffset(start);
		if (end - start < 0) {
			param.setLimit(0);
		} else {
			param.setLimit(end - start);
		}

		if (tags != null) {
			for (String tag : tags) {
				tag = tag.trim();

				if (tag.length() > 2) {
					if (tag.length() > 14 && tag.startsWith("sys:bibtexkey:")) {
						// :bibtexkey:
						((BibTexParam) param).setBibtexKey(tag.substring(14).trim());
						continue;
					}

					if (tag.length() > 9 && tag.startsWith("sys:days:")) {
						// clear the tagindex and set the value
						param.getTagIndex().clear();
						param.setDays(Integer.parseInt(tag.substring(9).trim()));
						break;
					}
					
					if (tag.charAt(0) != '-' && tag.charAt(0) != '<' && tag.charAt(tag.length() - 1) != '>') {
						param.addTagName(tag);
						continue;
					}
					if (tag.startsWith("->")) {
						param.addSimpleConceptName(tag.substring(2).trim());
						continue;
					}
					if (tag.startsWith("-->")) {
						if (tag.length() > 3) {
							param.addTransitiveConceptName(tag.substring(3).trim());
						} else {
							param.addTagName(tag);
						}
						continue;
					}
					if (tag.substring(tag.length() - 3, tag.length()).equals("-->")) {
						if (tag.length() > 3) {
							param.addSimpleConceptWithParentName(tag.substring(0, tag.length() - 3).trim());
						} else {
							param.addTagName(tag);
						}
						continue;
					}
					if (tag.substring(tag.length() - 2, tag.length()).equals("->")) {
						param.addSimpleConceptWithParentName(tag.substring(0, tag.length() - 2).trim());
						continue;
					}
					if (tag.startsWith("<->")) {
						if (tag.length() > 3) {
							param.addCorrelatedConceptName(tag.substring(3).trim());
						} else {
							param.addTagName(tag);
						}
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
		} else if (type == TagRelationParam.class) {
			return (T) new TagRelationParam();
		} else if (type == StatisticsParam.class) {
			return (T) new StatisticsParam();
		} else {
			throw new RuntimeException("Can't instantiate param: " + type.getName());
		}
	}
}