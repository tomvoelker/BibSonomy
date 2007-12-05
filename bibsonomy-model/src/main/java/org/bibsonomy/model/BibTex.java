package org.bibsonomy.model;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.HashID;
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
	private String bKey;

	// TODO: document me..
	private String misc;
	private String bibtexAbstract;
	private String entrytype;
	private String address;
	private String annote;
	private String author;
	private List<PersonName> authorList;
	private String title;
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
	private HashMap<String,String> miscFields;
	private String openURL; // this field holds the description part of an openURL to this bibtex object

	public String getOpenURL() {
		return this.openURL;
	}

	public void setOpenURL(String openURL) {
		this.openURL = openURL;
	}

	public String getPrivnote() {
		return this.privnote;
	}

	public void setPrivnote(String privnote) {
		this.privnote = privnote;
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
		this.authorList = null;
	}
	
	public List<PersonName> getAuthorList() {
		if (this.authorList == null) {
			this.authorList = PersonNameUtils.extractList(this.author);
		}
		return this.authorList;
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

	public void setBKey(String bkey) {
		this.bKey = bkey;
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
		this.editorList = null;
	}
	
	public List<PersonName> getEditorList() {
		if (this.editorList == null) {
			this.editorList = PersonNameUtils.extractList(this.editor);
		}
		return this.editorList;
	}

	public String getEntrytype() {
		return this.entrytype;
	}

	public void setEntrytype(String entrytype) {
		this.entrytype = entrytype;
	}

	public String getHowpublished() {
		return this.howpublished;
	}

	public void setHowpublished(String howpublished) {
		this.howpublished = howpublished;
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

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getScraperId() {
		return this.scraperId;
	}

	public void setScraperId(int scraperId) {
		this.scraperId = scraperId;
	}

	public String getSimHash0() {
		return SimHash.getSimHash0(this);
	}

	public String getSimHash1() {
		return SimHash.getSimHash1(this);
	}

	public String getSimHash2() {
		return SimHash.getSimHash2(this);
	}

	public String getSimHash3() {
		return SimHash.getSimHash3();
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void recalculateHashes() {
		this.setIntraHash(SimHash.getSimHash(this, HashID.INTRA_HASH));
		this.setInterHash(SimHash.getSimHash(this, HashID.INTER_HASH));
	}
	
	/**
	 * return a bibtex string representation of this BibTex Object
	 * 
	 * @return String bibtexString
	 */
	public String toBibtexString() {
		try {
			final BeanInfo bi = Introspector.getBeanInfo(this.getClass());
			
			StringBuffer sb = new StringBuffer();
			sb.append("@");
			sb.append(this.getEntrytype());
			sb.append("{");
			sb.append(this.getBibtexKey());
			sb.append(",\n");
			for (final PropertyDescriptor d : bi.getPropertyDescriptors()) {
				final Method getter = d.getReadMethod();
				// loop over all String attributes
				if (d.getPropertyType().equals(String.class) 
						&& getter.invoke(this, (Object[]) null) != null) {
					sb.append(d.getName());
					sb.append(" = ");
					sb.append("{");
					sb.append( (String) getter.invoke(this, (Object[]) null) );
					sb.append("}, \n");					
				}
			}				
			sb.append("}");	
			return sb.toString();
		} catch (IntrospectionException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}		
		return null;
	}
	
	/**
	 * Access a 
	 * 
	 * @param key
	 * @return String
	 */
	public String getMiscField(String key) {
		if (this.miscFields == null) {
			return null;
		}
		if (this.miscFields.containsKey(key)) {
			return this.miscFields.get(key);
		}
		return null;
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
}