package org.bibsonomy.database.managers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.BibTexExtraParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTexExtra;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexExtraDatabaseManager extends AbstractDatabaseManager {

	private final static BibTexExtraDatabaseManager singleton = new BibTexExtraDatabaseManager();
	private final BibTexDatabaseManager bibtexDb = BibTexDatabaseManager.getInstance();

	private BibTexExtraDatabaseManager() {
	}

	public static BibTexExtraDatabaseManager getInstance() {
		return singleton;
	}

	public List<BibTexExtra> getURL(final String hash, final String username, final DBSession session) {
		final BibTexExtraParam param = new BibTexExtraParam();
		param.setHash(hash);
		param.setUserName(username);
		return this.queryForList("getBibTexExtraURL", param, BibTexExtra.class, session);
	}

	public void createURL(final String hash, final String username, final String url, final String text, final DBSession session) {
		final BibTexExtraParam param = this.buildParam(hash, username, url, text, null, session);
		this.insert("insertBibTexExtraURL", param, session);
	}

	public void deleteURL(final String hash, final String username, final String url, final DBSession session) {
		final BibTexExtraParam param = this.buildParam(hash, username, url, null, null, session);
		this.delete("deleteBibTexExtraURL", param, session);
	}

	public String getBibTexPrivnoteForUser(final String hash, final String username, final DBSession session) {
		final BibTexExtraParam param = new BibTexExtraParam();
		param.setHash(hash);
		param.setUserName(username);
		return this.queryForObject("getBibTexPrivnoteForUser", param, String.class, session);
	}

	public void updateBibTexPrivnoteForUser(final String hash, final String username, final String note, final DBSession session) {
		final BibTexExtraParam param = new BibTexExtraParam();
		param.setHash(hash);
		param.setUserName(username);
		param.getResource().setNote(note);
		this.update("updateBibTexPrivnoteForUser", param, session);
	}

	private BibTexExtraParam buildParam(final String hash, final String username, final String url, final String text, final String note, final DBSession session) {
		final int contentId = this.bibtexDb.getContentIdForBibTex(hash, username, session);
		final BibTexExtraParam param = new BibTexExtraParam();
		param.setRequestedContentId(contentId);
		try {
			param.getBibtexExtra().setUrl(new URL(url));
		} catch (final MalformedURLException ex) {
			throw new RuntimeException(ex);
		}
		param.getBibtexExtra().setText(text);
		param.getResource().setNote(note);
		return param;
	}
}