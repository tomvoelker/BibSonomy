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
package org.bibsonomy.services.filesystem;

import java.util.Arrays;
import java.util.Collection;

/**
 * combines all logics to one simple interface to implement
 * @author dzo
 */
public interface FileLogic extends ProfilePictureLogic, TempFileLogic, JabRefFileLogic, CSLFileLogic, DocumentFileLogic {

	/** allowed browser bookmark export extensions */
	public static final Collection<String> BROWSER_IMPORT_EXTENSIONS = Arrays.asList("html", "htm");
	/**
	 * all extensions allowed for document upload
	 * pdf, ps, djv, djvu, txt extensions
	 */
	public static final Collection<String> DOCUMENT_EXTENSIONS = Arrays.asList(
		"pdf", "ps", 
		"djv", "djvu", 
		"txt", "tex",
		"doc", "docx", "ppt", "pptx", "xls", "xlsx", 
		"ods", "odt", "odp",
		"jpg", "jpeg", "tif", "tiff", "png",
		"htm", "html",
		"epub"
		);
	/**
	 * the extension of a BibTeX file
	 */
	public static final String BIBTEX_EXTENSION = "bib";
	/**
	 * bibtex, endnote extension
	 */
	public static final Collection<String> BIBTEX_ENDNOTE_EXTENSIONS = Arrays.asList(BIBTEX_EXTENSION, "endnote", "ris");
}
