package org.bibsonomy.database.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.systemstags.database.EntryTypeSystemTag;
import org.bibsonomy.database.systemstags.database.YearSystemTag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.StringUtils;

/**
 * Supplies methods to adapt the LogicInterface to the database layer.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class LogicInterfaceHelper {
	
	protected static final Log logger = LogFactory.getLog(ChainElement.class);

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
	public static <T extends GenericParam> T buildParam(final Class<T> type, final Order order, final int start, final int end) {
		final T param = getParam(type);

		param.setOrder(order);
		param.setOffset(start);
		if (end - start < 0) {
			param.setLimit(0);
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
	 * @param loginUser logged in user as specified for {@link PostLogicInterface#getPosts}
	 * @return the fresh param object 
	 */
	public static <T extends GenericParam> T buildParam(final Class<T> type, final GroupingEntity grouping, final String groupingName, final List<String> tags, final String hash, final Order order, final int start, final int end, final String search, final FilterEntity filter, final User loginUser) {
		/*
		 * delegate to simpler method
		 */
		final T param = buildParam(type, order, start, end);

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
			
			if (param instanceof BibTexParam || param instanceof TagParam || param instanceof StatisticsParam) {
				((GenericParam) param).setSimHash(id);
			}
			param.setHash(hash.substring(1));
		} else {
			param.setHash(hash);
		}
		
		param.setUserName(loginUser.getName());
		param.setGrouping(grouping);
		
		// default search searches over all possible fields
		if (search != null) {
			param.setSearch(search);
			param.setSearchEntity(SearchEntity.ALL);
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

		if (tags != null) {
			for (String tag : tags) {
				tag = tag.trim();
				logger.debug("working on input tag: " + tag);

				if (tag.length() > 2) {
					if (tag.contains(":")) {
						if (handleSystemTag(tag, param)) {
							continue;
						}
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
		else
		{
			logger.debug("input tags are null");
		}

		return param;
	}

	@SuppressWarnings("deprecation") // TODO: lucene can't handle system tags
	private static boolean handleSystemTag(String tag, GenericParam param) {
		logger.debug("working on possible system tag " + tag);
		String tagName;
		String tagValue;
		String[] tags = tag.split(":");
		if (tag.startsWith("sys:") || tag.startsWith("system:")) {
			tagName = tags[1];
			tagValue = StringUtils.implodeStringArray(Arrays.copyOfRange(tags, 2, tags.length), ":").trim();
		} else {
			tagName = tags[0];
			tagValue = StringUtils.implodeStringArray(Arrays.copyOfRange(tags, 1, tags.length), ":").trim();
		}
/*
 * rja, 2010-02-09: turned off, since currently no system tags of this kind are
 * contained in the SystemTagFactory
 */
//		SystemTagType sTag = SystemTagFactory.createTag(tagName, tag.substring(tag.indexOf(tagName) + tagName.length()));
//		if (sTag != null && SystemTagsUtil.getAttributeValue(sTag, SystemTagFactory.GROUPING) != null) {
//			GroupingEntity ge = GroupingEntity.getGroupingEntity(SystemTagsUtil.getAttributeValue(sTag, SystemTagFactory.GROUPING));
//			if (ge != null) {
//				param.setGrouping(ge);
//				logger.debug("set grouping entity to " + ge);
//				if (GroupingEntity.USER.equals(ge)) {
//					param.setRequestedUserName(tagValue);
//				}
//				if (GroupingEntity.GROUP.equals(ge)) {
//					param.setRequestedGroupName(tagValue);
//				}
//			}
//			return true;
//		} else 
		if (tagName.equals("bibtexkey")) {
			// :bibtexkey: add bibtex key to param object
			param.setBibtexKey(tagValue);
			logger.debug("set bibtex key to " + tagValue + " after matching for bibtexkey system tag");
			return true;
		} else if (tagName.equals("days")) {
			// :days: clear the tagindex and set the value
			param.getTagIndex().clear();
			param.setDays(Integer.parseInt(tagValue));
			logger.debug("set days to " + tagValue + " after matching for days system tag");
			return true;
		} else if (tagName.equals("title")) {
			// :title: set the title to tagValue
			param.setTitle(tagValue);
			param.setGrouping(GroupingEntity.ALL);
			logger.debug("set title to " + tagValue + " after matching for title system tag");
			return true;
		} else if (tagName.equals("author")) {
			// sys:author: set search entity accordingly
			param.setSearchEntity(SearchEntity.AUTHOR);
			param.setSearch(tagValue);
			logger.debug("set search to " + tagValue + " after matching for author system tag");
			return true;
		} else if (tagName.equals("search")) {
			if (tagValue.equals("lucene")) {
				param.setSearchEntity(SearchEntity.LUCENE);
				logger.debug("set search entity to 'lucene' after matching for search system tag");
				return true;
			}
		} else if (tagName.equals("user")) {
			// this is just a workaround until the SystemTagFactory stuff above is working
			param.setGrouping(GroupingEntity.USER);
			param.setRequestedUserName(tagValue);
			logger.debug("set grouping to 'user' and requestedUserName to " + tagValue + " after matching for user system tag");
			return true;
		} else if (tagName.equals("group")) {
			// this is just a workaround until the SystemTagFactory stuff above is working
			param.setGrouping(GroupingEntity.GROUP);
			param.setRequestedGroupName(tagValue);
			logger.debug("set grouping to 'group' and requestedGroupName to " + tagValue + " after matching for group system tag");
			return true;
		} else if (tagName.equals("year")) {
			// this just another workaround until the System Tags implementation works correctly
			if (! (param instanceof BibTexParam) ) {
				// do nothing for bookmarks here
				return true;
			}
			
			final BibTexParam bibTexParam = (BibTexParam) param;
			
			// TODO: factory!!
			final YearSystemTag yearTag = new YearSystemTag();
			yearTag.setName("Year");
			param.addToSystemTags(yearTag);
			
			// 1st case: year explicitly given
            if (tagValue.matches("[12]{1}[0-9]{3}")) {
            	yearTag.setYear(tagValue);
            	bibTexParam.setYear(tagValue); // TODO: lucene can't handle system tags
            	logger.debug("Set year to " + tagValue + " after matching year system tag");
            	return true;
            } 
            // 2nd case: range (e.g. 2001-2006)
            else if (tagValue.matches("[12]{1}[0-9]{3}-[12]{1}[0-9]{3}")) {
                String[] years = tagValue.split("-");
                yearTag.setFirstYear(years[0]);
                yearTag.setLastYear(years[1]);
                bibTexParam.setFirstYear(tagValue); // TODO: lucene can't handle system tags
                bibTexParam.setLastYear(tagValue); // TODO: lucene can't handle system tags
            	logger.debug("Set firstyear/lastyear to " + years[0] + "/" + years[1] + "after matching year system tag");
            	return true;
            }
            // 3rd case: upper bound (e.g -2005) means all years before 2005 
            else if(tagValue.matches("-[12]{1}[0-9]{3}")) {
            	yearTag.setLastYear(tagValue.substring(1,tagValue.length()));
            	bibTexParam.setLastYear(tagValue.substring(1,tagValue.length())); // TODO: lucene can't handle system tags
            	logger.debug("Set lastyear to " + tagValue + "after matching year system tag");
            	return true;
            }
            // 4th case: lower bound (e.g 1998-) means all years since 1998 
            else if(tagValue.matches("[12]{1}[0-9]{3}-")) {
            	yearTag.setFirstYear(tagValue.substring(0,tagValue.length()-1));
            	bibTexParam.setFirstYear(tagValue.substring(0,tagValue.length()-1)); // TODO: lucene can't handle system tags
            	logger.debug("Set firstyear to " + tagValue + "after matching year system tag");
            	return true;            	
            }			
		} else if (tagName.equals("entrytype")){
			// TODO: this should be done by a factory!!!
			final EntryTypeSystemTag sysTag = new EntryTypeSystemTag();
			sysTag.setEntryType(tagValue);
			sysTag.setName("EntryType");
			
			param.addToSystemTags(sysTag);
			logger.debug("Set entry type to '" + tagValue +"' after matching entrytype system tag");
			return true;
		}
		
		return false;
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