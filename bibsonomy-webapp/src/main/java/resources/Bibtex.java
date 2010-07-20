package resources;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.PersonNameUtils;

@Deprecated
public class Bibtex extends Resource {

	private static final int MAX_LEN_AUTHOR = 6000;
	private static final int MAX_LEN_EDITOR = 6000;
	private static final int MAX_LEN_YEAR   =   45;
	private static final int MAX_LEN_ENTRYTYPE =30;
	private static final int MAX_LEN_JOURNAL = 6000;
	private static final int MAX_LEN_BOOKTITLE=6000;
	


	public static final String[] entrylist = {"address","annote","author","booktitle","chapter","crossref","edition",
			"editor","howpublished","institution","journal","key","month","note","number","organization",
			"pages","publisher","school","series","type","volume","year","day","url"};
	public static final String[] entrytypes = {"article", "book", "booklet", "inbook", "incollection", "inproceedings",
			"manual", "mastersthesis", "misc", "phdthesis", "proceedings", "techreport", "unpublished"};

	private String bibtexAbstract;
	private String description;
	private String misc;            //contains all non standard field values
	private String bibtexKey;
	private String entrytype;
	private int scraperid = -1;
	
	private Map<String,String> entries;
	private Map<String,String> miscEntries = null;
	
	private boolean validB = false; // bibtexkey	
	private boolean validA = false; // author
	private boolean validE = false; // editor
	private boolean validY = false; // year
	private boolean validT = false; // entrytype
	
	// hashes
	private String simhash0 = null;
	private String simhash1 = null;
	private String simhash2 = null;
	
	
	// simple constructor
	public Bibtex() {
		super();
		//super.setContentType(Bibtex.CONTENT_TYPE);
	}

	@Override
	public String toString () {
		return super.toString() + 
			"[BibTeX: authors = " + getAuthor() + ", " +
					 "editors = " + getEditor() + 
					 "year = "    + getYear() + "]";
	}

	
	

	/** Returns the "misc" fields of this bibtex entry as a map of 
	 * field-value paris.
	 *  
	 * @return Field-Value pairs of misc entries.
	 */
	public Map<String,String> getMiscMap () {
		if (miscEntries == null) {
			miscEntries = new HashMap<String,String>();
			if (misc != null) {
				Matcher m = Pattern.compile("([a-zA-Z]+)\\s*=\\s*\\{(.+?)\\}").matcher(misc);
				while (m.find()) {
					miscEntries.put(m.group(1), m.group(2));
				}
			}
		}
		return miscEntries;
	}
	
	/*
	 * return validity-identifier
	 */
	public boolean isValidbibtexkey() {return validB;}
	public boolean isValidyear     () {return validY;}
	public boolean isValidentrytype() {return validT;}
	

	
	/*
	 * getter / setter
	 */
	/* set and validate bibtexkey, author, editor (title in superclass) */
	public void setBibtexKey (String b) {this.bibtexKey = b;	                              this.validB = b != null && !b.trim().equals("") && (b.indexOf(' ') == -1); setHashesToNull(); }
	public void setEntrytype(String t)  {this.entrytype = cropToLength(t, MAX_LEN_ENTRYTYPE); this.validT = t != null && !t.trim().equals("") && (t.indexOf(' ') == -1); setHashesToNull(); }
	public void setYear (String y)      {setEntry("year", cropToLength(y, MAX_LEN_YEAR));     this.validY = y != null && !y.trim().equals("");}
	public void setAuthor (String a)    {setEntry("author", cropToLength(a, MAX_LEN_AUTHOR)); this.validA = a != null && !a.trim().equals("");  }
	public void setEditor (String e)    {setEntry("editor", cropToLength(e, MAX_LEN_EDITOR)); this.validE = e != null && !e.trim().equals("");	 }
	@Override
	public void setTitle (String t)     {super.setTitle(t); setHashesToNull(); }
	
	
	public void setBibtexAbstract(String ba)   {this.bibtexAbstract=ba;	    }
	public void setDescription (String d)      {this.description = d;	    }
	public void setMisc (String mi)            {this.misc = mi;	            }
	public void setAddress (String ad)         {setEntry("address", ad);	}
	public void setAnnote (String an)          {setEntry("annote", an);     }	 
	public void setKey(String bk)              {setEntry("key",bk);	        }
	public void setBooktitle (String b)        {setEntry("booktitle", cropToLength(b, MAX_LEN_BOOKTITLE));}
	public void setChapter (String c)          {setEntry("chapter", c);	    }
	public void setCrossref(String cr)         {setEntry("crossref",cr);	}
	public void setDay (String da)             {setEntry("day", da);	    }
	public void setEdition (String e)          {setEntry("edition", e);	    }
	public void setHowpublished (String h)     {setEntry("howpublished", h);}
	public void setInstitution (String i)      {setEntry("institution", i);	}
	public void setJournal (String j)          {setEntry("journal", cropToLength(j, MAX_LEN_JOURNAL));}
	public void setMonth (String m)            {setEntry("month",m);	    }
	public void setNote (String n)             {setEntry("note",n);	        }
	public void setNumber (String n)           {setEntry("number",n);   	}
	public void setOrganization (String o)     {setEntry("organization",o);	}
	public void setPages (String pa)           {setEntry("pages",pa);	    }
	public void setPublisher (String pu)       {setEntry("publisher",pu);	}
	public void setSchool (String s)           {setEntry("school",s);	    }
	public void setSeries (String se)          {setEntry("series",se);	    } 
	public void setType (String t)             {setEntry("type", t);	    } 
	@Override
	public void setUrl (String u)       	   {setEntry("url", u);         }
	public void setVolume (String v)           {setEntry("volume",v);	    }
	
