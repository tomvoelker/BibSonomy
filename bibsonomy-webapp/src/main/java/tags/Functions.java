/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tags;

import static org.bibsonomy.model.util.BibTexUtils.ENTRYTYPES;
import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.markup.MyOwnSystemTag;
import org.bibsonomy.database.systemstags.markup.ReportedSystemTag;
import org.bibsonomy.model.Author;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.EndnoteUtils;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.util.DateTimeUtils;
import org.bibsonomy.util.EnumUtils;
import org.bibsonomy.util.JSONUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.XmlUtils;
import org.bibsonomy.util.id.DOIUtils;
import org.bibsonomy.web.spring.converter.StringToEnumConverter;
import org.bibsonomy.webapp.command.BaseCommand;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.caja.util.Sets;
import com.sksamuel.diffpatch.DiffMatchPatch;
import com.sksamuel.diffpatch.DiffMatchPatch.Diff;

/**
 * TODO: move to org.bibsonomy.webapp.util.tags package
 * 
 * Some taglib functions
 * 
 * @author Dominik Benz
 */
public class Functions {
	private static final Log log = LogFactory.getLog(Functions.class);

	// contains special characters, symbols, etc...
	private static final Properties chars = new Properties();

	// used to generate URLs
	private static URLGenerator urlGenerator;

	private static final DateTimeFormatter ISO8601_FORMAT_HELPER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");

	private static final DateTimeFormatter myDateFormatter = DateTimeFormat.forPattern("MMMM yyyy");

	private static final DateTimeFormatter myDateFormat = DateTimeFormat.forPattern("yyyy-MM");
	private static final DateTimeFormatter dmyDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");

	private static final DateTimeFormatter W3CDTF_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

	private static final DateTimeFormatter MEMENTO_FORMAT = DateTimeFormat.forPattern("yyyyMMddHHmm");

	/*
	 * used by computeTagFontSize.
	 * 
	 * - scalingFactor: Controls difference between smallest and largest tag
	 * (size of largest: 90 -> 200% font size; 40 -> ~170%; 20 -> ~150%; all for
	 * offset = 10) - offset: controls size of smallest tag ( 10 -> 100%) -
	 * default: default tag size returned in case of an error during computation
	 */
	private static final int TAGCLOUD_SIZE_SCALING_FACTOR = 45;
	private static final int TAGCLOUD_SIZE_OFFSET = 10;
	private static final int TAGCLOUD_SIZE_DEFAULT = 100;

