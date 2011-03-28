package de.unikassel.puma.openaccess.sword;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import org.bibsonomy.model.util.PersonNameUtils;


public class PumaData<T extends Resource> extends PumaPost<T> {
	private static final Log log = LogFactory.getLog(PumaData.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -4560925709698323262L;
	
	private Post<T> post = new Post<T>();
    /**
	 * @return the post
	 */
	public Post<T> getPost() {
		return post;
	}

	/**
	 * @param post the post to set
	 */
	public void setPost(Post<T> post) {
		this.post = post;
		if (post.getResource() instanceof BibTex) {
			BibTex resource = (BibTex) post.getResource(); 
			this.addAuthorPN(PersonNameUtils.extractList(resource.getAuthor()));
			
		}
	}
	
	protected List<String> author = new ArrayList<String>();


	protected String examinstitution = null;
    protected List<String> examreferee = new ArrayList<String>();
    protected XMLGregorianCalendar phdoralexam = null;
    protected List<String> sponsors = new ArrayList<String>();
    protected List<String> additionaltitle = new ArrayList<String>();
	private Map<String, List<String>> classification = new HashMap<String, List<String>>();
	
	/**
	 * @return the list of authors
	 */
	public List<String> getAuthor() {
		return author;
	}

	/**
	 * @param author list of authors to set
	 */
	public void setAuthor(List<String> authors) {
		this.author = authors;
	}	

	public void addAuthor (String author) {
		this.author.add(author);
	}

	public void addAuthor (List<String> authors) {
		this.author.addAll(authors);
	}

	public void addAuthorPN (List<PersonName> authors) {
		for ( PersonName authorname : authors ) {
			this.author.add(authorname.getName());
		}		
	}

	
	public Map<String, List<String>> getClassification() {
		return this.classification;
	}

	public void setClassification(Map<String, List<String>> classification) {
		this.classification = classification;
	}

	public void addClassification (String key, String value) {
		if (this.classification.containsKey(key)) {
			this.classification.get(key).add(value);
		} else {
			this.classification.put(key, new ArrayList<String>());
			this.classification.get(key).add(value);
		}
	}

	public void addClassification (String key, List<String> values) {
		if (null==this.classification) classification = new HashMap<String, List<String>>();
		if (!this.classification.containsKey(key)) {
			this.classification.put(key, new ArrayList<String>());
		}
		
		for ( String value : values ) {
			this.classification.get(key).add(value);
		}

	}

	
	/**
	 * @return the examinstitution
	 */
	public String getExaminstitution() {
		return examinstitution;
	}

	/**
	 * @param examinstitution the examinstitution to set
	 */
	public void setExaminstitution(String examinstitution) {
		this.examinstitution = examinstitution;
	}

	/**
	 * @return the examreferee
	 */
	public List<String> getExamreferee() {
		return examreferee;
	}

	/**
	 * @param examreferee the examreferee to set
	 */
	public void setExamreferee(List<String> examreferee) {
		this.examreferee = examreferee;
	}

	/**
	 * @param examreferee add examreferee to list
	 */
	public void addExamreferee(String examreferee) {
		this.examreferee.add(examreferee);
	}

	/**
	 * @return the phdoralexam
	 */
	public XMLGregorianCalendar getPhdoralexam() {
		return phdoralexam;
	}

	/**
	 * @param phdoralexam the phdoralexam to set
	 */
	public void setPhdoralexam(XMLGregorianCalendar phdoralexam) {
		this.phdoralexam = phdoralexam;
	}

	/**
	 * @param phdoralexam the phdoralexam to set
	 */
	public void setPhdoralexam(String phdoralexamString) {
		// convert string to date

		SimpleDateFormat sdf = new SimpleDateFormat("dd.mm.yyyy");
		Date phdoralexamDate = null;
		XMLGregorianCalendar phdoralexamXMLDate=null;
		
		try {
			phdoralexamDate = sdf.parse(phdoralexamString);
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(phdoralexamDate);
			try {
				phdoralexamXMLDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
			} catch (DatatypeConfigurationException e) {
				log.warn("DatatypeConfigurationException");
			}
		} catch(ParseException e) {
			// Quellzeit hat ein falsches Format
		}
		

		
		this.phdoralexam = phdoralexamXMLDate;
	}

	/**
	 * @return the sponsors
	 */
	public List<String> getSponsors() {
		return sponsors;
	}

	/**
	 * @param sponsors the sponsors to set
	 */
	public void setSponsors(List<String> sponsors) {
		this.sponsors = sponsors;
	}

	/**
	 * @param sponsor add sponsor to list
	 */
	public void addSponsor(String sponsor) {
		this.sponsors.add(sponsor);
	}

	/**
	 * @param title add additional title list
	 */
	public void addAdditionaltitle(String title) {
		this.additionaltitle.add(title);
	}

	/**
	 * @return the additionaltitle
	 */
	public List<String> getAdditionaltitle() {
		return additionaltitle;
	}

	/**
	 * @param additionaltitle the additionaltitle list to set
	 */
	public void setAdditionaltitle(List<String> additionaltitle) {
		this.additionaltitle = additionaltitle;
	}
	
	
}