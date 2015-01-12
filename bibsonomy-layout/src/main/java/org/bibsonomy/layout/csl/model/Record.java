/**
 * BibSonomy-Layout - Layout engine for the webapp.
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
package org.bibsonomy.layout.csl.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Models an entry. See the file 'csl-variables' at
 *   https://bitbucket.org/bdarcus/csl-schema/
 * for a list of supported variables.
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 */
public class Record {
	
	// id of the entry
	private String id;
	
	//*************************************************
	// persons / names
	// FIXME: more available, see http://citationstyles.org/downloads/specification.html#name-variables
	//*************************************************
	
	// author(s)
	private List<Person> author = new ArrayList<Person>();
	// editor(s)
	private List<Person> editor = new ArrayList<Person>();
	
	private List<Person> collection_editor = new ArrayList<Person>();
	
	private List<Person> container_author = new ArrayList<Person>();

	//*************************************************
	// dates
	// FIXME: there are some more dates available, see http://citationstyles.org/downloads/specification.html#date-variables
	//*************************************************	
	
	// date
	private Date issued;
	
	private Date accessed;
	
	//*************************************************
	// variables
	// see http://citationstyles.org/downloads/specification.html#standard-variables
	//*************************************************	
	
	// abstract
	private String abstractt;

	// notes made by a reader about the content of the resource
	private String annote;

	// the name of the archive
	private String archive;

	// the location within an archival collection (for example, box and folder)
	private String archive_location;

	// the place of the archive
	private String archive_place;

	// issuing authority (for patents) or judicial authority (such as court
	// for legal cases)
	private String authority;

	// ?
	private String call_number;

	// ?
	private String chapter_number;

	// the number used for the in_text citation mark in numeric styles
	private String citation_number;

	// the label used for the in_text citation mark in label styles
	private String citation_label;

	// collection number; for example, series number
	private String collection_number;

	// the tertiary title for the cited item; for example, a series title
	private String collection_title;

	// the secondary title for the cited item (book title for book chapters,
	// journal title for articles, etc.).
	private String container_title;

	/** documents of this bibsonomy post. not official part of csl */
	private List<DocumentCslWrapper> documents = new ArrayList<DocumentCslWrapper>();
	
	// doi identifier
	private String DOI;

	// an edition description
	private String edition;

	// the name or title of a related event such as a conference or hearing
	private String event;

	// the location or place for the related event
	private String event_place;
	
	private Date event_date;

	// The number of a preceding note containing the first reference to this
	// item. Relevant only for note_based styles, and null for first references.
	private String first_reference_note_number;

	//
	private String genre;

	//
	private String ISBN;
	
	private String ISSN;

	// the issue number for the container publication
	private String issue;

	// For legislation and patents; scope of geographic relevance for a
	// document.
	private String jurisdiction;

	// keyword(s)
	private String keyword;

	// a description to locate an item within some larger container or
	// collection; a volume or issue number is a kind of locator, for example.
	private String locator;

	// medium description (DVD, CD, etc.)
	private String medium;

	// a short inline note, often used to refer to additional details of the
	// resource
	private String note;

	// a document number; useful for reports and such
	private String number;

	// refers to the number of pages in a book or other document
	private String number_of_pages;

	// refers to the number of items in multi_volume books and such
	private String number_of_volumes;

	// the name of the original publisher
	private String original_publisher;

	// the place of the original publisher
	private String original_publisher_place;

	// title of a related original version; often useful in cases of translation
	private String original_title;

	// the range of pages an item covers in a containing item
	private String page;

	// the first page of an item within a containing item
	private String page_first;

	// the name of the publisher
	private String publisher;

	// the place of the publisher
	private String publisher_place;

	// for related referenced resources; used for legal case histories, but
	// may be relevant for other contexts.
	private String references;

	// a section description (for newspapers, etc.)
	private String section;

	// the (typically publication) status of an item; for example ;forthcoming;
	private String status;

	// the primary title for the cited item
	private String title;

	// url
	private String URL;

	// version
	private String version;

	// volume number for the container periodical
	private String volume;

	// The year suffix for author_date styles; e.g. the 'a' in '1999a'.
	private String year_suffix;

