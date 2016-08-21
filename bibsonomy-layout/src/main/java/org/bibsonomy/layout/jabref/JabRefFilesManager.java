package org.bibsonomy.layout.jabref;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 *
 * @author jp
 */
public class JabRefFilesManager {
	final private String directory = "org/bibsonomy/layout/jabref/";
	final private String jabRefFolderDirec = this.getClass().getClassLoader().getResource(directory).getPath();
	final private File jabRefFolder = new File(jabRefFolderDirec);
	final private String XMLFileDirec = this.getClass().getClassLoader().getResource(directory + "JabrefLayouts.xml")
			.getPath();
	final private File XMLFile = new File(XMLFileDirec);

	private HashMap<String, String> JabRefStyles = new HashMap<String, String>();

	/**
	 * Spring init
	 * 
	 * @throws IOException
	 */
	public void init() throws IOException {
		String title = null;
		if (XMLFile == null || !XMLFile.exists()) {
			throw new FileNotFoundException();
		}
		List<String> lines = Files.readAllLines(XMLFile.toPath());
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line);
		}
		String xml = sb.toString();
		Pattern name_pattern = Pattern.compile("<layout name=\\\"(.|\\s)*?\\\">");
		Pattern complete_pattern = Pattern.compile("<layout name=\\\"(.|\\s)*?\\\">(.|\\s)*?<.layout>");
		Pattern displayName_pattern = Pattern.compile("<displayName>(.|\\s)*?<.displayName>");
		Matcher nameMatcher = name_pattern.matcher(xml);
		Matcher completeMatcher = complete_pattern.matcher(xml);
		while (nameMatcher.find()) {
			completeMatcher.find();
			String complete = completeMatcher.group(0);
			String s = nameMatcher.group(0);

			String jabRefID = s.substring(s.indexOf('"') + 1, s.lastIndexOf('"'));
			Matcher displayNameMatcher = displayName_pattern.matcher(complete);
			while (displayNameMatcher.find()) {
				title = displayNameMatcher.group(0);
				if (title == null || title.length() < 14) {
					title = jabRefID;
				} else {
					title = title.substring(title.indexOf('>') + 1, title.lastIndexOf('<'));
				}
				if (jabRefID.contains("\"") || jabRefID.contains("public") || jabRefID.contains("displayName")
						|| title.contains("\"") || title.contains("public") || title.contains("displayName"))
					continue;
				JabRefStyles.put(jabRefID.trim(), title.trim());
			}
		}
		System.out.println("finished");
		System.out.println("For real tho");
	}

	/**
	 * @param JabRefID
	 * @return returns a display name to a given JabRef style. Throws something
	 *         if it didn't work
	 * @throws IOException
	 */
	public String nameToTitle(final String JabRefID) throws IOException {
		if(!JabRefStyles.containsKey(JabRefID)){
			return JabRefID;
		}
		return JabRefStyles.get(JabRefID);
	}
}
