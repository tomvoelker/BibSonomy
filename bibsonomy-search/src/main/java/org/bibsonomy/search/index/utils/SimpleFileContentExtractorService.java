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

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Document;
import org.bibsonomy.search.index.utils.extractor.ContentExtractor;
import org.bibsonomy.services.filesystem.FileLogic;

/**
 * service to extract file content
 *
 * @author dzo
 */
public class SimpleFileContentExtractorService implements FileContentExtractorService {
	private static final Log log = LogFactory.getLog(SimpleFileContentExtractorService.class);
	
	private FileLogic fileLogic;
	
	private List<ContentExtractor> extractors = Collections.emptyList();
	
	private ExecutorService executorService = Executors.newFixedThreadPool(1);
	
	/**
	 * 
	 * @param document
	 * @return the file content
	 */
	@Override
	public String extractContent(final Document document) {
		final File file = this.fileLogic.getFileForDocument(document);
		for (final ContentExtractor contentExtractor : this.extractors) {
			if (contentExtractor.supports(document.getFileName())) {
				final Future<String> task = this.executorService.submit(new Callable<String>() {
					@Override
					public String call() throws Exception {
						final String absolutePath = file.getAbsolutePath();
						try {
							final long time = System.currentTimeMillis();
							final String result = contentExtractor.extractContent(file);
							log.warn(absolutePath + " " + (System.currentTimeMillis() - time));
							return result;
						} catch (final Exception e) {
							log.error("error extracting content from file " + absolutePath);
							return null;
						}
					}
				});
				
				try {
					return task.get(30, TimeUnit.SECONDS);
				} catch (final InterruptedException | ExecutionException | TimeoutException e) {
					log.debug("could not extract content in time from document " + document.getFileHash());
					return null;
				}
			}
		}
		
		return null;
	}

	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(FileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}

	/**
	 * @param extractors the extractors to set
	 */
	public void setExtractors(List<ContentExtractor> extractors) {
		this.extractors = extractors;
	}

	/**
	 * @param executorService the executorService to set
	 */
	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
}
