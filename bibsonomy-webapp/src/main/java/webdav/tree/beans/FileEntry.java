package webdav.tree.beans;

import webdav.helper.PathHelper;

public final class FileEntry extends TreeEntry {
	private final String fileName;

	public FileEntry(final String name, final String fileName) {
		//super(PathHelper.getName(fileName));
		super(name);
		this.fileName = fileName;
	}

	public String getFileName() {
		return this.fileName;
	}
}