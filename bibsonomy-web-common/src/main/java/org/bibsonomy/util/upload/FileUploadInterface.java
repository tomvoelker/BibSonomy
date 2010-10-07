package org.bibsonomy.util.upload;

import org.bibsonomy.model.Document;
import org.bibsonomy.model.User;

/**
 * @author  Christian Kramer
 * @version $Id$
 */
public interface FileUploadInterface {

	/**
	 * firefox extion
	 */
	public static final String[] firefoxImportExt = { "html" };
	
	/**
	 * pdf, ps, djv, djvu, txt extensions
	 */
	public static final String[] fileUploadExt = { "pdf", "ps", "djv", "djvu", "txt" };
	
	/**
	 * png, jpg extensions
	 */
	public static final String[] pictureExt = { "png", "jpg", "jpeg" };
	
	/**
	 * layout defintion extension
	 */
	public static final String[] fileLayoutExt = { "layout" };
	
	/**
	 * bibtex, endnote extension
	 */
	public static final String[] bibtexEndnoteExt = {"bib", "endnote"};
	
	/**
	 * Writes the uploaded file to the disk and returns the file together
	 * with meta information in the document
	 *
	 * @return The document describing the file (including the file!).
	 * @throws Exception
	 */
	public Document writeUploadedFile() throws Exception;

	/**
	 * Stores the created file on the hard drive and returns a document object
	 * The parameter string is needed for the creation for the hashedName of the document object
	 * 
	 * @param hashedName 
	 * @param loginUser 
	 * @return the document object representation
	 * @throws Exception
	 */
	public Document writeUploadedFile(String hashedName, User loginUser) throws Exception;

}