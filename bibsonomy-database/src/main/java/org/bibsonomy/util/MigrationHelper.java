package org.bibsonomy.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author dzo
 */
public class MigrationHelper {
	/** the path to the migrations*/
	private static final String MIGRATION_PATH = "database/migrations";
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1 && args.length != 2) {
			System.err.println("please provide a source version and/or target version");
			System.exit(1);
		}
		
		final String sourceVersion;
		final String targetVersion;
		if (args.length == 1) {
			sourceVersion = null;
			targetVersion = args[0];
		} else {
			sourceVersion = args[0];
			targetVersion = args[1];
		}
		
		System.out.println("generating migration file (" + sourceVersion + " -> " + targetVersion + ")");
		final String migration = getMigration(sourceVersion, targetVersion);
		System.out.println(migration);
	}

	/**
	 * @param sourceVersion
	 * @param targetVersion
	 * @return sql migration statements
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static String getMigration(String sourceVersion, String targetVersion) throws URISyntaxException, IOException {
		final StringBuilder builder = new StringBuilder();
		final URL resource = MigrationHelper.class.getClassLoader().getResource(MIGRATION_PATH);
		final File file = new File(resource.toURI());
		final TreeSet<String> versions = new TreeSet<>();
		for (final File subDir : file.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		})) {
			versions.add(subDir.getName());
		}
		
		final SortedSet<String> versionsForMigration = versions.headSet(targetVersion, true).tailSet(sourceVersion);
		
		for (final String version : versionsForMigration) {
			builder.append("-- " + version + "\n");
			
			final File versionFolder = new File(file, version);
			
			final File[] sqlFiles = versionFolder.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".sql");
				}
			});
			
			for (final File sqlFile : sqlFiles) {
				builder.append("-- ").append(sqlFile.getName()).append("\n");
				final InputStream sqlFileStream = MigrationHelper.class.getClassLoader().getResourceAsStream(MIGRATION_PATH + "/" + version + "/" + sqlFile.getName());
				final BufferedReader reader = new BufferedReader(new InputStreamReader(sqlFileStream, StringUtils.CHARSET_UTF_8));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					builder.append("\n");
				}
				reader.close();
				sqlFileStream.close();
			}
		}
		return builder.toString();
	}
}
