package org.bibsonomy.database.systemstags.markup;

import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;

/**
 * @author sdo
 * @version $Id$
 */
public class MyOwnSystemTag extends AbstractSystemTagImpl implements MarkUpSystemTag {

    public static final String NAME = "myOwn";
    private static boolean toHide = false;
   
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
	return new MyOwnSystemTag();
    }

    @Override
    public boolean isInstance(final String tagName) {
	return NAME.toLowerCase().equals(tagName.toLowerCase());
    }

}
