package org.bibsonomy.database.systemstags.markup;
import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
/**
 * @author sdo
 * @version $Id$
 * This SystemTag is used to create own tags that are hidden from other users
 * The tag is of the form sys:hidden:<MyHiddenArgument> where the sys|system prefix is optional
 * The argument cannot be empty
 */
public class HiddenTagSystemTag extends AbstractSystemTagImpl implements MarkUpSystemTag {

	public static final String NAME = "hidden";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isToHide() {
		return true;
	}


	@Override
	public HiddenTagSystemTag newInstance() {
		return new HiddenTagSystemTag();
	}

	@Override
	public boolean isInstance(final String tagName) {
		return SystemTagsUtil.hasTypeAndArgument(tagName) && NAME.equals(SystemTagsUtil.extractType(tagName));
	}

}