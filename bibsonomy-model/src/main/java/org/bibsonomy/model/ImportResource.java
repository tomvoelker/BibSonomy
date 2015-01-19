/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model;

import org.bibsonomy.model.util.data.Data;

/**
 * @author jensi
 * 
 * XXX: find solution without subclassing bibtex
 */
public final class ImportResource extends BibTex {

	private static final long serialVersionUID = -7090432859414957747L;
	
	private final Data data;
	
	private final boolean alreadyParsed;

	private final BibTex resource;
	
	/**
	 * Creates a resource that still has to be parsed
	 * @param data
	 */
	public ImportResource(final Data data) {
		this(null, data);
	}
	
	/**
	 * Creates a resource that has already been parsed
	 */
	public ImportResource() {
		this.data = null;
		this.alreadyParsed = true;
		this.resource = null;
	}
	
	/**
	 * Creates a resource that still has to be parsed
	 * @param resource resource with additional fields (will be overridden if parsed data is available)
	 * @param data
	 */
	public ImportResource(final BibTex resource, final Data data) {
		this.resource = resource;
		this.data = data;
		this.alreadyParsed = false;
	}

	@Override
	public void recalculateHashes() {
	}

	/**
	 * @return the data
	 */
	public Data getData() {
		return this.data;
	}

	/**
	 * @return the alreadyParsed
	 */
	public boolean isAlreadyParsed() {
		return this.alreadyParsed;
	}

	/**
	 * @return the resource
	 */
	public BibTex getResource() {
		return this.resource;
	}

}
