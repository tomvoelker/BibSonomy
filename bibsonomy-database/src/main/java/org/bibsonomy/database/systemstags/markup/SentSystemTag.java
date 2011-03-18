package org.bibsonomy.database.systemstags.markup;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
/**
 * @author sdo
 * @version $Id$
 */
public class SentSystemTag extends AbstractSystemTagImpl implements MarkUpSystemTag{

    public static final String NAME = "sent";
    private static boolean toHide = true;

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
	return new SentSystemTag();
    }

    @Override
    public boolean isInstance(final String tagName) {
	// in general a systemTag must have a prefix and an argument
	return present(SystemTagsUtil.extractArgument(tagName));
    }
}
