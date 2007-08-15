package webdav.tree.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import resources.Bibtex;
import webdav.helper.PathHelper;
import webdav.tree.beans.BibtexEntry;
import webdav.tree.beans.FileEntry;
import webdav.tree.beans.TreeEntry;

public class TreeEntryHelper {

	/**
	 * Returns a list of {@link webdav.tree.beans.TreeEntry}-elements (more precisely
	 * {@link webdav.tree.beans.BibtexEntry}) named after the respective Bibtex-elements. The content
	 * of the entries is the BibTeX-representation of the Bibtex-elements.<br>
	 * To avoid duplicate files (i.e. duplicate BibTeX-keys) we put an increasing number in front of
	 * every filename. This way we can easily find files because we just have to remember the number.
	 * 
	 * @param entries A list of {@link resources.Bibtex}-elements
	 * @return A list of {@link webdav.tree.beans.BibtexEntry}-elements
	 */
	public static List<TreeEntry> getTreeEntriesFromBibtex(final List<Bibtex> entries) {
		if (entries == null) return Collections.emptyList();
		if (entries.size() == 0) return Collections.emptyList();

		final List<TreeEntry> rVal = new ArrayList<TreeEntry>();
		int i = 0;
		for (final Bibtex entry : entries) {
			rVal.add(new BibtexEntry(i++ + "-" + entry.getBibtexKey(), entry.getBibtex()));
		}

		return rVal;
	}

	/**
	 * This method works like {@link webdav.tree.helper.TreeEntryHelper#getTreeEntriesFromBibtex},
	 * but returns {@link webdav.tree.beans.FileEntry}-elements instead of
	 * {@link webdav.tree.beans.BibtexEntry}-elements.
	 * 
	 * @see webdav.tree.helper.TreeEntryHelper#getTreeEntriesFromBibtex(List)
	 * @param entries A {@link java.util.Map} with (file_name, file_hash)-pairs
	 * @param filespath Path where the files can be found
	 * @return A list of {@link webdav.tree.beans.FileEntry}-elements
	 */
	public static List<TreeEntry> getTreeEntriesFromFiles(final Map<String, String> entries, final String filespath) {
		if (entries == null || filespath == null) return Collections.emptyList();
		if (entries.size() == 0 || "".equals(filespath)) return Collections.emptyList();

		final List<TreeEntry> rVal = new ArrayList<TreeEntry>();
		for (final String key : entries.keySet()) {
			rVal.add(new FileEntry(key, PathHelper.buildPath(filespath, entries.get(key))));
		}
		return rVal;
	}
}