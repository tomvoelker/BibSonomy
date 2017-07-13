/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.DocumentParam;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.enums.FavouriteLayoutSource;
import org.bibsonomy.model.user.settings.FavouriteLayout;

/**
 * @author Christian Kramer
 */
public class DocumentDatabaseManager extends AbstractDatabaseManager {
	/**
	 * Documents not attached to posts get this value as content_id.
	 */
	public static final int DEFAULT_CONTENT_ID = 0;
	
	private static final DocumentDatabaseManager singleton = new DocumentDatabaseManager();
	
	private final DatabasePluginRegistry plugins;
	private UserDatabaseManager userDatabaseManager;
	
	/**
	 * @return DocumentDatabaseManager
	 */
	public static DocumentDatabaseManager getInstance() {
		return singleton;
	}

	private DocumentDatabaseManager() {
		this.plugins = DatabasePluginRegistry.getInstance();
		
	}

	/**
	 * Checks, if the post has already a document with that name attached.
	 * 
	 * One post might have several documents attached. The documents are
	 * identified by their file name. Only one document per filename/post is
	 * allowed
	 * 
	 * @param userName
	 * @param resourceHash
	 * @param fileName
	 * @param session
	 * @return <code>true</code> if a document is attached to that hash,
	 *         <code>false</code> otherwise
	 */
	public boolean checkForExistingDocuments(final String userName, final String resourceHash, final String fileName, final DBSession session) {
		return present(this.getDocumentForPost(userName, resourceHash, fileName, session));
	}
	
	public Document getDocumentForPost(final String userName, final String resourceHash, final String fileName, final DBSession session) {
		if (!present(resourceHash)) {
			return null;
		}

		final DocumentParam docParam = new DocumentParam();
		docParam.setUserName(userName);
		docParam.setResourceHash(resourceHash);
		docParam.setFileName(fileName);

		/*
		 * if a post with that filename attached exists, we return true
		 */
		return this.queryForObject("getDocumentForPost", docParam, Document.class, session);
	}

	/**
	 * Inserts a new document to the db
	 * 
	 * @param userName
	 * @param contentId
	 * @param fileHash
	 * @param fileName
	 * @param md5hash
	 * @param session
	 */
	public void addDocument(final String userName, final int contentId, final String fileHash, final String fileName, final String md5hash, final DBSession session) {
		final DocumentParam docParam = new DocumentParam();
		docParam.setUserName(userName);
		docParam.setFileHash(fileHash);
		docParam.setFileName(fileName);
		docParam.setContentId(contentId);
		docParam.setMd5hash(md5hash);
		
		this.insert("insertDoc", docParam, session);
		
		//updating favourite layouts
		User user = this.userDatabaseManager.getUserDetails(userName, session);
		UserSettings userSettings = user.getSettings();
		if (contentId == 0){
			//more or less
			userSettings.getFavouriteLayouts().add(new FavouriteLayout(FavouriteLayoutSource.CUSTOM, "CUSTOM " + userName + " " + fileName));
			this.userDatabaseManager.updateUserSettingsForUser(user, session);
		}
		
	}

	/**
	 * Updates an existing document with the new hash and filename
	 * 
	 * @param contentId
	 * @param fileHash
	 * @param fileName
	 * @param oldDate 
	 * @param userName 
	 * @param md5hash
	 * @param session
	 */
	public void updateDocument(final int contentId, final String fileHash, final String fileName, final Date oldDate, final String userName,
			final String md5hash, final DBSession session) {
		final DocumentParam docParam = new DocumentParam();
		docParam.setFileHash(fileHash);
		docParam.setFileName(fileName);
		docParam.setContentId(contentId);
		docParam.setUserName(userName);
		// we need it for logging
		docParam.setDate(oldDate);
		docParam.setMd5hash(md5hash);
		
		this.onDocumentUpdate(docParam, session);
		this.update("updateDoc", docParam, session);
	}

	/**
	 * retrieves a (layout) document
	 * 
	 * @param docParam
	 * @param session
	 * @return document
	 */
	private Document getDocumentForLayout(final DocumentParam docParam, final DBSession session) {
		return this.queryForObject("getDocumentForLayout", docParam, Document.class, session);
	}

	/**
	 * retrieves a (layout) document
	 * 
	 * @param userName
	 * @param fileHash
	 * @param session
	 * @return document
	 */
	public Document getDocument(final String userName, final String fileHash, DBSession session) {
		// create the docParam object
		final DocumentParam docParam = new DocumentParam();

		// fill the docParam object
		docParam.setFileHash(fileHash);
		docParam.setUserName(userName);
		docParam.setContentId(0);

		// get the requested document
		return this.getDocumentForLayout(docParam, session);
	}
	
	/**
	 * This method gets documents object with the given name and hash.
	 * 
	 * @param docParam
	 * @param session
	 * @return document
	 */
	public List<Document> getLayoutDocuments(final String userName, final DBSession session) {
		// create the docParam object
		final DocumentParam docParam = new DocumentParam();

		// fill the docParam object
		docParam.setUserName(userName);
		docParam.setContentId(0);

		// get the requested documents
		return this.queryForList("getLayoutDocuments", docParam, Document.class, session);
	}

	/**
	 * This method gets documents object with the given name and hash.
	 * 
	 * @param docParam
	 * @param session
	 * @return document
	 */
	private List<Document> getDocumentsForPost(final DocumentParam docParam, final DBSession session) {
		return this.queryForList("getDocumentsForPost", docParam, Document.class, session);
	}

