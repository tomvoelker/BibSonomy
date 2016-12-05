/**
 * BibSonomy-Layout - Layout engine for the webapp.
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
package org.bibsonomy.layout.csl;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.layout.csl.model.Date;
import org.bibsonomy.layout.csl.model.DateParts;
import org.bibsonomy.layout.csl.model.DocumentCslWrapper;
import org.bibsonomy.layout.csl.model.Person;
import org.bibsonomy.layout.csl.model.Record;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.TagUtils;

import net.sf.json.JsonConfig;
import net.sf.json.processors.PropertyNameProcessor;
import net.sf.json.util.PropertyFilter;

/**
 * TENTATIVE implementation of a mapping of our publication model to the CSL model,
 * which we transform to JSON later on.
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 */
public class CslModelConverter {
	private static final Log log = LogFactory.getLog(CslModelConverter.class);
	
	/**
	 * BibTeX entry types -> csl types
	 * XXX: mapping is incomplete
	 */
	private static Map<String, String> typemap;
	
	static {
		typemap = new HashMap<String, String>();
		
		typemap.put(BibTexUtils.ARTICLE, "article-journal");
		
		typemap.put(BibTexUtils.BOOK, "book");
		typemap.put(BibTexUtils.PROCEEDINGS, "book");
		typemap.put(BibTexUtils.PERIODICAL, "book");
		typemap.put(BibTexUtils.MANUAL, "book");
		
		typemap.put(BibTexUtils.BOOKLET, "pamphlet");
		
		typemap.put(BibTexUtils.INBOOK, "chapter");
		typemap.put(BibTexUtils.INCOLLECTION, "chapter");
		
		typemap.put(BibTexUtils.INPROCEEDINGS, "paper-conference");
		typemap.put(BibTexUtils.CONFERENCE, "paper-conference");
		
		typemap.put(BibTexUtils.PHD_THESIS, "thesis");
		typemap.put(BibTexUtils.MASTERS_THESIS, "thesis");
		
		typemap.put(BibTexUtils.TECH_REPORT, "report");
		
		typemap.put(BibTexUtils.PATENT, "patent");
		
		typemap.put(BibTexUtils.ELECTRONIC, "webpage");
		
		typemap.put(BibTexUtils.PRESENTATION, "speech");
		
		typemap.put(BibTexUtils.MISC, "article");
		
		typemap.put(BibTexUtils.STANDARD, "legislation");
		
		typemap.put(BibTexUtils.UNPUBLISHED, "manuscript");
		typemap.put(BibTexUtils.PREPRINT, "manuscript");
		
		
	}

