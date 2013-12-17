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
import java.io.Reader;

import org.bibsonomy.util.StringUtils;

/**
 * @author jensi
  */
public class DualDataWrapper implements DualData {

	private final Data first;
	private final Data second;

	/**
	 * construct
	 * @param first
	 * @param second
	 */
	public DualDataWrapper(final Data first, final Data second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public String getMimeType() {
		return "" + StringUtils.getSubStringBefore(first.getMimeType(),";") + ":" + StringUtils.getSubStringBefore(second.getMimeType(), ";");
	}

	@Override
	public InputStream getInputStream() {
		return this.first.getInputStream();
	}

	@Override
	public Reader getReader() {
		return this.first.getReader();
	}

	@Override
	public InputStream getInputStream2() {
		return this.second.getInputStream();
	}

	@Override
	public Reader getReader2() {
		return this.second.getReader();
	}

	@Override
	public Data getData2() {
		return second;
	}

}
