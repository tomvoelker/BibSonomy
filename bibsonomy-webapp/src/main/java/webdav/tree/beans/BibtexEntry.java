package webdav.tree.beans;

public final class BibtexEntry extends TreeEntry {
	private final String content;

	public BibtexEntry(final String name, final String content) {
		super(name);
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}
}