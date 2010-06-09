package org.bibsonomy.community.database;

import java.util.Collection;

import org.bibsonomy.community.database.param.CommunityResourceParam;
import org.bibsonomy.community.enums.Ordering;
import org.bibsonomy.community.model.Tag;
import org.bibsonomy.model.Resource;

public class TagManager extends AbstractDBManager {
	/** path to the ibatis database configuration file */
	private static final String SQL_MAP_CONFIG = "SqlMapConfig_communityPosts.xml";
	
	/** singleton pattern's instance reference */
	protected static TagManager instance = null;
	
	protected TagManager() {
		super(SQL_MAP_CONFIG);
	}

	/**
	 * @return An instance of this implementation of 
	 */
	public static TagManager getInstance() {
		if (instance == null) instance = new TagManager();
		return instance;
	}
	//------------------------------------------------------------------------
	// get posts
	//------------------------------------------------------------------------
	public Collection<Tag> getTagCloudForCommunity(final Integer runId, final Integer communityId, final Ordering ordering, final int limit, final int offset) {
		final CommunityResourceParam<Resource> param = new CommunityResourceParam<Resource>();
		param.setRunId(runId);
		param.setCommunityId(communityId);
		param.setLimit(limit);
		param.setOffset(offset);
		return ensureList(queryForTagList("getTagCloudPopularForCommunity", param));
	}
	

	//------------------------------------------------------------------------
	// helper functions
	//------------------------------------------------------------------------
	private Collection<Tag> queryForTagList(final String queryName, final CommunityResourceParam<Resource> param) {
		return queryForList(queryName, param);
	}
}
