package webdav.tree.beans;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class TreeEntryTest extends TestCase {

	public static List<BibtexEntry> getBibtexEntries() {
		final List<BibtexEntry> rVal = new ArrayList<BibtexEntry>();
		for (int i = 1; i <= 4; i++) {
			rVal.add(new BibtexEntry("name-" + i, "content-" + i));
		}
		return rVal;
	}

	public static List<FileEntry> getFileEntries() {
		final List<FileEntry> rVal = new ArrayList<FileEntry>();
		for (int i = 1; i <= 4; i++) {
			rVal.add(new FileEntry("name-" + 1, "/path/content-" + i));
		}
		return rVal;
	}

	public void testBibtexEntry() {
		final List<BibtexEntry> entries = TreeEntryTest.getBibtexEntries();
		for (final BibtexEntry entry : entries) {
			if (!entry.getName().startsWith("name") || !entry.getContent().startsWith("content")) {
				fail("Unknown entry: " + entry.getName());
			}
		}
	}

	public void testFileEntry() {
		final List<FileEntry> entries = TreeEntryTest.getFileEntries();
		for (final FileEntry entry : entries) {
			if (!entry.getName().startsWith("name") || !entry.getFileName().startsWith("/path/content")) {
				fail("Unknown entry: " + entry.getName());
			}
		}
	}
}