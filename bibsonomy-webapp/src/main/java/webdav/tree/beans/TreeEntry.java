package webdav.tree.beans;

public abstract class TreeEntry {
	private final String name;

	TreeEntry(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}