package org.bibsonomy.scrapingservice.writers;

import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class RDFWriter {

	private static final String NS_SWRC = "http://swrc.ontoware.org/ontology#";
	private static final String NS_OWL  = "http://www.w3.org/2002/07/owl#";
	
	private final OutputStream outputStream;
	private final Model model;

	public RDFWriter(OutputStream outputStream) {
		super();
		this.outputStream = outputStream;
		this.model = ModelFactory.createDefaultModel();
		/*
		 * set namespace prefixes
		 */
		this.model.setNsPrefix("swrc", NS_SWRC);
		this.model.setNsPrefix("owl", NS_OWL);
	}


	public void write (final URL url, final BibTex bibtex) {
		/*
		 * fill model
		 * FIXME: automatic type extraction
		 */
		final Resource type = model.createResource(NS_SWRC + "Inproceedings");
		final Resource resource = model.createResource(url.toString(), type);

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
		addProperty(resource, "key", bibtex.getBKey());
		addProperty(resource, "number", bibtex.getNumber());
		addProperty(resource, "month", bibtex.getMonth());
		addProperty(resource, "note", bibtex.getNote());
		addProperty(resource, "pages", bibtex.getPages());
		addProperty(resource, "series", bibtex.getSeries());
		addProperty(resource, "type", bibtex.getType());
		addProperty(resource, "volume", bibtex.getVolume());
		addProperty(resource, "year", bibtex.getYear());
		addProperty(resource, "abstract", bibtex.getBibtexAbstract());
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
}
