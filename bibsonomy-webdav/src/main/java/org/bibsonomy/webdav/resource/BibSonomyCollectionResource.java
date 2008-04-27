package org.bibsonomy.webdav.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.bibsonomy.webdav.BibSonomyBackend;

import com.atlassian.confluence.extra.webdav.servlet.WebdavResponse;
import com.atlassian.confluence.extra.webdav.servlet.resource.BaseCollectionResource;
import com.atlassian.confluence.extra.webdav.servlet.resource.CollectionResource;
import com.atlassian.confluence.extra.webdav.servlet.resource.ErrorReport;
import com.atlassian.confluence.extra.webdav.servlet.resource.ForbiddenException;

/**
 * An abstract base class for resources which are a collection of other
 * resources.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public abstract class BibSonomyCollectionResource extends BaseCollectionResource {

	private final String name;
	private final String displayName;
	private final Date date;

	/**
	 * Constructs the new resource.
	 * 
	 * @param parent
	 *            The resource's parent. May be <code>null</code>.
	 * @param backend
	 *            The backend
	 * @param name
	 *            The name of this resource
	 * @param displayName
	 *            The displayName of this resource
	 */
	public BibSonomyCollectionResource(final CollectionResource parent, final BibSonomyBackend backend, final String name, final String displayName) {
		super(parent, backend);
		this.name = name;
		this.displayName = displayName;
		this.date = new Date();
	}

	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public String getSafeName() {
		return getName();
	}

	public Date getCreationDate() {
		return this.date;
	}

	public Date getLastModified() {
		return this.date;
	}

	public String getContentLanguage() {
		return null;
	}

	public boolean isVirtual() {
		return false;
	}

	public boolean delete(ErrorReport errs) {
		errs.addError(this, WebdavResponse.SC_FORBIDDEN);
		return false;
	}

	public boolean copyTo(CollectionResource parent, String childName, boolean overwrite, boolean deepCopy, ErrorReport errs) {
		errs.addError(this, WebdavResponse.SC_FORBIDDEN);
		return false;
	}

	public boolean moveTo(CollectionResource parent, String childName, boolean overwrite, ErrorReport errs) {
		errs.addError(this, WebdavResponse.SC_FORBIDDEN);
		return false;
	}

	// TODO: implement write access
	public boolean saveChildData(String childName, InputStream input, int contentLength, String contentType, String encoding) throws IOException {
		throw new ForbiddenException();
	}

	// TODO: implement write access
	public void createChildCollection(String childName) {
		throw new ForbiddenException();
	}

	@Override
	public BibSonomyBackend getBackend() {
		return (BibSonomyBackend) super.getBackend();
	}
}