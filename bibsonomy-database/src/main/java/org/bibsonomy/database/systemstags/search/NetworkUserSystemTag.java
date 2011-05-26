package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.Resource;

/**
 * Systemtag for handling network users - that is users which have been 
 * imported from other social sites, e.g., facebook ('sys:network:facebook').
 * 
 * Due to compatibility issues, the empty tag is mapped to BibSonomy-Friendship
 * 
 * @author fei
 * @version $Id$
 */
public class NetworkUserSystemTag extends AbstractSearchSystemTagImpl implements SearchSystemTag {

    public static final String NAME = "network";

    /** FIXME: how do we properly use system tags??? */
    public final static String BibSonomyNetworkUser = "sys:network:bibsonomy";
    
    @Override
    public String getName() {
	return NAME;
    }

    @Override
    public NetworkUserSystemTag newInstance() {
	return new NetworkUserSystemTag();
    }

    @Override
    public void handleParam(GenericParam param) {
	// TODO: implement parameter action here
	log.debug("Network user handling not implemented.");
    }
    
    @Override
    public <T extends Resource> boolean allowsResource(Class<T> resourceType) {
	return true;
    }

}
