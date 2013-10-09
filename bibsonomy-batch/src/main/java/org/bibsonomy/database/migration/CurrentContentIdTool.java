package org.bibsonomy.database.migration;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Class that reads a cvs file (format: content_id, new_content_id)
 * and calculates the newest content_id of an entry
 * 
 * @author dzo
 */
public class CurrentContentIdTool {

	/**
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		final String fileName = args[0];
		final CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(new File(fileName))));
		final CSVWriter writer = new CSVWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(new File(args[1])))));
		String[] line = null;
		final BiMap<String, String> contentIdMap = HashBiMap.create();
		while ((line = reader.readNext()) != null) {
			final String contentId = line[0];
			final String newContentId = line[1];
			if (present(newContentId) && newContentId.trim().equals("0")) {
				continue;
			}
			contentIdMap.put(contentId, newContentId);
		}
		
		for (final Entry<String, String> contentIdEntry : contentIdMap.entrySet()) {
			final String contentId = contentIdEntry.getKey();
			final String newestContentId = getNewestContentIdForContentId(contentIdMap, contentId);
			final String[] data = new String[] { contentId, newestContentId };
			writer.writeNext(data);
		}
		
		writer.close();
		reader.close();
	}

	private static String getNewestContentIdForContentId(BiMap<String, String> contentIdMap, String contentId) {
		final String newContentId = contentIdMap.get(contentId);
		if (contentIdMap.containsValue(newContentId)) {
			final String newestContentId = getNewestContentIdForContentId(contentIdMap, newContentId);
			if (newestContentId != null) {
				return newestContentId;
			}
		}
		return newContentId;
	}
}
