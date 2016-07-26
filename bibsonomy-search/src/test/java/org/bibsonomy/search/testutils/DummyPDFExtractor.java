/**
 * BibSonomy Search - Helper classes for search modules.
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
package org.bibsonomy.search.testutils;

import java.io.File;
import java.io.IOException;

import org.bibsonomy.search.index.utils.extractor.ContentExtractor;

/**
 * dummy pdf extractor for tests
 *
 * @author dzo
 */
public class DummyPDFExtractor implements ContentExtractor {

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.index.utils.extractor.ContentExtractor#supports(java.lang.String)
	 */
	@Override
	public boolean supports(String fileName) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.index.utils.extractor.ContentExtractor#extractContent(java.io.File)
	 */
	@Override
	public String extractContent(File file) throws IOException {
		return null;
	}

}
