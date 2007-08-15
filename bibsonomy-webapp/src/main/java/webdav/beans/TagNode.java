package webdav.beans;

import java.io.File;

import webdav.helper.IdiomHelper;
import webdav.helper.PathHelper;
import webdav.tree.beans.BibtexEntry;
import webdav.tree.beans.FileEntry;
import webdav.tree.beans.TreeEntry;

/**
 * This class contains information about nodes in the directory tree. It has a
 * name and either is a collection or file. In the latter case it stores the
 * content as a string or as a path to a file.
 * 
 * @author Christian Schenk
 */
/*
 * Thoughts on "content" and "fileName": These two variables could be combined in one, if we don't
 * want to save content and a path to a file at the same time in one node, which isn't very likely
 * at all. The corresponding methods should be mutually exclusive then, i.e. they shouldn't both
 * return valid values (one should return content and the other null and vice versa). Because I
 * can't foresee future implementations (doh!) I haven't refactored this bean yet.
 */
public final class TagNode {
	/** The name of this TagNode */
	private final String name;
	/** Is this TagNode a collection (directory) */
	private boolean collection;
	/** Has this TagNode content */
	private boolean hasContent;
	/** Content as a string (in case of BibTeX) */
	private String content;
	/** The path to a file where the content can be found */
	private String fileName;
	/** The lenght of this content in bytes */
	private long contentLength;

	/**
	 * The constructor sets up all variables and especially the name of this
	 * TagNode, which is immutable and can't be changed afterwards.
	 * 
	 * @param path The path represented by this TagNode.
	 */
	public TagNode(final String path) {
		this.name = PathHelper.getName(path);
		this.collection = false;
		this.content = null;
		this.fileName = null;
		this.contentLength = 0;
		this.hasContent = false;
	}

	/**
	 * Gets the name of this TagNode.
	 * 
	 * @return String the name as a string.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns true whether this node is a collection (directory).
	 * 
	 * @return true if it is a collection, false otherwise.
	 */
	public boolean isCollection() {
		return this.collection;
	}

	/**
	 * Sets this node to a collection.
	 */
	public void setCollection() {
		this.collection = true;
	}

	/**
	 * We could have used the inverse value of <code>isCollection</code>, but
	 * we want to treat special nodes (like ActionNode) different, so that we can
	 * show a hint in a case where a special node is a collection but doesn't have
	 * any files with content (e.g. like "/actions").
	 * 
	 * @return true if this node has got content, false otherwise
	 */
	public boolean hasContent() {
		return this.hasContent;
	}

	/**
	 * Sets the content of this node.
	 * 
	 * @param entry Can either be an instance of BibtexEntry or FileEntry.
	 * @throws An UnsupportedOperationException if the instance of
	 *           <code>entry</code> is not supported.
	 */
	public void setContent(final TreeEntry entry) {
		this.hasContent = true;

		if (entry instanceof BibtexEntry) {
			this.content = ((BibtexEntry) entry).getContent();
			this.contentLength = this.content.getBytes().length;
		} else if (entry instanceof FileEntry) {
			this.fileName = ((FileEntry) entry).getFileName();
			this.contentLength = (new File(this.fileName)).length();
		} else {
			throw new UnsupportedOperationException("Class " + entry.getClass().getSimpleName() + " not supported");
		}
	}

	public String getContent() {
		return this.content;
	}

	public String getFileName() {
		return IdiomHelper.getTernaryExp(this.fileName != null, this.fileName, null);
	}

	public long getContentLength() {
		return this.contentLength;
	}

	@Override
	public String toString() {
		return "Node:'" + this.name + "'(" + (this.isCollection() ? "dir" : "file") + ")";
	}
}