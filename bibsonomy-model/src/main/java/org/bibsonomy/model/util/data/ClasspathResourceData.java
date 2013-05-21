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

package org.bibsonomy.model.util.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author jensi
 * @version $Id$
 */
public class ClasspathResourceData implements Data {

	private final String mimeType;
	private final String resourcePath;

	/**
	 * construct
	 * @param resourcePath
	 * @param mimeType
	 */
	public ClasspathResourceData(String resourcePath, String mimeType) {
		this.resourcePath = resourcePath;
		this.mimeType = mimeType;
	}
	
	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public InputStream getInputStream() {
		return getClass().getResourceAsStream(resourcePath);
	}

	@Override
	public Reader getReader() {
		return new InputStreamReader(getInputStream());
	}

}
