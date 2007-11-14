package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.log4j.Logger;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.DocumentParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Document;
import org.bibsonomy.util.ExceptionUtils;

/**
 * 
 * @version $Id$
 * @author Christian Kramer
 *
 */
public class DocumentDatabaseManager extends AbstractDatabaseManager{
	
	private static final Logger log = Logger.getLogger(UserDatabaseManager.class);
	private final static DocumentDatabaseManager singleton = new DocumentDatabaseManager();

	private DocumentDatabaseManager() {
	}

	/**
	 * @return DocumentDatabaseManager
	 */
	public static DocumentDatabaseManager getInstance() {
		return singleton;
	}
	
	/**
	 * This checks if the username equals to the resource-username it also checks if the resource is 
	 * a bibtex entry. If the username doesn't equal or it's not a bibtex entry it will return false.
	 * 
	 * @param docParam
	 * @param session
	 * @return true if the user who uploads the document is the owner of this bibtex entry otherwise false
	 */
	public boolean validateResource(final DocumentParam docParam, final DBSession session){
		final String userName = docParam.getUserName();
		final String resourceHash = docParam.getResourceHash();

		if (present(userName) == false || present(resourceHash) == false){
			return false;
		}
		final String userOfResource = this.getUserByResourceHash(docParam, session);

		return userOfResource.equals(userName);
	}
	
	/**
	 * 
	 * @param docParam
	 * @param session
	 * @return username of bibtex entry as String 
	 */
	public String getUserByResourceHash(final DocumentParam docParam, final DBSession session){
		return this.queryForObject("getUserByResourceHash", docParam, String.class, session);
	}
	
	/**
	 * 
	 * This checks for existing documents. If there's an existing document according to the 
	 * resourcehash the method will return true, otherwise false.
	 * 
	 * @param docParam
	 * @param session
	 * @return true/false 
	 */
	public boolean checkForExistingDocuments(final DocumentParam docParam, final DBSession session){
		final String resourceHash = docParam.getResourceHash();
		if (present(resourceHash) == false){
			return false;
		}
		final int contentId = this.getContentIdByHash(docParam, session);
		
		docParam.setContentId(contentId);
		
		final String existingHash = this.queryForObject("checkContentId", docParam, String.class, session);
		
		if (existingHash == null){
			return false;
		}
		return true;
	}
	
	/**
	 * Inserts a new document to the db
	 * 
	 * @param docParam
	 * @param session
	 */
	public void addDocument(final DocumentParam docParam, final DBSession session){
		if (docParam == null) ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Document object isn't present");
		this.insert("insertDoc", docParam, session);
	}
	
	/**
	 * Updates an existing document with the new hash and filename
	 * 
	 * @param docParam
	 * @param session
	 */
	public void updateDocument(final DocumentParam docParam, final DBSession session){
		if (docParam == null) ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Document object isn't present");
		this.update("updateDoc", docParam, session);
	}
	
	/**
	 * This method gets the content id of the bibtex resource 
	 * 
	 * @param docParam
	 * @param session
	 * @return the content id of the bibtex entry
	 */
	public int getContentIdByHash(final DocumentParam docParam, final DBSession session){
		return this.queryForObject("getContentIdByHash", docParam, int.class, session);
	}
	
	/**
	 * This method gets a document object with the name and the hash 
	 * 
	 * @param docParam
	 * @param session
	 * @return document
	 */
	public Document getDocument(final DocumentParam docParam, final DBSession session){
		return this.queryForObject("getDocument", docParam,Document.class, session);
	}
}