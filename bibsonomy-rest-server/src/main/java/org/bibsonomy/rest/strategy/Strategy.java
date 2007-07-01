package org.bibsonomy.rest.strategy;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public abstract class Strategy {

	protected final Context context;

	public Strategy(final Context context) {
		this.context = context;
	}

	/**
	 * Validates a state: correct userName, etc
	 * 
	 * @throws ValidationException
	 */
	public void validate() throws ValidationException {
	}

	/**
	 * @param request
	 * @param responseAdapter
	 * @throws InternServerException
	 * @throws NoSuchResourceException
	 *             if one part of the uri doesnt exist (the user, eg)
	 */
	public abstract void perform(HttpServletRequest request, Writer writer) throws InternServerException, NoSuchResourceException;

	/**
	 * @param userAgent
	 * @return the contentType of the answer document
	 */
	public abstract String getContentType(String userAgent);

	/**
	 * Chooses a GroupingEntity based on the parameterMap in the {@link Context}.
	 * 
	 * @return The GroupingEntity; it defaults to ALL.
	 */
	protected GroupingEntity chooseGroupingEntity() {
		String value = this.context.getStringAttribute("user", null);
		if (value != null) return GroupingEntity.USER;

		value = this.context.getStringAttribute("group", null);
		if (value != null) return GroupingEntity.GROUP;

		value = this.context.getStringAttribute("viewable", null);
		if (value != null) return GroupingEntity.VIEWABLE;

		value = this.context.getStringAttribute("friend", null);
		if (value != null) return GroupingEntity.FRIEND;

		return GroupingEntity.ALL;
	}
}