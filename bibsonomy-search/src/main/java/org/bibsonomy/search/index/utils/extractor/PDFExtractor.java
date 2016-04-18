package org.bibsonomy.search.index.utils.extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;

/**
 * extracts text from pdf
 * 
 * @author dzo
 */
public class PDFExtractor implements ContentExtractor {
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.index.utils.ContentExtractor#supports(java.io.File)
	 */
	@Override
	public boolean supports(final String fileName) {
		final String extension = FilenameUtils.getExtension(fileName);
		return "pdf".equalsIgnoreCase(extension);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.index.utils.ContentExtractor#extractContent(java.io.File)
	 */
	@Override
	public String extractContent(final File file) throws IOException {
		final FileInputStream input = new FileInputStream(file);
		final PdfReader pdfReader = new PdfReader(input);
		final PdfReaderContentParser pdfParser = new PdfReaderContentParser(pdfReader);
		final StringBuilder content = new StringBuilder();
		for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
			final SimpleTextExtractionStrategy listener = new SimpleTextExtractionStrategy();
			pdfParser.processContent(i, listener);
			content.append(listener.getResultantText());
			content.append(" ");
		}
		
		input.close();
		pdfReader.close();
		return content.toString().trim();
	}
}
