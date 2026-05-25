/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Wuerzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universitaet zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper.zotero;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.bibsonomy.junit.RemoteTest;
import org.bibsonomy.scraper.ScrapingContext;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Reuses historic scraper test URLs/selections against Zotero.
 *
 * @author tvolker
 */
@Category(RemoteTest.class)
@RunWith(Parameterized.class)
public class ZoteroHistoricScraperRemoteTest {

	private static final String PROPERTY_SOURCE_ROOT = "bibsonomy.scraper.zotero.historic.sourceRoot";
	private static final String PROPERTY_INCLUDE = "bibsonomy.scraper.zotero.historic.include";
	private static final String PROPERTY_LIMIT = "bibsonomy.scraper.zotero.historic.limit";
	private static final String DEFAULT_SOURCE_ROOT = "src/test/java";
	private static final Pattern STRING_ASSIGNMENT_PATTERN = Pattern.compile("(?:final\\s+)?String\\s+(\\w+)\\s*=\\s*([^;]+);");
	private static final Pattern ASSERT_SCRAPER_RESULT_PATTERN = Pattern.compile("assertScraperResult\\s*\\((.*?)\\)\\s*;", Pattern.DOTALL);
	private static final Pattern METHOD_NAME_PATTERN = Pattern.compile("\\bvoid\\s+(\\w+)\\s*\\(");

	private static ZoteroTranslationServerScraper scraper;

	private final HistoricScraperCase testCase;

	/**
	 * @param testCase historic scraper test case
	 */
	public ZoteroHistoricScraperRemoteTest(final HistoricScraperCase testCase) {
		this.testCase = testCase;
	}

	/**
	 * @return discovered historic scraper cases
	 * @throws IOException
	 */
	@Parameters(name = "{0}")
	public static Collection<Object[]> data() throws IOException {
		final List<HistoricScraperCase> cases = discoverCases(Paths.get(System.getProperty(PROPERTY_SOURCE_ROOT, DEFAULT_SOURCE_ROOT)));
		final String include = System.getProperty(PROPERTY_INCLUDE);
		final int limit = integerProperty(PROPERTY_LIMIT, 0);

		final List<Object[]> parameters = new ArrayList<Object[]>();
		for (final HistoricScraperCase testCase : cases) {
			if (matches(include, testCase)) {
				parameters.add(new Object[] { testCase });
				if (limit > 0 && parameters.size() >= limit) {
					break;
				}
			}
		}
		return parameters;
	}

