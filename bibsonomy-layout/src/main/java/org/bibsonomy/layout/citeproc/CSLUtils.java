package org.bibsonomy.layout.citeproc;

import static org.bibsonomy.util.ValidationUtils.present;

import de.undercouch.citeproc.bibtex.DateParser;
import de.undercouch.citeproc.bibtex.NameParser;
import de.undercouch.citeproc.bibtex.PageParser;
import de.undercouch.citeproc.bibtex.PageRange;
import de.undercouch.citeproc.csl.*;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.services.URLGenerator;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.LaTeXObject;
import org.jbibtex.LaTeXParser;
import org.jbibtex.LaTeXPrinter;
import org.jbibtex.ParseException;
import org.jbibtex.StringValue;
import org.jbibtex.TokenMgrException;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * FIXME: this class is a mess, was copied from somewhere else, some methods should not be here and are duplicating
 * already existing methods
 *
 * Utils for the CSL renderer
 *
 * @author mho
 * @author ag
 */
public final class CSLUtils {
	private static final Pattern CONTENT_PATTERN = Pattern.compile("<div class=\"csl-right-inline\">(.+?)</div>", Pattern.DOTALL);

	/**
	 * BibTex entry type -> CSL type
	 */
	private static final Map<String, CSLType> TYPEMAP = new HashMap<>();

	/**
	 * To convert from LaTeX string to user (normal) String.
	 */
	private static final ThreadLocal<LaTeXParser> latexParserThreadLocal;

	/**
	 * To convert from LaTeX string to user (normal) String.
	 */
	private static final ThreadLocal<LaTeXPrinter> latexPrinterThreadLocal;

	public static String CSL_TITLE_REPLACE_START_TAG = "CSLTITLESTART";
	public static String CSL_TITLE_REPLACE_END_TAG = "CSLTITLEEND";

	static {
		TYPEMAP.put(BibTexUtils.ARTICLE, CSLType.ARTICLE_JOURNAL);

		TYPEMAP.put(BibTexUtils.BOOK, CSLType.BOOK);
		TYPEMAP.put(BibTexUtils.PROCEEDINGS, CSLType.BOOK);
		TYPEMAP.put(BibTexUtils.PERIODICAL, CSLType.BOOK);
		TYPEMAP.put(BibTexUtils.MANUAL, CSLType.BOOK);

		TYPEMAP.put(BibTexUtils.BOOKLET, CSLType.PAMPHLET);

		TYPEMAP.put(BibTexUtils.INBOOK, CSLType.CHAPTER);
		TYPEMAP.put(BibTexUtils.INCOLLECTION, CSLType.CHAPTER);

		TYPEMAP.put(BibTexUtils.INPROCEEDINGS, CSLType.PAPER_CONFERENCE);
		TYPEMAP.put(BibTexUtils.CONFERENCE, CSLType.PAPER_CONFERENCE);

		TYPEMAP.put(BibTexUtils.PHD_THESIS, CSLType.THESIS);
		TYPEMAP.put(BibTexUtils.MASTERS_THESIS, CSLType.THESIS);

		TYPEMAP.put(BibTexUtils.TECH_REPORT, CSLType.REPORT);

		TYPEMAP.put(BibTexUtils.PATENT, CSLType.PATENT);

		TYPEMAP.put(BibTexUtils.ELECTRONIC, CSLType.WEBPAGE);

		TYPEMAP.put(BibTexUtils.MISC, CSLType.ARTICLE);

		TYPEMAP.put(BibTexUtils.STANDARD, CSLType.LEGISLATION);

		TYPEMAP.put(BibTexUtils.UNPUBLISHED, CSLType.MANUSCRIPT);
		TYPEMAP.put(BibTexUtils.PREPRINT, CSLType.MANUSCRIPT);

		// XXX: Mappings missing for elements from https://www.bibsonomy.org/help_en/Entrytypes:
		// - collection
		// - dataset
		// - preamble
		// - presentation
		// - techreport

		latexParserThreadLocal = ThreadLocal.withInitial(() -> {
			try {
				return new LaTeXParser();
			} catch (final ParseException e) {
				// can actually never happen because the default constructor
				// of LaTeXParser doesn't throw
				throw new RuntimeException(e);
			}
		});

		latexPrinterThreadLocal = ThreadLocal.withInitial(LaTeXPrinter::new);
	}

