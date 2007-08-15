package webdav.tree.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import resources.Bibtex;
import webdav.tree.beans.FileEntry;
import webdav.tree.beans.TreeEntry;

public class TreeEntryHelperTest extends TestCase {

	public void testGetTreeEntriesFromBibtex() {
		List<TreeEntry> entries = TreeEntryHelper.getTreeEntriesFromBibtex(this.buildBibtexList());
		assertEquals("0-key-1", entries.get(0).getName());
		assertEquals("1-key-2", entries.get(1).getName());

		entries = TreeEntryHelper.getTreeEntriesFromBibtex(null);
		assertEquals(0, entries.size());
		entries = TreeEntryHelper.getTreeEntriesFromBibtex(new ArrayList<Bibtex>());
		assertEquals(0, entries.size());
	}

	private List<Bibtex> buildBibtexList() {
		final List<Bibtex> rVal = new ArrayList<Bibtex>();
		rVal.add(this.getSimpleBibtex(1));
		rVal.add(this.getSimpleBibtex(2));
		return rVal;
	}

	private Bibtex getSimpleBibtex(final int nr) {
		final Bibtex rVal = new Bibtex();
		rVal.setBibtexKey("key-" + nr);
		rVal.setKey("key-" + nr);
		return rVal;
	}

	public void testGetTreeEntriesFromFiles() {
		final String rootpath = "/root/path/";
		List<TreeEntry> entries = TreeEntryHelper.getTreeEntriesFromFiles(this.buildFilesMap(), rootpath);
		assertEquals("test-1.txt", entries.get(1).getName());
		assertEquals(rootpath+"123", ((FileEntry) entries.get(1)).getFileName());
		assertEquals("test-2.txt", entries.get(0).getName());
		assertEquals(rootpath+"321", ((FileEntry) entries.get(0)).getFileName());

		entries = TreeEntryHelper.getTreeEntriesFromFiles(null, null);
		assertEquals(0, entries.size());
		entries = TreeEntryHelper.getTreeEntriesFromFiles(null, "");
		assertEquals(0, entries.size());
		entries = TreeEntryHelper.getTreeEntriesFromFiles(new HashMap<String, String>(), null);
		assertEquals(0, entries.size());
		entries = TreeEntryHelper.getTreeEntriesFromFiles(new HashMap<String, String>(), "");
		assertEquals(0, entries.size());
	}

	private Map<String, String> buildFilesMap() {
		final Map<String, String> rVal = new HashMap<String, String>();
		rVal.put("test-1.txt", "123");
		rVal.put("test-2.txt", "321");
		return rVal;
	}
}