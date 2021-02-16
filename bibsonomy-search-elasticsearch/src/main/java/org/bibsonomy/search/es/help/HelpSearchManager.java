/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.search.es.help;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.search.InvalidSearchRequestException;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.util.Mapping;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.services.help.HelpParser;
import org.bibsonomy.services.help.HelpParserFactory;
import org.bibsonomy.services.help.HelpSearch;
import org.bibsonomy.services.help.HelpSearchResult;
import org.bibsonomy.util.StringUtils;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.jsoup.Jsoup;

/**
 * manager for searching the help pages
 * 
 * @author dzo
 */
public class HelpSearchManager implements HelpSearch {
	private static final Log log = LogFactory.getLog(HelpSearchManager.class);
	
	private static final String HEADER_FIELD = "header";
	private static final String CONTENT_FIELD = "content";
	private static final String PATH_FIELD = "path";
	
	private static final String HELP_PAGE_TYPE = "help_page";
	
	private static final Mapping<XContentBuilder> MAPPING = new Mapping<>();

	private static final String SETTINGS;
	
	static {
		MAPPING.setType(HELP_PAGE_TYPE);
		try {
			final XContentBuilder mapping = XContentFactory.jsonBuilder()
					.startObject()
						.startObject(HELP_PAGE_TYPE)
							.field("date_detection", false)
							.startObject("properties")
								.startObject(HEADER_FIELD)
									.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
									.field("boost", 2.0)
								.endObject()
								.startObject(CONTENT_FIELD)
									.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
								.endObject()
							.endObject()
						.endObject()
					.endObject();
			MAPPING.setMappingInfo(mapping);
			mapping.close();
			SETTINGS = Strings.toString(XContentFactory.jsonBuilder()
					.startObject()
						.startObject("analysis")
							.startObject("analyzer")
								.startObject("my_analyzer")
									.field("tokenizer", "standard")
									.field("filter", Arrays.asList("lowercase", "standard"))
								.endObject()
							.endObject()
							.startObject("char_filter")
								.startObject("my_char_filter")
									.field("type", "html_strip")
								.endObject()
							.endObject()
						.endObject()
					.endObject());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean indexingDisabled;
	
	private String path;
	
	private String projectName;
	private String projectTheme;
	private String projectHome;
	private String projectEmail;
	private String projectNoSpamEmail;
	private String projectAPIEmail;

	private URLGenerator urlGenerator;
	
	private ESClient client;
	
	private HelpParserFactory factory;
	
	private final Semaphore updateLock = new Semaphore(1);
	
	/**
	 * re-index the complete help pages
	 */
	public void reindex() {
		if (this.indexingDisabled) {
			return;
		}

		if (!this.updateLock.tryAcquire()) {
			log.warn("reindexing in progress");
			return;
		}

		try {
			// get all language folders (folders that are not hidden and are not the code-samples dir)
			final Path pathToScan = Paths.get(this.path);

			try (final Stream<Path> languageFolders = Files.list(pathToScan).filter(Files::isDirectory).filter(path -> !path.toFile().isHidden()).filter(path -> !path.toFile().getName().equals("code-samples"))) {

				languageFolders.forEach(languageFolder -> {
					final String language = languageFolder.toFile().getName();
					final String indexName = getIndexNameForLanguage(language);

					if (!this.client.existsIndexWithName(indexName)) {
						this.client.createIndex(indexName, MAPPING, SETTINGS);
					}

					final Map<String, Map<String, Object>> jsonDocuments = new HashMap<>();

					try (final Stream<Path> filePaths = Files.walk(languageFolder).filter(path -> path.toString().toLowerCase().endsWith(HelpUtils.FILE_SUFFIX))) {
						filePaths.forEach(filePath -> {
							final File file = filePath.toFile();
							final HelpParser parser = this.factory.createParser(HelpUtils.buildReplacementMap(this.projectName, this.projectTheme, this.projectHome, this.projectEmail, this.projectNoSpamEmail, this.projectAPIEmail), this.urlGenerator);
							final String fileName = file.getName().replaceAll(HelpUtils.FILE_SUFFIX, "");
							try (final BufferedReader helpPage = new BufferedReader(new InputStreamReader(new FileInputStream(file), StringUtils.DEFAULT_CHARSET))) {
								final String markdown = StringUtils.getStringFromReader(helpPage);
								final String content = parser.parseText(markdown, language);

								final Path relativePath = languageFolder.relativize(filePath.getParent());

								if (containsContent(content)) {
									final Map<String, Object> doc = new HashMap<>();
									doc.put(HEADER_FIELD, fileName);
									doc.put(CONTENT_FIELD, content);
									final String value = relativePath.toString();
									doc.put(PATH_FIELD, value);
									jsonDocuments.put(value + "/" + fileName, doc);
								}
							} catch (final Exception e) {
								log.error("cannot parse file " + fileName, e);
							}
						});
					} catch (final IOException e) {
						log.error("error while updating help index", e);
					}

					// finally update the index by deleting all pages and reinsert them again
					this.client.deleteDocuments(indexName, HELP_PAGE_TYPE, (QueryBuilder) null);
					if (present(jsonDocuments)) {
						this.client.insertNewDocuments(indexName, HELP_PAGE_TYPE, jsonDocuments);
					}
				});
			} catch (final IOException e) {
				log.error("error while updating help index", e);
			}
		} finally {
			this.updateLock.release();
		}
	}

	private static boolean containsContent(String content) {
		return !REDIRECT_PATTERN.matcher(content).find();
	}

	/**
	 * @param language
	 * @return
	 * @throws URISyntaxException
	 */
	private String getIndexNameForLanguage(final String language) {
		try {
			return ElasticsearchUtils.normSystemHome(new URI(this.projectHome)) + "_help_" + language;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param language
	 * @param searchTerms
	 * @return all results
	 */
	@Override
	public SortedSet<HelpSearchResult> search(final String language, final String searchTerms) throws InvalidSearchRequestException {
		final String indexName = this.getIndexNameForLanguage(language);

		final QueryStringQueryBuilder searchQuery = QueryBuilders.queryStringQuery(searchTerms);
		final MatchQueryBuilder sidebarQuery = QueryBuilders.matchQuery(HEADER_FIELD, HelpUtils.HELP_SIDEBAR_NAME);
		final BoolQueryBuilder query = QueryBuilders.boolQuery();
		query.must(searchQuery);
		query.mustNot(sidebarQuery);

		// get matches
		final HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.field(CONTENT_FIELD);
		highlightBuilder.requireFieldMatch(false);
		highlightBuilder.fragmentSize(100);
		highlightBuilder.numOfFragments(1);

		final TreeSet<HelpSearchResult> results = new TreeSet<>();
		try {
			final SearchHits hits = this.client.search(indexName, HELP_PAGE_TYPE, query, highlightBuilder, null,0, 25, null, null);

			if (!present(hits)) {
				return results;
			}

			for (final SearchHit searchHit : hits.getHits()) {
				final HelpSearchResult result = new HelpSearchResult();
				final Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
				result.setPage(sourceAsMap.get(HEADER_FIELD).toString());
				result.setScore(searchHit.getScore());
				result.setPath(sourceAsMap.get(PATH_FIELD).toString());

				final Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();

				final HighlightField contentHighlight = highlightFields.get(CONTENT_FIELD);
				if (present(contentHighlight)) {
					final Text[] fragments = contentHighlight.getFragments();
					final StringBuilder builder = new StringBuilder();
					for (final Text fragment : fragments) {
						builder.append(removeHtml(fragment));
					}

					final String highlightText = builder.toString();
					result.setHighlightContent(highlightText);
				}

				results.add(result);
			}
		} catch (final IndexNotFoundException e) {
			log.error("index " + indexName + " not found");
		} catch (final SearchPhaseExecutionException e) {
			log.info("parsing query failed.", e);
			throw new InvalidSearchRequestException();
		}

		return results;
	}

	/**
	 * @param fragment
	 * @return
	 */
	private static String removeHtml(final Text fragment) {
		String text = Jsoup.parse(fragment.toString()).text();
		final int index = text.indexOf('>');
		if (index != -1) {
			text = text.substring(index + 1);
		}
		
		final int tagStartIndex = text.lastIndexOf('<');
		if (tagStartIndex != -1) {
			text = text.substring(0, tagStartIndex);
		}
		return text;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @param projectTheme the projectTheme to set
	 */
	public void setProjectTheme(String projectTheme) {
		this.projectTheme = projectTheme;
	}

	/**
	 * @param projectHome the projectHome to set
	 */
	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}
	
	/**
	 * @param projectEmail the projectEmail to set
	 */
	public void setProjectEmail(String projectEmail) {
		this.projectEmail = projectEmail;
	}
	
	/**
	 * @param projectNoSpamEmail the projectNoSpamEmail to set
	 */
	public void setProjectNoSpamEmail(String projectNoSpamEmail) {
		this.projectNoSpamEmail = projectNoSpamEmail;
	}
	
	/**
	 * @param projectAPIEmail the projectAPIEmail to set
	 */
	public void setProjectAPIEmail(String projectAPIEmail) {
		this.projectAPIEmail = projectAPIEmail;
	}

	/**
	 * @param urlGenerator the urlGenerator to set
	 */
	public void setUrlGenerator(final URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(ESClient client) {
		this.client = client;
	}

	/**
	 * @param factory the factory to set
	 */
	public void setFactory(HelpParserFactory factory) {
		this.factory = factory;
	}

	/**
	 * @param indexingDisabled the indexingDisabled to set
	 */
	public void setIndexingDisabled(boolean indexingDisabled) {
		this.indexingDisabled = indexingDisabled;
	}
}
