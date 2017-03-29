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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BibTexExtraParam;
import org.bibsonomy.database.params.BibtexExtendedParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.model.extra.ExtendedField;

/** 
 * @author Christian Schenk
 */
public class BibTexExtraDatabaseManager extends AbstractDatabaseManager {

	private final static BibTexExtraDatabaseManager singleton = new BibTexExtraDatabaseManager();
	private final DatabasePluginRegistry plugins;
	
	/**
	 * @return BibTexExtraDatabaseManager
	 */
	public static BibTexExtraDatabaseManager getInstance() {
		return singleton;
	}

	private BibTexExtraDatabaseManager() {
		this.plugins = DatabasePluginRegistry.getInstance();
	}
	
	/**
	 * Returns the URLs for a given publication.
	 * 
	 * @param hash
	 * @param username
	 * @param session
	 * @return list of BibTexExtra objects
	 */
	public List<BibTexExtra> getURL(final String hash, final String username, final DBSession session) {
		final BibTexExtraParam param = new BibTexExtraParam();
		param.setSimHash(HashID.INTRA_HASH); 
		param.setHash(hash);
		param.setUserName(username);
		return this.queryForList("getBibTexExtraURL", param, BibTexExtra.class, session);
	}

	/**
	 * Creates an URL for the publication with the given hash.
	 * 
	 * @param hash
	 * @param username
	 * @param url
	 * @param text
	 * @param session
	 */
	public void createURL(final String hash, final String username, final String url, final String text, final DBSession session) {
		final BibTexExtraParam param = this.buildURLParam(hash, username, url, text, session);
		this.insert("insertBibTexExtraURL", param, session);
	}

	/**
	 * Deletes the URL from the publication with the given hash.
	 * 
	 * @param hash
	 * @param username
	 * @param url
	 * @param session
	 */
	public void deleteURL(final String hash, final String username, final URL url, final DBSession session) {
		final BibTexExtraParam param = this.buildURLParam(hash, username, url, null, session);
		this.plugins.onBibTexExtraDelete(param, session);
		this.delete("deleteBibTexExtraURL", param, session);
	}

	/**
	 * Doesn't delete <em>all</em> URLs, but only those for the resource with
	 * the given hash.
	 * 
	 * @param contentId
	 * @param session
	 */
	public void deleteAllURLs(final int contentId, final DBSession session) {
		this.delete("deleteAllBibTexExtraURLs", contentId, session);
	}

	/**
	 * Migrates the URLs for a given contentId to its new contentId.
	 * 
	 * @param contentId
	 * @param newContentId
	 * @param session
	 */
	public void updateURL(final int contentId, final int newContentId, final DBSession session) {
		this.update("updateBibTexURL", this.buildContentIdParam(contentId, newContentId), session);
	}

