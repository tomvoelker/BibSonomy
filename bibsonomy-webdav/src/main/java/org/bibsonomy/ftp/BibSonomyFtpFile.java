package org.bibsonomy.ftp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ftpserver.ftplet.FtpFile;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class BibSonomyFtpFile implements FtpFile {

	private final String name;
	private List<FtpFile> children;
	private String content;

	/**
	 * @param name
	 *            the filename
	 */
	public BibSonomyFtpFile(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String getAbsolutePath() {
		if (this.name.startsWith("/")) return this.name;
		return "/" + this.name;
	}

	public String getOwnerName() {
		return "nobody";
	}

	public String getGroupName() {
		return "nogroup";
	}

	public boolean doesExist() {
		return true;
	}

	public long getLastModified() {
		// FIXME maybe that's not a good idea...
		return (new Date()).getTime();
	}

	public boolean setLastModified(long lm) {
		return false;
	}

	public int getLinkCount() {
		return 0;
	}

	public long getSize() {
		if (this.content == null) return 0;
		return this.content.length();
	}

	public boolean isDirectory() {
		if (name.equals("/") || name.equals("./")) return true;
		if (this.children != null && this.children.size() > 0) return true;
		return false;
	}

	public boolean isFile() {
		return !isDirectory();
	}

	public boolean isHidden() {
		return false;
	}

	public boolean isReadable() {
		return true;
	}

	public boolean isRemovable() {
		return false;
	}

	public boolean isWritable() {
		return false;
	}

	public List<FtpFile> listFiles() {
		return this.children;
	}

	public boolean mkdir() {
		return false;
	}

	public boolean delete() {
		return false;
	}

	public boolean move(FtpFile arg0) {
		return false;
	}

	/**
	 * @param child
	 */
	public void addChild(final FtpFile child) {
		if (this.children == null) this.children = new ArrayList<FtpFile>();
		this.children.add(child);
	}

	/**
	 * @param content
	 */
	public void setContent(final String content) {
		this.content = content;
	}

	public InputStream createInputStream(final long offset) throws IOException {
		if (!this.isReadable()) throw new IOException("No read permission.");

		// TODO: don't ignore offset parameter
		// final StringReader reader = new StringReader(this.content);
		// reader.skip(offset);

		// FIXME: possible encoding problem...
		return new ByteArrayInputStream(this.content.getBytes());
	}

	public OutputStream createOutputStream(final long offset) throws IOException {
		throw new IOException("No write permission.");
	}
}