package org.bibsonomy.importer.event.iswc.model;

/**
 * This class represents a Publication out of a RDF file with SWRC and FOAF structure.
 * The values are already in a bibtex friendly format.
 * @author tst
 */
public class Publication {
	
	/**
	 * bibtex field: author 
	 */
	private String author;
	/**
	 * bibtex field: title
	 */
	private String title;
	/**
	 * bibtex field: keywords
	 */
	private String keywords;
	/**
	 * bibtex field: abstract
	 */
	private String bibabstract;
	/**
	 * key of this Publication (may be not unique)
	 */
	private String bibtexkey;
	/**
	 * entrytype of this Publication 
	 */
	private String entrytype;
	/**
	 * bibtex field: month
	 */
	private String month;
	/**
	 * bibtex field: year
	 */
	private String year;
	/**
	 * bibtex field: crossref
	 */
	private String crossref;
	/**
	 * bibtex field: pages
	 */
	private String pages;
	/**
	 * bibtex field: adress
	 */
	private String address;
	/**
	 * bibtex field: booktitle
	 */
	private String booktitle;
	/**
	 * bibtex field: publisher
	 */
	private String publisher;
	/**
	 * bibtex field: series
	 */
	private String series;
	/**
	 * bibtex field: volume
	 */
	private String volume;
	/**
	 * bibtex field: editor
	 */
	private String editor;
	/**
	 * bibtex field: url
	 */
	private String url;
	
	/**
	 * init the Publication with empty strings (needed for building the content of the
	 * fields step by step).
	 */
	public Publication(){
		author = "";
		title = "";
		keywords = "";
		bibabstract = "";
		bibtexkey = "";
		entrytype = "";
		month = "";
		year = "";
		crossref = "";
		pages = "";
		address = "";
		booktitle = "";
		publisher = "";
		series = "";
		volume = "";
		editor = "";
		url="";
	}
	
	/**
	 * get bibtex field: url
	 * @return value of field url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * set bibtex field: url
	 * @param url new value for the field url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * get bibtex field: address
	 * @return value of field address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * set bibtex field: address
	 * @param address new value for the field address
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	
	/**
	 * get bibtex field: booktitle
	 * @return value of field booktitle
	 */
	public String getBooktitle() {
		return booktitle;
	}
	/**
	 * set bibtex field: booktitle
	 * @param booktitle new value for the field booktitle
	 */
	public void setBooktitle(String booktitle) {
		this.booktitle = booktitle;
	}
	
	/**
	 * get bibtex field: editor
	 * @return value of field editor
	 */
	public String getEditor() {
		return editor;
	}
	/**
	 * set bibtex field: editor
	 * @param editor new value for the field editor
	 */
	public void setEditor(String editor) {
		this.editor = editor;
	}
	
	/**
	 * get bibtex field: publisher
	 * @return value of field publisher
	 */
	public String getPublisher() {
		return publisher;
	}
	/**
	 * set bibtex field: publisher
	 * @param publisher new value for the field publisher
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
	/**
	 * get bibtex field: series
	 * @return value of field series
	 */
	public String getSeries() {
		return series;
	}
	/**
	 * set bibtex field: series
	 * @param series new value for the field series
	 */
	public void setSeries(String series) {
		this.series = series;
	}
	
	/**
	 * get bibtex field: volume
	 * @return value of field volume
	 */
	public String getVolume() {
		return volume;
	}
	/**
	 * set bibtex field: volume
	 * @param volume new value for the field volume
	 */
	public void setVolume(String volume) {
		this.volume = volume;
	}
	
	/**
	 * get bibtex field: pages 
	 * @return value of field pages 
	 */
	public String getPages() {
		return pages;
	}
	/**
	 * set bibtex field: pages
	 * @param pages new value for the field pages 
	 */
	public void setPages(String pages) {
		this.pages = pages;
	}
	
	/**
	 * get bibtex field: mont
	 * @return value of field mont
	 */
	public String getMonth() {
		return month;
	}
	/**
	 * set bibtex field: mont
	 * @param month new value for the field mont
	 */
	public void setMonth(String month) {
		this.month = month;
	}
	
	/**
	 * get bibtex field: year
	 * @return value of field year
	 */
	public String getYear() {
		return year;
	}
	/**
	 * set bibtex field: year
	 * @param year new value for the field year
	 */
	public void setYear(String year) {
		this.year = year;
	}
	
	/**
	 * get the entrytype of this Publication
	 * @return entrytype of this publication
	 */
	public String getEntrytype() {
		return entrytype;
	}
	/**
	 * set the entrytype of this publication
	 * @param entrytype new value for entrytype
	 */
	public void setEntrytype(String entrytype) {
		this.entrytype = entrytype;
	}

	/**
	 * get bibtex field: author
	 * @return value of field author
	 */
	public String getAuthor() {
		return author;
	}
	/**
	 * set bibtex field: author
	 * @param author new value for the field author
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * get bibtex field: abstract
	 * @return value of field abstract
	 */
	public String getBibabstract() {
		return bibabstract;
	}
	/**
	 * set bibtex field: abstract
	 * @param bibabstract new value for the field abstract
	 */
	public void setBibabstract(String bibabstract) {
		this.bibabstract = bibabstract;
	}

	/**
	 * get bibtex field: keywords
	 * @return value of field keywords
	 */
	public String getKeywords() {
		return keywords;
	}
	/**
	 * set bibtex field: keywords
	 * @param keywords new value for the field keywords
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	/**
	 * get bibtex field: title
	 * @return value of field title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * set bibtex field: title
	 * @param title new value for the field title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * get the (bibtex) key of this Publication
	 * @return key of this Publication
	 */
	public String getBibtexkey() {
		return bibtexkey;
	}
	/**
	 * set the (bibtex) key of this Publication
	 * @param bibtexkey new value for the key
	 */
	public void setBibtexkey(String bibtexkey) {
		this.bibtexkey = bibtexkey;
	}

	/**
	 * get bibtex field: crossref
	 * @return value of field crossref
	 */
	public String getCrossref() {
		return crossref;
	}
	/**
	 * set bibtex field: crossref
	 * @param crossref new value for the field crossref
	 */
	public void setCrossref(String crossref) {
		this.crossref = crossref;
	}
	
}
