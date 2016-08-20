package org.bibsonomy.layout.jabref;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 *
 * @author jp
 */
public class JabRefFilesManager {
	final String directory = "org/bibsonomy/layout/jabref/";
	final String jabRefFolderDirec = this.getClass().getClassLoader().getResource(directory).getPath();
	final File jabRefFolder = new File(jabRefFolderDirec);
	final String XMLFileDirec = this.getClass().getClassLoader().getResource(directory + "JabrefLayouts.xml").getPath();
	final File XMLFile = new File(XMLFileDirec);
	
	/**
	 * Spring init
	 */
	public void init() {
		//TODO!
	}
	
	/**
	 * @param JabrefID
	 * @return returns a display name to a given Jabref style. Throws something if it didn't work
	 * @throws IOException
	 */
	public String nameToTitle(final String JabrefID) throws IOException {
		String title = null;
		if (XMLFile == null || !XMLFile.exists()) {
			return "JabRef indexing XML File has been moved: " + XMLFile;
		}
		List<String> lines = Files.readAllLines(XMLFile.toPath());
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line);
		}
		String xml = sb.toString();
		Pattern name_pattern = Pattern.compile("<layout name=\\\"" + JabrefID + "\\\">(.|\\s)*?<.layout>");
		Pattern displayName_pattern = Pattern.compile("<displayName>(.|\\s)*?<.displayName>");
		Matcher nameMatcher = name_pattern.matcher(xml);
		while (nameMatcher.find()) {
			String s = nameMatcher.group(0);
			Matcher displayNameMatcher = displayName_pattern.matcher(s);
			while (displayNameMatcher.find()) {
				title = displayNameMatcher.group(0);
				if (title == null || title.length() < 14) {
					title = JabrefID;
				} else {
					title = title.substring(title.indexOf('>') + 1, title.lastIndexOf('<'));
				}
			}
		}
		return title;
	}
}
