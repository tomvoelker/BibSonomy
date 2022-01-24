/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.webapp.util.DidYouKnowMessage;
import org.bibsonomy.webapp.util.RequestWrapperContext;

/**
 * Base class for command objects. Contains request and response fields
 * that are commonly used across a lot of controllers.
 * 
 * Command objects normally contain 
 * all request arguments that a controller needs and all response values,
 * which it creates. Views use the information in commands for rendering.
 * 
 * @author Jens Illig
 */
@Getter
@Setter
public class BaseCommand implements ContextCommand, DidYouKnowMessageCommand {
	
	private RequestWrapperContext context;

	/** a message-key to display a message on any jspx */
	private String messageKey;
	
	private List<String> messageParams;
	
	private DidYouKnowMessage didYouKnowMessage;
	
	@Deprecated
	private String pageTitle;

	/**
	 * @return the page title
	 */
	@Deprecated // i18n in jspx!
	public String getPageTitle() {
		return this.pageTitle;
	}

	/**
	 * 
	 * @param pageTitle the page title
	 */
	@Deprecated
	public void setPageTitle(final String pageTitle) {
		this.pageTitle = pageTitle;
	}
	
	/** The context contains the loginUser, the ckey, and other things
	 * which can not be changed by the user.
	 * 
	 * @return The context.
	 */
	@Override
	public RequestWrapperContext getContext() {
		return this.context;
	}

	/** Add a context to this command.
	 * @param context
	 */
	@Override
	public void setContext(final RequestWrapperContext context) {
		this.context = context;
	}
	
	/**
	 * sets the message with the provided parameters
	 * @param key
	 * @param params
	 */
	public void setMessage(String key, List<String> params) {
		this.setMessageKey(key);
		this.setMessageParams(params);
	}
	
	@Override
	public DidYouKnowMessage getDidYouKnowMessage() {
		return this.didYouKnowMessage;
	}

	@Override
	public void setDidYouKnowMessage(DidYouKnowMessage didYouKnowMessage) {
		this.didYouKnowMessage = didYouKnowMessage;
	}
}