	// Type
	private String type;

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the author
	 */
	public List<Person> getAuthor() {
		return this.author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(List<Person> author) {
		this.author = author;
	}

	/**
	 * @return the editor
	 */
	public List<Person> getEditor() {
		return this.editor;
	}

	/**
	 * @param editor the editor to set
	 */
	public void setEditor(List<Person> editor) {
		this.editor = editor;
	}

	/**
	 * @return the collection_editor
	 */
	public List<Person> getCollection_editor() {
		return this.collection_editor;
	}

	/**
	 * @param collection_editor the collection_editor to set
	 */
	public void setCollection_editor(List<Person> collection_editor) {
		this.collection_editor = collection_editor;
	}

	/**
	 * @return the container_author
	 */
	public List<Person> getContainer_author() {
		return this.container_author;
	}

	/**
	 * @param container_author the container_author to set
	 */
	public void setContainer_author(List<Person> container_author) {
		this.container_author = container_author;
	}

	/**
	 * @return the issued
	 */
	public Date getIssued() {
		return this.issued;
	}

	/**
	 * @param issued the issued to set
	 */
	public void setIssued(Date issued) {
		this.issued = issued;
	}

	/**
	 * @return the accessed
	 */
	public Date getAccessed() {
		return this.accessed;
	}

	/**
	 * @param accessed the accessed to set
	 */
	public void setAccessed(Date accessed) {
		this.accessed = accessed;
	}

	/**
	 * @return the abstractt
	 */
	public String getAbstractt() {
		return this.abstractt;
	}

	/**
	 * @param abstractt the abstractt to set
	 */
	public void setAbstractt(String abstractt) {
		this.abstractt = abstractt;
	}

	/**
	 * @return the annote
	 */
	public String getAnnote() {
		return this.annote;
	}

	/**
	 * @param annote the annote to set
	 */
	public void setAnnote(String annote) {
		this.annote = annote;
	}

	/**
	 * @return the archive
	 */
	public String getArchive() {
		return this.archive;
	}

	/**
	 * @param archive the archive to set
	 */
	public void setArchive(String archive) {
		this.archive = archive;
	}

	/**
	 * @return the archive_location
	 */
	public String getArchive_location() {
		return this.archive_location;
	}

	/**
	 * @param archive_location the archive_location to set
	 */
	public void setArchive_location(String archive_location) {
		this.archive_location = archive_location;
	}

	/**
	 * @return the archive_place
	 */
	public String getArchive_place() {
		return this.archive_place;
	}

	/**
	 * @param archive_place the archive_place to set
	 */
	public void setArchive_place(String archive_place) {
		this.archive_place = archive_place;
	}

	/**
	 * @return the authority
	 */
	public String getAuthority() {
		return this.authority;
	}

	/**
	 * @param authority the authority to set
	 */
	public void setAuthority(String authority) {
		this.authority = authority;
	}

	/**
	 * @return the call_number
	 */
	public String getCall_number() {
		return this.call_number;
	}

	/**
	 * @param call_number the call_number to set
	 */
	public void setCall_number(String call_number) {
		this.call_number = call_number;
	}

	/**
	 * @return the chapter_number
	 */
	public String getChapter_number() {
		return this.chapter_number;
	}

	/**
	 * @param chapter_number the chapter_number to set
	 */
	public void setChapter_number(String chapter_number) {
		this.chapter_number = chapter_number;
	}

	/**
	 * @return the citation_number
	 */
	public String getCitation_number() {
		return this.citation_number;
	}

	/**
	 * @param citation_number the citation_number to set
	 */
	public void setCitation_number(String citation_number) {
		this.citation_number = citation_number;
	}

	/**
	 * @return the citation_label
	 */
	public String getCitation_label() {
		return this.citation_label;
	}

	/**
	 * @param citation_label the citation_label to set
	 */
	public void setCitation_label(String citation_label) {
		this.citation_label = citation_label;
	}

	/**
	 * @return the collection_number
	 */
	public String getCollection_number() {
		return this.collection_number;
	}

	/**
	 * @param collection_number the collection_number to set
	 */
	public void setCollection_number(String collection_number) {
		this.collection_number = collection_number;
	}

	/**
	 * @return the collection_title
	 */
	public String getCollection_title() {
		return this.collection_title;
	}

	/**
	 * @param collection_title the collection_title to set
	 */
	public void setCollection_title(String collection_title) {
		this.collection_title = collection_title;
	}

	/**
	 * @return the container_title
	 */
	public String getContainer_title() {
		return this.container_title;
	}

	/**
	 * @param container_title the container_title to set
	 */
	public void setContainer_title(String container_title) {
		this.container_title = container_title;
	}

	/**
	 * @return the documents
	 */
	public List<DocumentCslWrapper> getDocuments() {
		return this.documents;
	}

	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<DocumentCslWrapper> documents) {
		this.documents = documents;
	}

	/**
	 * @return the dOI
	 */
	public String getDOI() {
		return this.DOI;
	}

	/**
	 * @param dOI the dOI to set
	 */
	public void setDOI(String dOI) {
		DOI = dOI;
	}

	/**
	 * @return the edition
	 */
	public String getEdition() {
		return this.edition;
	}

	/**
	 * @param edition the edition to set
	 */
	public void setEdition(String edition) {
		this.edition = edition;
	}

	/**
	 * @return the event
	 */
	public String getEvent() {
		return this.event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(String event) {
		this.event = event;
	}

	/**
	 * @return the event_place
	 */
	public String getEvent_place() {
		return this.event_place;
	}

	/**
	 * @param event_place the event_place to set
	 */
	public void setEvent_place(String event_place) {
		this.event_place = event_place;
	}

	/**
	 * @return the event_date
	 */
	public Date getEvent_date() {
		return this.event_date;
	}

	/**
	 * @param event_date the event_date to set
	 */
	public void setEvent_date(Date event_date) {
		this.event_date = event_date;
	}

	/**
	 * @return the first_reference_note_number
	 */
	public String getFirst_reference_note_number() {
		return this.first_reference_note_number;
	}

	/**
	 * @param first_reference_note_number the first_reference_note_number to set
	 */
	public void setFirst_reference_note_number(String first_reference_note_number) {
		this.first_reference_note_number = first_reference_note_number;
	}

	/**
	 * @return the genre
	 */
	public String getGenre() {
		return this.genre;
	}

	/**
	 * @param genre the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}

	/**
	 * @return the iSBN
	 */
	public String getISBN() {
		return this.ISBN;
	}

	/**
	 * @param iSBN the iSBN to set
	 */
	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}