	/**
	 * converts the posts to {@link CSLItemData}
	 *
	 * @return the converted posts
	 * @deprecated serial version. Too slow. Use {@link #convertConcurretlyToCslItemData(List, boolean)}
	 * @param posts
	 */
	public static CSLItemDataConversionResult[] convertToCslItemData(final List<Post<? extends BibTex>> posts, boolean addSurroundingTextTags) {
		final Queue<CSLItemDataConversionResult> items = new LinkedBlockingQueue<>();
		posts.forEach(bibTexPost -> items.add(convertToCslItemData(bibTexPost, addSurroundingTextTags)));
		return items.toArray(new CSLItemDataConversionResult[0]);
	}

	/**
	 * converts the posts to {@link CSLItemData}
	 *
	 * @return the converted posts
	 */
	public static CSLItemDataConversionResult[] convertConcurretlyToCslItemData(final List<Post<? extends BibTex>> posts, boolean addSurroundingTextTags) {
		final Queue<CSLItemDataConversionResult> items = new LinkedBlockingQueue<>();
		posts.parallelStream().forEach(bibTexPost -> items.add(convertToCslItemData(bibTexPost, addSurroundingTextTags)));
		return items.toArray(new CSLItemDataConversionResult[0]);
	}

	/**
	 * Convert the publication contained in the bibsonomy {@code post} into a CSL Item.
	 *
	 * @param post the bibsonomy post
	 * @return the converted post publication as {@link CSLItemData} if the conversion was possible or <tt>null</tt> if
	 * it wasn't, and a list of issues if there were issues during the conversion
	 * @see <a href="https://www.bibsonomy.org/help_en/Entrytypes">bibsonomy entry types</a>
	 * @see <a href="http://docs.citationstyles.org/en/stable/specification.html#appendix-iii-types">CSL Types</a>
	 */
	public static CSLItemDataConversionResult convertToCslItemData(final Post<? extends BibTex> post, boolean addSurroundingTextTags) {
		final CSLItemDataConversionResult result = new CSLItemDataConversionResult();
		final BibTex publication = post.getResource();

		try {
			publication.parseMiscField();
		} catch (final InvalidModelException e) {
			// ignore
		}

		final String id = getPostUID(post);
		final CSLItemDataBuilder cslDataBuilder = new CSLItemDataBuilder();
		final CSLName[] editors = getCSLNames(publication.getEditor(), addSurroundingTextTags);
		cslDataBuilder.id(id);

		cslDataBuilder.type(getCSLType(publication.getEntrytype()))
				.author(getCSLNames(publication.getAuthor(), addSurroundingTextTags))
				.editor(editors)
				.collectionEditor(editors);

		// mapping address
		final String venue = publication.getMiscField("venue");
		final String location = publication.getMiscField("location");
		final String address = publication.getAddress();
		if (present(venue)) {
			cslDataBuilder.eventPlace(BibTexUtils.cleanBibTex(venue));
			final String eventTitle = publication.getMiscField("eventTitle");
			if (present(eventTitle)) {
				cslDataBuilder.event(BibTexUtils.cleanBibTex(eventTitle));
			}
		} else if (present(location)) {
			final String cleanedLocation = BibTexUtils.cleanBibTex(location);
			cslDataBuilder
					.eventPlace(cleanedLocation)
					.publisherPlace(cleanedLocation);
		} else if (present(address)) {
			final String cleanedAddress = BibTexUtils.cleanBibTex(address);
			cslDataBuilder
					.eventPlace(cleanedAddress)
					.publisherPlace(cleanedAddress);
		}

		// mapping bibtexkey
		cslDataBuilder.citationLabel(BibTexUtils.cleanBibTex(publication.getBibtexKey()));

		// mapping journal, booktitle and series
		final String cleanedJournal = BibTexUtils.cleanBibTex(publication.getJournal());
		final String cleanedBooktitle = BibTexUtils.cleanBibTex(publication.getBooktitle());
		final String cleanedSeries = BibTexUtils.cleanBibTex(publication.getSeries());
		final String colTitleToUse;
		if (present(cleanedJournal)) {
			colTitleToUse = cleanedJournal;
		} else if (present(cleanedBooktitle)) {
			colTitleToUse = cleanedBooktitle;
		} else {
			colTitleToUse = cleanedSeries;
		}

		cslDataBuilder.containerTitle(colTitleToUse);
		cslDataBuilder.collectionTitle(colTitleToUse);

		// mapping publisher, techreport, thesis, organization
		if (present(publication.getPublisher())) {
			cslDataBuilder.publisher(BibTexUtils.cleanBibTex(publication.getPublisher()));
		} else if (BibTexUtils.TECH_REPORT.equals(publication.getEntrytype())) {
			if (publication.getInstitution() != null) {
				cslDataBuilder.publisher(BibTexUtils.cleanBibTex(publication.getInstitution()));
			}
		} else if (BibTexUtils.PHD_THESIS.equals(publication.getEntrytype())) {
			if (publication.getSchool() != null) {
				cslDataBuilder.publisher(BibTexUtils.cleanBibTex(publication.getSchool()));
			}
			// MHO DISABLED
//		} else if (BibTexUtilsUBS.PUBLICATION_TYPES.contains(publication.getType())) {
//			if ((publication.getSchool() != null) || (publication.getInstitution() != null)) {
//				final String separator = ((publication.getSchool() != null) && (publication.getInstitution() != null)) ? ", " : "";
//				final String school = BibTexUtils.cleanBibTex(publication.getSchool());
//				final String institution = BibTexUtils.cleanBibTex(publication.getInstitution());
//				cslDataBuilder.publisher(school + separator + institution);
//			}
		} else {
			if (publication.getOrganization() != null) {
				cslDataBuilder.publisher(BibTexUtils.cleanBibTex(publication.getOrganization()));
			}
		}

		// map genre as per https://aurimasv.github.io/z2csl/typeMap.xml#map-thesis
		if (present((publication.getEntrytype()))) {
			switch (publication.getEntrytype()) {
				case BibTexUtils.BOOK:
				case BibTexUtils.ELECTRONIC:
				case BibTexUtils.MANUAL:
				case BibTexUtils.MASTERS_THESIS:
				case BibTexUtils.PERIODICAL:
				case BibTexUtils.PHD_THESIS:
				case BibTexUtils.PROCEEDINGS:
				case BibTexUtils.TECH_REPORT:
				case BibTexUtils.UNPUBLISHED:
					cslDataBuilder.genre(publication.getType());
					break;
				default:
					// ignore genre
					break;
			}
		}

		// mapping chapter
		final String chapter = publication.getChapter();
		if (present(chapter)) {
			cslDataBuilder.chapterNumber(BibTexUtils.cleanBibTex(chapter));
		}

		// mapping title
		final String title = BibTexUtils.cleanBibTex(publication.getTitle());
		if (present(title)) {
			if (addSurroundingTextTags) {
				cslDataBuilder.title(CSL_TITLE_REPLACE_START_TAG + title + CSL_TITLE_REPLACE_END_TAG);
			} else {
				cslDataBuilder.title(title);
			}
		} else {
			// XXX: title is a required field
			cslDataBuilder.title(chapter);
		}

		// mapping number
		final String cleanedNumber = BibTexUtils.cleanBibTex(publication.getNumber());
		cslDataBuilder.number(cleanedNumber);

		final String cleanedIssue = BibTexUtils.cleanBibTex(publication.getMiscField("issue"));
		final String issueToUse;
		if (present(cleanedIssue)) {
			issueToUse = cleanedIssue;
		} else {
			issueToUse = cleanedNumber;
		}
		cslDataBuilder.issue(issueToUse);

		final String accessed = BibTexUtils.cleanBibTex(publication.getMiscField("accessed"));
		if (present(accessed)) {
			final CSLDateBuilder accessedDateBuilder = new CSLDateBuilder();
			accessedDateBuilder.literal(accessed);
			cslDataBuilder.accessed(accessedDateBuilder.build());
		}

		// date mapping
		final String urlDate = BibTexUtils.cleanBibTex(publication.getMiscField("urldate"));
		final String cleanedDate = BibTexUtils.cleanBibTex(publication.getMiscField("date"));
		if (BibTexUtils.ELECTRONIC.equals(publication.getEntrytype()) && present(urlDate)) {
			final CSLDate date = DateParser.toDate(urlDate);
			cslDataBuilder.issued(date);
		} else if (present(cleanedDate)) {
			final CSLDate date = DateParser.toDate(cleanedDate);
			cslDataBuilder.issued(date);
			cslDataBuilder.eventDate(date);
		} else {
			// XXX: AG 2020-06-16: This date parser is much weaker than
			// {@link de.undercouch.citeproc.bibtex.BibTeXConverter#toItemData(BibTeXEntry) (!!). I'm keeping this
			// code and not using that of BibTeXConverter because BibTeXConverter ignores the day.
			//
			final CSLDateBuilder dateBuilder = new CSLDateBuilder();
			try {
				final int year = Integer.parseInt(publication.getYear());
				dateBuilder.dateParts(year);

				final String cleanedMonth = BibTexUtils.cleanBibTex(publication.getMonth());
				int month = 0;
				if (present(cleanedMonth)) {
					month = Integer.parseInt(cleanedMonth);
					dateBuilder.dateParts(year, month);
				}

				final String cleanedDay = BibTexUtils.cleanBibTex(publication.getDay());
				if (present(cleanedDay)) {
					dateBuilder.dateParts(year, month, Integer.parseInt(cleanedDay));
				}
			} catch (final NumberFormatException e) {
				// Note: dateBuilder.raw(publication.getYear()) would be an option, but I would be silencing the problem
				// and I cannot alert the editor easily, thus I'll throw an exception and let it surface up to the GUI
				// MHO DISABLED
				//result.addIssue(Messages.get().container(
				//		Messages.ERR_PARSING_DATE_2, getFriendlyName(post), e.getMessage()));
			}
			final CSLDate date = dateBuilder.build();
			cslDataBuilder.issued(date);
			cslDataBuilder.eventDate(date);
		}

		final String cleanedPages = BibTexUtils.cleanBibTex(publication.getPages());
		cslDataBuilder.page(cleanedPages);

		if (present(cleanedPages)) {
			try {
				final PageRange pageRange = PageParser.parse(cleanedPages);
				cslDataBuilder.pageFirst(pageRange.getPageFirst());

				final Integer numberOfPages = pageRange.getNumberOfPages();
				if (present(numberOfPages)) {
					cslDataBuilder.numberOfPages(String.valueOf(numberOfPages));
				}
			} catch (final NumberFormatException e) {
				cslDataBuilder.numberOfPages(cleanedPages);
				// MHO DISABLED
				//result.addIssue(Messages.get().container(Messages.ERR_PARSING_PAGES_2, getFriendlyName(post), e.getMessage()));
			}
		}

		final String language = BibTexUtils.cleanBibTex(publication.getMiscField("language"));
		if (present(language)) {
			cslDataBuilder.language(language);
		}

		if (publication.getEdition() != null) {
			cslDataBuilder.edition(BibTexUtils.cleanBibTex(publication.getEdition()));
		}

		cslDataBuilder.volume(BibTexUtils.cleanBibTex(publication.getVolume()))
				.keyword(TagUtils.toTagString(post.getTags(), " "))
				.URL(BibTexUtils.cleanBibTex(publication.getUrl()))
				.status(BibTexUtils.cleanBibTex(publication.getMiscField("status")))
				.ISBN(BibTexUtils.cleanBibTex(publication.getMiscField("isbn")))
				.ISSN(BibTexUtils.cleanBibTex(publication.getMiscField("issn")))
				.version(BibTexUtils.cleanBibTex(publication.getMiscField("revision")))
				.annote(BibTexUtils.cleanBibTex(publication.getAnnote()))
				.abstrct(publication.getAbstract())
				.DOI(BibTexUtils.cleanBibTex(publication.getMiscField("doi")))
				.note(BibTexUtils.cleanBibTex(publication.getNote()));

		result.setItemData(cslDataBuilder.build());
		return result;
	}

