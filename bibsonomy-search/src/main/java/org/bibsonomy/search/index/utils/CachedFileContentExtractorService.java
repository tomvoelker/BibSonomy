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
package org.bibsonomy.search.index.utils;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Document;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.util.StringUtils;

/**
 * a cache wrapper for a {@link FileContentExtractorService}
 *
 * @author dzo
 */
public class CachedFileContentExtractorService implements FileContentExtractorService {
	private static final Log log = LogFactory.getLog(CachedFileContentExtractorService.class);
	
	private FileContentExtractorService fileContentExtractorService;
	
	private FileLogic fileLogic;
	
	/**
	 * @param fileContentExtractorService
	 * @param fileLogic
	 */
	public CachedFileContentExtractorService(FileContentExtractorService fileContentExtractorService, FileLogic fileLogic) {
		this.fileContentExtractorService = fileContentExtractorService;
		this.fileLogic = fileLogic;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.index.utils.FileContentExtractorService#extractContent(org.bibsonomy.model.Document)
	 */
	@Override
	public String extractContent(final Document document) {
		final File file = getCacheFile(document);
		// XXX: not nice, currently for the tests TODO: fix
		if (file == null) {
			return null;
		}
		synchronized(this) {
			if (file.exists()) {
				try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StringUtils.DEFAULT_CHARSET));) {
					return StringUtils.getStringFromReader(reader);
				} catch (final IOException e) {
					log.error("failed to load cached file " + file.getName(), e);
					return null;
				}
			}
			
			final String result = this.fileContentExtractorService.extractContent(document);
			try {
				// create the new file
				file.createNewFile();
				
				if (!present(result)) {
					return result;
				}
				// write it to the file
				final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StringUtils.DEFAULT_CHARSET));
				writer.write(result);
				writer.close();
			} catch (final IOException e) {
				log.error("error writing cache file " + file.getName(), e);
			}
			return result;
		}
	}

	/**
	 * @param document
	 * @return
	 */
	private File getCacheFile(Document document) {
		return fileLogic.getContentCacheFileForDocument(document);
	}

}
