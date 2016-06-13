/**
 * BibSonomy Search - Helper classes for search modules.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.search.index.utils.extractor;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.bibsonomy.util.Sets;
import org.bibsonomy.util.StringUtils;

/**
 * a {@link ContentExtractor} that extracts content from simple plain files
 * like txt
 * 
 * @author dzo
 */
public class PlainTextExtractor implements ContentExtractor {
	
	private static final Set<String> SUPPORTED_EXTENSIONS = Sets.asSet("txt", "sql", "md", "csv", "pig", "xml", "json", "tex");
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.index.utils.ContentExtractor#supports(java.io.File)
	 */
	@Override
	public boolean supports(final String fileName) {
		final String extension = FilenameUtils.getExtension(fileName);
		return present(extension) && SUPPORTED_EXTENSIONS.contains(extension.toLowerCase());
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.index.utils.ContentExtractor#extractContent(java.io.File)
	 */
	@Override
	public String extractContent(final File file) throws IOException {
		final byte[] allReadBytes = Files.readAllBytes(Paths.get(file.toURI()));
		return new String(allReadBytes, StringUtils.DEFAULT_CHARSET);
	}

}
