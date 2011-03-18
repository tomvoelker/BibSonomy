package org.bibsonomy.database.systemstags.markup;

import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;

/**
 * @author sdo
 * @version $Id$
 */
public class RelevantForSystemTag extends AbstractSystemTagImpl implements MarkUpSystemTag {

    public static final String NAME = "relevantfor";
    private static boolean toHide = true;

    /*
     * TODO: check how arguments are being handled! Should this be done in here?
     */


    @Override
    public String getName() {
	return NAME;
    }

    @Override
    public boolean isToHide() {
	return toHide;
    }

    @Override
    public MarkUpSystemTag newInstance() {
	return new RelevantForSystemTag();
    }
    
}
