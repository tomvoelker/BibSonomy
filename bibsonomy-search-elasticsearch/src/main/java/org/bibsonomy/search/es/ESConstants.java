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
package org.bibsonomy.search.es;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bibsonomy.util.Sets;
import org.bibsonomy.util.tex.TexDecode;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentFactory;

/**
 * constants for elastic search engine
 * 
 * @author lutful
 * @author dzo
 */
public final class ESConstants {
	
	private static final String BRACKETS_CHAR_FILTER_NAME = "brackets";
	private static final String CURLY_BRACKETS_CHAR_FILTER_NAME = "curly_brackets";
	private static final String ASCII_FOLDING_PRESERVE_TOKEN_FILTER_NAME = "ascii_folding_preserve";
	private static final String BIBTEX_MAPPING = "BibTeX_mapping";

	/** settings of each created index */
	public static final String SETTINGS;
	
	static {
		try {
			SETTINGS = Strings.toString(XContentFactory.jsonBuilder()
					.startObject()
						.startObject("analysis")
							.startObject("char_filter")
								.startObject(BIBTEX_MAPPING)
									.field("type", "mapping")
									.field("mappings", getBibTeXDecodeMapping())
								.endObject()
								.startObject(CURLY_BRACKETS_CHAR_FILTER_NAME)
									.field("type", "pattern_replace")
									.field("pattern", TexDecode.CURLY_BRACKETS)
									.field("replacement", "")
								.endObject()
								.startObject(BRACKETS_CHAR_FILTER_NAME)
									.field("type", "pattern_replace")
									.field("pattern", TexDecode.BRACKETS)
									.field("replacement", "")
								.endObject()
							.endObject()
							.startObject("filter")
								.startObject(ASCII_FOLDING_PRESERVE_TOKEN_FILTER_NAME)
									.field("type", "asciifolding")
									.field("preserve_original", true)
								.endObject()
							.endObject()
							.startObject("analyzer")
								.startObject("default")
									.field("type", "custom")
									.field("char_filter", Arrays.asList(BIBTEX_MAPPING, BRACKETS_CHAR_FILTER_NAME, CURLY_BRACKETS_CHAR_FILTER_NAME))
									.field("tokenizer", "standard")
									.field("filter", Arrays.asList(ASCII_FOLDING_PRESERVE_TOKEN_FILTER_NAME, "lowercase", "standard"))
								.endObject()
							.endObject()
						.endObject()
					.endObject());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * some constants for index settings
	 */
	public interface IndexSettings {
		/** properties field key */
		String PROPERTIES = "properties";
		/** flag to copy the field also to the other fields */
		String COPY_TO = "copy_to";
		/** boost the field (search in _all field) */
		String BOOST_FIELD = "boost";
		/** relation field */
		String RELATION_FIELD = "relations";
		/** type text */
		String TEXT_TYPE = "text";
		/** type keyword used only for filtering */
		String KEYWORD_TYPE = "keyword";
		/** type nested */
		String NESTED_TYPE = "nested";
		/** date type */
		String DATE_TYPE = "date";
		/** join type */
		String JOIN_TYPE = "join";
		/** the type field */
		String TYPE_FIELD = "type";
		/** the index field */
		String INDEX_FIELD = "index";
		/** e.g the date format field */
		String FORMAT_FIELD = "format";
		/** iso date format (optional time) */
		String FORMAT_DATE_OPTIONAL_TIME = "dateOptionalTime";
		/** iso date format */
		String DATE_TIME_FORMAT = "date_time";
		/** field should not be indexed */
		String NOT_INDEXED = "false";
		/** set to false to disable indexing */
		String ENABLED = "enabled";
	}
	
	/** Index type for the system information */
	public static final String SYSTEM_INFO_INDEX_TYPE = "SystemInformation";
	
	/** phdthesis+type resolved to habil, phd, master, bachelor*/
	public static final String NORMALIZED_ENTRY_TYPE_FIELD_NAME = "entryTypeNorm";
	
	/** current full names (including titles) of the author person-entities */
	public static final String AUTHOR_ENTITY_NAMES_FIELD_NAME = "authorEntityNames";
	
	/** Ids of the associated author person-entities */
	public static final String AUTHOR_ENTITY_IDS_FIELD_NAME = "authorEntityIds";

	/** current full names (including titles) of the associated authors, editors, supervisors, etc */
	public static final String PERSON_ENTITY_NAMES_FIELD_NAME = "personEntityNames";
	
	/** prefix for temporary index */
	public static final String TEMP_INDEX_PREFIX = "TempIndex";
	
	/** the max number of docs per bulk insert */
	public static final int BULK_INSERT_SIZE = 1000;

	/** contains all field information */
	public interface Fields {
		/** the name of the user of the post */
		public static final String USER_NAME = "user_name";
		/** list of all users that posted this post (with the same interhash) */
		String ALL_USERS = "all_users";
		/** the groups of the post */
		public static final String GROUPS = "groups";
		/** the tags of the post */
		public static final String TAGS = "tags";
		/** field name in th index schema */
		public static final String SYSTEM_URL = "systemUrl";
		/** the date (creation) of the post */
		public static final String DATE = "date";
		/** the latest date of the post */
		public static final String CHANGE_DATE = "change_date";
		/** the description */
		public static final String DESCRIPTION = "description";
		/** Ids of the associated authors, editors, supervisors, etc */
		public static final String PERSON_ENTITY_IDS_FIELD_NAME = "personEntityIds";

		public interface Resource {
			/** the title of the resource */
			String TITLE = "title";
			/** the inter hash of the resource */
			String INTERHASH = "interhash";
			/** the intra hash of the resource */
			String INTRAHASH = "intrahash";
		}
		
		public interface Bookmark {
			/** the url of the bookmark */
			String URL = "url";
		}
		
		public interface Publication {
			/** field that contains all docs */
			String ALL_DOCS = "all_docs";

			/** field that contains all authors */
			String ALL_AUTHORS = "author";

			String AUTHORS = "authors";
			String EDITORS = "editors";
			String PERSON_NAME = "name";
			String PERSON_ID = "person_id";
			String OTHER_PERSON_RESOURCE_RELATIONS = "other_relations";
			String PERSON_RELATION_TYPE = "relation_type";
			
			String SCHOOL = "school";
			/** the publication's year */
			String YEAR = "year";
			/** the bibtex key field name */
			String BIBTEXKEY = "bibtexkey";
			String ADDRESS = "address";
			String ENTRY_TYPE = "entrytype";
			String ANNOTE = "annote";
			String KEY = "bkey";
			String ABSTRACT = "abstract";
			String BOOKTITLE = "booktitle";
			String CHAPTER = "chapter";
			String CROSSREF = "crossref";
			String DAY = "day";
			String EDITION = "edition";
			String HOWPUBLISHED = "howPublished";
			String INSTITUTION = "institution";
			String JOURNAL = "journal";
			String MONTH = "month";
			String NOTE = "note";
			String NUMBER = "number";
			String ORGANIZATION = "organization";
			String PAGES = "pages";
			String PRIVNOTE = "privnote";
			String PUBLISHER = "publisher";
			String SERIES = "series";
			String TYPE = "type";
			String URL = "url";
			String VOLUME = "volume";
			String DOCUMENTS = "documents";
			/** the nested field containing all misc fields */
			String MISC = "misc";
			/** all misc field values */
			String MISC_FIELDS_VALUES = "misc_values";
			/** misc fields */
			String MISC_FIELDS = "misc_fields";
			/** key field */
			String MISC_KEY = "key";
			/** value field */
			String MISC_VALUE = "value";
			/** the doi (special misc field) */
			String DOI = "doi";
			/** the issn (special misc field) */
			String ISSN = "issn";
			/** the isbn (special misc field) */
			String ISBN = "isbn";
			/** the language */
			String LANGUAGE = "language";
			/** a list of special misc fields */
			Set<String> SPECIAL_MISC_FIELDS = Sets.asSet(DOI, ISSN, ISBN, LANGUAGE);
			/** the document */
			interface Document {
				String NAME = "name";
				String TEXT = "text";
				String HASH = "hash";
				String CONTENT_HASH = "content_hash";
				String DATE = "date";
			}
		}
	}

	/**
	 * @return
	 */
	private static List<String> getBibTeXDecodeMapping() {
		final List<String> decodingList = new LinkedList<>();
		for (final Entry<String, String> decodeEntry : TexDecode.getTexMap().entrySet()) {
			decodingList.add(escape(decodeEntry.getKey()) + "=>" + decodeEntry.getValue());
		}
		return decodingList;
	}

	/**
	 * @param string
	 * @return the escaped string for the mapping
	 */
	private static String escape(final String string) {
		return string.replaceAll("\\\\", "\\\\\\\\");
	}
}
