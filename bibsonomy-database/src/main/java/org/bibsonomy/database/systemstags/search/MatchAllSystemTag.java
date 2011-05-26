package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.Resource;

/**
 * Systemtag for matching every other tag (i.e. wild card representation)
 * 
 * @author fei
 * @version $Id$
 */
public class MatchAllSystemTag extends AbstractSearchSystemTagImpl implements SearchSystemTag {

    public static final String NAME = "all";

    /** FIXME: how do we properly use system tags??? */
    public final static String VALUE = "sys:all";
    
    @Override
    public String getName() {
	return NAME;
    }

    @Override
    public MatchAllSystemTag newInstance() {
	return new MatchAllSystemTag();
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
