package org.bibsonomy.services.filesystem;

import java.util.Arrays;
import java.util.Collection;

/**
 * combines all logics to one simple interface to implement
 * @author dzo
 * @version $Id$
 */
public interface FileLogic extends ProfilePictureLogic, TempFileLogic, JabRefFileLogic, DocumentFileLogic {

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
