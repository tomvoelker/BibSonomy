/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.common.exceptions;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author rja
 */
public class UnsupportedFileTypeException extends RuntimeException {
	private static final long serialVersionUID = 6493856479182895955L;
	
	private final Collection<String> allowedExt;

	/**
	 * Constructs a new unsupported file type exception with the specified
	 * allowed extensions. The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link #initCause(Throwable)}.
	 * 
	 * @param allowedExt
	 * 				the supported file type extensions.
	 */
	public UnsupportedFileTypeException(final Collection<String> allowedExt) {
		super("Please check your file. Only " + getExceptionExtensions(allowedExt) + " files are accepted.");
		this.allowedExt = allowedExt;
	}
	
	/**
	 * Converts the given files extensions to upper cases and connects them with "," and "or", e.g.:
	 * input:
	 *   "pdf", "ps", "djv", "djvu", "txt"
	 * output:
	 *   "PDF, PS, TXT or DJVU"
	 * @param allowedExt
	 * @return
	 */
	private static String getExceptionExtensions(final Collection<String> allowedExt) {
		final StringBuilder buf = new StringBuilder();
		Iterator<String> iterator = allowedExt.iterator();
		if (allowedExt.size() == 1) {
			return iterator.next().toUpperCase();
		}
		while (iterator.hasNext()) {
			final String extension = iterator.next().toUpperCase();
			if (!iterator.hasNext()) {
				buf.append("or ");
			}
			buf.append(extension);
			if (iterator.hasNext()) {
				buf.append(", ");
			}
		}
		return buf.toString();
	}

	/**
	 * @return the allowed Extensions
	 */
	public Collection<String> getAllowedExt() {
		return allowedExt;
	}
}