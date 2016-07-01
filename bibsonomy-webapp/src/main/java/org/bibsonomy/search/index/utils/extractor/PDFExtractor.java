/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
 * XXX: itext has a AGPL license so we moved the extractor here to release
 * all other subprojects with a LGPL license
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
