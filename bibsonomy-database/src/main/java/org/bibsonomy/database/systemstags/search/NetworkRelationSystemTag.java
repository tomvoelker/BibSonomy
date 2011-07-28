package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.systemstags.SystemTagsUtil;

/**
 * System tag for representing relationships in social networking systems 
 * such as facebook, BibSonomy, etc.  
 * 
 * @author fmi
 * @version $Id$
 */
public class NetworkRelationSystemTag extends UserRelationSystemTag {

	/**
	 * the name of the network system tag
	 */
	public static final String NAME = "network";
	
	// FIXME: SystemTagsUtil and SystemTagFactory have a cyclic dependency which is triggered if included here
	/** the system tag for representing BibSonomy's friendship relation (==trust network) */
	public final static String BibSonomyFriendSystemTag = SystemTagsUtil.buildSystemTagString(NAME, "bibsonomy-friend");// "sys:network:bibsonomy-friend";  // 
	
	/** the system tag for representing BibSonomy's follower relation  */
	public final static String BibSonomyFollowerSystemTag = "sys:network:bibsonomy-follower";  //SystemTagsUtil.buildSystemTagString(NAME, "bibsonomy-follower");
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public NetworkRelationSystemTag newInstance() {
		return new NetworkRelationSystemTag();
	}
}
