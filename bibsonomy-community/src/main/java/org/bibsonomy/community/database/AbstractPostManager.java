package org.bibsonomy.community.database;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.database.param.CommunityParam;
import org.bibsonomy.community.database.param.CommunityResourceParam;
import org.bibsonomy.community.enums.Ordering;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.ResourceCluster;
import org.bibsonomy.model.Resource;

/** 
 * base class for database managers accessing bibsonomy posts
 * @author fei
 *
 * @param <R>
 */
public abstract class AbstractPostManager<R extends Resource> extends AbstractDBManager {
	/** path to the ibatis database configuration file */
	private static final String SQL_MAP_CONFIG = "SqlMapConfig_communityPosts.xml";
	
	private static final Log log = LogFactory.getLog(AbstractPostManager.class);
	
	/**
	 * constructor
	 */
	protected AbstractPostManager() {
		super(SQL_MAP_CONFIG);
		loadConfiguration();
	}
	
	//------------------------------------------------------------------------
	// get posts
	//------------------------------------------------------------------------
	/**
	 * retrieve a list of posts, ordered desc according to given ordering
	 * 
	 * @param communityId
	 * @param order
	 * @param limit
	 * @param offset
	 * @return
	 */
	public Collection<Post<R>> getPostsForCommunity(final Integer communityId, final Ordering ordering, final int limit, final int offset) {
		final CommunityResourceParam<R> param = getResourceParam();
		param.setCommunityId(communityId);
		param.setOrdering(ordering);
		param.setLimit(limit);
		param.setOffset(offset);
		
		return ensureList(getPostsForCommunityInternal(param));
	}

	//------------------------------------------------------------------------
	// abstract interface definition
	//------------------------------------------------------------------------
	protected abstract CommunityResourceParam<R> getResourceParam();
	protected abstract Collection<Post<R>> getPostsForCommunityInternal(CommunityResourceParam<R> param);
}
