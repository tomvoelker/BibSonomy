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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.util.Mapping;
import org.bibsonomy.services.help.HelpParser;
import org.bibsonomy.services.help.HelpParserFactory;
import org.bibsonomy.services.help.HelpSearch;
import org.bibsonomy.services.help.HelpSearchResult;
import org.bibsonomy.util.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
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
import org.elasticsearch.search.highlight.HighlightField;
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
	
	private static final String HELP_PAGE_TYPE = "help_page";
	
	private static final Mapping<String> MAPPING = new Mapping<>();

	private static final String SETTINGS;
	
	static {
		MAPPING.setType(HELP_PAGE_TYPE);
		try {
			final XContentBuilder mapping = XContentFactory.jsonBuilder()
					.startObject()
						.startObject(HELP_PAGE_TYPE)
							/*
							 * set the date detection to false: we load the misc
							 * fields as field = value into es (=> dynamic mapping)
							 */
							.field("date_detection", false)
							.startObject("properties")
								.startObject(HEADER_FIELD)
									.field("type", "string")
									.field("store", "true")
								.endObject()
								.startObject(CONTENT_FIELD)
									.field("type", "string")
									.field("store", "true")
								.endObject()
							.endObject()
						.endObject()
					.endObject();
			MAPPING.setMappingInfo(mapping.string());
			mapping.close();
			SETTINGS = XContentFactory.jsonBuilder()
					.startObject()
						.startObject("analyzer")
							.startObject("default")
								.field("type", "custom")
								.field("char_filter", Arrays.asList("html_strip"))
								.field("tokenizer", "standard")
								.field("filter", Arrays.asList("lowercase", "snowball"))
							.endObject()
						.endObject()
					.endObject().string();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private String path;
	
	private String projectName;
	private String projectTheme;
	private String projectHome;
	
	private ESClient client;
	
	private HelpParserFactory factory;
	
	private final Semaphore updateLock = new Semaphore(1);
	
	/**
	 * re-index the complete help pages
	 */
	public void reindex() {
		if (!this.updateLock.tryAcquire()) {
			log.warn("reindexing in progress");
			return;
		}
		
		try {
			final File baseFolder = new File(this.path);
			final File[] languageFolders = baseFolder.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory() && !pathname.isHidden();
				}
			});
			
			for (final File languageFolder : languageFolders) {
				final String language = languageFolder.getName();
				
				final String indexName = getIndexNameForLanguage(language);
				
				if (!this.client.existsIndexWithName(indexName)) {
					this.client.createIndex(indexName, Collections.singleton(MAPPING), SETTINGS);
				}
				
				final File[] files = languageFolder.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(HelpUtils.FILE_SUFFIX);
					}
				});
				
				final Map<String, Map<String, Object>> jsonDocuments = new HashMap<>();
				for (final File file : files) {
					final HelpParser parser = this.factory.createParser(HelpUtils.buildReplacementMap(this.projectName, this.projectTheme, this.projectHome));
					final String fileName = file.getName().replaceAll(HelpUtils.FILE_SUFFIX, "");
					try (final BufferedReader helpPage = new BufferedReader(new InputStreamReader(new FileInputStream(file), StringUtils.DEFAULT_CHARSET))) {
						final String markdown = StringUtils.getStringFromReader(helpPage);
						final String content = parser.parseText(markdown);
						final Map<String, Object> doc = new HashMap<>();
						doc.put(HEADER_FIELD, fileName);
						doc.put(CONTENT_FIELD, content);
						jsonDocuments.put(fileName, doc);
					} catch (final Exception e) {
						log.error("cannot parse file " + fileName, e);
					}
				}
				
				this.client.deleteDocuments(indexName, HELP_PAGE_TYPE, (QueryBuilder) null);
				this.client.insertNewDocuments(indexName, HELP_PAGE_TYPE, jsonDocuments);
			}
		} finally {
			this.updateLock.release();
		}
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
	public SortedSet<HelpSearchResult> search(final String language, final String searchTerms) {
		final String indexName = this.getIndexNameForLanguage(language);
		final SearchRequestBuilder searchBuilder = this.client.prepareSearch(indexName);
		final QueryStringQueryBuilder searchQuery = QueryBuilders.queryStringQuery(searchTerms);
		final MatchQueryBuilder sidebarQuery = QueryBuilders.matchQuery(HEADER_FIELD, HelpUtils.HELP_SIDEBAR_NAME);
		final BoolQueryBuilder query = QueryBuilders.boolQuery();
		query.must(searchQuery);
		query.mustNot(sidebarQuery);
		
		searchBuilder.setQuery(query);
		searchBuilder.setTypes(HELP_PAGE_TYPE);
		searchBuilder.setSearchType(SearchType.DEFAULT);
		searchBuilder.addHighlightedField(CONTENT_FIELD, 100, 1);
		searchBuilder.setHighlighterRequireFieldMatch(false);
		final TreeSet<HelpSearchResult> results = new TreeSet<>();
		try {
			final SearchResponse response = searchBuilder.execute().actionGet();
			if (response != null) {
				final SearchHits hits = response.getHits();
				for (final SearchHit searchHit : hits.hits()) {
					final HelpSearchResult result = new HelpSearchResult();
					result.setPage(searchHit.getId());
					result.setScore(searchHit.getScore());
					
					final Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
					
					final HighlightField contentHighlight = highlightFields.get(CONTENT_FIELD);
					if (present(contentHighlight)) {
						final Text[] fragments = contentHighlight.getFragments();
						final StringBuilder builder = new StringBuilder();
						for (final Text fragment : fragments) {
							builder.append(Jsoup.parse(fragment.toString()).text());
						}
						
						final String highlightText = builder.toString();
						result.setHighlightContent(highlightText);
					}
					
					results.add(result);
				}
			}
		} catch (final IndexNotFoundException e) {
			log.error("index " + indexName + " not found");
		}
		
		return results;
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
}
