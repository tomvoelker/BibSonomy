/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.MiscFieldConflictResolutionStrategy;
import org.bibsonomy.model.util.SimHash;

/**
 * This is the BibTex resource, which is used to handle BibTex-entries. It is
 * derived from {@link org.bibsonomy.model.Resource}. It contains a lot of
 * BibTex fields like the author, publisher etc.
 *
 * @author Christian Schenk
 * @author jensi
 * @author tom 
 */
public class BibTex extends Resource {
	/** for persistence (Serializable) */
	private static final long serialVersionUID = -8528225443908615779L;
	
	/** Logging used for problems cloning objects **/
	private static final Log log = LogFactory.getLog(BibTex.class);
	/**
	 * Use this key to reference a citation, e.g. \cite{hotho2006information}.
	 * TODO: rename to something like citationKey ?
	 */
	private String bibtexKey;

	/**
	 * This key is used by BibTeX on sorting purpose if there is neither an
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
	// TODO: rename to authors
	private List<PersonName> author;
	private String booktitle;
	private String chapter;
	private String crossref;
	private String edition;
	// TODO: rename to editors
	private List<PersonName> editor;
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
	// TODO: rename to privateNote
	// TODO: this is user specific and should be moved to the post
	private String privnote;
	private Map<String, String> miscFields;

	private List<BibTexExtra> extraUrls;

	private ScraperMetadata scraperMetadata;

	/**
	 *  param to check when the 'misc' field has been parsed. When it is true,
	 *  one can be sure that all key/value pairs contained in the 'misc'-field 
	 *  are also present in the miscFields-map.
	 *  NOTE: This variable has been cannibalized to represent the sync state.
	 *        One may rename it for that purpose.
	 */
	private boolean miscFieldParsed = true; 

	/**
	 * A document attached to this bibtex resource.
	 */
	private List<Document> documents;

	/**
	 * @return privnote
	 */
	public String getPrivnote() {
		return this.privnote;
	}

