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
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bibsonomy.util.tex.TexDecode;
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
			SETTINGS = XContentFactory.jsonBuilder()
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
					.endObject().string();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Index type for the system information
	 */
	public static final String SYSTEM_INFO_INDEX_TYPE = "SystemInformation";
	
	/** phdthesis+type resolved to habil, phd, master, bachelor*/
	public static final String NORMALIZED_ENTRY_TYPE_FIELD_NAME = "entryTypeNorm";
	
	/** current full names (including titles) of the author person-entities */
	public static final String AUTHOR_ENTITY_NAMES_FIELD_NAME = "authorEntityNames";
	
	/** Ids of the associated author person-entities */
	public static final String AUTHOR_ENTITY_IDS_FIELD_NAME = "authorEntityIds";

	/** current full names (including titles) of the associated authors, editors, supervisors, etc */
	public static final String PERSON_ENTITY_NAMES_FIELD_NAME = "personEntityNames";
	
	/**
	 * prefix for temporary index
	 */
	public static final String TEMP_INDEX_PREFIX = "TempIndex";
	
	/** the max number of docs per bulk insert */
	public static final int BULK_INSERT_SIZE = 5000;

	/** contains all field information */
	public static final class Fields {

		/** the all field of Elasticsearch */
		private static final String ALL_FIELD = "_all";

		/** all fields that are mapped */
		public static final Set<String> ALL_FIELDS = new HashSet<>();

		static {
			addAllFields(Fields.class);
			addAllFields(Resource.class);
			addAllFields(Bookmark.class);
			addAllFields(Publication.class);
			addAllFields(Publication.Document.class);
		}

		private static void addAllFields(final Class<?> clazz) {
			try {
				final Field fieldlist[] = clazz.getDeclaredFields();
				for (final Field field : fieldlist) {
					if (field.getType().equals(String.class)) {
						ALL_FIELDS.add((String) field.get(null));
					}
				}
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}

		/** field that contains all docs */
		public static final String ALL_DOCS = "all_docs";

		/** private search content should be copied to this field */
		public static final String PRIVATE_ALL_FIELD = "all_private";

		/** the content id of the post */
		public static final String CONTENT_ID = "content_id";
		/** the name of the user of the post */
		public static final String USER_NAME = "user_name";
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
		
		
		public static interface Resource {
			/** the title of the resource */
			public static final String TITLE = "title";
			/** the inter hash of the resource */ 
			public static final String INTERHASH = "interhash";
			/** the intra hash of the resource */
			public static final String INTRAHASH = "intrahash";
		}
		
		public static interface Bookmark {
			/** the url of the bookmark */
			public static final String URL = "url";
		}
		
		public static interface Publication {
			public static final String AUTHORS = "authors";
			public static final String EDITORS = "editors";
			public static final String PERSON_NAME = "name";
			
			public static final String SCHOOL = "school";
			/** the publication's year */
			public static final String YEAR = "year";
			/** the bibtex key field name */
			public static final String BIBTEXKEY = "bibtexkey";
			public static final String ADDRESS = "address";
			public static final String ENTRY_TYPE = "entrytype";
			public static final String ANNOTE = "annote";
			public static final String KEY = "bkey";
			public static final String ABSTRACT = "abstract";
			public static final String BOOKTITLE = "booktitle";
			public static final String CHAPTER = "chapter";
			public static final String CROSSREF = "crossref";
			public static final String DAY = "day";
			public static final String EDITION = "edition";
			public static final String HOWPUBLISHED = "howPublished";
			public static final String INSTITUTION = "institution";
			public static final String JOURNAL = "journal";
			public static final String MISC = "misc";
			public static final String MONTH = "month";
			public static final String NOTE = "note";
			public static final String NUMBER = "number";
			public static final String ORGANIZATION = "organization";
			public static final String PAGES = "pages";
			public static final String PRIVNOTE = "privnote";
			public static final String PUBLISHER = "publisher";
			public static final String SERIES = "series";
			public static final String TYPE = "type";
			public static final String URL = "url";
			public static final String VOLUME = "volume";
			public static final String DOCUMENTS = "documents";
			
			public static interface Document {
				public static final String NAME = "name";
				public static final String TEXT = "text";
				public static final String HASH = "hash";
				public static final String CONTENT_HASH = "content_hash";
				public static final String DATE = "date";
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
	 * @param key
	 * @return
	 */
	private static String escape(final String string) {
		return string.replaceAll("\\\\", "\\\\\\\\");
	}
}