	/**
	 * Returns the named documents for the given user name and hash
	 * 
	 * @param userName
	 * @param resourceHash
	 * @param session
	 * @return a list of documents
	 */
	public List<Document> getDocumentsForPost(final String userName, final String resourceHash, final DBSession session) {
		// create the docParam object
		final DocumentParam docParam = new DocumentParam();

		// fill the docParam object
		docParam.setResourceHash(resourceHash);
		docParam.setUserName(userName);

		// get the requested document
		final List<Document> doc = getDocumentsForPost(docParam, session);
		
		if (doc == null) {
			throw new IllegalStateException("No documents for this publication.");
		}
		
		return doc;
	}


	private void deleteDocumentLayout(final DocumentParam docParam, final DBSession session) {
		this.delete("deleteDocWithNoPost", docParam, session);
	}

	/**
	 * deletes a document which is not connected to a post
	 * 
	 * @param contentId
	 * @param userName
	 * @param fileHash
	 * @param session
	 */
	public void deleteDocumentWithNoPost(final int contentId, final String userName, final String fileHash, final DBSession session) {
		// create a DocumentParam object
		final DocumentParam docParam = new DocumentParam();
		docParam.setFileHash(fileHash);
		docParam.setUserName(userName);
		docParam.setContentId(contentId);
		
		Document document = getDocument(userName, fileHash, session);
		String styleName = document.getFileName();
		
		// finally delete the document
		deleteDocumentLayout(docParam, session);
		
		//updating favourite layouts
		User user = this.userDatabaseManager.getUserDetails(userName, session);
		UserSettings userSettings = user.getSettings();
		FavouriteLayout foundLayout = null;
		for (FavouriteLayout layout : userSettings.getFavouriteLayouts()){
			if (layout.getSource() == FavouriteLayoutSource.CUSTOM){
				String fileNameFromLayout = layout.getStyle().substring(layout.getStyle().lastIndexOf(' ')).trim();
				if (fileNameFromLayout.equalsIgnoreCase(styleName)){
					foundLayout = layout;
					break;
				}
			}
		}
		if(foundLayout != null){
			userSettings.getFavouriteLayouts().remove(foundLayout);
			this.userDatabaseManager.updateUserSettingsForUser(user, session);
		}
		
	}

	private void deleteDocumentForPost(final DocumentParam docParam, final DBSession session) {
		this.onDocumentDelete(docParam, session);
		this.delete("deleteDoc", docParam, session);
	}
	
	/**
	 * This method deletes an existing document
	 * 
	 * @param contentId
	 * @param userName
	 * @param fileName
	 * @param session
	 */
	public void deleteDocument(final int contentId, final Document document, final DBSession session) {
		// create a DocumentParam object
		final DocumentParam docParam = documentToParam(document);
		docParam.setContentId(contentId);
		// finally delete the document
		deleteDocumentForPost(docParam, session);
	}
	
	/**
	 * Creates DocumentParam from Document. Note you must set the contentId manually
	 * @param document
	 * @return corresponding DocumentParam
	 */
	private DocumentParam documentToParam(Document document) {
		final DocumentParam docParam = new DocumentParam();
		docParam.setFileName(document.getFileName());
		docParam.setUserName(document.getUserName());
		docParam.setFileHash(document.getFileHash());
		docParam.setMd5hash(document.getMd5hash());
		docParam.setDate(document.getDate());
		return docParam;
	}
	
	/**
	 * called on a document will be deleted
	 * 
	 * @param param
	 * @param session
	 */
	public void onDocumentDelete(final DocumentParam param,  final DBSession session) {
		this.plugins.onDocumentDelete(param, session);
	}

	/**
	 * called when a document is updated
	 * 
	 * @param param
	 * @param session
	 */
	private void onDocumentUpdate(final DocumentParam param,  final DBSession session) {
		this.plugins.onDocumentUpdate(param, session);
	}
	
	/**
	 * deletes all documents for a post (with logging)
	 * @param contentId
	 * @param session
	 */
	public void deleteAllDocumentsForPost(int contentId, DBSession session) {
		final List<Document> documents = this.queryForList("getDocumentsForDelete", contentId, Document.class, session);
		for (final Document document : documents) {
			final DocumentParam docParam = documentToParam(document);
			docParam.setContentId(contentId);
			this.onDocumentDelete(docParam, session);
		}
		
		this.delete("deleteAllDocumentForContentId", contentId, session);
	}
	
	/**
	 * @param filters 
	 * @param session
	 * @return the number of documents
	 */
	public int getGlobalDocumentCount(final Set<Filter> filters, DBSession session) {
		final StatisticsParam param = new StatisticsParam();
		param.setFilters(filters);
		final Integer result = this.queryForObject("getDocumentCount", param, Integer.class, session);
		return saveConvertToint(result);
	}

	/**
	 * @param filters 
	 * @param session
	 * @return the number of layout files
	 */
	public int getNumberOfLayoutDocuments(Set<Filter> filters, DBSession session) {
		final StatisticsParam param = new StatisticsParam();
		param.setFilters(filters);
		final Integer result = this.queryForObject("getLayoutDocumentCount", param, Integer.class, session);
		return saveConvertToint(result);
	}
	
	/**
	 * @param userDatabaseManager
	 */
	public void setUserDatabaseManager(UserDatabaseManager userDatabaseManager){
		this.userDatabaseManager = userDatabaseManager;
	}
}