	/**
	 * Calculates a unique identifier (UID) for a bibtex post. PUMA allows copies of the same bibtex per user.
	 *
	 * @return unique bibtex identifier.
	 */
	public static String getPostUID(final Post<? extends BibTex> post) {
		//return post.getResource().getIntraHash() + post.getUser().getName().hashCode();
		return post.getResource().getIntraHash();
	}

	private static CSLName[] getCSLNames(final List<? extends PersonName> author, boolean addSurroundingTextTags) {
		if (!present(author)) {
			return null;
		}

		final CSLName[] cslNames = new CSLName[author.size()];
		for (int i = 0; i < author.size(); i++) {
			final PersonName personName = author.get(i);
			cslNames[i] = NameParser.parse(BibTexUtils.cleanBibTex(personName.toString()))[0];
		}

		return cslNames;
	}

	/**
	 * @param entrytype BibTex entry type
	 * @return corresponding CSL type
	 */
	private static CSLType getCSLType(final String entrytype) {
		return TYPEMAP.get(entrytype);
	}

	/**
	 * removes the numbers from the rendered output
	 *
	 * @return cslOutPut without list numbers
	 */
	public static String cleanBib(final String cslOutPut) {
		final Matcher matcher = createMatcher(cslOutPut);
		if (matcher.find()) {
			return matcher.group(1).trim();
		}
		return cslOutPut;
	}

