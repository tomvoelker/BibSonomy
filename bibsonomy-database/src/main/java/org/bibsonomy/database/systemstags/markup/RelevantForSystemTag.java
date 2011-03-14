package org.bibsonomy.database.systemstags.markup;

import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTag;

/**
 * @author sdo
 * @version $Id$
 */
public class RelevantForSystemTag extends AbstractSystemTagImpl implements SystemTag {

    public static final String NAME = "relevantfor";
    private static boolean toHide = true;

    @Override
    public String getName() {
	return NAME;
    }
 
    @Override
    public boolean isToHide() {
	return toHide;
    }

}