	public String getBibtexKey ()     {	return bibtexKey;	            }
	public String getMisc ()          {	return misc;	                }
	public String getBibtexAbstract() { return bibtexAbstract;	        }
	public String getDescription ()   {	return description;	            }
	public String getEntrytype()      {	return entrytype;	            }
	public String getAddress ()       {	return getEntry("address");     }
	public String getAnnote ()        {	return getEntry("annote");      }	
	public String getAuthor ()        {	return getEntry("author");      } 
	public String getKey()            { return getEntry("key");	        }
	public String getBooktitle ()     {	return getEntry("booktitle");   }
	public String getChapter ()       {	return getEntry("chapter");	    }
	public String getCrossref()       {	return getEntry("crossref");	}
	public String getDay ()           {	return getEntry("day");	        }
	public String getEdition ()       {	return getEntry("edition");	    }
	public String getEditor ()        {	return getEntry("editor");	    }
	public String getHowpublished ()  {	return getEntry("howpublished");}
	public String getInstitution ()   {	return getEntry("institution");	}
	public String getJournal ()       {	return getEntry("journal");	    }
	public String getMonth ()         {	return getEntry("month");	    }
	public String getNote ()          {	return getEntry("note");	    }
	public String getNumber ()        {	return getEntry("number");	    }
	public String getOrganization ()  {	return getEntry("organization");}
	public String getPages ()         {	return getEntry("pages");	    }
	public String getPublisher ()     {	return getEntry("publisher");	}
	public String getSchool ()        {	return getEntry("school");	    }
	public String getSeries ()        {	return getEntry("series");	    }
	public String getType ()          {	return getEntry("type");	    }
	@Override
	public String getUrl ()           {	return getEntry("url");	        }
	public String getVolume ()        {	return getEntry("volume");	    }
	public String getYear ()          {	return getEntry("year");	    }
	public String getAbstract ()      {	return getEntry("abstract");	    }
	
	/* return an URL, which is not broken, i.e. it is valid */
	public String getCleanurl () {
		return super.cleanUrl(this.getUrl());
	}
	
	
	
	/** Generates a bibtex key of the form
	 * firstauthorslastnameYEARfirstrelevantwordoftitle
	 * @return a bibtex key
	 */
	public String getGeneratedBibtexKey () {
		return BibTexUtils.generateBibtexKey(getAuthor(), getEditor(), getYear(), getTitle());
	}
	
