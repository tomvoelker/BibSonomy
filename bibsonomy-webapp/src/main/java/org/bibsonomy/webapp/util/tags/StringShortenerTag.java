/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.webapp.util.tags;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * TODO: document the use of this tag
 * 
 * @author sbo <sbo@cs.uni-kassel.de>
 */
public class StringShortenerTag extends RequestContextAwareTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String value;
	
	private int maxlen;
	
	/**
	 * @return maxlen
	 */
	public int getMaxlen() {
		return this.maxlen;
	}

	/**
	 * @param maxlen
	 */
	public void setMaxlen(final int maxlen) {
		this.maxlen = maxlen;
	}

	/**
	 * @return filename
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param value
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	protected int doStartTagInternal() throws Exception {
		String newFilename = "";
		if(value.length() >= maxlen) {
			
			int offset = maxlen / 2;
			
			newFilename += value.substring(0, offset) + "…" + value.substring(value.length()-offset, value.length());
			this.pageContext.getOut().print(newFilename);
		} else {
			this.pageContext.getOut().print(value);
		}
		return SKIP_BODY;

	}

}