	// load special characters
	static {
		try {
			chars.load(Functions.class.getClassLoader().getResourceAsStream("chars.properties"));
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * lookup a special character
	 * 
	 * @param key
	 * @return String
	 */
	public static String ch(final String key) {
		if (chars.getProperty(key) != null) {
			return chars.getProperty(key);
		}
		return "???" + key + "???";
	}

	/**
	 * Normalizes input string according to Unicode Standard Annex #15
	 * 
	 * @param str
	 * @param decomp
	 *        one of NFC, NFD, NFKC, NFKD @see Normalizer.Form
	 * @return normalized String
	 */
	@Deprecated
	// TODO: remove with old layout
	public static String normalize(final String str, final String decomp) {
		Normalizer.Form form;
		try {
			form = Normalizer.Form.valueOf(decomp);
		} catch (final Exception e) {
			form = Normalizer.Form.NFD;
		}
		return Normalizer.normalize(str + form.toString(), form);
	}

	/**
	 * replaces occurrences of whitespace in the by only one occurrence of the
	 * respective whitespace character
	 * 
	 * @param s
	 *        a String
	 * @return trimmed String
	 */
	public static String trimWhiteSpace(final String s) {
		/*
		 * remove empty lines
		 */
		return s.replaceAll("(?m)\n\\s*\n", "\n");
	}

	/**
	 * Removes all "non-trivial" characters from the file name. If the file name
	 * is empty "export" is returned
	 * 
	 * @param file
	 *        a file name
	 * @return cleaned file name
	 */
	public static String makeCleanFileName(final String file) {
		if (!present(file)) {
			return "export";
		}

		return UrlUtils.safeURIDecode(file).replaceAll("[^a-zA-Z0-9-_]", "_");
	}

	/**
	 * wrapper for {@link UrlUtils#safeURIDecode(String)}
	 * 
	 * @param uri
	 *        a URI string
	 * @return the decoded URI string
	 */
	public static String decodeURI(final String uri) {
		return UrlUtils.safeURIDecode(uri);
	}

	/**
	 * wrapper for {@link UrlUtils#safeURIEncode(String)}
	 * 
	 * @param uri
	 *        a URI string
	 * @return the encoded URI string
	 */
	public static String encodeURI(final String uri) {
		return UrlUtils.safeURIEncode(uri);
	}

	/**
	 * converts a collection of tags into a space-separated string of tags
	 * 
	 * @param tags
	 *        a list of tags
	 * @return a space-separated string of tags
	 */
	public static String toTagString(final Collection<Tag> tags) {
		return TagUtils.toTagString(tags, " ");
	}

	/**
	 * get the Path component of a URI string
	 * 
	 * @param uriString
	 *        a URI string
	 * @return the path component of the given URI string
	 */
	public static String getPath(final String uriString) {
		try {
			return new URI(UrlUtils.encodeURLExceptReservedChars(uriString)).getPath();
		} catch (final Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Cuts the last segment of the url string until last slash. TODO: If the
	 * path contains more than three slashes, then the cut is after the third
	 * slash. (Previous to adding this restrictions, tags that included a slash
	 * could not be handled on /user/USER/TAG; remove as soon as the bug in
	 * urlrewrite lib is fixed
	 * 
	 * @param uriString
	 *        the url
	 * @return last segment of the url string until last slash
	 */
	public static String getLowerPath(final String uriString) {

		final int count = org.apache.commons.lang.StringUtils.countMatches(uriString, "/");

		final int lio;
		if (count > 2) {
			lio = uriString.indexOf("/", uriString.indexOf("/") + 1);
			// lio = uriString.indexOf("/", uriString.indexOf("/") + 1);
		} else {
			lio = uriString.lastIndexOf("/");
		}

		if (lio > 0) {
			try {
				/*
				 * FIXME: why do we wrap the result (which is a path!) into a
				 * URI to then extract the path again?
				 */
				return new URI(UrlUtils.encodeURLExceptReservedChars(uriString.substring(0, lio))).getPath();
			} catch (final Exception ex) {
				// ignore
			}
		}
		return "";
	}

	/**
	 * extract query part of given URI string, within a leading "?"
	 * 
	 * @param uriString
	 *        a URI string
	 * @return query part of the given URI string, within a leading "?"
	 */
	public static String getQuery(final String uriString) {
		try {
			final URI uri = new URI(UrlUtils.encodeURLExceptReservedChars(uriString));
			final String query = uri.getQuery();
			if (present(query)) {
				return "?" + query;
			}
			return "";
		} catch (final Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @param url
	 *        the url to check
	 * @return <code>true</code> iff the url is a link to a pdf or ps file
	 */
	public static boolean isLinkToDocument(final String url) {
		return StringUtils.matchExtension(url, FileLogic.DOCUMENT_EXTENSIONS);
	}

	/**
	 * Computes font size for given tag frequency and maximum tag frequency
	 * inside tag cloud.
	 * 
	 * This is used as attribute font-size=X%. We expect 0 < tagMinFrequency <=
	 * tagFrequency <= tagMaxFrequency. We return a value between 200 and 300 if
	 * tagsizemode=popular, and between 100 and 200 otherwise.
	 * 
	 * @param tagFrequency
	 *        - the frequency of the tag
	 * @param tagMinFrequency
	 *        - the minimum frequency within the tag cloud
	 * @param tagMaxFrequency
	 *        - the maximum frequency within the tag cloud
	 * @param tagSizeMode
	 *        - which kind of tag cloud is to be done (the one for the
	 *        popular tags page vs. standard)
	 * @return font size for the tag cloud with the given parameters
	 */
	public static Integer computeTagFontsize(final Integer tagFrequency, final Integer tagMinFrequency, final Integer tagMaxFrequency, final String tagSizeMode) {
		try {
			Double size = ((tagFrequency.doubleValue() - tagMinFrequency) / (tagMaxFrequency - tagMinFrequency)) * TAGCLOUD_SIZE_SCALING_FACTOR;
			if ("popular".equals(tagSizeMode)) {
				size *= 10;
			}
			size += TAGCLOUD_SIZE_OFFSET;
			size = Math.log10(size);
			size *= 100;
			return size.intValue() == 0 ? TAGCLOUD_SIZE_DEFAULT : size.intValue();
		} catch (final Exception ex) {
			return TAGCLOUD_SIZE_DEFAULT;
		}
	}

	/**
	 * Wrapper for org.bibsonomy.util.UrlUtils.cleanUrl
	 * 
	 * @see org.bibsonomy.util.UrlUtils
	 * @param url
	 * @return the cleaned url
	 */
	public static String cleanUrl(final String url) {
		return UrlUtils.cleanUrl(url);
	}

	/**
	 * wrapper for for org.bibsonomy.util.UrlUtils.setParam
	 * 
	 * @param url
	 *        an url string
	 * @param paramName
	 *        parameter name
	 * @param paramValue
	 *        parameter value
	 * @return an url string with the requested parameter set
	 */
	public static String setParam(final String url, final String paramName, final String paramValue) {
		if (url == null) {
			return url;
		}
		return UrlUtils.setParam(url, paramName, paramValue);
	}

	/**
	 * wrapper for for org.bibsonomy.util.UrlUtils.removeParam
	 * 
	 * @param url
	 *        - a url string
	 * @param paramName
	 *        - a parameter to be removed
	 * @return the given url string with the parameter removed
	 */
	public static String removeParam(final String url, final String paramName) {
		return UrlUtils.removeParam(url, paramName);
	}

	/**
	 * wrapper for org.bibsonomy.model.util.BibTexUtils.cleanBibtex
	 * 
	 * @see org.bibsonomy.model.util.BibTexUtils#cleanBibTex(String)
	 * @param bibtex
	 * @return the clean bibtex string
	 */
	public static String cleanBibtex(final String bibtex) {
		return BibTexUtils.cleanBibTex(bibtex);
	}

	/**
	 * returns the SpamStatus as string for admin pages
	 * 
	 * @param id
	 *        id of the spammer state
	 * @return string representation
	 */
	public static String getPredictionString(final Integer id) {
		return SpamStatus.getStatus(id).toString();
	}

	/**
	 * Retrieves if given status is a spammer status
	 * 
	 * @param id
	 * @return <code>true</code> iff given status is a spammer status
	 */
	public static Boolean isSpammer(final Integer id) {
		final SpamStatus status = SpamStatus.getStatus(id);
		return SpamStatus.isSpammer(status);
	}

	/**
	 * returns a map of key-value:
	 * new bibTex and the old one are compared according to each field,
	 * keys are the fields which have different values
	 * 
	 * @param newPost
	 * @param oldPost
	 * @return
	 */
	public static Map<String, String> diffEntries(final Post<? extends Resource> newPost, final Post<? extends Resource> oldPost) {
		final Map<String, String> diffMap = new LinkedHashMap<String, String>();
		diffEntriesPost(newPost, oldPost, diffMap);

		if (oldPost.getResource() instanceof BibTex) {
			final BibTex newBib = (BibTex) newPost.getResource();
			final BibTex oldBib = (BibTex) oldPost.getResource();

			diffPersonEntry(diffMap, "author", newBib.getAuthor(), oldBib.getAuthor());
			diffPersonEntry(diffMap, "editor", newBib.getEditor(), oldBib.getEditor());

			diffStringEntry(diffMap, "entrytype", newBib.getEntrytype(), oldBib.getEntrytype());
			diffStringEntry(diffMap, "year", newBib.getYear(), oldBib.getYear());

			diffStringEntry(diffMap, "booktitle", newBib.getBooktitle(), oldBib.getBooktitle());
			diffStringEntry(diffMap, "journal", newBib.getJournal(), oldBib.getJournal());
			diffStringEntry(diffMap, "volume", newBib.getVolume(), oldBib.getVolume());
			diffStringEntry(diffMap, "number", newBib.getNumber(), oldBib.getNumber());
			diffStringEntry(diffMap, "pages", newBib.getPages(), oldBib.getPages());
			diffStringEntry(diffMap, "month", newBib.getMonth(), oldBib.getMonth());
			diffStringEntry(diffMap, "day", newBib.getDay(), oldBib.getDay());
			diffStringEntry(diffMap, "publisher", newBib.getPublisher(), oldBib.getPublisher());
			diffStringEntry(diffMap, "address", newBib.getAddress(), oldBib.getAddress());
			diffStringEntry(diffMap, "edition", newBib.getEdition(), oldBib.getEdition());
			diffStringEntry(diffMap, "chapter", newBib.getChapter(), oldBib.getChapter());
			diffStringEntry(diffMap, "url", newBib.getUrl(), oldBib.getUrl());
			diffStringEntry(diffMap, "key", newBib.getKey(), oldBib.getKey());
			diffStringEntry(diffMap, "howpublished", newBib.getHowpublished(), oldBib.getHowpublished());
			diffStringEntry(diffMap, "institution", newBib.getInstitution(), oldBib.getInstitution());
			diffStringEntry(diffMap, "organization", newBib.getOrganization(), oldBib.getOrganization());
			diffStringEntry(diffMap, "school", newBib.getSchool(), oldBib.getSchool());
			diffStringEntry(diffMap, "series", newBib.getSeries(), oldBib.getSeries());
			diffStringEntry(diffMap, "crossref", newBib.getCrossref(), oldBib.getCrossref());
			diffStringEntry(diffMap, "misc", newBib.getMisc(), oldBib.getMisc());
			diffStringEntry(diffMap, "bibtexAbstract", newBib.getAbstract(), oldBib.getAbstract());
			diffStringEntry(diffMap, "privnote", newBib.getPrivnote(), oldBib.getPrivnote());
			diffStringEntry(diffMap, "annote", newBib.getAnnote(), oldBib.getAnnote());
			diffStringEntry(diffMap, "note", newBib.getNote(), oldBib.getNote());
		} else {
			diffStringEntry(diffMap, "url", ((Bookmark) newPost.getResource()).getUrl(), ((Bookmark) oldPost.getResource()).getUrl());
		}

		return diffMap;
	}

	private static void diffStringEntry(final Map<String, String> diffMap, final String key, final String newString, final String oldString) {
		// TODO: do we really want to use cleanbibtex here?
		if (!cleanBibtex(newString).equals(cleanBibtex(oldString))) {
			diffMap.put(key, compareString(newString, oldString));
		}
	}

	private static void diffPersonEntry(final Map<String, String> diffMap, final String key, final List<PersonName> newList, final List<PersonName> oldList) {
		if (present(newList) || present(oldList)) {
			final String newListAsString = present(newList) ? PersonNameUtils.serializePersonNames(newList, false, ", ") : "";
			final String oldListAsString = present(oldList) ? PersonNameUtils.serializePersonNames(oldList, false, ", ") : "";

			if (!newListAsString.equals(oldListAsString)) {
				diffMap.put(key, compareString(newListAsString, oldListAsString));
			}
		}
	}

	/**
	 * @param newPost
	 * @param oldPost
	 * @param diffMap
	 */
	public static void diffEntriesPost(final Post<? extends Resource> newPost, final Post<? extends Resource> oldPost, final Map<String, String> diffMap) {
		final Resource newResource = newPost.getResource();
		final Resource oldResource = oldPost.getResource();

		diffStringEntry(diffMap, "title", newResource.getTitle(), oldResource.getTitle());
		diffStringEntry(diffMap, "description", newPost.getDescription(), oldPost.getDescription());
		
		if (!newPost.getTags().equals(oldPost.getTags())) {
			diffMap.put("tags", compareTagSets(newPost.getTags(), oldPost.getTags()));
		}
		if (!newPost.getGroups().equals(oldPost.getGroups())) {
			diffMap.put("groups", diffGroupSetEntry(newPost.getGroups(), oldPost.getGroups()));
		}
	}
	
	/**
	 * TODO: how are groups sorted?
	 * 
	 * @param groups1
	 * @param groups2
	 */
	private static String diffGroupSetEntry(Set<Group> groups1, Set<Group> groups2) {
		final StringBuilder newSetAsString = new StringBuilder();
		final StringBuilder oldSetAsString = new StringBuilder();
		
		for (Group group : groups1) {
			newSetAsString.append(group.getName());
			newSetAsString.append(" ");
		}
		
		for (Group group : groups2) {
			oldSetAsString.append(group.getName());
			oldSetAsString.append(" ");
		}
		
		return compareString(newSetAsString.toString().trim(), oldSetAsString.toString().trim());
	}

	private static String compareTagSets(final Set<Tag> newTags, final Set<Tag> oldTags) {
		final String commonTags = toTagString(Sets.intersection(newTags, oldTags));
		final String addedTags = toTagString(Sets.difference(newTags, oldTags));
		final String deletedTags = toTagString(Sets.difference(oldTags, newTags));
		return compareString(commonTags + ((present(commonTags) && present(addedTags)) ? " " : "") + addedTags, commonTags + ((present(commonTags) && present(deletedTags)) ? " " : "") + deletedTags);
	}

	/**
	 * Compares two strings character-based.
	 * 
	 * @param newValue and oldValue
	 * @param oldValue
	 * @return The difference between two strings. (inserted: green, deleted:
	 *         red, not_changed: black)
	 */
	public static String compareString(String newValue, String oldValue) {

		if (newValue == null) {
			newValue = " ";
		}
		if (oldValue == null) {
			oldValue = " ";
		}
		final DiffMatchPatch dmp = new DiffMatchPatch();

		// computes the diff
		final LinkedList<Diff> d = dmp.diff_main(newValue, oldValue);

		// cleans the result so that be more human readable.
		dmp.diff_cleanupSemantic(d);

		// applies appropriate colors to the result. (red, green)
		return customized_diff_prettyHtml(d);

	}

	// TODO: move to view layer
	@Deprecated
	public static String customized_diff_prettyHtml(final LinkedList<Diff> diffs) {
		final StringBuilder html = new StringBuilder();
		for (final Diff aDiff : diffs) {
			final String text = aDiff.text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "&para;<br>");
			switch (aDiff.operation) {
			case INSERT:
				html.append("<span style=\"background:#e6ffe6;\">").append(text).append("</span>");
				break;
			case DELETE:
				html.append("<del style=\"background:#ffe6e6;\">").append(text).append("</del>");
				break;
			case EQUAL:
				html.append("<span>").append(text).append("</span>");
				break;
			}
		}
		return html.toString();
	}

	/**
	 * TODO: remove!? Compares two strings word-based. (Maybe usefull in
	 * future!)
	 * 
	 * @param newValue
	 *        and oldValue
	 * @return The difference between two strings. (inserted: green, deleted:
	 *         red, not_changed)
	 */
	/*
	 * public static String compareString(String newValue, String oldValue) {
	 * 
	 * 
	 * if(newValue == null){
	 * newValue=" ";
	 * }
	 * if(oldValue == null){
	 * oldValue =" ";
	 * }
	 * diff_match_patch dmp = new diff_match_patch();
	 * 
	 * //split the texts based on words
	 * LinesToCharsResult a = dmp.diff_linesToWords(newValue, oldValue);
	 * 
	 * String lineText1 = a.chars1;
	 * String lineText2 = a.chars2;
	 * List<String> lineArray = a.lineArray;
	 * 
	 * LinkedList<Diff> diffs = dmp.diff_main(lineText1, lineText2, false);
	 * 
	 * dmp.diff_charsToLines(diffs, lineArray);
	 * 
	 * //cleans the result so that be more human readable.
	 * dmp.diff_cleanupSemantic(diffs);
	 * 
	 * //applies appropriate colors to the result. (red, green)
	 * return dmp.diff_prettyHtml(diffs);
	 * }
	 */
	/**
	 * Quotes a String such that it is usable for JSON.
	 * 
	 * @param value
	 * @return The quoted String.
	 */
	public static String quoteJSON(final String value) {
		return JSONUtils.quoteJSON(value);
	}

	/**
	 * First, replaces certain BibTex characters, and then quotes JSON relevant
	 * characters.
	 * 
	 * @param value
	 * @return The cleaned String.
	 */
	public static String quoteJSONcleanBibTeX(final String value) {
		return JSONUtils.quoteJSON(BibTexUtils.cleanBibTex(value));
	}

	/**
	 * @return The list of available bibtex entry types
	 */
	public static String[] getBibTeXEntryTypes() {
		return ENTRYTYPES;
	}

	/**
	 * Maps BibTeX entry types to SWRC entry types.
	 * 
	 * @param bibtexEntryType
	 * @return the SWRC entry type
	 */
	public static String getSWRCEntryType(final String bibtexEntryType) {
		return EndnoteUtils.getSWRCEntryType(bibtexEntryType);
	}

	/**
	 * Maps BibTeX entry types to RIS entry types.
	 * 
	 * @param bibtexEntryType
	 * @return The RIS entry type
	 */
	public static String getRISEntryType(final String bibtexEntryType) {
		return EndnoteUtils.getRISEntryType(bibtexEntryType);
	}

	/**
	 * returns the css Class for a given tag
	 * 
	 * @param tagCount
	 *        the count aof the current Tag
	 * @param maxTagCount
	 *        the maximum tag count
	 * @return the css class for the tag
	 */
	public static String getTagSize(final Integer tagCount, final Integer maxTagCount) {
		/*
		 * catch incorrect values
		 */
		if ((tagCount == 0) || (maxTagCount == 0)) {
			return "tagtiny";
		}

		final int percentage = ((tagCount * 100) / maxTagCount);

		if (percentage < 25) {
			return "tagtiny";
		} else if ((percentage >= 25) && (percentage < 50)) {
			return "tagnormal";
		} else if ((percentage >= 50) && (percentage < 75)) {
			return "taglarge";
		} else if (percentage >= 75) {
			return "taghuge";
		}

		return "";
	}

	/**
	 * Calculates the percentage of font size for clouds of author names
	 * 
	 * @param author
	 * @param maxCount
	 * 
	 * @return value between 0 and 100 %
	 */
	public static double authorFontSize(final Author author, final Integer maxCount) {
		return ((author.getCtr() * 100) / (maxCount / 2)) + 50;
	}

	/**
	 * @param count
	 * @return the % of r g and b
	 */
	public static int otherPeopleColor(int count) {
		// set maximum
		if (count > 1024) {
			count = 1024;
		}
		return (int) (100.0 - Math.log((count / Math.log(2)) * 2.0));
	}

	/**
	 * Returns the host name of a URL.
	 * 
	 * @param urlString
	 *        - the URL as string
	 * @return The host name of the URL.
	 */
	public static String getHostName(final String urlString) {
		try {
			return new URL(urlString).getHost();
		} catch (final MalformedURLException ex) {
			return "unknownHost";
		}
	}

	/**
	 * Returns a short (max. 160 characters) description of the post.
	 * 
	 * @param post
	 * @return A short description of the post.
	 */
	public static String shortPublicationDescription(final Post<BibTex> post) {
		final StringBuilder buf = new StringBuilder();
		final BibTex resource = post.getResource();
		if (resource != null) {
			final String title = resource.getTitle();
			if (title != null) {
				buf.append(shorten(title, 50));
			}

			final String author = PersonNameUtils.serializePersonNames(resource.getAuthor());
			if (present(author)) {
				buf.append(", " + shorten(author, 20));
			}

			final String year = resource.getYear();
			if (year != null) {
				buf.append(", " + shorten(year, 4));
			}
		}

		return buf.toString();
	}

	/**
	 * If the string is longer than <code>length</code>: shortens the given
	 * string to <code>length - 3</code> and appends <code>...</code>. Else:
	 * returns the string.
	 * 
	 * @param s
	 *        - the string
	 * @param length
	 *        - maximal length of the string
	 * @return The shortened string
	 */
	public static String shorten(final String s, final Integer length) {
		if ((s != null) && (s.length() > length)) {
			return s.substring(0, length - 3) + "...";
		}
		return s;
	}

	/**
	 * TODO: convert to tag to use the urlgenerator configured in
	 * bibsonomy2-servlet.xml Access the built-in utility function for BibTeX
	 * export
	 * 
	 * @param post
	 *        - a publication post
	 * @param projectHome
	 * @param lastFirstNames
	 *        - should person names appear in "Last, First" form?
	 * @param generatedBibtexKeys
	 *        - should the BibTeX keys be generated or the one from the
	 *        database?
	 * @return A BibTeX string of this post
	 */
	public static String toBibtexString(final Post<BibTex> post, final String projectHome, final Boolean lastFirstNames, final Boolean generatedBibtexKeys) {
		int flags = 0;
		if (!lastFirstNames) {
			flags |= BibTexUtils.SERIALIZE_BIBTEX_OPTION_FIRST_LAST;
		}
		if (generatedBibtexKeys) {
			flags |= BibTexUtils.SERIALIZE_BIBTEX_OPTION_GENERATED_BIBTEXKEYS;
		}
		if (urlGenerator == null) {
			urlGenerator = new URLGenerator(projectHome);
		}
		return BibTexUtils.toBibtexString(post, flags, urlGenerator) + "\n\n";
	}

	/**
	 * @param post
	 *        the post to be rendered
	 * @param skipDummyValues
	 *        whether to skip fields containing dummyValues like noauthor
	 * @return an endnote string
	 */
	public static String toEndnoteString(final Post<BibTex> post, final Boolean skipDummyValues) {
		return EndnoteUtils.toEndnoteString(post, skipDummyValues);
	}

	/**
	 * Formats the date to ISO 8601, e.g., 2012-11-07T14:43:16+0100
	 * 
	 * Currently Java's formatter doesn't support this standard therefore we can
	 * not use the fmt:formatDate tag with a pattern
	 * 
	 * @param date
	 * @return the formatted date
	 */
	public static String formatDateISO8601(final Date date) {
		if (present(date)) {
			try {
				return ISO8601_FORMAT_HELPER.print(new DateTime(date));
			} catch (final Exception e) {
				log.error("error while formating date to ISO8601", e);
				return "";
			}
		}
		return "";
	}

	/**
	 * Formats the date to RFC 1123, e.g., "Wed, 12 Mar 2013 12:12:12 GMT"
	 * (needed for Memento).
	 * 
	 * Currently Java's formatter doesn't support this standard therefore we can
	 * not use the fmt:formatDate tag with a pattern
	 * 
	 * @param date
	 * @return the formatted date
	 */
	public static String formatDateRFC1123(final Date date) {
		return DateTimeUtils.formatDateRFC1123(date);
	}

	/**
	 * Formats the date to W3CDTF, e.g., 2012-11-07T14:43:16+01:00 (needed for
	 * RSS feeds)
	 * 
	 * Currently Java's formatter doesn't support this standard therefore we can
	 * not use the fmt:formatDate tag with a pattern
	 * 
	 * @param date
	 * @return the formatted date
	 */
	public static String formatDateW3CDTF(final Date date) {
		if (present(date)) {
			return W3CDTF_FORMAT.print(new DateTime(date));
		}
		return "";
	}

	/**
	 * Formats the date for Memento, e.g., 201211071443 (equivalent to
	 * 2012-11-07 14:43)
	 * 
	 * Currently Java's formatter doesn't support this standard therefore we can
	 * not use the fmt:formatDate tag with a pattern
	 * 
	 * @param date
	 * @return the formatted date
	 */
	public static String formatDateMemento(final Date date) {
		if (present(date)) {
			return MEMENTO_FORMAT.print(new DateTime(date));
		}
		return "";
	}

	/**
	 * Formats the date with the given locale.
	 * 
	 * @param day
	 * @param month
	 * @param year
	 * @param locale
	 * @return The formatted date. Depending on how detailed the date is (year
	 *         only, month+year, day+month+year) the date is formatted in
	 *         different ways.
	 */
	public static String getDate(final String day, final String month, final String year, final Locale locale) {
		if (present(year)) {
			final String cleanYear = BibTexUtils.cleanBibTex(year);
			if (present(month)) {
				final String cleanMonth = BibTexUtils.cleanBibTex(month);
				final String monthAsNumber = BibTexUtils.getMonthAsNumber(cleanMonth);
				if (present(day)) {
					final String cleanDay = BibTexUtils.cleanBibTex(day.trim());
					try {
						final DateTime dt = dmyDateFormat.parseDateTime(cleanYear + "-" + monthAsNumber + "-" + cleanDay);
						return DateTimeFormat.mediumDate().withLocale(locale).print(dt);
					} catch (final Exception ex) {
						// return default date
						return cleanDay + " " + cleanMonth + " " + cleanYear;
					}
				}
				/*
				 * no day given
				 */
				try {
					final DateTime dt = myDateFormat.parseDateTime(cleanYear + "-" + monthAsNumber);
					return myDateFormatter.withLocale(locale).print(dt);
				} catch (final Exception ex) {
					// return default date
					return cleanMonth + " " + cleanYear;
				}
			}
			/*
			 * no month given
			 */
			return cleanYear;
		}
		return "";
	}

	/**
	 * @param collection
	 * @param resourceName
	 * @return <code>true</code> iff the resourceClass is in the collection
	 */
	public static boolean containsResourceClass(final Collection<?> collection, final String resourceName) {
		return contains(collection, ResourceFactory.getResourceClass(resourceName));
	}

	/**
	 * Checks if the given collection contains the given object.
	 * 
	 * @param collection
	 * @param object
	 * @return <code>true</code>, iff object is contained in set.
	 */
	public static boolean contains(final Collection<?> collection, final Object object) {
		return (collection != null) && collection.contains(object);
	}

	/**
	 * Retrieve the next user similarity, based on the ordering of user
	 * similarities as described in {@link UserRelation}. For erroneous or
	 * invalid input, folkrank as default measure is returned.
	 * 
	 * @param userSimilarity
	 *        - a user similarity
	 * @return the "next" user similarity
	 */
	public static String toggleUserSimilarity(final String userSimilarity) {
		if (!present(userSimilarity)) {
			return UserRelation.FOLKRANK.name().toLowerCase();
		}
		final UserRelation rel = EnumUtils.searchEnumByName(UserRelation.values(), userSimilarity);
		if (rel == null) {
			return UserRelation.FOLKRANK.name().toLowerCase();
		}
		// the four relevant user relations have the ID's 0 to 3 - so we add 1
		// and
		// compute modulo 4
		final int nextId = (rel.getId() + 1) % 4;
		return UserRelation.getUserRelationById(nextId).name().toLowerCase();
	}

	/**
	 * Simply extracts a DOI out of a string
	 * 
	 * @param doiString
	 * @return DOI string
	 */
	public static String extractDOI(final String doiString) {
		return DOIUtils.extractDOI(doiString);
	}

	/**
	 * Remove XML control characters from a given String.
	 * 
	 * @see XmlUtils
	 * @param s
	 *        - the string from which the control characters are to be
	 *        removed
	 * @return the string with control characters removed.
	 */
	public static String removeInvalidXmlChars(final String s) {
		return XmlUtils.removeInvalidXmlChars(s);
	}

	/**
	 * 
	 * @param className
	 * @param value
	 * @return the enum representation
	 * @throws ClassNotFoundException
	 */
	public static <T extends Enum<T>> T convertToEnum(final String className, final String value) throws ClassNotFoundException {
		@SuppressWarnings("unchecked")
		final Class<T> enumClass = (Class<T>) Class.forName(className);
		return new StringToEnumConverter<T>(enumClass).convert(value);
	}

	/**
	 * Checks if post has system tag myown
	 * 
	 * @param post
	 * @return <code>true</code> iff post contains {@link MyOwnSystemTag} system
	 *         tag
	 */
	public static boolean hasTagMyown(final Post<? extends Resource> post) {
		return SystemTagsUtil.containsSystemTag(post.getTags(), MyOwnSystemTag.NAME);
	}

	/**
	 * checks if post has system tag reported for the specified group TODO:
	 * merge with hasTagMyown!
	 * 
	 * @param tags
	 * @param group
	 * @return <code>true</code> if post was already reported
	 */
	public static boolean hasReportedSystemTag(final Set<Tag> tags, final String group) {
		return SystemTagsUtil.containsSystemTag(tags, ReportedSystemTag.NAME, group);
	}

	/**
	 * wrapper for {@link UserUtils#userIsGroup(User)}
	 * 
	 * @param user
	 * @return @see {@link UserUtils#userIsGroup(User)}
	 */
	public static boolean userIsGroup(final User user) {
		return UserUtils.userIsGroup(user);
	}

	/**
	 * 
	 * @param discussionItems
	 * @return a list of unique users, discussed a publication
	 */
	public static List<String> uniqueDiscussionUsers(final List<DiscussionItem> discussionItems) {
		/*
		 * FIXME: Use a set to guarantee the uniqueness of user names!
		 */
		final List<String> users = new ArrayList<String>();

		for (final DiscussionItem item : discussionItems) {
			if (!users.contains(item.getUser().getName())) {
				users.add(item.getUser().getName());
			}
		}
		return users;
	}

	/**
	 * 
	 * @param filename
	 * @return all invalid characters for html attribute id replaced by '-'.
	 */
	public static String downloadFileId(final String filename) {
		return filename.replaceAll("[^A-Za-z0-9]", "-");
	}

	/**
	 * returns true, if command implements DidYouKnowMessageCommand interface
	 * 
	 * @param command
	 * @return true|false
	 */
	/**
	 * returns true, if command implements DidYouKnowMessageCommand interface
	 * and has a didYouKnowMessage set
	 * 
	 * @param command
	 * @return true|false
	 */
	@Deprecated
	// TODO: (bootstrap) remove and use not empty check
	public static Boolean hasDidYouKnowMessage(final BaseCommand command) {
		return (command.getDidYouKnowMessage() != null);
	}

	public static Boolean isRegularGroup(final Group group) {
		return GroupUtils.isValidGroup(group) && !GroupUtils.isExclusiveGroup(group);
	}

}