	/**
	 * Builds a string which can be used to build an OpenURL
	 * see http://www.exlibrisgroup.com/sfx_openurl.htm
	 * 
	 * @return the DESCRIPTION part of the OpenURL of this BibTeX object
	 */
	public String getOpenurl () {
		/*
		 * stores the completed URL (just the DESCRIPTION part)
		 */
		StringBuffer openurl = new StringBuffer();
		
		/*
		 * extract first authors parts of the name
		 */
		// get first author (if author not present, use editor)
		String author = getAuthor();
		if (author == null) {
			author = getEditor();
		}
		/*
		 *  This is neccessary because of posts which have neither author nor editor!
		 */
		if (author == null) {
			author = "";
		}
		author = author.replaceFirst(" and .*", "").trim();
		// get first authors last name
		String aulast = author.replaceFirst(".* ", "");
		// get first authors first name
		String aufirst = author.replaceFirst("[\\s\\.].*", "");
		// check, if first name is just an initial
		String auinit1 = null;
		if (aufirst.length() == 1) {
			auinit1 = aufirst;
			aufirst = null;
		}
		
		// extract misc fields
		getMiscMap();
		// extract DOI
		String doi = miscEntries.get("doi");
		if (doi != null) {
			// TODO: urls rausfiltern testen
			Matcher m = Pattern.compile("http://.+/(.+?/.+?$)").matcher(doi);
			if (m.find()) {
				doi = m.group(1);
			}
		}
			 
		try {
			// append year (always given!)
			openurl.append("date=" + getYear().trim());
			// append doi
			if (doi != null) {
				appendOpenURL(openurl,"id", "doi:" + doi.trim());
			}
			// append isbn + issn
			appendOpenURL(openurl,"isbn", miscEntries.get("isbn"));
			appendOpenURL(openurl,"issn", miscEntries.get("issn"));
			// append name information for first author
			appendOpenURL(openurl, "aulast", aulast);
			appendOpenURL(openurl, "aufirst", aufirst);
			appendOpenURL(openurl, "auinit1", auinit1);
			// genres == entrytypes
			if (entrytype.toLowerCase().equals("journal")) {
				appendOpenURL(openurl, "genre", "journal");
				appendOpenURL(openurl, "title", getTitle());
			} else if (entrytype.toLowerCase().equals("book")) {
				appendOpenURL(openurl, "genre", "book");
				appendOpenURL(openurl, "title", getTitle());
			} else if (entrytype.toLowerCase().equals("article")) {
				appendOpenURL(openurl, "genre", "article");
				appendOpenURL(openurl, "title", getJournal());
				appendOpenURL(openurl, "atitle", getTitle());
			} else if (entrytype.toLowerCase().equals("inbook")) {
				appendOpenURL(openurl, "genre", "bookitem");
				appendOpenURL(openurl, "title", getBooktitle());
				appendOpenURL(openurl, "atitle", getTitle());
			} else if (entrytype.toLowerCase().equals("proceedings")) {
				appendOpenURL(openurl, "genre", "proceeding");
				appendOpenURL(openurl, "title", getBooktitle());
				appendOpenURL(openurl, "atitle", getTitle());
			} else {
				appendOpenURL(openurl, "title", getBooktitle());
				appendOpenURL(openurl, "atitle", getTitle());
			}
			appendOpenURL(openurl, "volume", getVolume());
			appendOpenURL(openurl, "issue", getNumber());
			

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		return openurl.toString();
	}
	
	private void appendOpenURL(StringBuffer s, String name, String value) throws UnsupportedEncodingException {
		if (value != null && !value.trim().equals("")) {
			s.append("&" + name + "=" + URLEncoder.encode(value.trim(), "UTF-8"));
		}
	}
	
	/* change Map */
	public String getEntry (String entry) {		
		if (entries == null) {
			return null;
		} else {
			return entries.get(entry);	
		}
	}
	
	// sets entry in hashmap, generated new hash map if needed, resets hashes so that they get regenerated
	public void setEntry (String entry, String value) {		
		if(entries==null){
			entries = new TreeMap<String,String> ();
		}
		entries.put(entry,value);
		setHashesToNull(); // this way hashes are regenerated the next time getHash() is called 	
	}
	public Map<String,String> getEntries() {
		return entries;
	}
	
	// returns authors as list
	public List <String> getAuthorlist () {
		return getPersonlist(this.getAuthor());
	}

	// returns authors as list
	public List <String> getEditorlist () {
		return getPersonlist(this.getEditor());
	}
	
	private List<String> getPersonlist (String s) {
		List<String> persons = new LinkedList<String>();
		if (s == null) {
			return persons;
		}
		final List<PersonName> authors = PersonNameUtils.extractList(s);
		for (final PersonName a : authors) {
			persons.add(a.getName());
		}
		return persons;
	}
	
	/** Tries to detect the firstname and lastname of each author or editor.
	 *  
	 * @return List of names separated to the firstname and lastname
	 */
	private List<String[]> getNamesSeparated(String s) {
		final List<String[]> authors = new LinkedList<String[]>();
		if (s != null) {
			final Collection<PersonName> authorObjs = PersonNameUtils.extractList(s);
			for (final PersonName a : authorObjs) {
				authors.add(new String[] {a.getFirstName(), a.getLastName()});
			}
		}		
		return authors;	
	}	
	
	
	/**
	 * returns a list of authornames separated to firstname and lastname
	 * @return A list of author names.
	 */
	public List<String[]> getAuthornamesseparated() {		
		return getNamesSeparated(this.getAuthor()); 
	}
	
	/**
	 * returns a list of editornames separated to firstname and lastname
	 * @return A list of editor names.
	 */
	public List<String[]> getEditornamesseparated() {		
		return getNamesSeparated(this.getEditor());
	}
		

	// sets all hashes to null (so they're regenerated at the next getHash() call)
	private void setHashesToNull() {
		simhash0 = null;
		simhash1 = null;
		simhash2 = null;
	}

	public int getScraperid() {
		return scraperid;
	}

	public void setScraperid(int scraperid) {
		this.scraperid = scraperid;
	}
	
}