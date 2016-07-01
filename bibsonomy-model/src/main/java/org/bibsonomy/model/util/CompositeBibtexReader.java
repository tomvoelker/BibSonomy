/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.model.util;

import java.util.Collection;
import java.util.Map;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.util.data.Data;

/**
 * @author jensi
 */
public class CompositeBibtexReader implements BibTexReader {
	private final Map<String, BibTexReader> bibtexReadersByMimeType;
	
	/**
	 * instantiate
	 * @param bibtexReadersByMimeType
	 */
	public CompositeBibtexReader(final Map<String, BibTexReader> bibtexReadersByMimeType) {
		this.bibtexReadersByMimeType = bibtexReadersByMimeType;
	}
	
	@Override
	public Collection<BibTex> read(ImportResource importRes) {
		
		final Data data = importRes.getData();
		String type = data.getMimeType();
		if (type == null) {
			throw new IllegalArgumentException("null mimetype");
		}
		final BibTexReader bibReader = this.bibtexReadersByMimeType.get(type);
		if (bibReader == null) {
			throw new UnsupportedOperationException("unsupported import mimetype '" + type + "'");
		}
		return bibReader.read(importRes);
	}

}
