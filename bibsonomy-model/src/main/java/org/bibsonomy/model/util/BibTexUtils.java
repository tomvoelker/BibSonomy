/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.model.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.comparators.BibTexPostComparator;
import org.bibsonomy.model.comparators.BibTexPostInterhashComparator;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.tex.TexDecode;

import bibtex.dom.BibtexAbstractEntry;
import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.dom.BibtexString;
import bibtex.parser.BibtexParser;

/**
 * Some BibTex utility functions.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class BibTexUtils {
	private static final Log log = LogFactory.getLog(BibTexUtils.class);
	
	private static final String GET_METHOD_PREFIX = "get";
	
	/**
	 * Enable the output of the plain misc field when serializing 
	 * publications into BibTeX entries.
	 */
	public static final int SERIALIZE_BIBTEX_OPTION_PLAIN_MISCFIELD = 0x01;
	/**
	 * Enable the output of person names in "First Last" form instead
	 * of "Last, First" form.
	 */
	public static final int SERIALIZE_BIBTEX_OPTION_FIRST_LAST = 0x02;
	/**
	 * Enable the output of generated BibTeX keys instead of the 
	 * keys from the database.
	 */
	public static final int SERIALIZE_BIBTEX_OPTION_GENERATED_BIBTEXKEYS = 0x04;
	

	/**
	 * This field from the post is added to the BibTeX string (in addition to 
	 * all fields from the resource) 
	 */
	public static final String ADDITIONAL_MISC_FIELD_BIBURL = "biburl";

	/**
	 * This field is intended to point towards the (plain text) bibtex record
	 * of the given bibtex entry, i.e. usually towards something like
	 *   http://www.bibsonomy.org/bib/bibtex/INTRAHASH/USERNAME
	 * Needed mainly when exporting via JabRef filters.
	 */
	public static final String ADDITIONAL_MISC_FIELD_BIBRECORD = "bibrecord";
	
	
	/**
	 * This field from the post is added to the BibTeX string (in addition to 
	 * all fields from the resource) 
	 */
	public static final String ADDITIONAL_MISC_FIELD_DESCRIPTION = "description";

	/**
	 * This field from the post is added to the BibTeX string (in addition to 
	 * all fields from the resource). It is needed by the DBLP update to allow
	 * setting of the post date.
	 */
	public static final String ADDITIONAL_MISC_FIELD_DATE = "date";

	/**
	 * This field from the post is added to the BibTeX string (in addition to 
	 * all fields from the resource). It represents the "date" field of the post.
	 */
	public static final String ADDITIONAL_MISC_FIELD_ADDED_AT = "added-at";
	/**
	 * This field from the post is added to the BibTeX string (in addition to 
	 * all fields from the resource). It represents the "changeDate" of the post. 
	 */
	public static final String ADDITIONAL_MISC_FIELD_TIMESTAMP = "timestamp";

	/**
	 * ISO date + time for "added-at" and "timestamp" field  
	 */
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	/**
	 * This field from the post is added to the BibTeX string (in addition to 
	 * all fields from the resource)
	 */
	public static final String ADDITIONAL_MISC_FIELD_KEYWORDS = "keywords";

	/**
	 * This field from the post is added to the BibTeX string (in addition to 
	 * all fields from the resource)
	 */
	public static final String ADDITIONAL_MISC_FIELD_PRIVNOTE = "privnote";

	/**
	 * This fields from the post are added to the BibTeX string (in addition to 
	 * all fields from the resource)
	 */
	public static final String[] ADDITIONAL_MISC_FIELDS = new String[] {
		ADDITIONAL_MISC_FIELD_DESCRIPTION,
		ADDITIONAL_MISC_FIELD_KEYWORDS,
		ADDITIONAL_MISC_FIELD_BIBURL,
		ADDITIONAL_MISC_FIELD_PRIVNOTE,
		ADDITIONAL_MISC_FIELD_ADDED_AT,
		ADDITIONAL_MISC_FIELD_TIMESTAMP
	};
	
	/** default opening bracket */
	public static final char DEFAULT_OPENING_BRACKET = '{';
	
	/** default closing bracket */
	public static final char DEFAULT_CLOSING_BRACKET = '}';
	
	/** value separator used to separate key/value pairs; i.e. key=val SEP key2=val2*/
	public static final char KEYVALUE_SEPARATOR = ',';
	
	private static final String KEYVALUE_SEPARATOR_STRING = String.valueOf(KEYVALUE_SEPARATOR);
	
	/** assignment operator to assign keys to values; i.e. key OP val, ...*/
	public static final char ASSIGNMENT_OPERATOR = '=';
	
	/**
	 * Indentation used for key/value pairs when converted to a BibTeX string. 
	 */
	private static final String DEFAULT_INTENDATION = "  ";
	
	private static final String BIBTEX_MONTH_FIELD = "month";

	private static final String BIBTEX_EDITOR_FIELD = "editor";

	private static final String BIBTEX_AUTHOR_FIELD = "author";
	
	/** prefix for tags indicating post owners when duplicates=merge */
	private static final String MERGED_PREFIX = "merged:";

	
	
	/**
	 * To remove the preprint entry type remove all occurrences of this string and the corresponding types 
	 * in swrcEntryTypes and risEntryTypes in class Functions in webapp module.
	 */
	public static final String PREPRINT = "preprint";
	
	/**
	 * the supported entrytypes of a bibtex
	 * be careful when changing order some code uses the order to map entrytypes to (swrc|ris) entrytypes
	 * 
	 * e.g., in org.bibsonomy.model.util.BibTexUtils.ENTRYTYPES 
	 * 
	 * FIXME: this is bad. Please fix this behaviour. 
	 */
	public static final String[] ENTRYTYPES = {"article", "book", "booklet", "conference", "electronic", "inbook", "incollection", "inproceedings",
		"manual", "mastersthesis", "misc", "patent", "periodical", "phdthesis", "preamble", "presentation", "proceedings", "standard", "techreport", "unpublished",
		PREPRINT
	};
	

	/*
	 * patterns used for matching
	 */
	private static final Pattern YEAR_PATTERN = Pattern.compile("\\d{4}");
	private static final Pattern DOI_PATTERN = Pattern.compile("http://.+/(.+?/.+?$)");
	private static final Pattern LAST_COMMA_PATTERN = Pattern.compile(".+\\}?\\s*,\\s*\\}\\s*$", Pattern.MULTILINE | Pattern.DOTALL);
	private static final Pattern NUMERIC_PATTERN = Pattern.compile("^\\d+$");

	/*
	 * fields to be excluded when creating bibtex strings.
	 */
	private static final Set<String> EXCLUDE_FIELDS = new HashSet<String>(Arrays.asList(
			"bibtexkey",       // added at beginning of entry
			"entrytype",       // added at beginning of entry
			"misc",            // contains several fields; handled separately
			"month",           // handled separately
			"openurl", 
			"simhash0", // not added
			"simhash1", // not added
			"simhash2", // not added
			"simhash3"  // not added
	));

	/**
	 * Some BibTeX styles translate month abbreviations into (language specific) 
	 * month names. If we find such a month abbreviation, we should not put 
	 * braces around the string.
	 */
	private static final Map<String, Integer> BIBTEX_MONTHS = new HashMap<String, Integer>();
	static {
		final String[] months = new String[] {
				"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"
		};
		for (int i = 0; i < months.length; i++) {
			BIBTEX_MONTHS.put(months[i], i + 1);
		}
	}
	
	
	/**
	 * Builds a string from a given bibtex object which can be used to build an OpenURL
	 * see http://www.exlibrisgroup.com/sfx_openurl.htm
	 *
	 * @param bib the bibtex object
	 * @return the DESCRIPTION part of the OpenURL of this BibTeX object
	 */
	public static String getOpenurl(final BibTex bib) {
		// stores the completed URL (just the DESCRIPTION part)
		final StringBuilder openurl = new StringBuilder();

		/*
		 * extract first authors parts of the name
		 */
		// get first author (if author not present, use editor)
		List<PersonName> author = bib.getAuthor();
		if (!present(author)) {
			author = bib.getEditor();
		}
		final PersonName personName; 
		if (present(author)) {
			personName = author.get(0);
		} else {
			// TODO: this is only necessary because of broken (DBLP) entries which have neither author nor editor!
			personName = new PersonName();
		}
		// check, if first name is just an initial
		final String auinit1;
		final String firstName = personName.getFirstName();
		if (present(firstName) && firstName.length() == 1) {
			auinit1 = firstName;
		} else {
			auinit1 = null;
		}

		// parse misc fields
		bib.parseMiscField();
		// extract DOI
		String doi = bib.getMiscField("doi");
		if (doi != null) {
			// TODO: urls rausfiltern testen
			final Matcher m = DOI_PATTERN.matcher(doi);
			if (m.find()) {
				doi = m.group(1);
			}
		}

		try {
			// append year (due to inconsistent database not always given!)
			if (present(bib.getYear())) {
				openurl.append("date=" + bib.getYear().trim());
			}
			// append doi
			if (present(doi)) {
				appendOpenURL(openurl,"id", "doi:" + doi.trim());
			}
			// append isbn + issn
			appendOpenURL(openurl,"isbn", bib.getMiscField("isbn"));
			appendOpenURL(openurl,"issn", bib.getMiscField("issn"));
			// append name information for first author
			appendOpenURL(openurl, "aulast", personName.getLastName());
			appendOpenURL(openurl, "aufirst", firstName);
			appendOpenURL(openurl, "auinit1", auinit1);
			// genres == entrytypes
			final String entryType = bib.getEntrytype().toLowerCase();
			if (entryType.equals("journal")) {
				appendOpenURL(openurl, "genre", "journal");
				appendOpenURL(openurl, "title", bib.getTitle());
			} else if (entryType.equals("book")) {
				appendOpenURL(openurl, "genre", "book");
				appendOpenURL(openurl, "title", bib.getTitle());
			} else if (entryType.equals("article")) {
				appendOpenURL(openurl, "genre", "article");
				appendOpenURL(openurl, "title", bib.getJournal());
				appendOpenURL(openurl, "atitle", bib.getTitle());
			} else if (entryType.equals("inbook")) {
				appendOpenURL(openurl, "genre", "bookitem");
				appendOpenURL(openurl, "title", bib.getBooktitle());
				appendOpenURL(openurl, "atitle", bib.getTitle());
			} else if (entryType.equals("proceedings")) {
				appendOpenURL(openurl, "genre", "proceeding");
				appendOpenURL(openurl, "title", bib.getBooktitle());
				appendOpenURL(openurl, "atitle", bib.getTitle());
			} else {
				appendOpenURL(openurl, "title", bib.getBooktitle());
				appendOpenURL(openurl, "atitle", bib.getTitle());
			}
			appendOpenURL(openurl, "volume", bib.getVolume());
			appendOpenURL(openurl, "issue", bib.getNumber());
		} catch (final UnsupportedEncodingException ex) {
			log.error("error while generating openURL", ex);
		}

		return openurl.toString();
	}

	private static void appendOpenURL(final StringBuilder buffer, final String name, final String value) throws UnsupportedEncodingException {
		if (present(value)) {
			buffer.append("&" + name + "=" + URLEncoder.encode(value.trim(), "UTF-8"));
		}
	}


	/**
 	 * Returns a BibTeX string representation of the given publication. By default, 
	 * the contained misc fields are parsed before the BibTeX string is generated,
	 * the authors and editors are in "Last, First" form, and the BibTeX key is 
	 * untouched. 
	 * 
	 * @param bib
	 * @return The BibTeX-serialized publication with the default person name 
	 * order ({@link PersonNameUtils#DEFAULT_LAST_FIRST_NAMES}.
	 */
	public static String toBibtexString(final BibTex bib) {
		return toBibtexString(bib, 0);
	}


	/**
	 * XXX: we don't use java.beans because the package is missing in Android SDK
	 * 
	 * return a bibtex string representation of the given bibtex object
	 * 
	 * @param bib - a bibtex object
	 * @param flags - flags to change the serialization behavior. A bit mask 
	 * that may include {@link #SERIALIZE_BIBTEX_OPTION_FIRST_LAST}, {@link #SERIALIZE_BIBTEX_OPTION_GENERATED_BIBTEXKEYS}, {@link #SERIALIZE_BIBTEX_OPTION_PLAIN_MISCFIELD}.
	 * @return String bibtexString 
	 */
	public static String toBibtexString(final BibTex bib, final int flags) {
		/*
		 * get all values to generate the BibTeX first to sort all entries
		 * alphabetically
		 */
		final SortedMap<String, String> values = new TreeMap<String, String>();
		final boolean lastFirstNames = !hasFlag(flags, SERIALIZE_BIBTEX_OPTION_FIRST_LAST);
		
		final Method[] methods = BibTex.class.getMethods();
		for (final Method method : methods) {
			if (method.getParameterTypes().length == 0 && String.class.equals(method.getReturnType()) && method.getName().startsWith(GET_METHOD_PREFIX)) {
				try {
					final String key = method.getName().replaceFirst(GET_METHOD_PREFIX, "").toLowerCase();
					if (!EXCLUDE_FIELDS.contains(key)) {
						String value = (String) method.invoke(bib, (Object[]) null);
					
						if (present(value)) {
							if (!NUMERIC_PATTERN.matcher(value).matches()) {
								value = addBibTeXBrackets(value);
							}
							values.put(key, value);
						}
					}
				} catch (final Exception ex) {
					log.error("exception while converting publication to BibTeX", ex);
				}
			}
		}
		
		/*
		 * append author and editor
		 */
		if (present(bib.getAuthor())) {
			values.put(BIBTEX_AUTHOR_FIELD, addBibTeXBrackets(PersonNameUtils.serializePersonNames(bib.getAuthor(), lastFirstNames)));
		}
		
		if (present(bib.getEditor())) {
			values.put(BIBTEX_EDITOR_FIELD, addBibTeXBrackets(PersonNameUtils.serializePersonNames(bib.getEditor(), lastFirstNames)));
		}
		
		/*
		 * process miscFields map, if present
		 */
		if (present(bib.getMiscFields())) {
			if (!hasFlag(flags, SERIALIZE_BIBTEX_OPTION_PLAIN_MISCFIELD) && !bib.isMiscFieldParsed()) {
				// parse misc field, if not yet done
				bib.parseMiscField();
			}
			
			for (final Entry<String, String> miscField : bib.getMiscFields().entrySet()) {
				values.put(miscField.getKey().toLowerCase(), addBibTeXBrackets(miscField.getValue()));
			}
		}

		/*
		 * add month
		 */
		final String month = bib.getMonth();
		if (present(month)) {
			// we don't add {}, this is done by getMonth(), if necessary
			values.put(BIBTEX_MONTH_FIELD, getMonth(month));
		}
		
		/*
		 * start with entrytype and key
		 */
		final String bibtexKey = hasFlag(flags, SERIALIZE_BIBTEX_OPTION_GENERATED_BIBTEXKEYS) ? generateBibtexKey(bib): bib.getBibtexKey();
		
		final StringBuilder buffer = new StringBuilder("@").append(bib.getEntrytype()).append(DEFAULT_OPENING_BRACKET).append(bibtexKey).append(KEYVALUE_SEPARATOR).append("\n");

		for (final Entry<String, String> entry : values.entrySet()) {
			buffer.append(DEFAULT_INTENDATION).append(entry.getKey().toLowerCase())
			.append(" ").append(ASSIGNMENT_OPERATOR).append(" ")
			.append(entry.getValue()).append(KEYVALUE_SEPARATOR).append("\n");
		}
		
		/*
		 * include plain misc fields if desired
		 */
		if (hasFlag(flags, SERIALIZE_BIBTEX_OPTION_PLAIN_MISCFIELD) && present(bib.getMisc())) {
			buffer.append(DEFAULT_INTENDATION).append(bib.getMisc()).append(KEYVALUE_SEPARATOR).append("\n");
		}
		
		/*
		 * remove last comma
		 */
		buffer.delete(buffer.lastIndexOf(KEYVALUE_SEPARATOR_STRING), buffer.length());
		buffer.append("\n").append(DEFAULT_CLOSING_BRACKET);	

		return buffer.toString();
	}

	/**
	 * @param value
	 * @return
	 */
	private static String addBibTeXBrackets(final String value) {
		return DEFAULT_OPENING_BRACKET + value + DEFAULT_CLOSING_BRACKET;
	}


	/**
	 * Some BibTeX styles translate month abbreviations into (language specific) 
	 * month names. If we find such a month abbreviation, we should not put 
	 * braces around the string. This method returns the correct string - with
	 * braces, if it's not an abbreviation, without otherwise.
	 * 
	 * @param month
	 * @return The correctly 'quoted' month.
	 */
	public static String getMonth(final String month) {
		if (month != null && BIBTEX_MONTHS.containsKey(month.toLowerCase().trim())) return month;
		return addBibTeXBrackets(month);
	}


	/**
	 * Tries to extract the month number from the given string. The following 
	 * input formats are supported:
	 * <ul>
	 * <li>long English month name: January, february, MARCH, ...</li>
	 * <li>abbreviated English month name: Jan, feb, MAR, ...</li>
	 * <li>month as number: 01, 2, 3, ...</li>
	 * </ul> 
	 * <strong>Note:</strong> if an unreadable month is given, the untouched
	 * string is returned. 
	 * 
	 * 
	 * @param month
	 * @return The month represented as number in the range 1, ..., 12
	 */
	public static String getMonthAsNumber(final String month) {
		if (present(month)) {
			final String trimmed = month.replace('#', ' ').trim();
			if (trimmed.length() >= 3) {
				final String abbrev = trimmed.toLowerCase().substring(0, 3);
				if (BIBTEX_MONTHS.containsKey(abbrev)) {
					return BIBTEX_MONTHS.get(abbrev).toString();
				}
			}
			return trimmed;
		}
		return month;
	}

	/**
	 * Creates a bibtex string with some bibsonomy-specific information using 
	 * {@link #toBibtexString(Post, int)}.
	 * 
	 * <ul>
	 * 		<li>tags in <code>keywords</code> field</li>
	 * 		<li>URL to bibtex details page in <code>biburl</code> field</li>
	 * 		<li>description in the <code>description</code> field</li>
	 * </ul>
	 * 
	 * @see #toBibtexString(BibTex, int)
	 * 
	 * @param post - a publication post
	 * @param flags - flags to change the serialization behavior. A bit mask 
	 * that may include {@link #SERIALIZE_BIBTEX_OPTION_FIRST_LAST}, {@link #SERIALIZE_BIBTEX_OPTION_GENERATED_BIBTEXKEYS}, {@link #SERIALIZE_BIBTEX_OPTION_PLAIN_MISCFIELD}.
	 * @param urlGenerator - to generate a proper URL pointing to the post. 
	 * 
	 * @return A string representation of the posts in BibTeX format.
	 */
	public static String toBibtexString(final Post<BibTex> post, final int flags, final URLGenerator urlGenerator) {
		post.getResource().addMiscField(ADDITIONAL_MISC_FIELD_BIBURL, urlGenerator.getPublicationUrl(post.getResource(), post.getUser()).toString());
		return toBibtexString(post, flags);
	}
	
	/**
	 * Same as {@link #toBibtexString(Post, int, URLGenerator)} but with the 
	 * default flags (=0)
	 * 
	 * @param post
	 * @param urlGenerator
	 * @return A string representation of the posts in BibTeX format.
	 */
	public static String toBibtexString(final Post<BibTex> post, final URLGenerator urlGenerator) {
		return toBibtexString(post, 0, urlGenerator);
	}


	/**
	 * Return a BibTeX representation of the given post. By default, 
	 * the contained misc fields are parsed before the BibTeX string is generated,
	 * the authors and editors are in "Last, First" form, and the BibTeX key is 
	 * untouched. 
	 * 
	 * @param post - a post
	 * @return - a bibtex string representation of this post.
	 */
	public static String toBibtexString(final Post<BibTex> post) {
		return toBibtexString(post, 0);
	}

	/**
	 * Creates a BibTeX string containing more than only the fields in the 
	 * BibTeX object:
	 * 
	 * <ul>
	 * 		<li>tags in the <code>keywords</code> field</li>
	 *      <li>description in the <code>description</code> field</li>
	 * </ul>
	 * 
	 * @param post - a BibTeX post.
	 * @param flags - flags to change the serialization behavior. A bit mask 
	 * that may include {@link #SERIALIZE_BIBTEX_OPTION_FIRST_LAST}, {@link #SERIALIZE_BIBTEX_OPTION_GENERATED_BIBTEXKEYS}, {@link #SERIALIZE_BIBTEX_OPTION_PLAIN_MISCFIELD}.
	 * 
	 * @return A string representation of the post in BibTeX format.
	 */
	public static String toBibtexString(final Post<BibTex> post, final int flags) {
		final BibTex bib = post.getResource();	
		/*
		 * add additional fields.
		 *  
		 * ATTENTION: if you add fields here, you have to add them also 
		 * (in SimpleBibTeXParser.updateWithParsedBibTeX!)
		 * in ADDITIONAL_MISC_FIELDS. Thus when someone enters a bibtex field with the 
		 * name of your added field, it will not be stored in the misc section.
		 */
		bib.addMiscField(ADDITIONAL_MISC_FIELD_KEYWORDS, TagUtils.toTagString(post.getTags(), " "));
		if (present(post.getDescription())) {
			bib.addMiscField(ADDITIONAL_MISC_FIELD_DESCRIPTION, post.getDescription());
		}
		if (present(post.getDate())) {
			bib.addMiscField(ADDITIONAL_MISC_FIELD_ADDED_AT, DATE_FORMAT.format(post.getDate()));
		}
		if (present(post.getChangeDate())) {
			bib.addMiscField(ADDITIONAL_MISC_FIELD_TIMESTAMP, DATE_FORMAT.format(post.getDate()));
		}
		return toBibtexString(bib, flags);
	}
	
	private static BibtexFile parseBibtex(String bibtext, BibtexParser parser) throws IOException, bibtex.parser.ParseException {
		BibtexFile file = new BibtexFile();
		BufferedReader sr = new BufferedReader(new StringReader(bibtext));
		// parse source
		parser.parse(file, sr);
		return file;
	}
	
	/**
	 * For each BibTeX entry generate and add a key if not present yet.
	 * 
	 * @param bibtex
	 * @return the bibtex with generated keys where missing
	 * @throws IOException
	 * @throws bibtex.parser.ParseException 
	 */
	public static String addBibtexKeyIfNotPresent(String bibtex) throws IOException, bibtex.parser.ParseException {
		BibtexFile bibFile = parseBibtex(bibtex, new BibtexParser(true));
		@SuppressWarnings("unchecked")
		List<BibtexAbstractEntry> entries = bibFile.getEntries();
		for (BibtexAbstractEntry entry : entries) {
			if (entry instanceof BibtexEntry) {
				String key = ((BibtexEntry) entry).getEntryKey();
				if (present(key)) continue;
				@SuppressWarnings({ "rawtypes", "unchecked" })
				Map fields = new HashMap(((BibtexEntry) entry).getFields());
				BibtexString author = (BibtexString) fields.get("author");
				BibtexString editor = (BibtexString)fields.get("editor");
				BibtexString year = (BibtexString)fields.get("year");
				BibtexString title = (BibtexString) fields.get("title");
				key = generateBibtexKey(
						author == null ? "" : author.getContent(),
						editor == null ? "" : editor.getContent(),
						year == null ? "" : year.getContent(),
						title == null ? "" : title.getContent());
				((BibtexEntry) entry).setEntryKey(key);
			}
		}
		return bibFile.toString();
	}

	/**
	 * @see #generateBibtexKey(List, List, String, String)
	 * @param bib
	 * 
	 * @return The generated BibTeX key.
	 */
	public static String generateBibtexKey(final BibTex bib) {
		if (bib == null) return "";
		return generateBibtexKey(bib.getAuthor(), bib.getEditor(), bib.getYear(), bib.getTitle());
	}

	/**
	 * Generates a BibTeX key for the given strings. Please use {@link #generateBibtexKey(List, List, String, String)}, 
	 * if authors and editors are available as list.  
	 * 
	 * @param authors
	 * @param editors
	 * @param year
	 * @param title
	 * @return The generated BibTeX key.
	 */
	public static String generateBibtexKey(final String authors, final String editors, final String year, final String title) {
		return generateBibtexKey(PersonNameUtils.discoverPersonNamesIgnoreExceptions(authors), PersonNameUtils.discoverPersonNamesIgnoreExceptions(editors), year, title);
	}
	
	/**
	 * Generates a bibtex key of the form "first persons lastname from authors
	 * or editors" or "noauthororeditor" concatenated with year.
	 * 
	 * @param authors
	 *            some string representation of the list of authors with their
	 *            first- and lastnames
	 * @param editors
	 *            some string representation of the list of editors with their
	 *            first- and lastnames
	 * @param year
	 * @param title
	 * @return a bibtex key for a bibtex with the fieldvalues given by arguments
	 */
	public static String generateBibtexKey(final List<PersonName> authors, final List<PersonName> editors, final String year, final String title) {
		/*
		 * TODO: pick either author or editor. CHECK for null values.
		 * What to do with Chinese authors and other broken names?
		 * How to extract the first RELEVANT word of the title?
		 * remove Sonderzeichen, LaTeX markup!
		 */
		final StringBuilder buffer = new StringBuilder();

		/* get author */
		String first = PersonNameUtils.getFirstPersonsLastName(authors);
		if (first == null) {
			first = PersonNameUtils.getFirstPersonsLastName(editors);
			if (first == null) {
				first = "noauthororeditor";
			}
		}
		buffer.append(first);

		/* the year */ 
		if (year != null) {
			buffer.append(year.trim());
		}

		/* first relevant word of the title */
		if (title != null) {
			/* best guess: pick first word with more than 4 characters, longest first word */
			// FIXME: what do we want to do inside this if statement?
			buffer.append(getFirstRelevantWord(title).toLowerCase());
		}

		return StringUtils.foldToASCII(buffer.toString().toLowerCase()).replaceAll("[^a-z0-9]", "");
	}

	/**
	 * Relevant = longer than four characters (= 0-9a-z)
	 * 
	 * @param title
	 * @return
	 */
	private static String getFirstRelevantWord(final String title) {
		final String[] split = title.split("\\s");
		for (final String s : split) {
			final String ss = s.replaceAll("[^a-zA-Z0-9]", "");
			if (ss.length() > 4) {
				return ss;
			}
		}
		return "";
	}



	/**
	 * Cleans up a string containing LaTeX markup
	 * 
	 * @param bibtex a bibtex string
	 * @return the cleaned bibtex string
	 */
	public static String cleanBibTex(String bibtex) {
		if (!present(bibtex)) return "";			

		// replace markup
		bibtex = bibtex.replaceAll("\\\\[a-z]+\\{([^\\}]+)\\}", "$1");  // \\markup{marked_up_text}		

		// decode Latex macros into unicode characters
		return TexDecode.decode(bibtex).trim();
	}

	/**
	 * Tries to find a year (four connected digits) in a string and returns them as int.
	 * If it fails, returns Integer.MAX_VALUE.
	 * 
	 * @param year
	 * @return an integer representation of the year, or Integer.MAX_VALUE if it fails
	 */
	public static int getYear(final String year) {
		try {
			return Integer.parseInt(year);
		} catch (final NumberFormatException ignore) {
			/*
			 * try to get four digits ...
			 */
			final Matcher m = YEAR_PATTERN.matcher(year);
			if (m.find()) {
				return Integer.parseInt(m.group());
			}
		}
		return Integer.MAX_VALUE;
	}

	/**
	 * Sort a list of bibtex posts (and eventually remove duplicates).
	 * 
	 * @param bibtexList
	 * @param sortKeys
	 * @param sortOrders
	 */
	public static void sortBibTexList(final List<Post<BibTex>> bibtexList, final List<SortKey> sortKeys, final List<SortOrder> sortOrders) {
		if (present(bibtexList) && bibtexList.size() > 1) {
			Collections.sort(bibtexList, new BibTexPostComparator(sortKeys, sortOrders));
		}
	}

	/**
	 * Sorts a list of bibtex posts and removes duplicates.
	 * 
	 * @param bibtexList
	 */
	public static void removeDuplicates(final List<Post<BibTex>> bibtexList) {
		final Set<Post<BibTex>> temp = new TreeSet<Post<BibTex>>(new BibTexPostInterhashComparator());
		temp.addAll(bibtexList);
		// FIXME: a bit cumbersome at this point - but we need to work on the bibtexList
		bibtexList.clear();
		bibtexList.addAll(temp);
	}
	
	
	/**
	 * Merge duplicates within a list of publication posts. Returns a list of publications
	 * with unique interhashes; tags of duplicates (according to the interhash) are aggregated. 
	 * 
	 * @param publicationList
	 */
	public static void mergeDuplicates(final List<Post<BibTex>> publicationList) {
		Map<String,Post<BibTex>> hashToPost = new HashMap<String, Post<BibTex>>();
		for (Post<BibTex> post : publicationList) {
			// add merged:USERNAME tag to indicate all users who own the post
			post.addTag(MERGED_PREFIX + post.getUser().getName());
			final String hash = post.getResource().getInterHash();
			// create new map entry, if not yet present
			if (! hashToPost.containsKey(hash)) {				
				hashToPost.put(hash, post);
			}
			else {
				// add all tags to existing post in map
				hashToPost.get(hash).getTags().addAll(post.getTags()); 				
			}
		}
		publicationList.clear();
		publicationList.addAll(hashToPost.values());
 	}
	
	
	
	/**
	 * 
	 * @param bibtexList
	 */
	public static void aggregateDuplicates(final List<Post<BibTex>> bibtexList) {
		
	}


	/** Adds the field <code>fieldName</code> to the BibTeX entry, if the entry 
	 * does not already contain it.
	 * 
	 * @param bibtex - the BibTeX entry
	 * @param fieldName - the name of the field
	 * @param fieldValue - the value of the field
	 * 
	 * @return The new BibTeX entry.
	 */
	public static String addFieldIfNotContained(final String bibtex, final String fieldName, final String fieldValue) {
		if (bibtex == null) return bibtex;

		final StringBuffer buf = new StringBuffer(bibtex);
		addFieldIfNotContained(buf, fieldName, fieldValue);
		return buf.toString();
	}

	/** Adds the field <code>fieldName</code> to the BibTeX entry, if the entry 
	 * does not already contain it.
	 * 
	 * @param bibtex - the BibTeX entry
	 * @param fieldName - the name of the field
	 * @param fieldValue - the value of the field
	 * 
	 */
	public static void addFieldIfNotContained(final StringBuffer bibtex, final String fieldName, final String fieldValue) {
		if (bibtex == null) return;
		/*
		 * it seems, we can do regex stuff only on strings ... so we have 
		 * to convert the buffer into a string :-(
		 */
		final String bibtexString = bibtex.toString();
		/*
		 * The only way safe to find out if the entry already contains
		 * the field is to parse it. This is expensive, thus we only 
		 * do simple heuristics, which is of course, error prone! 
		 */
		if (!bibtexString.matches("(?s).*" + fieldName + "\\s*=\\s*.*")) {
			/*
			 * add the field at the end before the last brace
			 */
			addField(bibtex, fieldName, fieldValue);
		}
	}

	/** Adds the given field at the end of the given BibTeX entry by placing
	 * it before the last brace. 
	 * 
	 * @param bibtex - the BibTeX entry
	 * @param fieldName - the name of the field
	 * @param fieldValue - the value of the field
	 */
	public static void addField(final StringBuffer bibtex, final String fieldName, final String fieldValue) {
		/*
		 * ignore empty bibtex and empty field values
		 */
		if (bibtex == null || fieldValue == null || fieldValue.trim().equals("")) return;

		/*
		 * remove last comma if there is one (before closing last curly bracket)
		 */
		final String bib = bibtex.toString().trim();
		final Matcher m = LAST_COMMA_PATTERN.matcher(bib);

		if (m.matches()) {
			final int _lastIndex = bib.lastIndexOf(",");
			bibtex.replace(_lastIndex, _lastIndex + 1, "");
		}

		final int lastIndexOf = bibtex.lastIndexOf("}");
		if (lastIndexOf > 0) {
			bibtex.replace(lastIndexOf, bibtex.length(), "," + fieldName + " = {" + fieldValue + "}\n}");
		}
	}

	/**
	 * Converts the key = value pairs contained in the 
	 * miscFields map of a {@link BibTex} object into a serialized representation
	 * in the misc-Field. It appends 
	 * 
	 *  key1 = {value1}, key2 = {value2}, ...
	 *  
	 * for all defined miscFields to the return string.
	 * 
	 * @param valueMap - a map containing key/value pairs
	 * @return - a string representation of the given object.
	 */
	public static String serializeMapToBibTeX(final Map<String, String> valueMap) {
		final StringBuilder miscFieldsSerialized = new StringBuilder();
		// loop over misc fields, if any
		if (present(valueMap)) {
			final Iterator<String> it = valueMap.keySet().iterator();
			while (it.hasNext()) {				
				final String currKey = it.next();
				miscFieldsSerialized.append(DEFAULT_INTENDATION + currKey.toLowerCase() + " " + ASSIGNMENT_OPERATOR + " " + DEFAULT_OPENING_BRACKET + valueMap.get(currKey) + DEFAULT_CLOSING_BRACKET);
				if (it.hasNext()) {
					miscFieldsSerialized.append(KEYVALUE_SEPARATOR + "\n");
				}
			}

		}
		// write serialized misc fields into misc field
		return miscFieldsSerialized.toString();				
	}


	/**
	 * Parse a given misc field string into a hashmap containing key/value pairs.
	 * 
	 * @param miscFieldString - the misc field string
	 * @return a hashmap containg the parsed key/value pairs.
	 */
	public static Map<String,String> parseMiscFieldString(final String miscFieldString) {
		return StringUtils.parseBracketedKeyValuePairs(miscFieldString, ASSIGNMENT_OPERATOR, KEYVALUE_SEPARATOR, DEFAULT_OPENING_BRACKET, DEFAULT_CLOSING_BRACKET);		
	}

    /**
     * Indicates whether a particular flag is set or not.
     * @param flags - the flags where we look if testFlag is set
     * @param testFlag - the flag we want to find in flags
     */
    private static boolean hasFlag(final int flags, final int testFlag) {
        return (flags & testFlag) != 0;
    }
}