	/**
	 * Converts LaTeX string to normal text
	 *
	 * @deprecated Use {@link BibTexUtils#cleanBibTex}. AG 2020-06-19: I wrote this method to fix a regression in
	 * rendering author names.<br/>
	 * Apparently, {@code cleanBibTex} also deal with the conversion LaTeX -> String. The implementation is completly
	 * different, so I would favour the bibsonomy version over this method to keep consistency throughout the bibsonomy
	 * echosystem.<br/>
	 * N.B.: I'm not deleting this method though, because I have my doubts that {@code cleanBibTex} be so robust like
	 * this implementation (borrowed from {@link de.undercouch.citeproc.bibtex.BibTeXConverter#toItemData(BibTeXEntry)}
	 */
	public static String laTeXToUserString(final String laTeXString) {
		final StringValue stringValue = new StringValue(laTeXString, StringValue.Style.BRACED);
		String userString = stringValue.toUserString().replaceAll("\\r", "");

		// convert LaTeX string to normal text
		try {
			final List<LaTeXObject> objs = latexParserThreadLocal.get().parse(new StringReader(userString));
			userString = latexPrinterThreadLocal.get().print(objs).replaceAll("\\n", " ").replaceAll("\\r", "").trim();
		} catch (final ParseException | TokenMgrException ex) {
			// ignore
		}
		return userString;
	}

