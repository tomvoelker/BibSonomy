package org.bibsonomy.database.managers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BibTexExtraParam;
import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.model.extra.ExtendedFields;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexExtraDatabaseManager extends AbstractDatabaseManager {

	private final static BibTexExtraDatabaseManager singleton = new BibTexExtraDatabaseManager();
	
	/**
	 * @return BibTexExtraDatabaseManager
	 */
	public static BibTexExtraDatabaseManager getInstance() {
		return singleton;
	}
	
	
	private final BibTexDatabaseManager bibtexDb;

	private BibTexExtraDatabaseManager() {
		this.bibtexDb = BibTexDatabaseManager.getInstance();
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
	public void deleteURL(final String hash, final String username, final String url, final DBSession session) {
		final BibTexExtraParam param = this.buildURLParam(hash, username, url, null, session);
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

	private BibTexExtraParam buildURLParam(final String hash, final String username, final String url, final String text, final DBSession session) {
		final int contentId = this.bibtexDb.getContentIdForPost(hash, username, session);
		final BibTexExtraParam param = new BibTexExtraParam();
		param.setRequestedContentId(contentId);
		try {
			param.getBibtexExtra().setUrl(new URL(url));
		} catch (final MalformedURLException ex) {
			throw new RuntimeException(ex);
		}
		param.getBibtexExtra().setText(text);
		return param;
	}

	/**
	 * Returns the private note for a publication with the given hash.
	 * 
	 * @param hash
	 * @param username
	 * @param session
	 * @return private note
	 */
	public String getBibTexPrivnoteForUser(final String hash, final String username, final DBSession session) {
		final BibTexExtraParam param = this.buildPrivnoteParam(hash, username, null);
		return this.queryForObject("getBibTexPrivnoteForUser", param, String.class, session);
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
		param.getResource().setNote(note);
		return param;
	}

	/**
	 * Deletes the document.
	 * 
	 * @param contentId
	 * @param session
	 */
	public void deleteDocument(final int contentId, final DBSession session) {
		this.delete("deleteDocument", contentId, session);
	}

	/**
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
	 * Returns the extended fields for a publication with the given hash.
	 * 
	 * @param hash
	 * @param username
	 * @param session
	 * @return list of ExtendedFields objects
	 */
	public List<ExtendedFields> getExtendedFields(final String hash, final String username, final DBSession session) {
		final BibTexExtraParam param = new BibTexExtraParam();
		param.setHash(hash);
		param.setUserName(username);
		return this.queryForList("getExtendedFields", param, ExtendedFields.class, session);
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
	public void deleteExtendedFieldsData(final int contentId, final DBSession session) {
		this.delete("deleteExtendedFieldsData", contentId, session);
	}

	private BibTexExtraParam buildContentIdParam(final int contentId, final int newContentId) {
		final BibTexExtraParam param = new BibTexExtraParam();
		param.setNewContentId(newContentId);
		param.setRequestedContentId(contentId);
		return param;
	}

}