	/**
	 * Convert a publication post into a CSL record.
	 * This mapping is based on http://www.docear.org/2012/08/08/docear4word-mapping-bibtex-fields-and-types-with-the-citation-style-language/
	 * 
	 * @param post
	 *            - the publication post
	 * @return the corresponding CSL model
	 */
	public static Record convertPost(final Post<? extends Resource> post) {
		final Record rec = new Record();
		final BibTex publication = (BibTex) post.getResource();
		// parse the misc field to extract DOI, ISBN, … TODO: database module should return the model with a parsed misc field
		if (!publication.isMiscFieldParsed()) {
			try {
				publication.parseMiscField();
			} catch (final InvalidModelException e) {
				log.debug("error while parsing misc fields", e);
			}
		}
		
		// id
		rec.setId(createId(post));
		
		// type
		rec.setType(mapToCslType(publication.getEntrytype()));

		final String cleanedLocation = BibTexUtils.cleanBibTex(publication.getMiscField("location"));
		if (present(cleanedLocation)) {
			rec.setEvent_place(cleanedLocation);
			rec.setPublisher_place(cleanedLocation);
		} else {
			final String cleanedAddress = BibTexUtils.cleanBibTex(publication.getAddress());
			rec.setEvent_place(cleanedAddress);
			rec.setPublisher_place(cleanedAddress);
		}
		
		// mapping authors, editors
		if (present(publication.getAuthor())) {
			for (final PersonName author : publication.getAuthor()) {
				final Person person = convertToPerson(author);
				rec.getAuthor().add(person);
			}
		}
		
		if (present(publication.getEditor())) {
			for (final PersonName editor : publication.getEditor()) {
				final Person person = convertToPerson(editor);
				rec.getEditor().add(person);
				rec.getCollection_editor().add(person);
				rec.getContainer_author().add(person);
			}
		}
		
		// mapping bibtexkey
		rec.setCitation_label(BibTexUtils.cleanBibTex(publication.getBibtexKey()));
		
		// mapping journal, booktitle and series
		final String cleanedJournal = BibTexUtils.cleanBibTex(publication.getJournal());
		final String cleanedBooktitle = BibTexUtils.cleanBibTex(publication.getBooktitle());
		final String cleanedSeries = BibTexUtils.cleanBibTex(publication.getSeries());
		final String containerTitleToUse;
		if (present(cleanedJournal)) {
			containerTitleToUse = cleanedJournal;
		} else if (present(cleanedBooktitle)) {
			containerTitleToUse = cleanedBooktitle;
		} else {
			containerTitleToUse = "";
		}
		
		rec.setContainer_title(containerTitleToUse);
		rec.setCollection_title(cleanedSeries);
		
		// mapping publisher, techreport, thesis, organization
		if (present(publication.getPublisher())) {
			rec.setPublisher(BibTexUtils.cleanBibTex(publication.getPublisher()));
		} else if (BibTexUtils.TECH_REPORT.equals(publication.getEntrytype())) {
			rec.setPublisher(BibTexUtils.cleanBibTex(publication.getInstitution()));
		} else if (BibTexUtils.PHD_THESIS.equals(publication.getEntrytype())) {
			rec.setPublisher(BibTexUtils.cleanBibTex(publication.getSchool()));
			rec.setGenre("PhD dissertation");
		} else if (BibTexUtils.MASTERS_THESIS.equals(publication.getEntrytype())) {
			rec.setPublisher(BibTexUtils.cleanBibTex(publication.getSchool()));
			rec.setGenre("Master thesis");
		} else {
			rec.setPublisher(BibTexUtils.cleanBibTex(publication.getOrganization()));
		}
		
		// mapping title
		rec.setTitle(BibTexUtils.cleanBibTex(publication.getTitle()));
		
		final String chapter = publication.getChapter();
		if (present(chapter)) {
			rec.setChapter_number(chapter);
		}
		
		// mapping number
		final String cleanedNumber = BibTexUtils.cleanBibTex(publication.getNumber());
		rec.setNumber(cleanedNumber);
		
		final String cleanedIssue = BibTexUtils.cleanBibTex(publication.getMiscField("issue"));
		final String issueToUse;
		if (present(cleanedIssue)) {
			issueToUse = cleanedIssue;
		} else {
			issueToUse = cleanedNumber;
		}
		rec.setIssue(issueToUse);
		
		final String accessed = BibTexUtils.cleanBibTex(publication.getMiscField("accessed"));
		if (present(accessed)) {
			final Date accessedDate = new Date();
			accessedDate.setLiteral(accessed);
			rec.setAccessed(accessedDate);
		}
		
		// date mapping
		final String urlDate = BibTexUtils.cleanBibTex(publication.getMiscField("urldate"));
		final String cleanedDate = BibTexUtils.cleanBibTex(publication.getMiscField("date"));
		final Date date = new Date();
		if (BibTexUtils.ELECTRONIC.equals(publication.getEntrytype()) && present(urlDate)) {
			date.setRaw(urlDate);
		} else if (present(cleanedDate)) {
			date.setRaw(cleanedDate);
			rec.setEvent_date(date);
		} else {
			final DateParts dateParts = new DateParts(publication.getYear());
			final String cleanedMonth = BibTexUtils.cleanBibTex(publication.getMonth());
			final String cleanedDay = BibTexUtils.cleanBibTex(publication.getDay());
			
			if (present(cleanedMonth)) {
				dateParts.add(cleanedMonth);
			}
			if (present(cleanedDay)) {
				if (!present(cleanedMonth)) {
					dateParts.add("");
				}
				dateParts.add(cleanedDay);
			}
			date.setDate_parts(Collections.singletonList(dateParts));
			
			date.setLiteral(publication.getYear()); // FIXME: wrong remove after updating typo3 plugin
			rec.setEvent_date(date);
		}
		rec.setIssued(date);
		
		// mapping pages
		final String cleanedPages = BibTexUtils.cleanBibTex(publication.getPages());
		rec.setPage(cleanedPages);
		
		final String firstPage = BibTexUtils.extractFirstPage(cleanedPages);
		final String lastPage = BibTexUtils.extractLastPage(cleanedPages);
		rec.setPage_first(firstPage);
		try {
			final int lastPageAsInteger = Integer.parseInt(lastPage);
			final int firstPageAsInteger = Integer.parseInt(firstPage);
			
			final int numberOfPages = lastPageAsInteger - firstPageAsInteger;
			if (numberOfPages > 0) {
				rec.setNumber_of_pages(String.valueOf(numberOfPages));
			}
		} catch (final NumberFormatException e) {
			// ignore
		}
		
		rec.setVolume(BibTexUtils.cleanBibTex(publication.getVolume()));
		rec.setKeyword(TagUtils.toTagString(post.getTags(), " "));
		rec.setURL(BibTexUtils.cleanBibTex(publication.getUrl()));
		rec.setStatus(BibTexUtils.cleanBibTex(publication.getMiscField("status")));
		rec.setISBN(BibTexUtils.cleanBibTex(publication.getMiscField("isbn")));
		rec.setISSN(BibTexUtils.cleanBibTex(publication.getMiscField("issn")));
		rec.setVersion(BibTexUtils.cleanBibTex(publication.getMiscField("revision")));
		rec.setAnnote(BibTexUtils.cleanBibTex(publication.getAnnote()));
		rec.setEdition(BibTexUtils.cleanBibTex(publication.getEdition()));
		rec.setAbstractt(BibTexUtils.cleanBibTex(publication.getAbstract()));
		rec.setDOI(BibTexUtils.cleanBibTex(publication.getMiscField("doi")));
		rec.setNote(BibTexUtils.cleanBibTex(publication.getNote()));
		
		rec.setDocuments(convertList(publication.getDocuments()));
		
		rec.setMisc(publication.getMiscFields());
		
		rec.setInterhash(publication.getInterHash());
		rec.setIntrahash(publication.getIntraHash());
		final User user = post.getUser();
		rec.setUsername(user != null ? user.getName() : null);
		return rec;
	}

