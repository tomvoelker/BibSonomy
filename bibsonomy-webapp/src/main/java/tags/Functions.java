package tags;

import static org.bibsonomy.model.util.BibTexUtils.ENTRYTYPES;
import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.markup.MyOwnSystemTag;
import org.bibsonomy.model.Author;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.util.EnumUtils;
import org.bibsonomy.util.JSONUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.XmlUtils;
import org.bibsonomy.util.id.DOIUtils;
import org.bibsonomy.util.upload.FileUploadInterface;
import org.bibsonomy.web.spring.converter.StringToEnumConverter;
import org.springframework.format.datetime.DateFormatter;

/**
 * TODO: move to org.bibsonomy.webapp.util.tags package
 * 
 * Some taglib functions
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class Functions  {
	
	/**
	 * Mapping of BibTeX entry types to SWRC entry types
	 * @see BibTexUtils#ENTRYTYPES
	 */
	public static final String[] swrcEntryTypes   = {"Article",        "Book", "Booklet", "Misc",       "Misc",       "InBook",       "InCollection", "InProceedings",    "Manual",  "MasterThesis",  "Misc",    "Misc",    "Misc",       "PhDThesis", "Misc",     "Misc",         "Proceedings",            "Misc",     "TechnicalReport", "Unpublished"};
	private static final String[] risEntryTypes    = {"Journal Article","Book", "Book",    "Generic",    "Generic",    "Book Section", "Book Section", "Conference Paper", "Generic", "Thesis",        "Generic", "Generic", "Generic",    "Thesis",    "Generic",  "Generic",      "Conference Proceedings", "Generic",  "Report",          "Unpublished Work"};

	// contains special characters, symbols, etc...
	private static final Properties chars = new Properties();

	// used to generate URLs
	private static URLGenerator urlGenerator;
	
	private static final SimpleDateFormat ISO8601_FORMAT_HELPER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	private static final DateFormatter myDateFormatter = new DateFormatter("MMMM yyyy");
	private static final DateFormatter dmyDateFormatter = new DateFormatter();
	static {
		dmyDateFormatter.setStyle(DateFormat.MEDIUM);
	}
	private static final DateFormat myDateFormat = new SimpleDateFormat("yyyy-MM");
	private static final DateFormat dmyDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
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
	public static String ch (final String key) {
		if (chars.getProperty(key) != null) {
			return chars.getProperty(key);
		}
		return "???" + key + "???";
	}

	/**
	 * Normalizes input string according to Unicode Standard Annex #15
	 * @param str
	 * @param decomp one of NFC, NFD, NFKC, NFKD @see Normalizer.Form
	 * @return normalized String
	 */
	public static String normalize( final String str, final String decomp ) {
		Normalizer.Form form; 
		try {
			form = Normalizer.Form.valueOf(decomp);
		} catch (final Exception e) {
			form = Normalizer.Form.NFD; 
		}
		return Normalizer.normalize(str+form.toString(), form);
	}

	/**
	 * replaces occurrences of whitespace in the by only one occurrence of the 
	 * respective whitespace character  
	 * 
	 * @param s a String
	 * @return trimmed String
	 */	
	public static String trimWhiteSpace (final String s) {
		/*
		 * remove empty lines
		 */
		return s.replaceAll("(?m)\n\\s*\n", "\n");
	}

	/** Removes all "non-trivial" characters from the file name.
	 * If the file name is empty "export" is returned
	 * @param file a file name
	 * @return cleaned file name
	 */
	public static String makeCleanFileName (final String file) {
		if (!present(file)) {
			return "export";
		}
		
		return UrlUtils.safeURIDecode(file).replaceAll("[^a-zA-Z0-9-_]", "_");
	}

	/**
	 * wrapper for {@link UrlUtils#safeURIDecode(String)}
	 * 
	 * @param uri a URI string
	 * @return the decoded URI string
	 */
	public static String decodeURI(final String uri) {
		return UrlUtils.safeURIDecode(uri);
	}

	/**
	 * wrapper for {@link UrlUtils#safeURIEncode(String)}
	 * 
	 * @param uri a URI string
	 * @return the encoded URI string
	 */
	public static String encodeURI(final String uri) {
		return UrlUtils.safeURIEncode(uri);
	}

	/**
	 * converts a collection of tags into a space-separated string of tags 
	 * 
	 * @param tags a list of tags
	 * @return a space-separated string of tags
	 */
	public static String toTagString(final Collection<Tag> tags) {		
		return TagUtils.toTagString(tags, " ");
	}

	/**
	 * get the Path component of a URI string
	 * 
	 * @param uriString a URI string
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
	 * Cuts the last segment of the url string until last slash
	 * 
	 * @param uriString the url
	 * @return last segment of the url string until last slash
	 */
	public static String getLowerPath(final String uriString) {
		final int lio = uriString.lastIndexOf("/");
		if (lio > 0) {
			try {
				/*
				 * FIXME: why do we wrap the result (which is a path!) into a URI
				 * to then extract the path again? 
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
	 * @param uriString a URI string
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
	 * @param url the url to check
	 * @return <code>true</code> iff the url is a link to a pdf or ps file
	 */
	public static boolean isLinkToDocument(final String url) {
		return StringUtils.matchExtension(url, FileUploadInterface.fileUploadExt);
	}

	/**
	 * Computes font size for given tag frequency and maximum tag frequency inside tag cloud
	 * 
	 * this is used as attribute font-size=X%, hence values between 100 and 300 are returned
	 * 
	 * FIXME: how much sense does it make to give a /frequency/ (which is 
	 * assumed to be between 0 and 100) as Integer?
	 * 
	 * @param tagFrequency
	 * @param tagMinFrequency TODO
	 * @param tagMaxFrequency
	 * @param tagSizeMode 
	 * @return font size for the tag cloud with the given parameters
	 */
	public static Integer computeTagFontsize(final Integer tagFrequency, Integer tagMinFrequency, final Integer tagMaxFrequency, final String tagSizeMode) {
			/*
			 * we expect 0 < tagFrequency < tagMaxFrequency.
			 * we return a value between 200 and 300 if tagsizemode=popular, and between 100 and 200 otherwise.  
			 */
			int scalingFactor = 45; // controls difference between smallest and largest tag 
									// (size of largest: 90 -> 200% font size; 40 -> ~170%; 20 -> ~150%; all for offset = 10)
			int offset = 8;		    // controls size of smallest tag ( 10 -> 100%)
			if ("popular".equals(tagSizeMode)) {
				scalingFactor *= 10;
			}
			Double size = ( ( (tagFrequency.doubleValue() - tagMinFrequency ) / (tagMaxFrequency - tagMinFrequency) ) * scalingFactor ) + offset; 
			size = Math.log10(size); 
			size *= 100;
			return size.intValue();
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
	 * @param url an url string
	 * @param paramName parameter name
	 * @param paramValue parameter value
	 * @return an url string with the requested parameter set
	 */
	public static String setParam(final String url, final String paramName, final String paramValue) {
		return UrlUtils.setParam(url, paramName, paramValue); 
	}

	/**
	 * wrapper for for org.bibsonomy.util.UrlUtils.removeParam
	 * 
	 * @param url
	 * 		- a url string
	 * @param paramName
	 * 		- a parameter to be removed
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
	 * @param id id of the spammer state
	 * @return string representation
	 */
	public static String getPredictionString(final Integer id) {
		return SpamStatus.getStatus(id).toString();
	}	

	/**
	 * Retrieves if given status is a spammer status
	 * @param id
	 * @return <code>true</code> iff given status is a spammer status
	 */
	public static Boolean isSpammer(final Integer id) {
		final SpamStatus status = SpamStatus.getStatus(id);
		return SpamStatus.isSpammer(status);
	}

	/**
	 * Quotes a String such that it is usable for JSON.
	 * 
	 * @param value
	 * @return The quoted String.
	 */
	public static String quoteJSON(final String value) {
		return JSONUtils.quoteJSON(value);
	}

	/** First, replaces certain BibTex characters, 
	 * and then quotes JSON relevant characters. 
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
	 * TODO: very inefficient ... use a static map instead
	 *  
	 * @param bibtexEntryType
	 * @return the SWRC entry type
	 */
	public static String getSWRCEntryType(final String bibtexEntryType) {
		for (int i = 0; i < ENTRYTYPES.length; i++) {
			/* Comparison with current entrytype value */
			if (ENTRYTYPES[i].equals(bibtexEntryType)) {
				/* match found -> print and stop loop */
				return swrcEntryTypes[i];
			}
		}
		return "Misc";
	}

	/**
	 * Maps BibTeX entry types to RIS entry types.
	 * 
	 * TODO: very inefficient ... use a static map instead
	 *  
	 * @param bibtexEntryType
	 * @return The RIS entry type
	 */
	public static String getRISEntryType(final String bibtexEntryType) {
		for (int i = 0; i < ENTRYTYPES.length; i++) {
			/* Comparison with current entrytype value */
			if (ENTRYTYPES[i].equals(bibtexEntryType)) {
				/* match found -> print and stop loop */
				return risEntryTypes[i];
			}
		}
		return "Generic";
	}


	/**
	 * returns the css Class for a given tag
	 * @param tagCount the count aof the current Tag
	 * @param maxTagCount the maximum tag count
	 * @return the css class for the tag
	 */
	public static String getTagSize(final Integer tagCount, final Integer maxTagCount) {
		/*
		 * catch incorrect values
		 */
		if (tagCount == 0 || maxTagCount == 0) return "tagtiny";

		final int percentage = ((tagCount * 100) / maxTagCount);

		if (percentage < 25) {
			return  "tagtiny";
		} else if (percentage >= 25 && percentage < 50) {
			return  "tagnormal";
		} else if (percentage >= 50 && percentage < 75) {
			return  "taglarge";
		} else if (percentage >= 75) {
			return  "taghuge";
		}

		return "";
	}

	/**
	 * Calculates the percentage of font size for clouds of author names
	 * @param author 
	 * @param maxCount 
	 * 
	 * @return value between 0 and 100 %
	 */
	public static double authorFontSize(final Author author, final Integer maxCount) {		
		return ((author.getCtr() * 100) / (maxCount / 2) ) + 50;
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
		return (int) (100.0 - Math.log(count / Math.log(2) * 2.0));
	}

	/** Returns the host name of a URL.
	 * 
	 * @param urlString - the URL as string
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
			if (title != null) buf.append(shorten(title, 50));

			final String author = PersonNameUtils.serializePersonNames(resource.getAuthor());
			if (present(author)) buf.append(", " + shorten(author, 20));

			final String year = resource.getYear();
			if (year != null) buf.append(", " + shorten(year, 4));
		}

		return buf.toString();
	}

	/**
	 * If the string is longer than <code>length</code>: shortens the given string to 
	 * <code>length - 3</code> and appends <code>...</code>. Else: returns the string.
	 * @param s - the string
	 * @param length - maximal length of the string
	 * @return The shortened string
	 */
	public static String shorten(final String s, final Integer length) {
		if (s != null && s.length() > length) return s.substring(0, length - 3) + "...";
		return s;
	}

	/**
	 * Access the built-in utility function for BibTeX export
	 * 
	 * @param post - a publication post
	 * @param projectHome 
	 * @param lastFirstNames - should person names appear in "Last, First" form? 
	 * @param generatedBibtexKeys - should the BibTeX keys be generated or the one from the database?
	 * @return A BibTeX string of this post
	 */
	public static String toBibtexString(final Post<BibTex> post, final String projectHome, final Boolean lastFirstNames, final Boolean generatedBibtexKeys) {
		int flags = 0;
		if (!lastFirstNames) flags |= BibTexUtils.SERIALIZE_BIBTEX_OPTION_FIRST_LAST;
		if (generatedBibtexKeys) flags |= BibTexUtils.SERIALIZE_BIBTEX_OPTION_GENERATED_BIBTEXKEYS;
		if (urlGenerator == null) {
			urlGenerator = new URLGenerator(projectHome);
		}
		return BibTexUtils.toBibtexString(post, flags, urlGenerator) + "\n\n";
	}
	
	/**
	 * formats the date to ISO 8601 for rss feeds, e.g.
	 * currently java's formatter doesn't support this standard therefore we can
	 * not use the fmt:formatDate tag with a pattern
	 * 
	 * @param date 
	 * @return the formatted date
	 */
	public static String formatDateISO8601(final Date date) {
		final String dateStr = ISO8601_FORMAT_HELPER.format(date);
		// convert format 2011-08-29'T'23:23:23+0200 to 2011-08-29'T'23:23:23+02:00
		return dateStr.substring(0, dateStr.length() - 2) + ":" + dateStr.substring(dateStr.length() - 2, dateStr.length());
	}
	
	/**
	 * Formats the date with the given locale.
	 * 
	 * @param day
	 * @param month
	 * @param year
	 * @param locale
	 * @return The formatted date. Depending on how detailed the date is (year 
	 * only, month+year, day+month+year) the date is formatted in different ways.
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
						return dmyDateFormatter.print(dmyDateFormat.parse(cleanYear + "-" + monthAsNumber + "-" + cleanDay), locale);
					} catch (final Exception ex) {
						// return default date
						return cleanDay + " " + cleanMonth + " " + cleanYear;
					}
				}
				/*
				 * no day given
				 */
				try {
					return myDateFormatter.print(myDateFormat.parse(cleanYear + "-" + monthAsNumber), locale);
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
	
	/** Checks if the given collection contains the given object.
	 * 
	 * @param collection
	 * @param object
	 * @return <code>true</code>, iff object is contained in set.
	 */
	public static boolean contains(final Collection<?> collection, final Object object) {
		return collection != null && collection.contains(object);
	}

	/**
	 * Retrieve the next user similarity, based on the ordering of user similarities
	 * as described in {@link UserRelation}. For erroneous or invalid input, 
	 * folkrank as default measure is returned.
	 * 
	 * @param userSimilarity - a user similarity
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
		// the four relevant user relations have the ID's 0 to 3 - so we add 1 and 
		// compute modulo 4
		final int nextId = (rel.getId() + 1 ) % 4;
		return UserRelation.getUserRelationById(nextId).name().toLowerCase();
	}

	/**
	 * Simply extracts a DOI out of a string
	 * 
	 * @param doiString
	 * @return DOI string
	 */
	public static String extractDOI(final String doiString){
		return DOIUtils.extractDOI(doiString);
	}
	
	/**
	 * Remove XML control characters from a given String.
	 * 
	 * @see XmlUtils
	 * @param s - the string from which the control characters are to be removed
	 * @return the string with control characters removed.
	 */
	public static String removeXmlControlChars(final String s) {
		return XmlUtils.removeXmlControlCharacters(s);
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
	 * @param	post
	 * @return	<code>true</code> iff post contains {@link MyOwnSystemTag}
	 * 			system tag
	 */
	public static boolean hasTagMyown(final Post<? extends Resource> post) {
		return SystemTagsUtil.containsSystemTag(post.getTags(), MyOwnSystemTag.NAME);
	}
}
