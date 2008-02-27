package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.BibTexExtraParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.model.extra.ExtendedFields;
import org.bibsonomy.model.logic.LogicInterface;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexExtraDatabaseManager extends AbstractDatabaseManager {

	private final static BibTexExtraDatabaseManager singleton = new BibTexExtraDatabaseManager();
	private final BibTexDatabaseManager bibtexDb = BibTexDatabaseManager.getInstance();
	/** Denotes whether documents are public, i.e. everybody sees everything */
	private final boolean PUBLIC_DOCUMENTS = false;

	private BibTexExtraDatabaseManager() {
	}

	public static BibTexExtraDatabaseManager getInstance() {
		return singleton;
	}

	public List<BibTexExtra> getURL(final String hash, final String username, final DBSession session) {
		final BibTexExtraParam param = new BibTexExtraParam();
		param.setSimHash(HashID.INTRA_HASH); 
		param.setHash(hash);
		param.setUserName(username);
		return this.queryForList("getBibTexExtraURL", param, BibTexExtra.class, session);
	}

	public void createURL(final String hash, final String username, final String url, final String text, final DBSession session) {
		final BibTexExtraParam param = this.buildURLParam(hash, username, url, text, session);
		this.insert("insertBibTexExtraURL", param, session);
	}

	public void deleteURL(final String hash, final String username, final String url, final DBSession session) {
		final BibTexExtraParam param = this.buildURLParam(hash, username, url, null, session);
		this.delete("deleteBibTexExtraURL", param, session);
	}

	/**
	 * Doesn't delete <em>all</em> URLs, but only those for the resource with
	 * the given hash.
	 */
	public void deleteAllURLs(final int contentId, final DBSession session) {
		this.delete("deleteAllBibTexExtraURLs", contentId, session);
	}

	public void updateURL(final int contentId, final int newContentId, final DBSession session) {
		this.update("updateBibTexURL", this.buildContentIdParam(contentId, newContentId), session);
	}

	private BibTexExtraParam buildURLParam(final String hash, final String username, final String url, final String text, final DBSession session) {
		final int contentId = this.bibtexDb.getContentIdForBibTex(hash, username, session);
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

	public String getBibTexPrivnoteForUser(final String hash, final String username, final DBSession session) {
		final BibTexExtraParam param = this.buildPrivnoteParam(hash, username, null);
		return this.queryForObject("getBibTexPrivnoteForUser", param, String.class, session);
	}

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
	 * Returns the filename for the document with the given hash.
	 * @deprecated Documents are contained in BibTeX posts now. Access is now 
	 * possible via the more general method {@link LogicInterface#getDocument(String, String, String)}.
	 * 
	 */
	@Deprecated
	public String getDocumentByHash(final String hash, final DBSession session) {
		return this.getDocumentByHashAndUser(hash, null, session);
	}

	/**
	 * Returns the filename for the document with the given hash and username.
	 * 
	 * @deprecated Documents are contained in BibTeX posts now. Access is now 
	 * possible via the more general method {@link LogicInterface#getDocument(String, String, String)}.
	 * 
	 */
	@Deprecated
	public String getDocumentByHashAndUser(final String hash, final String username, final DBSession session) {
		if (present(hash) == false) throw new RuntimeException("Hash must be present.");
		// if all documents are public -> return them
		if (this.PUBLIC_DOCUMENTS) return this.queryForObject("getDocumentByHash", hash, String.class, session);
		// if documents aren't public we need a username and return the document
		if (present(username) == false) throw new RuntimeException("Username must be present.");
		final BibTexExtraParam param = new BibTexExtraParam();
		param.setHash(hash);
		param.setUserName(username);
		return this.queryForObject("getDocumentByHashAndUser", param, String.class, session);
	}

	public void deleteDocument(final int contentId, final DBSession session) {
		this.delete("deleteDocument", contentId, session);
	}

	public void updateDocument(final int contentId, final int newContentId, final DBSession session) {
		this.delete("updateDocument", this.buildContentIdParam(contentId, newContentId), session);
	}

	public List<ExtendedFields> getExtendedFields(final String hash, final String username, final DBSession session) {
		final BibTexExtraParam param = new BibTexExtraParam();
		param.setHash(hash);
		param.setUserName(username);
		return this.queryForList("getExtendedFields", param, ExtendedFields.class, session);
	}

	public void updateExtendedFieldsData(final int contentId, final int newContentId, final DBSession session) {
		this.update("updateExtendedFieldsData", this.buildContentIdParam(contentId, newContentId), session);
	}

	public void deleteExtendedFieldsData(final int contentId, final DBSession session) {
		this.delete("deleteExtendedFieldsData", contentId, session);
	}

	private BibTexExtraParam buildContentIdParam(final int contentId, final int newContentId) {
		final BibTexExtraParam param = new BibTexExtraParam();
		param.setNewContentId(newContentId);
		param.setRequestedContentId(contentId);
		return param;
	}

	public void deleteCollector(final int contentId, final DBSession session) {
		this.delete("deleteBibTexCollector", contentId, session);
	}
}