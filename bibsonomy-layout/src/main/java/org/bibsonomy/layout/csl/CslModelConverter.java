/**
 *
 *  BibSonomy-Layout - Layout engine for the webapp.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.layout.csl;

import static org.bibsonomy.model.util.BibTexUtils.cleanBibTex;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JsonConfig;
import net.sf.json.processors.PropertyNameProcessor;
import net.sf.json.util.PropertyFilter;

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
import org.bibsonomy.model.util.BibTexUtils;

/**
 * TENTATIVE implementation of a mapping of our publication model to the CSL model,
 * which we transform to JSON later on.
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 */
public class CslModelConverter {

	/**
	 * BibTeX entry types -> csl types
	 * see http://xbiblio-devel.2463403.n2.nabble.com/Citeproc-json-data-input-specs-td5135372.html
	 * FIXME: this is incomplete!
	 */
	private static Map<String, String> typemap;

	static {
		
		/*
		 * This mapping based on 
		 * http://www.docear.org/2012/08/08/docear4word-mapping-bibtex-fields-and-types-with-the-citation-style-language/
		 * (sbo 2013-10-16)
		 */
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
		
		typemap.put(BibTexUtils.MISC, "article");
		
		typemap.put(BibTexUtils.STANDARD, "legislation");
		
		typemap.put(BibTexUtils.UNPUBLISHED, "manuscript");
		typemap.put(BibTexUtils.PREPRINT, "manuscript");
	}

	/**
	 * Convert a bibtex post into a CSL record.
	 * 
	 * @param post
	 *            - the bibtex post
	 * @return the corresponding CSL model
	 */
	public static Record convertPost(final Post<? extends Resource> post) {
		final Record rec = new Record();
		final BibTex bib = (BibTex) post.getResource();
		
		/*
		 * This mapping based on 
		 * http://www.docear.org/2012/08/08/docear4word-mapping-bibtex-fields-and-types-with-the-citation-style-language/
		 * (sbo 2013-10-16)
		 */
		
		// id
		rec.setId(createId(post));
		// type
		rec.setType(mapToCslType(bib.getEntrytype()));

		// mapping address
		rec.setEvent_place(cleanBibTex(bib.getAddress()));
		rec.setPublisher_place(cleanBibTex(bib.getAddress()));
		
		// mapping authors, editors
		if (present(bib.getAuthor())) {
			for (final PersonName author : bib.getAuthor()) {
				final Person person = convertToPerson(author);
				rec.getAuthor().add(person);
			}
		}
		if (present(bib.getEditor())) {
			for (final PersonName editor : bib.getEditor()) {
				final Person person = convertToPerson(editor);
				rec.getEditor().add(person);
			}
		}

		// date mapping
		final Date date = new Date();
		date.setDate_parts(Collections.singletonList(new DateParts(bib.getYear())));
		date.setLiteral(bib.getYear());
		rec.setIssued(date);
		
		// mapping abstract
		rec.setAbstractt(cleanBibTex(bib.getAbstract()));
		
		// mapping bibtexkey
		rec.setCitation_label(cleanBibTex(bib.getBibtexKey()));
		
		
		/***************************
		 *** COLLECTION TITLE    ***
		 ***************************/

//		collection-title
//		title of the collection holding the item (e.g. the series title for a book)
		
		if(present(bib.getSeries())) {
			rec.setCollection_title(cleanBibTex(bib.getSeries()));
		}
		
		/***************************
		 *** CONTAINER TITLE     *** 
		 ***************************/
		
//		container-title
//		title of the container holding the item (e.g. the book title for a book chapter, the journal title for a journal article)
		
		if (BibTexUtils.ARTICLE.equals(bib.getEntrytype()) &&
			present(bib.getJournal())) {
				
				rec.setContainer_title(cleanBibTex(bib.getJournal()));
				
		} else {
			
			rec.setContainer_title(cleanBibTex(bib.getBooktitle()));
		}
		
		
		// mapping edition
		rec.setEdition(cleanBibTex(bib.getEdition()));
		
		// mapping publisher, techreport, thesis, organization
		if (present(bib.getPublisher())) {
			rec.setPublisher(cleanBibTex(bib.getPublisher()));
		} else if (BibTexUtils.TECH_REPORT.equals(bib.getEntrytype())) {
			rec.setPublisher(cleanBibTex(bib.getInstitution()));
		} else if (BibTexUtils.PHD_THESIS.equals(bib.getEntrytype())) {
			rec.setPublisher(cleanBibTex(bib.getSchool()));
			rec.setGenre("PhD dissertation");
		} else if (BibTexUtils.MASTERS_THESIS.equals(bib.getEntrytype())) {
			rec.setPublisher(cleanBibTex(bib.getSchool()));
			rec.setGenre("Master thesis");
		} else {
			rec.setPublisher(cleanBibTex(bib.getOrganization()));
		}
		
		// mapping chapter, title
		if (present(bib.getChapter())) {
			rec.setTitle(cleanBibTex(bib.getChapter()));
		} else {
			rec.setTitle(cleanBibTex(bib.getTitle()));
		}
		
		// mapping note
		rec.setNote(cleanBibTex(bib.getNote()));
		
		// mapping number
		rec.setNumber(cleanBibTex(bib.getNumber()));
		rec.setIssue(cleanBibTex(bib.getNumber()));
		
		// mapping pages
		rec.setPage(cleanBibTex(bib.getPages()));
		rec.setNumber_of_pages(bib.getPages());
		rec.setPage_first(bib.getPages());
		
		rec.setVolume(cleanBibTex(bib.getVolume()));

		rec.setURL(cleanBibTex(bib.getUrl()));
		
		rec.setDOI(cleanBibTex(bib.getMiscField("doi")));
		rec.setISBN(cleanBibTex(bib.getMiscField("isbn")));

		rec.setDocuments(convertList(bib.getDocuments()));
		
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
		person.setGiven(cleanBibTex(personName.getFirstName()));
		person.setFamily(cleanBibTex(personName.getLastName()));
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
		return ((BibTex) post.getResource()).getIntraHash() + post.getUser().getName();
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

}
