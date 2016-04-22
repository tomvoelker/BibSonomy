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
public class FileContentExtractorService {
	private static final Log log = LogFactory.getLog(FileContentExtractorService.class);
	
	private FileLogic fileLogic;
	
	private List<ContentExtractor> extractors = Collections.emptyList();
	
	private ExecutorService executorService = Executors.newFixedThreadPool(1);
	
	/**
	 * 
	 * @param document
	 * @return the file content
	 */
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
					return task.get(2 * 60, TimeUnit.SECONDS); // FIXME: change timeout
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