	/**
	 * @return the iSSN
	 */
	public String getISSN() {
		return this.ISSN;
	}

	/**
	 * @param iSSN the iSSN to set
	 */
	public void setISSN(String iSSN) {
		ISSN = iSSN;
	}

	/**
	 * @return the issue
	 */
	public String getIssue() {
		return this.issue;
	}

	/**
	 * @param issue the issue to set
	 */
	public void setIssue(String issue) {
		this.issue = issue;
	}

	/**
	 * @return the jurisdiction
	 */
	public String getJurisdiction() {
		return this.jurisdiction;
	}

	/**
	 * @param jurisdiction the jurisdiction to set
	 */
	public void setJurisdiction(String jurisdiction) {
		this.jurisdiction = jurisdiction;
	}

	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return this.keyword;
	}

	/**
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * @return the locator
	 */
	public String getLocator() {
		return this.locator;
	}

	/**
	 * @param locator the locator to set
	 */
	public void setLocator(String locator) {
		this.locator = locator;
	}

	/**
	 * @return the medium
	 */
	public String getMedium() {
		return this.medium;
	}

	/**
	 * @param medium the medium to set
	 */
	public void setMedium(String medium) {
		this.medium = medium;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return this.note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return this.number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the number_of_pages
	 */
	public String getNumber_of_pages() {
		return this.number_of_pages;
	}

	/**
	 * @param number_of_pages the number_of_pages to set
	 */
	public void setNumber_of_pages(String number_of_pages) {
		this.number_of_pages = number_of_pages;
	}

	/**
	 * @return the number_of_volumes
	 */
	public String getNumber_of_volumes() {
		return this.number_of_volumes;
	}

	/**
	 * @param number_of_volumes the number_of_volumes to set
	 */
	public void setNumber_of_volumes(String number_of_volumes) {
		this.number_of_volumes = number_of_volumes;
	}

	/**
	 * @return the original_publisher
	 */
	public String getOriginal_publisher() {
		return this.original_publisher;
	}

	/**
	 * @param original_publisher the original_publisher to set
	 */
	public void setOriginal_publisher(String original_publisher) {
		this.original_publisher = original_publisher;
	}

	/**
	 * @return the original_publisher_place
	 */
	public String getOriginal_publisher_place() {
		return this.original_publisher_place;
	}

	/**
	 * @param original_publisher_place the original_publisher_place to set
	 */
	public void setOriginal_publisher_place(String original_publisher_place) {
		this.original_publisher_place = original_publisher_place;
	}

	/**
	 * @return the original_title
	 */
	public String getOriginal_title() {
		return this.original_title;
	}

	/**
	 * @param original_title the original_title to set
	 */
	public void setOriginal_title(String original_title) {
		this.original_title = original_title;
	}

	/**
	 * @return the page
	 */
	public String getPage() {
		return this.page;
	}

	/**
	 * @param page the page to set
	 */
	public void setPage(String page) {
		this.page = page;
	}

	/**
	 * @return the page_first
	 */
	public String getPage_first() {
		return this.page_first;
	}

	/**
	 * @param page_first the page_first to set
	 */
	public void setPage_first(String page_first) {
		this.page_first = page_first;
	}

	/**
	 * @return the publisher
	 */
	public String getPublisher() {
		return this.publisher;
	}

	/**
	 * @param publisher the publisher to set
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	/**
	 * @return the publisher_place
	 */
	public String getPublisher_place() {
		return this.publisher_place;
	}

	/**
	 * @param publisher_place the publisher_place to set
	 */
	public void setPublisher_place(String publisher_place) {
		this.publisher_place = publisher_place;
	}

	/**
	 * @return the references
	 */
	public String getReferences() {
		return this.references;
	}

	/**
	 * @param references the references to set
	 */
	public void setReferences(String references) {
		this.references = references;
	}

	/**
	 * @return the section
	 */
	public String getSection() {
		return this.section;
	}

	/**
	 * @param section the section to set
	 */
	public void setSection(String section) {
		this.section = section;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the uRL
	 */
	public String getURL() {
		return this.URL;
	}

	/**
	 * @param uRL the uRL to set
	 */
	public void setURL(String uRL) {
		URL = uRL;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the volume
	 */
	public String getVolume() {
		return this.volume;
	}

	/**
	 * @param volume the volume to set
	 */
	public void setVolume(String volume) {
		this.volume = volume;
	}

	/**
	 * @return the year_suffix
	 */
	public String getYear_suffix() {
		return this.year_suffix;
	}

	/**
	 * @param year_suffix the year_suffix to set
	 */
	public void setYear_suffix(String year_suffix) {
		this.year_suffix = year_suffix;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
