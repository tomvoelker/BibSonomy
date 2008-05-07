package org.bibsonomy.webdav.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.bibsonomy.webdav.BibSonomyBackend;

import com.atlassian.confluence.extra.webdav.servlet.WebdavResponse;
import com.atlassian.confluence.extra.webdav.servlet.resource.BaseResource;
import com.atlassian.confluence.extra.webdav.servlet.resource.CollectionResource;
import com.atlassian.confluence.extra.webdav.servlet.resource.ErrorReport;
import com.atlassian.confluence.extra.webdav.servlet.resource.SingleResource;

/**
 * Abstract base class for single-resource BibSonomy items.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class BibSonomyResource extends BaseResource implements SingleResource {

	private final String name;
	private final String content;
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
	 * @param content
	 *            The content of this resource
	 */
	public BibSonomyResource(final CollectionResource parent, final BibSonomyBackend backend, final String name, final String content) {
		super(parent, backend);
		this.name = name;
		this.content = content;
		this.date = new Date();
	}

	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		return this.name;
	}

	public String getSafeName() {
		return this.getName();
	}

	/**
	 * @return content
	 */
	public String getContent() {
		return this.content;
	}

	public int getContentLength() throws IOException {
		return this.content.length();
	}

	public String getContentType() {
		return null;
	}

	public String getETag() {
		return null;
	}

	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(this.content.getBytes());
	}

	// TODO: implement write access
	public void saveData(InputStream input, int contentLength, String contentType, String encoding) throws IOException {
	}

	// TODO: implement write access
	public boolean copyTo(CollectionResource parent, String childName, boolean overwrite, boolean deepCopy, ErrorReport errs) {
		errs.addError(this, WebdavResponse.SC_FORBIDDEN);
		return false;
	}

	// TODO: implement write access
	public boolean moveTo(CollectionResource parent, String childName, boolean overwrite, ErrorReport errs) {
		errs.addError(this, WebdavResponse.SC_FORBIDDEN);
		return false;
	}

	// TODO: implement write access
	public boolean delete(ErrorReport errs) {
		errs.addError(this, WebdavResponse.SC_FORBIDDEN);
		return false;
	}

	public String getContentLanguage() {
		return null;
	}

	public Date getCreationDate() {
		return this.date;
	}

	public Date getLastModified() {
		return this.date;
	}

	public boolean isVirtual() {
		return false;
	}

	@Override
	public BibSonomyBackend getBackend() {
		return (BibSonomyBackend) super.getBackend();
	}
}