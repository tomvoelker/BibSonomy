package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;

/**
 * @author sdo
 * @version $Id$
 */
public abstract class AbstractSearchSystemTagImpl extends AbstractSystemTagImpl implements SearchSystemTag{

    @Override
    public boolean isToHide() {
	return false;
    }


}
