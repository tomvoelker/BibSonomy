/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.model;

import java.util.HashMap;
import java.util.List;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.SimHash;

/**
 * This is the BibTex resource, which is used to handle BibTex-entries. It is
 * derived from {@link org.bibsonomy.model.Resource}. It contains a lot of
 * BibTex fields like the author, publisher etc.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTex extends Resource {

	/**
	 * Use this key to reference a citation, e.g. \cite{hotho2006information}.
	 * TODO: rename to something like citationKey ?
	 */
	private String bibtexKey;

	/**
	 * This key is used by bibtex on sorting purpose if there is neither an
	 * {@link #author} nor an {@link #editor} defined.
	 * TODO: rename to something like sortingKey ?
	 */
	private String key;

	// TODO: document me..
	private String misc;
	private String bibtexAbstract;
	private String entrytype;
	private String address;
	private String annote;
	private String author;
	private List<PersonName> authorList;
	private String booktitle;
	private String chapter;
	private String crossref;
	private String edition;
	private String editor;
	private List<PersonName> editorList;
	private String howpublished;
	private String institution;
	private String organization;
	private String journal;
	private String note;
	private String number;
	private String pages;
	private String publisher;
	private String school;
	private String series;
	private String volume;
	private String day;
	private String month;
	private String year;
	private String type;
	private int scraperId;
	private String url;
	private String privnote;
	private HashMap<String, String> miscFields;
	// this field holds the description part of an openURL to this bibtex object
	private String openURL;
	
	private List<BibTexExtra> extraUrls;
	
	private ScraperMetadata scraperMetadata;
	
	/**
	 * A document attached to this bibtex resource.
	 */
	private List<Document> documents;

	/**
	 * @return openURL
	 */
	public String getOpenURL() {
		return this.openURL;
	}

	/**
	 * @param openURL
	 */
	public void setOpenURL(String openURL) {
		this.openURL = openURL;
	}

	/**
	 * @return privnote
	 */
	public String getPrivnote() {
		return this.privnote;
	}

	/**
	 * @param privnote
	 */
	public void setPrivnote(String privnote) {
		this.privnote = privnote;
	}

	/**
	 * @return address
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * @param address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return annote
	 */
	public String getAnnote() {
		return this.annote;
	}

	/**
	 * @param annote
	 */
	public void setAnnote(String annote) {
		this.annote = annote;
	}

	/**
	 * @return author
	 */
	public String getAuthor() {
		return this.author;
	}

	/**
	 * @param author
	 */
	public void setAuthor(String author) {
		this.author = author;
		this.authorList = null;
	}
	
	/**
	 * @return authorList
	 */
	public List<PersonName> getAuthorList() {
		if (this.authorList == null) {
			this.authorList = PersonNameUtils.extractList(this.author);
		}
		return this.authorList;
	}

	/**
	 * @return bibtexAbstract
	 */
	public String getAbstract() {
		return this.bibtexAbstract;
	}

	/**
	 * @param bibtexAbstract
	 */
	public void setAbstract(String bibtexAbstract) {
		this.bibtexAbstract = bibtexAbstract;
	}

	/**
	 * @return bibtexKey
	 */
	public String getBibtexKey() {
		return this.bibtexKey;
	}

	/**
	 * @param bibtexKey
	 */
	public void setBibtexKey(String bibtexKey) {
		this.bibtexKey = bibtexKey;
	}

	/**
	 * @return bkey
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return booktitle
	 */
	public String getBooktitle() {
		return this.booktitle;
	}

	/**
	 * @param booktitle
	 */
	public void setBooktitle(String booktitle) {
		this.booktitle = booktitle;
	}

	/**
	 * @return chapter
	 */
	public String getChapter() {
		return this.chapter;
	}

	/**
	 * @param chapter
	 */
	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	/**
	 * @return crossref
	 */
	public String getCrossref() {
		return this.crossref;
	}

	/**
	 * @param crossref
	 */
	public void setCrossref(String crossref) {
		this.crossref = crossref;
	}

	/**
	 * @return day
	 */
	public String getDay() {
		return this.day;
	}

	/**
	 * @param day
	 */
	public void setDay(String day) {
		this.day = day;
	}

	/**
	 * @return edition
	 */
	public String getEdition() {
		return this.edition;
	}

	/**
	 * @param edition
	 */
	public void setEdition(String edition) {
		this.edition = edition;
	}

	/**
	 * @return editor
	 */
	public String getEditor() {
		return this.editor;
	}

	/**
	 * @param editor
	 */
	public void setEditor(String editor) {
		this.editor = editor;
		this.editorList = null;
	}
	
	/**
	 * @return editorList
	 */
	public List<PersonName> getEditorList() {
		if (this.editorList == null) {
			this.editorList = PersonNameUtils.extractList(this.editor);
		}
		return this.editorList;
	}

	/**
	 * @return entrytype
	 */
	public String getEntrytype() {
		return this.entrytype;
	}

	/**
	 * @param entrytype
	 */
	public void setEntrytype(String entrytype) {
		this.entrytype = entrytype;
	}

	/**
	 * @return howpublished
	 */
	public String getHowpublished() {
		return this.howpublished;
	}

	/**
	 * @param howpublished
	 */
	public void setHowpublished(String howpublished) {
		this.howpublished = howpublished;
	}

	/**
	 * @return institution
	 */
	public String getInstitution() {
		return this.institution;
	}

	/**
	 * @param institution
	 */
	public void setInstitution(String institution) {
		this.institution = institution;
	}

	/**
	 * @return journal
	 */
	public String getJournal() {
		return this.journal;
	}

	/**
	 * @param journal
	 */
	public void setJournal(String journal) {
		this.journal = journal;
	}

	/**
	 * @return misc
	 */
	public String getMisc() {
		return this.misc;
	}

	/**
	 * @param misc
	 */
	public void setMisc(String misc) {
		this.misc = misc;
	}

	/**
	 * @return month
	 */
	public String getMonth() {
		return this.month;
	}

	/**
	 * @param month
	 */
	public void setMonth(String month) {
		this.month = month;
	}

	/**
	 * @return note
	 */
	public String getNote() {
		return this.note;
	}

	/**
	 * @param note
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * @return number
	 */
	public String getNumber() {
		return this.number;
	}

	/**
	 * @param number
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return organization
	 */
	public String getOrganization() {
		return this.organization;
	}

	/**
	 * @param organization
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}

	/**
	 * @return pages
	 */
	public String getPages() {
		return this.pages;
	}

	/**
	 * @param pages
	 */
	public void setPages(String pages) {
		this.pages = pages;
	}

	/**
	 * @return publisher
	 */
	public String getPublisher() {
		return this.publisher;
	}

	/**
	 * @param publisher
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	/**
	 * @return school
	 */
	public String getSchool() {
		return this.school;
	}

	/**
	 * @param school
	 */
	public void setSchool(String school) {
		this.school = school;
	}

	/**
	 * @return series
	 */
	public String getSeries() {
		return this.series;
	}

	/**
	 * @param series
	 */
	public void setSeries(String series) {
		this.series = series;
	}

	/**
	 * @return volume
	 */
	public String getVolume() {
		return this.volume;
	}

	/**
	 * @param volume
	 */
	public void setVolume(String volume) {
		this.volume = volume;
	}

	/**
	 * @return year
	 */
	public String getYear() {
		return this.year;
	}

	/**
	 * @param year
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * @return type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return simHash0
	 */
	public String getSimHash0() {
		return SimHash.getSimHash0(this);
	}

	/**
	 * @return simHash1
	 */
	public String getSimHash1() {
		return SimHash.getSimHash1(this);
	}

	/**
	 * @return simHash2
	 */
	public String getSimHash2() {
		return SimHash.getSimHash2(this);
	}

	/**
	 * @return simHash3
	 */
	public String getSimHash3() {
		return SimHash.getSimHash3();
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void recalculateHashes() {
		this.setIntraHash(SimHash.getSimHash(this, HashID.INTRA_HASH));
		this.setInterHash(SimHash.getSimHash(this, HashID.INTER_HASH));
	}

	/**
	 * @param key
	 * @return String
	 */
	public String getMiscField(String key) {
		if (this.miscFields == null || this.miscFields.containsKey(key) == false) return null;
		return this.miscFields.get(key);
	}

	/**
	 * @param key
	 * @param value
	 */
	public void addMiscField(String key, String value) {
		if (this.miscFields == null) {
			this.miscFields = new HashMap<String, String>();
		}
		this.miscFields.put(key, value);
	}

	/**
	 * Getter for MiscFields
	 * 
	 * @return a HashMap containing the miscFields
	 */
	public HashMap<String, String> getMiscFields() {
		return this.miscFields;
	}

	/** 
	 * @return The list of documents associated with this BibTeX post.
	 */
	public List<Document> getDocuments() {
		return this.documents;
	}

	/**
	 * @param documents
	 */
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}		
	
	@Override
	public String toString() {
		return super.toString() + " by <" + author + ">";	
	}

	public List<BibTexExtra> getExtraUrls() {
		return this.extraUrls;
	}

	public void setExtraUrls(List<BibTexExtra> extraUrls) {
		this.extraUrls = extraUrls;
	}

	/**
	 * The meta data from the scraper which scraped this publication.
	 * 
	 * @return The scraper meta data
	 */
	public ScraperMetadata getScraperMetadata() {
		return this.scraperMetadata;
	}

	/**
	 * Set the metadata from the scraper which scraped this publication.
	 * 
	 * @param scraperMetadata
	 */
	public void setScraperMetadata(ScraperMetadata scraperMetadata) {
		this.scraperMetadata = scraperMetadata;
	}
	/**
	 * @return scraperId
	 */
	public int getScraperId() {
		return this.scraperId;
	}

	/**
	 * @param scraperId
	 */
	public void setScraperId(int scraperId) {
		this.scraperId = scraperId;
	}

}