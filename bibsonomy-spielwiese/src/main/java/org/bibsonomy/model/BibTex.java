package org.bibsonomy.model;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This is the BibTex resource, which is used to handle BibTex-entries. It is
 * derived from {@link org.bibsonomy.model.Resource}. It contains a lot of
 * BibTex fields like the author, publisher etc.
 * 
 * @author Christian Schenk
 */
public class BibTex extends Resource {

	
	
	
	private int group;
	private String userName;
	// private String type;
	// private char simhash0, ..., simhash3 FIXME ???
	// private String privnote;
	// private int scraperId;

	private String bibtexKey;
	private String bKey;
	private String misc;
	private String bibtexAbstract;
	private String description;
	private String entrytype;
	private String address;
	private String annote;
	private String author;
	//private String key; FIXME ???
	private String title;
	private String booktitle;
	private String chapter;
	private String crossref;
	private String edition;
	private String editor;
	private String howPublished;
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

	private List <Tag> bibtexTags;
    private BibtexUrl url;
    
	public List<Tag> getBibtexTags() {
		return bibtexTags;
	}
	public void setBibtexTags(List<Tag> bibtexTags) {
		this.bibtexTags = bibtexTags;
	}
	public String getAddress() {
		return this.address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAnnote() {
		return this.annote;
	}
	public void setAnnote(String annote) {
		this.annote = annote;
	}
	public String getAuthor() {
		return this.author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getBibtexAbstract() {
		return this.bibtexAbstract;
	}
	public void setBibtexAbstract(String bibtexAbstract) {
		this.bibtexAbstract = bibtexAbstract;
	}
	public String getBibtexKey() {
		return this.bibtexKey;
	}
	public void setBibtexKey(String bibtexKey) {
		this.bibtexKey = bibtexKey;
	}
	public String getBKey() {
		return this.bKey;
	}
	public void setBKey(String key) {
		this.bKey = key;
	}
	public String getBooktitle() {
		return this.booktitle;
	}
	public void setBooktitle(String booktitle) {
		this.booktitle = booktitle;
	}
	public String getChapter() {
		return this.chapter;
	}
	public void setChapter(String chapter) {
		this.chapter = chapter;
	}
	public String getCrossref() {
		return this.crossref;
	}
	public void setCrossref(String crossref) {
		this.crossref = crossref;
	}
	public String getDay() {
		return this.day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEdition() {
		return this.edition;
	}
	public void setEdition(String edition) {
		this.edition = edition;
	}
	public String getEditor() {
		return this.editor;
	}
	public void setEditor(String editor) {
		this.editor = editor;
	}
	public String getEntrytype() {
		return this.entrytype;
	}
	public void setEntrytype(String entrytype) {
		this.entrytype = entrytype;
	}
	public int getGroup() {
		return this.group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public String getHowPublished() {
		return this.howPublished;
	}
	public void setHowPublished(String howPublished) {
		this.howPublished = howPublished;
	}
	public String getInstitution() {
		return this.institution;
	}
	public void setInstitution(String institution) {
		this.institution = institution;
	}
	public String getJournal() {
		return this.journal;
	}
	public void setJournal(String journal) {
		this.journal = journal;
	}
	public String getMisc() {
		return this.misc;
	}
	public void setMisc(String misc) {
		this.misc = misc;
	}
	public String getMonth() {
		return this.month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getNote() {
		return this.note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getNumber() {
		return this.number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getOrganization() {
		return this.organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getPages() {
		return this.pages;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
//	public String getPrivnote() {
//		return this.privnote;
//	}
//	public void setPrivnote(String privnote) {
//		this.privnote = privnote;
//	}
	public String getPublisher() {
		return this.publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getSchool() {
		return this.school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
//	public int getScraperId() {
//		return this.scraperId;
//	}
//	public void setScraperId(int scraperId) {
//		this.scraperId = scraperId;
//	}
	public String getSeries() {
		return this.series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
//	public String getType() {
//		return this.type;
//	}
//	public void setType(String type) {
//		this.type = type;
//	}
	public BibtexUrl getUrl() {
		return this.url;
	}
	public void setUrl(BibtexUrl url) {
		this.url = url;
	}
	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getVolume() {
		return this.volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getYear() {
		return this.year;
	}
	public void setYear(String year) {
		this.year = year;
	}
}