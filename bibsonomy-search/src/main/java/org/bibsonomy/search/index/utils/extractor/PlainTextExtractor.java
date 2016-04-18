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
	
	private static final Set<String> SUPPORTED_EXTENSIONS = Sets.asSet("txt", "sql", "md", "csv", "pig", "xml", "json");
	
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
