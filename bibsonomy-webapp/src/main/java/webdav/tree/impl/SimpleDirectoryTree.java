package webdav.tree.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import resources.Bibtex;
import webdav.beans.TagNode;
import webdav.db.DbFacade;
import webdav.helper.FileHelper;
import webdav.helper.PathHelper;
import webdav.tree.AbstractDirectoryTree;
import webdav.tree.beans.FileEntry;
import webdav.tree.beans.TreeEntry;
import webdav.tree.helper.TreeEntryHelper;
import webdav.tree.sort.NodeSorter;
import webdav.tree.sort.impl.SimpleAlphabeticalSorter;

/**
 * This implementation of {@link webdav.tree.DirectoryTree} is simple though complete.
 * 
 * @author Christian Schenk
 */
public class SimpleDirectoryTree extends AbstractDirectoryTree {

	private final DbFacade db;
	private final String prefix = "/files";
	/** Path to the directory where files are saved on the harddisk */
	private final String fileStoreRootPath;
	/** Path to the directory where temporary files are saved on the harddisk */
	private final String tmpStoreRootPath;

	public SimpleDirectoryTree(final String storeRootPath) {
		super(storeRootPath);

		this.db = DbFacade.getInstance();

		this.fileStoreRootPath = PathHelper.buildPath(this.storeRootPath, "files");
		this.tmpStoreRootPath = PathHelper.buildPath(this.storeRootPath, "tmp");
		FileHelper.createDirectory(this.fileStoreRootPath);
		FileHelper.createDirectory(this.tmpStoreRootPath);

		this.update();
	}

	public void update() {
		super.update();

		final List<String> tags = this.db.getAction().getAllTags();

		final NodeSorter sorter = new SimpleAlphabeticalSorter(tags);
		for (final String node : sorter.getNodes()) {
			final List<String> children = sorter.getChildren(node);
			if (this.db.getAction().hasOnlyEmptyChildren(children)) continue;

			// insert parent for the following nodes
			this.tagNodeCollection.addNode(PathHelper.buildPath(this.prefix, node));

			// insert child nodes
			for (final String child : children) {
				// get bibtex-entries from db
				final List<Bibtex> dbBib = this.db.getAction().getBibtex(child);
				final List<TreeEntry> bibtex = TreeEntryHelper.getTreeEntriesFromBibtex(dbBib);
				// get file-entries from db
				final Map<String, String> dbFiles = this.db.getAction().getFilesWithTagName(child);
				final List<TreeEntry> files = TreeEntryHelper.getTreeEntriesFromFiles(dbFiles, this.fileStoreRootPath);

				this.addTagNode(PathHelper.buildPath(node, child), bibtex, files);
			}
		}
	}

	/**
	 * Creates the following tree:
	 * <pre>
	 *  tagName
	 *  '-- bibtex
	 *  '   '-- ...
	 *  '-- files
	 *      '-- ...
	 * </pre>
	 * BibTeX-Files have to be present, uploaded files don't.
	 */
	private void addTagNode(final String tagName, final List<TreeEntry> bibtex, final List<TreeEntry> files) {
		if (bibtex == null || files == null) return;
		if (bibtex.size() == 0) return;
		// tagName
		this.tagNodeCollection.addNode(PathHelper.buildPath(this.prefix, tagName));
		// bibtexEntry
		this.addEntries(tagName, "bibtex", bibtex);
		// files
		this.addEntries(tagName, "files", files);
	}

	/**
	 * Adds a list of {@link webdav.tree.beans.TreeEntry}-element to a given position in the tree.
	 */
	private void addEntries(final String tagName, final String dirName, final List<TreeEntry> entries) {
		final String dir = PathHelper.buildPath(tagName, dirName);
		final String nodeName = PathHelper.buildPath(this.prefix, dir);

		// We're adding this node and declare it as a collection. This way we can have an empty
		// "files"-directory. Otherwise it wouldn't be possible to initially upload files if we couldn't
		// change into that directory.
		this.tagNodeCollection.addNode(nodeName);
		this.tagNodeCollection.getNode(nodeName).setCollection();

		for (final TreeEntry entry : entries) {
			this.tagNodeCollection.addNode(PathHelper.buildPath(this.prefix, dir + "/" + entry.getName()), entry);
		}
	}

	/**
	 * In our case we just want files to be uploaded to directories like
	 * <tt>/files/x/x-tag/files/...</tt>.
	 */
	public boolean isPathWriteable(final String path) {
		if (path.split("/").length == 6) {
			final String parentPath = PathHelper.getParent(path);
			if ("files".equals(parentPath.substring(parentPath.lastIndexOf("/") + 1))) {
				return true;
			}
		}
		return false;
	}

	private String getTagNameFromPath(final String path) {
		if (!this.isPathWriteable(path)) throw new UnsupportedOperationException("Path is invalid +(" + path + ")");
		return path.split("/")[3];
	}

	private String getFileName(final String name, final String hash) {
		return PathHelper.buildPath(this.fileStoreRootPath, hash);
	}

	public void addNode(final String nodePath, final InputStream input) throws IOException {
		final String file_name = PathHelper.getName(nodePath);
		final String tag_name = this.getTagNameFromPath(nodePath);

		// write tmp-file and get file_hash
		final String tmpFileName = PathHelper.buildPath(this.tmpStoreRootPath, file_name);
		FileHelper.writeInputToOutputStream(input, tmpFileName);
		final String file_hash = FileHelper.getMD5HexFromFile(tmpFileName);

		// if the file is not saved in the db with this tag
		if (!this.db.getAction().isFileExistingWithTagName(tag_name, file_hash)) {
			final String newFileName = this.getFileName(file_name, file_hash);
			// add it to the collection
			this.tagNodeCollection.addNode(nodePath, new FileEntry(file_name, newFileName));
			// if the file_hash exists, then just update the database with the new tag
			if (!this.db.getAction().isFileExisting(file_hash)) {
				// if the file_hash doesn't exist copy the temporary file
				FileHelper.copyFile(tmpFileName, newFileName);
			}
			this.db.getAction().insertFile(tag_name, file_name, file_hash);
		}
		// delete the temporary file
		FileHelper.deleteFile(tmpFileName);
	}

	/**
	 * Removes the given node and all nodes that point to the same file.
	 * 
	 * @param str The path of the node which should be deleted
	 */
	public void removeNode(final String str) {
		final TagNode node = this.tagNodeCollection.getNode(str);
		if (node.isCollection()) return;
		if (node.getContent() != null) return;
		if (node.getFileName() == null) return;

		final String file_hash = PathHelper.getName(node.getFileName());

		// delete the file on the harddisk
		FileHelper.deleteFile(PathHelper.buildPath(this.fileStoreRootPath, file_hash));
		// remove all entries with the given file_hash
		this.db.getAction().deleteFile(file_hash);
		// remove the obvious node
		this.tagNodeCollection.removeNode(str);
		// remove all nodes that are pointing to the same file
		final List<String> allNodes = this.tagNodeCollection.getAllNodes();
		for (final String nodePath : allNodes) {
			final TagNode curNode = this.tagNodeCollection.getNode(nodePath);
			if (curNode.getFileName() == null) continue;
			if (file_hash.equals(PathHelper.getName(curNode.getFileName()))) {
				this.tagNodeCollection.removeNode(nodePath);
			}
		}
	}
}