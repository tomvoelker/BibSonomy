package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;

/**
 * @author dzo
 * @version $Id$
 */
public class EntryTypeSystemTag extends AbstractSearchSystemTagImpl implements SearchSystemTag {

    public static final String NAME = "entrytype";

    @Override
    public String getName() {
	return NAME;
    }

    public EntryTypeSystemTag newInstance() {
	return new EntryTypeSystemTag();
    }

    @Override
    public void handleParam(GenericParam param) {
	param.addToSystemTags(this);
	log.debug("Set entry type to '" + this.getArgument() +"' after matching entrytype system tag");
    }

    @Override
    public <T extends Resource> boolean allowsResource(Class<T> resourceType) {
	if (resourceType == Bookmark.class) {
	    return false;
	}
	return true;
    }

}
