package webdav.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import resources.Bibtex;
import webdav.helper.BibtexHelper;

/**
 * This class contains a list of well defined interactions with the database.
 * 
 * @author Christian Schenk
 */
public class DbActions {
	/** A reference to the {@link webdav.db.DbFacade} */
	private final DbFacade db;

	/** Get all distinct tags of BibTeX-entries */
	private final String BIBTEX_GET_ALL_TAGS = "SELECT DISTINCT tag_name FROM tas WHERE content_type=2";

	private final String FILES_GET_FILES_WITH_TAG_NAME = "SELECT file_name, file_hash FROM files WHERE tag_name=?";
	private final String FILES_INSERT = "INSERT INTO files (tag_name, file_name, file_hash) VALUES (?,?,?)";
	private final String FILES_DELETE = "DELETE FROM files WHERE file_hash=?";
	private final String FILES_FILE_EXISTING = "SELECT count(*) FROM files WHERE file_hash=?";
	private final String FILES_FILE_EXISTING_WITH_TAG_NAME = "SELECT count(*) FROM files WHERE tag_name=? AND file_hash=?";

	DbActions(final DbFacade db) {
		this.db = db;
	}


	// ==== Common Methods ================================

	public List<String> getAllTags() {
		final List<String> rVal = new ArrayList<String>();
		final List<Object[]> objs = this.db.queryArray(this.BIBTEX_GET_ALL_TAGS, null);
		for (final Object[] obj : objs) {
			rVal.add((String) obj[0]);
		}
		return rVal;
	}


	// ==== BibTeX Methods ================================

	public List<Bibtex> getBibtex(final String tagname) {
		final List<Object[]> objs = this.db.queryArray(BibtexHelper.getBibtexWithTagName(), new String[] { tagname });
		if (objs.size() == 0) return Collections.emptyList();

		final List<Bibtex> rVal = new ArrayList<Bibtex>();
		for (final Object[] obj : objs) {
			rVal.add(BibtexHelper.getBibtex(obj));
		}
		return rVal;
	}

	public boolean hasOnlyEmptyChildren(final List<String> children) {
		for (final String child : children) {
			if (this.getBibtex(child).size() > 0) return false;
		}
		return true;
	}


	// ==== Files Methods ================================

	public Map<String, String> getFilesWithTagName(final String tagname) {
		final List<Object[]> objs = this.db.queryArray(this.FILES_GET_FILES_WITH_TAG_NAME, new String[] { tagname });
		if (objs.size() == 0) return Collections.emptyMap();

		final Map<String, String> rVal = new HashMap<String, String>();
		for (final Object[] obj : objs) {
			rVal.put((String) obj[0], (String) obj[1]);
		}
		return rVal;
	}

	public boolean isFileExisting(final String file_hash) {
		return this.db.queryBoolean(this.FILES_FILE_EXISTING, new String[] { file_hash });
	}

	public boolean isFileExistingWithTagName(final String tag_name, final String file_hash) {
		return this.db.queryBoolean(this.FILES_FILE_EXISTING_WITH_TAG_NAME, new String[] { tag_name, file_hash });
	}

	public void insertFile(final String tag_name, final String file_name, final String file_hash) {
		this.db.update(this.FILES_INSERT, new String[] { tag_name, file_name, file_hash });
	}

	public void deleteFile(final String file_hash) {
		this.db.update(this.FILES_DELETE, new String[] { file_hash });
	}
}