	/**
	 * @param privnote
	 */
	public void setPrivnote(final String privnote) {
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
	public void setAddress(final String address) {
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
	public void setAnnote(final String annote) {
		this.annote = annote;
	}

	/**
	 * @return author
	 */
	public List<PersonName> getAuthor() {
		return this.author;
	}

	/**
	 * @param author
	 */
	public void setAuthor(final List<PersonName> author) {
		this.author = author;
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
	public void setAbstract(final String bibtexAbstract) {
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
	public void setBibtexKey(final String bibtexKey) {
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
	public void setKey(final String key) {
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
	public void setBooktitle(final String booktitle) {
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
	public void setChapter(final String chapter) {
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
	public void setCrossref(final String crossref) {
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
	public void setDay(final String day) {
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
	public void setEdition(final String edition) {
		this.edition = edition;
	}

	/**
	 * @return editor
	 */
	public List<PersonName> getEditor() {
		return this.editor;
	}


	/**
	 * @param editor
	 */
	public void setEditor(final List<PersonName> editor) {
		this.editor = editor;
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
	public void setEntrytype(final String entrytype) {
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
	public void setHowpublished(final String howpublished) {
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
	public void setInstitution(final String institution) {
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
	public void setJournal(final String journal) {
		this.journal = journal;
	}

	/**
	 * @return misc
	 */
	public String getMisc() {
		if(!this.miscFieldParsed){ // if not in sync do sync
			this.syncMiscFields(MiscFieldConflictResolutionStrategy.MISC_FIELD_MAP_WINS); // by default map wins 
		};
		return this.misc;
	}

	/**
	 * A new misc string is set (replaced the old one). 
	 * NOTE: This will also *replace* the miscFields map, not sync with it!
	 *  
	 * @param misc
	 */
	public void setMisc(final String misc) throws InvalidModelException {
		String oldmisc = this.misc; // save old misc in case this goes wrong
		this.misc = misc;
		this.miscFieldParsed = false;// sync is broken
		try {
			this.parseMiscField();       // sync will be restored
		} catch (Exception e)  {
			this.misc = oldmisc;  // in case our sync went wrong
			throw new InvalidModelException("Misc string " + misc + " could not be set."); 
		}	
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
	public void setMonth(final String month) {
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
	public void setNote(final String note) {
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
	public void setNumber(final String number) {
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
	public void setOrganization(final String organization) {
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
	public void setPages(final String pages) {
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
	public void setPublisher(final String publisher) {
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
	public void setSchool(final String school) {
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
	public void setSeries(final String series) {
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
	public void setVolume(final String volume) {
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
	public void setYear(final String year) {
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
	public void setType(final String type) {
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
	public void setUrl(final String url) {
		this.url = url;
	}

	@Override
	public void recalculateHashes() {
		this.setIntraHash(SimHash.getSimHash(this, HashID.INTRA_HASH));
		this.setInterHash(SimHash.getSimHash(this, HashID.INTER_HASH));
	}

	/**
	 * FIXME: this method must be called after {@link #parseMiscField()} or {@link #syncMiscFields(MiscFieldConflictResolutionStrategy)}
	 * check state and throw an IllegalStateException?
	 *
	 * @param miscKey
	 * @return String
	 */
	public String getMiscField(final String miscKey) {
		if (this.miscFields == null || !this.miscFields.containsKey(miscKey)) return null;
		if(!this.miscFieldParsed){
			this.syncMiscFields(MiscFieldConflictResolutionStrategy.MISC_FIELD_MAP_WINS); // by default map wins 
		}
		return this.miscFields.get(miscKey);
	}

	/**
	 * 
	 * @param miscKey
	 * @param value
	 */
	public void addMiscField(final String miscKey, final String value) {
		this.syncMiscFields(MiscFieldConflictResolutionStrategy.MISC_FIELD_MAP_WINS); // map allocate implied and sync
		this.miscFields.put(miscKey, value);  
		this.syncMiscFields(MiscFieldConflictResolutionStrategy.MISC_FIELD_MAP_WINS); // sync restored

	}

	/**
	 * Getter for MiscFields. This returns the map
	 * containing the key/value pairs of the internal map.
	 *
	 * FIXME: an unmodifiable map would be good here - but breaks the depthEqualityTester elsewhere (dbe)
	 * NOTE:  We assume here that the misc and miscField are in sync by checking miscFieldParsed
	 *
	 * @return an map containing the miscFields
	 */
	public Map<String, String> getMiscFields() {
		if(!this.miscFieldParsed){  // if not parsed we have to sync before get
			this.syncMiscFields(MiscFieldConflictResolutionStrategy.MISC_FIELD_MAP_WINS);
		};
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
	public void setDocuments(final List<Document> documents) {
		this.documents = documents;
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
	public void setScraperMetadata(final ScraperMetadata scraperMetadata) {
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
	public void setScraperId(final int scraperId) {
		this.scraperId = scraperId;
	}

	/**
	 * @param extraUrls the extraUrls to set
	 */
	public void setExtraUrls(final List<BibTexExtra> extraUrls) {
		this.extraUrls = extraUrls;
	}

	/**
	 * @return the extraUrls
	 */
	public List<BibTexExtra> getExtraUrls() {
		return extraUrls;
	}

	@Override
	public String toString() {
		return super.toString() + " by <" + author + ">";
	}

	/**
	 * Setter for all misc fields. By setting all misc fields, the string in 
	 * misc becomes obsolete and has to be replaced.   
	 * 
	 * @param miscFields
	 */
	public void setMiscFields(final Map<String,String> miscFields) {
		this.miscFields = miscFields;
		this.miscFieldParsed = false;   // sync broken since new values
		this.serializeMiscFields();     // replace misc with serialized 
		                                // parsed miscField -> sync
	}

	/**
	 * Parses the 'misc'-field string and stores the obtained key/valued pairs
	 * in the internal miscFields map.
	 * @throws InvalidModelException
	 */
	public void parseMiscField() throws InvalidModelException {
		if (this.misc != null && this.misc.length() > 0) { // in case there is really something to do (content in misc string)
				this.miscFields = BibTexUtils.parseMiscFieldString(this.misc);
			}
	    this.miscFieldParsed = true;
	}

	/**
	 * Serializes the internal miscFields map into the a string 
	 * representation and stores it in the 'misc'-field.
	 * NOTE: this will override all misc fields that are not parsed before using {@link #parseMiscField()}
	 */
	public void serializeMiscFields() {
		this.misc = BibTexUtils.serializeMapToBibTeX(this.miscFields);
		this.miscFieldParsed = true;
	}

	/**
	 * Synchronizes the misc and miscFields attributes of this object in
	 * the following way:
	 *
	 * 1/ the content of the 'misc' field is parsed and stored as key/valued pairs in the 'miscFields' attribute
	 *    (possibly overwriting existing values)
	 * 2/ the 'miscFields'-attribute is serialized and the result is stored in the 'misc' attribute
	 * @deprecated use {@link #syncMiscFields(MiscFieldConflictResolutionStrategy)} if you want to sync the misc field
	 * with the map representation and {@link #resetMiscFieldMap()} if you want to reinitialize the misc field map
	 */
	@Deprecated // TODO: remove with 4.0.0
	public void syncMiscFields() {
		this.resetMiscFieldMap();
	}

	/**
	 * resets the map representation with the content of the misc field
	 * and serializes the map to the misc field
	 */
	public void resetMiscFieldMap() {
		this.parseMiscField();
		this.serializeMiscFields();
		this.miscFieldParsed = true;
	}

	/**
	 * syncs the misc field with the misc fields map
	 * @param conflictResolutionStrategy the strategy to use when both fields contain the same key
	 */
	public void syncMiscFields(final MiscFieldConflictResolutionStrategy conflictResolutionStrategy) {
		final Map<String, String> miscFieldParsed = BibTexUtils.parseMiscFieldString(this.misc);

		if (this.miscFields == null) {
			this.miscFields = new LinkedHashMap<>();
		}

		// add all misc field entries to the map
		for (final Map.Entry<String, String> miscFieldEntry : miscFieldParsed.entrySet()) {
			final String miscFieldKey = miscFieldEntry.getKey();
			String miscFieldValue = miscFieldEntry.getValue();

			if  (this.miscFields.containsKey(miscFieldKey)) {
				miscFieldValue = conflictResolutionStrategy.resoloveConflict(miscFieldKey, miscFieldValue, this.miscFields.get(miscFieldKey));
			}

			this.miscFields.put(miscFieldKey, miscFieldValue);
		}

		// write all to the misc field
		this.serializeMiscFields();
	}

	/**
	 * Check whether the 'misc'-field of this bibtex entry has been parsed. When
	 * this is true, one can be sure that all key/value pairs present in the 
	 * 'misc' field are also present in the internal miscFields hashmap.
	 *
	 * @return <code>true</code> if the 'misc' field is parsed, false otherwise. 
	 */
	public boolean isMiscFieldParsed() {
		return this.miscFieldParsed;
	}

	/**
	 * Remove a misc field from the parsed map.
	 *
	 * NOTE: you have to call {@link #syncMiscFields(MiscFieldConflictResolutionStrategy)} or
	 * {@link #serializeMiscFields()}
	 *
	 * @param miscKey - the requested key
	 * @return - the previous value for key
	 */
	public String removeMiscField(final String miscKey) {
		if (this.miscFields != null && this.miscFields.containsKey(miscKey)) {
			if(!this.miscFieldParsed){ // if misc and miscFields are not in sync
				this.syncMiscFields(MiscFieldConflictResolutionStrategy.MISC_FIELD_MAP_WINS); // default miscFields wins
			}
			String valueForKey = this.miscFields.remove(miscKey);
			this.miscFieldParsed = false;  // sync is broken 
			this.serializeMiscFields();    // sync is restored 
			return valueForKey;
		}
		return null;
	}

	/**
	 * clear parsed misc fields 
	 * NOTE: Unsure if it is wise to provide such a method in a misc-miscField-sync setting
	 */
	public void clearMiscFields() {
		if (this.miscFields != null) {
			this.miscFields.clear();
		}
		this.miscFieldParsed = false; // this is true in any case
									  // at this point
	}

	/**
	 * @param role
	 * @return
	 */
	public List<PersonName> getPersonNamesByRole(PersonResourceRelationType role) {
		final List<PersonName> publicationNames;
		if (role == PersonResourceRelationType.AUTHOR) {
			publicationNames = getAuthor();
		} else if (role == PersonResourceRelationType.EDITOR) {
			publicationNames = getEditor();
		} else {
			publicationNames = null;
		}
		return publicationNames;
	}
	
	/**
	 * 	clone an BibTex object 
	 */

	public BibTex clone(){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (BibTex) ois.readObject();
		} catch (IOException e) {
			log.error("IO Problem cloning an object: in BibTeX ", e);
			return null;
		} catch (ClassNotFoundException e) {
			log.error("ClassNotFroundException during cloning an object: in BibTex", e);
			return null;
		}
	}

}
