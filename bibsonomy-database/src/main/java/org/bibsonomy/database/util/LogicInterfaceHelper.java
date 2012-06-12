package org.bibsonomy.database.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.SearchSystemTag;
import org.bibsonomy.model.Tag;
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
	private static final Log logger = LogFactory.getLog(LogicInterfaceHelper.class);
	
	protected static final int DEFAULT_LIST_LIMIT = 10;
	
	/**
	 * 
	 * Builds a parameter object for the given parameters from the LogicInterface.
	 * 
	 * @param <T>
	 * @param type
	 * @param order
	 * @param start
	 * @param end
	 * @return - the filled parameter object
	 */
	public static <T extends GenericParam> T buildParam(final Class<T> type, final Order order, int start, final int end) {
		final T param = getParam(type);
		
		if (start < 0) {
			start = 0;
		}
		param.setOrder(order);
		param.setOffset(start);
		if (end - start < 0) {
			param.setLimit(DEFAULT_LIST_LIMIT);
		} else {
			param.setLimit(end - start);
		}
		
		return param;
	}
	
	/**
	 * Builds a param object for the given parameters from the LogicInterface.
	 * 
	 * @param <T> the type of param object to be build
	 * @param type the type of param object to be build
	 * @param grouping as specified for {@link PostLogicInterface#getPosts}
	 * @param groupingName as specified for {@link PostLogicInterface#getPosts} 
	 * @param tags as specified for {@link PostLogicInterface#getPosts} 
	 * @param hash as specified for {@link PostLogicInterface#getPosts}
	 * @param order as specified for {@link PostLogicInterface#getPosts}
	 * @param start as specified for {@link PostLogicInterface#getPosts} 
	 * @param end as specified for {@link PostLogicInterface#getPosts}
	 * @param search as specified for {@link PostLogicInterface#getPosts} 
	 * @param filter as specified for {@link PostLogicInterface#getPosts}
	 * @param startDate as specified for {@link PostLogicInterface#getPosts}
	 * @param endDate as specified for {@link PostLogicInterface#getPosts}
	 * @param loginUser logged in user as specified for {@link PostLogicInterface#getPosts}
	 * @return the fresh param object 
	 */
	public static <T extends GenericParam> T buildParam(final Class<T> type, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final Order order, final int start, final int end, final Date startDate, final Date endDate, final String search, final FilterEntity filter, final User loginUser) {
		/*
		 * delegate to simpler method
		 */
		final T param = buildParam(type, order, start, end);

		// if hash length is 33 ,than use the first character as hash type
		if (hash != null && hash.length() == 33) {
			HashID id = HashID.SIM_HASH1;
			try {
				id = HashID.getSimHash(Integer.valueOf(hash.substring(0, 1)));
			} catch (final NumberFormatException ex) {
				throw new RuntimeException(ex);
			}
			
			if (param instanceof BibTexParam || param instanceof TagParam || param instanceof StatisticsParam) {
				param.setSimHash(id);
			}
			param.setHash(hash.substring(1));
		} else {
			param.setHash(hash);
		}
		/*
		 * set start and end date
		 */
		param.setStartDate(startDate);
		param.setEndDate(endDate);
		
		param.setUserName(loginUser.getName());
		param.setGrouping(grouping);
		
		// default search searches over all possible fields
		if (search != null) {
			param.setSearch(search);
		}
		
		if (grouping != GroupingEntity.GROUP) {
			param.setRequestedUserName(groupingName);
		}
		
		param.setRequestedGroupName(groupingName);
		
		// add filters
		param.setFilter(filter);

		// set the groups the logged-in user may see 
		//  - every user may see public posts - this one is added in the constructor of DBLogic
		//  - groups the logged-in user is explicitely member of
		param.addGroupsAndGroupnames(UserUtils.getListOfGroups(loginUser));
		//  - private / friends groups are set later on 
		//    (@see org.bibsonomy.database.util.DatabaseUtils.prepareGetPostForUser)

		if (present(tags)) {
			for (String tag : tags) {
				tag = tag.trim();
				logger.debug("working on input tag: " + tag);

				if (tag.length() > 2) {
					/*
					 * is tag a system tag?
					 */
					final SearchSystemTag searchTag = SystemTagsUtil.createSearchSystemTag(tag);
					if (present(searchTag)) {
						searchTag.handleParam(param);
						continue;
					}

					if (tag.charAt(0) != '-' && tag.charAt(0) != '<' && tag.charAt(tag.length() - 1) != '>') {
						param.addTagName(tag);
						continue;
					}
					
					// match tags like "->tag"
					if (tag.startsWith(Tag.CONCEPT_PREFIX)) {
						param.addSimpleConceptName(tag.substring(Tag.CONCEPT_PREFIX.length()).trim());
						continue;
					}
					
					// match tags like "-->tag"
					if (tag.startsWith(Tag.TRANSITIVE_CONCEPT_PREFIX)) {
						if (tag.length() > Tag.TRANSITIVE_CONCEPT_PREFIX.length()) {
							param.addTransitiveConceptName(tag.substring(Tag.TRANSITIVE_CONCEPT_PREFIX.length()).trim());
						} else {
							param.addTagName(tag);
						}
						continue;
					}
					
					// match tags like "tag-->"
					if (tag.endsWith(Tag.TRANSITIVE_CONCEPT_PREFIX)) {
						if (tag.length() > Tag.TRANSITIVE_CONCEPT_PREFIX.length()) {
							param.addSimpleConceptWithParentName(tag.substring(0, tag.length() - Tag.TRANSITIVE_CONCEPT_PREFIX.length()).trim());
						} else {
							param.addTagName(tag);
						}
						continue;
					}
					
					// match tags like "tag->"
					if (tag.endsWith(Tag.CONCEPT_PREFIX)) {
						param.addSimpleConceptWithParentName(tag.substring(0, tag.length() - Tag.CONCEPT_PREFIX.length()).trim());
						continue;
					}
					
					// match tags like "<->tag"
					if (tag.startsWith(Tag.CORRELATED_CONCEPT_PREFIX)) {
						if (tag.length() > Tag.CORRELATED_CONCEPT_PREFIX.length()) {
							param.addCorrelatedConceptName(tag.substring(Tag.CORRELATED_CONCEPT_PREFIX.length()).trim());
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
		else {
			logger.debug("input tags are null");
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