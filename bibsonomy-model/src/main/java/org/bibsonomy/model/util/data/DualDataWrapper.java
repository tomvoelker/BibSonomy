/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
