/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.services.filesystem.extension;

import java.util.Collection;

import org.bibsonomy.util.StringUtils;

/**
 * @author dzo
 * @version $Id$
 */
public class ListExtensionChecker implements ExtensionChecker {
	
	private final Collection<String> allowedExtensions;
	
	/**
	 * @param allowedExtensions all allowed extensions
	 */
	public ListExtensionChecker(Collection<String> allowedExtensions) {
		super();
		this.allowedExtensions = allowedExtensions;
	}

	@Override
	public boolean checkExtension(String extension) {
		return StringUtils.matchExtension(extension, this.allowedExtensions);
	}

	/**
	 * @return the allowedExtensions
	 */
	@Override
	public Collection<String> getAllowedExtensions() {
		return allowedExtensions;
	}

}