	private static Matcher createMatcher(final String cslOutPut) {
		return CONTENT_PATTERN.matcher(cslOutPut);
	}

	public static class CSLItemDataConversionResult {
		CSLItemData itemData;
		// MHO DISABLED
		//List<CmsMessageContainer> issues = new ArrayList<>();
		//void addIssue(final CmsMessageContainer issue) {
		//	issues.add(issue);
		//}
		//public List<CmsMessageContainer> getIssues() {
		//	return issues;
		//}

		public CSLItemData getItemData() {
			return itemData;
		}

		void setItemData(final CSLItemData itemData) {
			this.itemData = itemData;
		}
	}

	public static String replacePlaceholdersFromCSLRendering(String renderedCSL, Post<? extends BibTex> post, URLGenerator urlGenerator) {

//		if (present(post.getSystemUrl()) && !Functions isSameHost(post.systemUrl, properties['project.home']) {
//			relativeUrlGenerator.getPublicationUrlByIntraHashAndUsername(post.resource.intraHash, post.user.name)
//		} else {
//			relativeUrlGenerator.getPostUrl(post)
//		}

//		<c:when test="${properties['genealogy.activated'] eq 'true'}">
//		<c:url var='pubAuthorUrl' value='${urlGenerator.getDisambiguationUrl(publication.interHash, authorEditorUppper, loopStatus.index)}'/>
//
//		<c:url var='pubAuthorUrl' value='${urlGenerator.getAuthorUrlByPersonName(person)}'/>




//		if (ResourceFactory.isCommunityResource(post.getResource())) {
//
//		} else {
//
//		}

		// set author links
//		for (int i=0; i< post.getResource().getAuthor().size(); i++) {
//			String authorUrl = urlGenerator.getDisambiguationUrl(
//					post.getResource().getInterHash(),
//					PersonResourceRelationType.AUTHOR,
//					i);
//
//			renderedCSL = renderedCSL
//					.replaceFirst(CSL_AUTHOR_REPLACE_START_TAG, "<a href=\""+ authorUrl +"\">")
//					.replaceFirst(CSL_AUTHOR_REPLACE_END_TAG, "</a>");
//		}

		// set title link
		String titleUrl = urlGenerator.getPostUrl(post);

		renderedCSL = renderedCSL
				.replace(CSL_TITLE_REPLACE_START_TAG, "<a class=\"pubEntryCSLTitleUrl\" href=\""+titleUrl+"\"><span class=\"pubEntryCSLTitle\">")
				.replace(CSL_TITLE_REPLACE_END_TAG, "</span></a>");

		return renderedCSL;
	}

}