	@Deprecated
	private BibTexExtraParam buildURLParam(final String hash, final String username, final String urlString, final String text, final DBSession session) {
		try {
			final URL url = new URL(urlString);
			return this.buildURLParam(hash, username, url, text, session);
		} catch (final MalformedURLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private BibTexExtraParam buildURLParam(final String hash, final String username, final URL url, final String text, final DBSession session) {
		final int contentId = BibTexDatabaseManager.getInstance().getContentIdForPost(hash, username, session);
		final BibTexExtraParam param = new BibTexExtraParam();
		param.setRequestedContentId(contentId);
		param.getBibtexExtra().setUrl(url);
		param.getBibtexExtra().setText(text);
		
		return param;
	}

	/**
	 * Updates the private note for a publication with the given hash.
	 * 
	 * @param hash
	 * @param username
	 * @param note
	 * @param session
	 */
	public void updateBibTexPrivnoteForUser(final String hash, final String username, final String note, final DBSession session) {
		final BibTexExtraParam param = this.buildPrivnoteParam(hash, username, note);
		this.update("updateBibTexPrivnoteForUser", param, session);
	}

	private BibTexExtraParam buildPrivnoteParam(final String hash, final String username, final String note) {
		final BibTexExtraParam param = new BibTexExtraParam();
		param.setHash(hash);
		param.setSimHash(HashID.INTRA_HASH);
		param.setUserName(username);
		
		final BibTex publication = new BibTex();
		publication.setNote(note);
		param.setResource(publication);
		
		return param;
	}

	/**
	 * TODO: move to documents manager
	 * Migrates the document for a given contentId to its new contentId.
	 * 
	 * @param contentId
	 * @param newContentId
	 * @param session
	 */
	public void updateDocument(final int contentId, final int newContentId, final DBSession session) {
		this.delete("updateDocument", this.buildContentIdParam(contentId, newContentId), session);
	}
	
	/**
	 * creates an extended field for the given user
	 * 
	 * @param userName
	 * @param hash
	 * @param key
	 * @param value
	 * @param session
	 */
	public void createExtendedField(final String userName, final String hash, final String key, final String value, final DBSession session) {
		final BibtexExtendedParam param = new BibtexExtendedParam();
		param.setHash(hash);
		param.setUserName(userName);
		
		final ExtendedField exField = new ExtendedField();
		exField.setKey(key);
		exField.setValue(value);
		exField.setCreated(new Date());
		
		param.setExtendedField(exField);
		
		final int contentId = BibTexDatabaseManager.getInstance().getContentIdForPost(hash, userName, session);
		param.setRequestedContentId(contentId);
		
		this.insert("insertExtendedBibtex", param, session);
	}

	/**
	 * Returns the extended fields for a publication with the given hash.
	 * 
	 * @param hash
	 * @param username
	 * @param session
	 * @return list of ExtendedFields objects
	 */
	public Map<String, List<String>> getExtendedFields(final String username, final String hash, final DBSession session) {
		final BibtexExtendedParam param = new BibtexExtendedParam();
		
		param.setHash(hash);
		param.setUserName(username);
		param.setSimHash(HashID.INTRA_HASH);
		
		return this.queryForMap("getExtendedFields", param, "key", "valueList", session);
	}
	
	/**
	 * Returns the extended fields for a publication with the given hash.
	 * @param username
	 * @param hash
	 * @param key 
	 * @param session
	 * 
	 * @return list of ExtendedFields objects
	 */
	public Map<String, List<String>> getExtendedFieldsByKey(final String username, final String hash, final String key, final DBSession session) {
		final BibtexExtendedParam param = new BibtexExtendedParam();
		
		final ExtendedField exField = new ExtendedField();
		exField.setKey(key);
		param.setExtendedField(exField);
		
		param.setHash(hash);
		param.setUserName(username);
		param.setSimHash(HashID.INTRA_HASH);
		
		return this.queryForMap("getExtendedFieldsByKey", param, "key", "valueList", session);
	}

	/**
	 * Migrates the extended fields for a given contentId to its new contentId.
	 * 
	 * @param contentId
	 * @param newContentId
	 * @param session
	 */
	public void updateExtendedFieldsData(final int contentId, final int newContentId, final DBSession session) {
		this.update("updateExtendedFieldsData", this.buildContentIdParam(contentId, newContentId), session);
	}

	/**
	 * Deletes the extended fields.
	 * 
	 * @param contentId
	 * @param session
	 */
	public void deleteAllExtendedFieldsData(final int contentId, final DBSession session) {
		this.delete("deleteAllExtendedFieldsData", contentId, session);
	}
	
	/**
	 * deletes an extended field by key
	 * 
	 * @param userName
	 * @param hash
	 * @param key
	 * @param session
	 */
	public void deleteExtendedFieldsByKey(final String userName, final String hash, final String key, final DBSession session) {
	    final BibtexExtendedParam param = this.buildExtendedParam(userName, hash, key, null);

	    this.delete("deleteExtendedFieldByKey", param, session);
	}

	/**
	 * deletes an extended field by key and value
	 * 
	 * @param userName
	 * @param hash
	 * @param key
	 * @param value
	 * @param session
	 */
	public void deleteExtendedFieldByKeyValue(final String userName, final String hash, final String key, final String value, final DBSession session) {
	    final BibtexExtendedParam param = this.buildExtendedParam(userName, hash, key, value);
	    
	    this.delete("deleteExtendedFieldByKeyValue", param, session);
	}

	private BibtexExtendedParam buildExtendedParam(final String userName, final String hash, final String key, final String value) {
	    final BibtexExtendedParam param = new BibtexExtendedParam();
	    final ExtendedField ex = new ExtendedField();
	    ex.setKey(key);
	    ex.setValue(value);
	    
	    param.setExtendedField(ex);
	    
	    param.setHash(hash);
	    param.setSimHash(HashID.INTRA_HASH);
	    param.setUserName(userName);

	    return param;
	}
	
	private BibTexExtraParam buildContentIdParam(final int contentId, final int newContentId) {
		final BibTexExtraParam param = new BibTexExtraParam();
		param.setNewContentId(newContentId);
		param.setRequestedContentId(contentId);
		return param;
	}
}