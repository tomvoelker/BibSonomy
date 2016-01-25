/**
 * BibSonomy-OpenAccess - Check Open Access Policies for Publications
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package de.unikassel.puma.openaccess.sword;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 
 * @author sven
 *
 * @param <T>
 */
public class PumaData<T extends Resource> implements Serializable {
	private static final long serialVersionUID = -4560925709698323262L;
	
	private static final Log log = LogFactory.getLog(PumaData.class);
	
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy");

	
	private Post<T> post = new Post<T>();
    /**
	 * @return the post
	 */
	public Post<T> getPost() {
		return this.post;
	}

	/**
	 * @param post the post to set
	 */
	public void setPost(final Post<T> post) {
		this.post = post;
		if (post.getResource() instanceof BibTex) {
			final BibTex resource = (BibTex) post.getResource(); 
			this.setAuthor(resource.getAuthor());		
		}
	}
	
	protected List<PersonName> author = new LinkedList<PersonName>();


	protected String examinstitution = null;
    protected List<String> examreferee = new ArrayList<String>();
    protected XMLGregorianCalendar phdoralexam = null;
    protected List<String> sponsors = new ArrayList<String>();
    protected List<String> additionaltitle = new ArrayList<String>();
	private Map<String, List<String>> classification = new HashMap<String, List<String>>();
	
	/**
	 * @return the list of authors
	 */
	public List<PersonName> getAuthor() {
		return this.author;
	}

	/**
	 * @param author list of authors to set
	 */
	public void setAuthor(final List<PersonName> authors) {
		this.author = authors;
	}	


	public Map<String, List<String>> getClassification() {
		return this.classification;
	}

	public void setClassification(final Map<String, List<String>> classification) {
		this.classification = classification;
	}

	public void addClassification (final String key, final String value) {
		if (this.classification.containsKey(key)) {
			this.classification.get(key).add(value);
		} else {
			this.classification.put(key, new ArrayList<String>());
			this.classification.get(key).add(value);
		}
	}

	public void addClassification (final String key, final List<String> values) {
		if (null==this.classification) {
			this.classification = new HashMap<String, List<String>>();
		}
		if (!this.classification.containsKey(key)) {
			this.classification.put(key, new ArrayList<String>());
		}
		
		for ( final String value : values ) {
			this.classification.get(key).add(value);
		}

	}

	
	/**
	 * @return the examinstitution
	 */
	public String getExaminstitution() {
		return this.examinstitution;
	}

	/**
	 * @param examinstitution the examinstitution to set
	 */
	public void setExaminstitution(final String examinstitution) {
		this.examinstitution = examinstitution;
	}

	/**
	 * @return the examreferee
	 */
	public List<String> getExamreferee() {
		return this.examreferee;
	}

	/**
	 * @param examreferee the examreferee to set
	 */
	public void setExamreferee(final List<String> examreferee) {
		this.examreferee = examreferee;
	}

	/**
	 * @param examreferee add examreferee to list
	 */
	public void addExamreferee(final String examreferee) {
		this.examreferee.add(examreferee);
	}

	/**
	 * @return the phdoralexam
	 */
	public XMLGregorianCalendar getPhdoralexam() {
		return this.phdoralexam;
	}

	/**
	 * @param phdoralexam the phdoralexam to set
	 */
	public void setPhdoralexam(final XMLGregorianCalendar phdoralexam) {
		this.phdoralexam = phdoralexam;
	}

	/**
	 * @param phdoralexamString the phdoralexam to set
	 */
	public void setPhdoralexam(final String phdoralexamString) {
		// convert string to date
		Date phdoralexamDate = null;
		XMLGregorianCalendar phdoralexamXMLDate=null;
				
		phdoralexamDate = fmt.parseDateTime(phdoralexamString).toDate();
		final GregorianCalendar c = new GregorianCalendar();
		c.setTime(phdoralexamDate);
		try {
			phdoralexamXMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (final DatatypeConfigurationException e) {
			log.warn("DatatypeConfigurationException");
		}		 
		
		this.phdoralexam = phdoralexamXMLDate;
	}

	/**
	 * @return the sponsors
	 */
	public List<String> getSponsors() {
		return this.sponsors;
	}

	/**
	 * @param sponsors the sponsors to set
	 */
	public void setSponsors(final List<String> sponsors) {
		this.sponsors = sponsors;
	}

	/**
	 * @param sponsor add sponsor to list
	 */
	public void addSponsor(final String sponsor) {
		this.sponsors.add(sponsor);
	}

	/**
	 * @param title add additional title list
	 */
	public void addAdditionaltitle(final String title) {
		this.additionaltitle.add(title);
	}

	/**
	 * @return the additionaltitle
	 */
	public List<String> getAdditionaltitle() {
		return this.additionaltitle;
	}

	/**
	 * @param additionaltitle the additionaltitle list to set
	 */
	public void setAdditionaltitle(final List<String> additionaltitle) {
		this.additionaltitle = additionaltitle;
	}
	
	
}