	/**
	 *
	 */
	@BeforeClass
	public static void setUpClass() {
		final ZoteroTranslationServerConfig config = ZoteroTranslationServerConfig.fromEnvironment();
		Assume.assumeTrue("Set " + ZoteroTranslationServerConfig.ENV_URL + " to run Zotero historic remote scraper tests.", config.isZoteroEnabled());
		scraper = new ZoteroTranslationServerScraper(config);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testHistoricScraperCase() throws Exception {
		final ScrapingContext context = this.createContext();

		assertTrue(this.testCase + ": Zotero did not scrape a result", scraper.scrape(context));
		ZoteroSemanticBibTeXAssert.assertSemanticMatch(this.testCase.getResultFile(), context.getBibtexResult(), this.testCase.toString());
	}

	private ScrapingContext createContext() throws Exception {
		final URL url = present(this.testCase.getUrl()) ? new URL(this.testCase.getUrl()) : null;
		final ScrapingContext context = new ScrapingContext(url);
		if (present(this.testCase.getSelection())) {
			context.setSelectedText(this.testCase.getSelection());
		}
		return context;
	}

	private static List<HistoricScraperCase> discoverCases(final Path sourceRoot) throws IOException {
		if (!Files.isDirectory(sourceRoot)) {
			return Collections.emptyList();
		}

		final List<HistoricScraperCase> cases = new ArrayList<HistoricScraperCase>();
		try (final Stream<Path> paths = Files.walk(sourceRoot)) {
			paths.filter(path -> path.getFileName().toString().endsWith("Test.java"))
							.sorted()
							.forEach(path -> cases.addAll(discoverCases(sourceRoot, path)));
		}
		Collections.sort(cases, new Comparator<HistoricScraperCase>() {
			@Override
			public int compare(final HistoricScraperCase left, final HistoricScraperCase right) {
				return left.toString().compareTo(right.toString());
			}
		});
		return cases;
	}

	private static List<HistoricScraperCase> discoverCases(final Path sourceRoot, final Path sourceFile) {
		if ("RemoteTestAssert.java".equals(sourceFile.getFileName().toString())) {
			return Collections.emptyList();
		}

		final String source;
		try {
			source = new String(Files.readAllBytes(sourceFile), Charset.forName("UTF-8"));
		} catch (final IOException ex) {
			throw new RuntimeException("Could not read " + sourceFile, ex);
		}

		final int firstTest = source.indexOf("@Test");
		if (firstTest < 0) {
			return Collections.emptyList();
		}

		final Map<String, String> classVariables = extractStringAssignments(source.substring(0, firstTest), Collections.<String, String> emptyMap());
		final List<HistoricScraperCase> cases = new ArrayList<HistoricScraperCase>();
		int index = firstTest;
		while (index >= 0 && index < source.length()) {
			final int brace = source.indexOf('{', index);
			if (brace < 0) {
				break;
			}
			final int end = findMatchingBrace(source, brace);
			if (end < 0) {
				break;
			}

			final String methodHeader = source.substring(index, brace);
			final String methodBody = source.substring(brace + 1, end);
			if (!methodHeader.contains("@Ignore")) {
				final String methodName = methodName(methodHeader);
				final Map<String, String> methodVariables = extractStringAssignments(methodBody, classVariables);
				final Matcher callMatcher = ASSERT_SCRAPER_RESULT_PATTERN.matcher(methodBody);
				while (callMatcher.find()) {
					final HistoricScraperCase testCase = createCase(sourceRoot, sourceFile, methodName, methodVariables, callMatcher.group(1));
					if (testCase != null) {
						cases.add(testCase);
					}
				}
			}
			index = source.indexOf("@Test", end);
		}
		return cases;
	}

	private static HistoricScraperCase createCase(final Path sourceRoot, final Path sourceFile, final String methodName, final Map<String, String> variables, final String arguments) {
		final List<String> args = splitArguments(arguments);
		if (args.size() < 3) {
			return null;
		}

		final String url = resolve(args.get(0), variables);
		final String selection;
		final String scraperClass;
		final String resultFile;
		if (args.size() == 3) {
			selection = null;
			scraperClass = args.get(1).trim();
			resultFile = resolve(args.get(2), variables);
		} else {
			selection = resolve(args.get(1), variables);
			scraperClass = args.get(2).trim();
			resultFile = resolve(args.get(3), variables);
		}

		if ((!present(url) && !present(selection)) || !present(resultFile)) {
			return null;
		}
		return new HistoricScraperCase(caseId(sourceRoot, sourceFile, methodName), shortClassName(scraperClass), url, selection, resultFile);
	}

	private static String caseId(final Path sourceRoot, final Path sourceFile, final String methodName) {
		return sourceRoot.relativize(sourceFile).toString().replace('\\', '.').replace('/', '.').replaceAll("\\.java$", "") + "#" + methodName;
	}

	private static String shortClassName(final String scraperClass) {
		return scraperClass.replace(".class", "").trim();
	}

	private static String methodName(final String methodHeader) {
		final Matcher matcher = METHOD_NAME_PATTERN.matcher(methodHeader);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "unknown";
	}

	private static Map<String, String> extractStringAssignments(final String source, final Map<String, String> baseVariables) {
		final Map<String, String> variables = new HashMap<String, String>(baseVariables);
		final Matcher matcher = STRING_ASSIGNMENT_PATTERN.matcher(source);
		while (matcher.find()) {
			final String value = resolve(matcher.group(2), variables);
			if (value != null) {
				variables.put(matcher.group(1), value);
			}
		}
		return variables;
	}

	private static String resolve(final String expression, final Map<String, String> variables) {
		final String trimmed = expression.trim();
		if ("null".equals(trimmed)) {
			return null;
		}

		final List<String> parts = splitConcat(trimmed);
		final StringBuilder value = new StringBuilder();
		for (final String part : parts) {
			final String term = part.trim();
			if (term.startsWith("\"") && term.endsWith("\"")) {
				value.append(unquote(term));
			} else if (variables.containsKey(term)) {
				value.append(variables.get(term));
			} else {
				return null;
			}
		}
		return value.toString();
	}

	private static List<String> splitArguments(final String arguments) {
		return split(arguments, ',');
	}

	private static List<String> splitConcat(final String expression) {
		return split(expression, '+');
	}

	private static List<String> split(final String value, final char separator) {
		final List<String> parts = new ArrayList<String>();
		final StringBuilder current = new StringBuilder();
		boolean quoted = false;
		boolean escaped = false;
		int nested = 0;
		for (int i = 0; i < value.length(); i++) {
			final char c = value.charAt(i);
			if (escaped) {
				current.append(c);
				escaped = false;
				continue;
			}
			if (c == '\\') {
				current.append(c);
				escaped = true;
				continue;
			}
			if (c == '"') {
				quoted = !quoted;
				current.append(c);
				continue;
			}
			if (!quoted && c == '(') {
				nested++;
			} else if (!quoted && c == ')') {
				nested--;
			}
			if (!quoted && nested == 0 && c == separator) {
				parts.add(current.toString());
				current.setLength(0);
			} else {
				current.append(c);
			}
		}
		parts.add(current.toString());
		return parts;
	}

	private static String unquote(final String value) {
		return value.substring(1, value.length() - 1)
						.replace("\\\"", "\"")
						.replace("\\\\", "\\");
	}

	private static int findMatchingBrace(final String source, final int openBrace) {
		boolean quoted = false;
		boolean escaped = false;
		int depth = 0;
		for (int i = openBrace; i < source.length(); i++) {
			final char c = source.charAt(i);
			if (escaped) {
				escaped = false;
				continue;
			}
			if (c == '\\') {
				escaped = true;
				continue;
			}
			if (c == '"') {
				quoted = !quoted;
				continue;
			}
			if (quoted) {
				continue;
			}
			if (c == '{') {
				depth++;
			} else if (c == '}') {
				depth--;
				if (depth == 0) {
					return i;
				}
			}
		}
		return -1;
	}

	private static boolean matches(final String include, final HistoricScraperCase testCase) {
		return !present(include) || Pattern.compile(include).matcher(testCase.searchText()).find();
	}

	private static int integerProperty(final String property, final int defaultValue) {
		final String value = System.getProperty(property);
		if (!present(value)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (final NumberFormatException ex) {
			return defaultValue;
		}
	}

	private static final class HistoricScraperCase {
		private final String id;
		private final String legacyScraper;
		private final String url;
		private final String selection;
		private final String resultFile;

		private HistoricScraperCase(final String id, final String legacyScraper, final String url, final String selection, final String resultFile) {
			this.id = id;
			this.legacyScraper = legacyScraper;
			this.url = url;
			this.selection = selection;
			this.resultFile = resultFile;
		}

		private String getUrl() {
			return this.url;
		}

		private String getSelection() {
			return this.selection;
		}

		private String getResultFile() {
			return this.resultFile;
		}

		private String searchText() {
			return this.id + " " + this.legacyScraper + " " + this.url + " " + this.selection + " " + this.resultFile;
		}

		@Override
		public String toString() {
			return this.id + " [" + this.legacyScraper + "]";
		}
	}
}
