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

package org.bibsonomy.model.util;

import java.util.Collection;
import java.util.Map;

import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.util.data.Data;

/**
 * @author jensi
 * @version $Id$
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
	public Collection<ImportResource> read(ImportResource importRes) {
		
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
