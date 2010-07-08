package org.bibsonomy.community.database;

import java.util.Collection;

import org.bibsonomy.community.database.param.CommunityResourceParam;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.model.BibTex;

/**
 * class for accessing bibtex posts 
 * @author fei
 *
 */
public class BibTexPostManager extends AbstractPostManager<BibTex> {

	/** singleton pattern's instance reference */
	protected static BibTexPostManager instance = null;
	
	/** disabled constructor */
	private BibTexPostManager() {}

	/**
	 * @return An instance of this implementation of 
	 */
	public static BibTexPostManager getInstance() {
		if (instance == null) instance = new BibTexPostManager();
		return instance;
	}
	
	@Override
	protected Collection<Post<BibTex>> getPostsForCommunityInternal(CommunityResourceParam<BibTex> param) {
		Collection<Post<BibTex>> resources;
		switch( param.getOrdering() ) {
		case ADDED:
			resources = queryForList("getNewestBibTexForCommunity", param);
			break;
		default:
			resources = queryForList("getBibTexForCommunity", param);
		};
		
		return resources;
	}

	@Override
	protected CommunityResourceParam<BibTex> getResourceParam() {
		return new CommunityResourceParam<BibTex>();
	}

}
