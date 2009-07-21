/**
 *  
 *  BibSonomy-Scrapingservice - Web application to test the BibSonomy web page scrapers (see
 * 		bibsonomy-scraper)
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.scrapingservice.writers;

import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class RDFWriter {

	/*
	 * Mapping of BibTeX entry types to SWRC entry types
	 * FIXME: copied from Functions.java tag library.
	 */
	private static String[] bibtexEntryTypes = {"article","book","booklet","inbook","incollection","inproceedings","manual","masterthesis","misc","phdthesis","proceedings","techreport",     "unpublished"}; 
	private static String[] swrcEntryTypes   = {"Article","Book","Booklet","InBook","InCollection","InProceedings","Manual","MasterThesis","Misc","PhDThesis","Proceedings","TechnicalReport","Unpublished"}; 
	private static Map<String,String> entryTypeMap = new HashMap<String, String>();
	static {
		for (int i = 0; i < bibtexEntryTypes.length; i++) {
			entryTypeMap.put(bibtexEntryTypes[i], swrcEntryTypes[i]);
		}
	}
	
	private static final String NS_SWRC = "http://swrc.ontoware.org/ontology#";
	private static final String NS_OWL  = "http://www.w3.org/2002/07/owl#";
	
	private final OutputStream outputStream;
	private final Model model;

	public RDFWriter(final OutputStream outputStream) {
		super();
		this.outputStream = outputStream;
		this.model = ModelFactory.createDefaultModel();
		/*
		 * set namespace prefixes
		 */
		this.model.setNsPrefix("swrc", NS_SWRC);
		this.model.setNsPrefix("owl", NS_OWL);
	}


	/** Writes the given BibTex in RDF/XML-ABBREV notation to the outputstream.
	 * 
	 * @param resourceUrl - the URL of the bibtex, will be used as URI of the created 
	 * resource.
	 * @param bibtex
	 * 
	 * FIXME: add exception handling around critical sections (e.g., using the URL 
	 * from bibtex.getUrl() to create a resource, using keys from the misc-fields
	 * as XML-entity names, etc.)
	 */
	public void write (final URI resourceUri, final BibTex bibtex) {
		/*
		 * fill model
		 * FIXME: automatic type extraction
		 */
		final Resource type = model.createResource(NS_SWRC + getSWRCEntryType(bibtex.getEntrytype()));
		final Resource resource = model.createResource(resourceUri.toString(), type);

		/*
		 * complex properties
		 */
		final Resource organizationClass = model.createResource(NS_SWRC + "Organization");
		/*
		 * institution, publisher, school
		 */
		if (bibtex.getInstitution() != null) {
			final Resource organization = model.createResource(organizationClass);
			organization.addProperty(model.createProperty(NS_SWRC + "name"), bibtex.getInstitution());
			resource.addProperty(model.createProperty(NS_SWRC + "institution"), organization);
		}
		if (bibtex.getPublisher() != null) {
			final Resource publisher = model.createResource(organizationClass);
			publisher.addProperty(model.createProperty(NS_SWRC + "name"), bibtex.getPublisher());
			resource.addProperty(model.createProperty(NS_SWRC + "publisher"), publisher);
		}
		if (bibtex.getSchool() != null) {
			final Resource universityClass = model.createResource(NS_SWRC + "University");
			final Resource school = model.createResource(universityClass);
			school.addProperty(model.createProperty(NS_SWRC + "name"), bibtex.getSchool());
			resource.addProperty(model.createProperty(NS_SWRC + "school"), school);
		}
		/*
		 * author, editor
		 */
		final Resource personClass = model.createResource(NS_SWRC + "Person");
		final List<PersonName> authorList = bibtex.getAuthorList();
		for (final PersonName name: authorList) {
			final Resource author = model.createResource(personClass);
			author.addProperty(model.createProperty(NS_SWRC + "name"), name.getFirstName() + " " + name.getLastName());
			resource.addProperty(model.createProperty(NS_SWRC + "author"), author);
		}
		final List<PersonName> editorList = bibtex.getEditorList();
		for (final PersonName name: editorList) {
			final Resource editor = model.createResource(personClass);
			editor.addProperty(model.createProperty(NS_SWRC + "name"), name.getFirstName() + " " + name.getLastName());
			resource.addProperty(model.createProperty(NS_SWRC + "editor"), editor);
		}
		/*
		 * url
		 */
		if (bibtex.getUrl() != null) {
			final Resource url2 = model.createResource(bibtex.getUrl());
			resource.addProperty(model.createProperty(NS_OWL + "sameAs"), url2);
		}
		
		
		
		/*
		 * misc fields (ISBN, DOI, etc.)
		 * NOTE: this is not clean, as they might not be part of SWRC
		 */
		final HashMap<String, String> miscFields = bibtex.getMiscFields();
		final Set<String> keySet = miscFields.keySet();
		for (final String key: keySet) {
			final String cleanedKey = cleanKey(key);
			addProperty(resource, cleanedKey, miscFields.get(key));
		}

		
		/*
		 * simple properties
		 */
		addProperty(resource, "title", bibtex.getTitle());
		addProperty(resource, "booktitle", bibtex.getBooktitle());
		addProperty(resource, "address", bibtex.getAddress());
		addProperty(resource, "chapter", bibtex.getChapter());
		addProperty(resource, "crossref", bibtex.getCrossref());
		addProperty(resource, "edition", bibtex.getEdition());
		addProperty(resource, "howpublished", bibtex.getHowpublished());
		addProperty(resource, "journal", bibtex.getJournal());
		addProperty(resource, "key", bibtex.getKey());
		addProperty(resource, "number", bibtex.getNumber());
		addProperty(resource, "month", bibtex.getMonth());
		addProperty(resource, "note", bibtex.getNote());
		addProperty(resource, "pages", bibtex.getPages());
		addProperty(resource, "series", bibtex.getSeries());
		addProperty(resource, "type", bibtex.getType());
		addProperty(resource, "volume", bibtex.getVolume());
		addProperty(resource, "year", bibtex.getYear());
		addProperty(resource, "abstract", bibtex.getAbstract());
		addProperty(resource, "series", bibtex.getSeries());


		/*
		 * write and close the model
		 */
		model.write(outputStream, "RDF/XML-ABBREV");
		model.close();
	}

	/** Make the key XML conform
	 * @param key
	 * @return
	 */
	private String cleanKey (final String key) {
		if (key != null) {
			return key.replaceAll("[^A-Za-z0-9\\-_]", "");
		}
		return key;
	}
	
	private void addProperty(final Resource resource, final String property, final String value) {
		if (value != null) {
			resource.addProperty(model.createProperty(NS_SWRC + property), value);
		}
	}
	
	/** Maps BibTeX entry types to SWRC entry types.
	 * FIXME: copied from Functions.java tag lib.
	 * @param bibtexEntryType
	 * @return
	 */
	private static String getSWRCEntryType(final String bibtexEntryType) {
		if (entryTypeMap.containsKey(bibtexEntryType)) return entryTypeMap.get(bibtexEntryType);
		return "Misc";
	}
}