	private static List<DocumentCslWrapper> convertList(final List<Document> documents) {
		final List<DocumentCslWrapper> list = new LinkedList<DocumentCslWrapper>();
		if (present(documents)) {
			for (final Document d : documents) {
				list.add(new DocumentCslWrapper(d));
			}
		}
		return list;
	}

	private static Person convertToPerson(final PersonName personName) {
		final Person person = new Person();
		person.setGiven(BibTexUtils.cleanBibTex(personName.getFirstName()));
		person.setFamily(BibTexUtils.cleanBibTex(personName.getLastName()));
		return person;
	}

	/**
	 * create ID for a post
	 * 
	 * @param post
	 *            - the post
	 * @return the ID
	 */
	private static final String createId(final Post<? extends Resource> post) {
		final BibTex publication = (BibTex) post.getResource();
		final User user = post.getUser();
		if (present(user)) {
			return publication.getIntraHash() + post.getUser().getName();
		}
		
		return publication.getInterHash();
	}

	/**
	 * get the mapping of bibtex types to CSL types.
	 * 
	 * @param bibtexType
	 *            - the bibtex entrytype
	 * @return the corresponding csl type
	 */
	private static final String mapToCslType(final String bibtexType) {
		return typemap.get(bibtexType);
	}
	
	/**
	 * @return	the json configuration to use when serializing object structure
	 * 			to JSON
	 */
	public static JsonConfig getJsonConfig() {
		final JsonConfig jsonConfig = new JsonConfig();
		// output only not-null fields
		jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
			@Override
			public boolean apply(final Object source, final String name, final Object value) {
				if (value == null) {
					return true;
				}
				return false;
			}
		});
		// transform underscores into "-"
		jsonConfig.registerJsonPropertyNameProcessor(Person.class, new PropertyNameProcessor() {

			@Override
			public String processPropertyName(final Class arg0, final String arg1) {
				return arg1.replace("_", "-");
			}
		});
		jsonConfig.registerJsonPropertyNameProcessor(Record.class, new PropertyNameProcessor() {

			@Override
			public String processPropertyName(final Class arg0, final String arg1) {
				// special handling for abstract field
				if ("abstractt".equals(arg1)) {
					return "abstract";
				}
				return arg1.replace("_", "-");
			}
		});
		jsonConfig.registerJsonPropertyNameProcessor(Date.class, new PropertyNameProcessor() {

			@Override
			public String processPropertyName(final Class arg0, final String arg1) {
				return arg1.replace("_", "-");
			}
		});
		
		return jsonConfig;
	}
	
	private static String ucfirst(String string){
		return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
	}

}
