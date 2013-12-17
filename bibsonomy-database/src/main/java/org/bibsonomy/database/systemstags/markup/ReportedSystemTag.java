package org.bibsonomy.database.systemstags.markup;

import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsUtil;

/**
 * @author dzo
  */
public class ReportedSystemTag extends AbstractSystemTagImpl implements MarkUpSystemTag {

	/**
	 * the name of the report system tag
	 */
	public static final String NAME = "reported";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isInstance(String tagName) {
		return SystemTagsUtil.hasTypeAndArgument(tagName) && NAME.equals(SystemTagsUtil.extractType(tagName));
	}

	@Override
	public boolean isToHide() {
		return true;
	}

	@Override
	public MarkUpSystemTag newInstance() {
		try {
			return (MarkUpSystemTag) super.clone();
		} catch (CloneNotSupportedException ex) {
			// never ever reached
			return null;
		}
	}

}
