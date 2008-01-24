package beans;

import resources.Bibtex;

public class BibtexHandlerBean extends ResourceSuperBean<Bibtex> {
	
	private static final long serialVersionUID = 3258135768999540276L;
	private String oldhash = ""; 
	private Bibtex oldentry;
	private int extract_id;

	// extract_id is the key for the ie result for this entry. extract_id = 0 means no ie
	public int getExtract_id() {
		return extract_id;
	}
	public void setExtract_id(int extract_id) {
		this.extract_id = extract_id;
	}
	/* to show oldentry, if entry already exists */
	public Bibtex getOldentry() {
		return oldentry;
	}
	public void setOldentry(Bibtex oldentry) {
		this.oldentry = oldentry;
		this.oldhash = oldentry.getHash();
	}

	// remember the hash of the bibtex entry for logging table 
	public String getOldhash() {
		return oldhash;
	}
	public void setOldhash(String oldhash) {
		this.oldhash = oldhash;
	}
	
	/* check validity */
	public boolean isValid () {
		if (!resource.hasValidTags()) { 
			addError ("tags", "please enter valid tags");
		}
		if (!resource.isValidtitle()) {
			addError("title", "please enter a valid title");
		}
		if (!resource.isValidyear()) {
			addError("year", "please enter the year of publication");
		}
		if (!resource.isValidbibtexkey()) {
			addError("bibtexKey", "please enter a valid BibTeX key");
		}
		return resource.isValid();
	}
	
	
	
	public BibtexHandlerBean() {
		super();
		resource = new Bibtex();
	}
	
	public BibtexHandlerBean (Bibtex b) {
		super();
		this.resource = b;
	}
	
	/* these two methods are hacks, they're inserted for a hack in BibtexHandler
	 * to allow generation of warnings for erroneous manual posts 
	 */
	public Bibtex getBibtex () {
		return resource;
	}
	public void setBibtex (Bibtex b) {
		this.resource = b;
	}
	
	/*
	 * getter
	 */	
	public String[] getEntrytypes ()  {return Bibtex.entrytypes;         }
	public String getCrossref()       {return resource.getCrossref();   	 }	
	public String getType()           {return resource.getType();      	 }
	public String getTitle()          {return resource.getTitle();     	 }
	public String getUrl()            {return resource.getUrl();       	 }
	public String getDay()            {return resource.getDay();	         } 
	public String getDescription()    {return resource.getDescription();	 }
	public String getAuthor()         {return resource.getAuthor();	     }
	public String getBibtexAbstract() {return resource.getBibtexAbstract();}
	public String getEditor()         {return resource.getEditor();	     } 
	public String getJournal() 		  {return resource.getJournal();    	 }
	public String getVolume() 		  {return resource.getVolume();	     }
	public String getChapter() 		  {return resource.getChapter();    	 }
	public String getEdition() 		  {return resource.getEdition();		 }
	public String getYear() 		  {return resource.getYear();			 }
	public String getMonth() 		  {return resource.getMonth();		 }
	public String getMisc() 		  {return resource.getMisc();			 }
	public String getBooktitle() 	  {return resource.getBooktitle();	 }
	public String getHowpublished()   {return resource.getHowpublished();	 }
	public String getInstitution()    {return resource.getInstitution();	 }
	public String getOrganization()   {return resource.getOrganization();	 }
	public String getPublisher()      {return resource.getPublisher();	 }
	public String getAddress()        {return resource.getAddress();		 }
	public String getSchool()         {return resource.getSchool();		 }
	public String getSeries()         {return resource.getSeries();		 }
	public String getAnnote() 		  {return resource.getAnnote();		 }
	public String getKey() 			  {return resource.getKey();			 }
	public String getNote() 		  {return resource.getNote();			 }
	public String getNumber() 		  {return resource.getNumber();		 }
	public String getPages() 		  {return resource.getPages();		 }
	public String getEntrytype() 	  {return resource.getEntrytype();	 }
	public String getBibtexKey() 	  {return resource.getBibtexKey();	 }
	public String getTags() 		  {return resource.getTag().toString(); } 
	public String getGroup() 		  {return resource.getGroup();		 }
	public String getUser() 		  {return resource.getUser();			 }
	public String getHash()			  {return resource.getHash();          }
	public int getScraperid()         {return resource.getScraperid();}
	public String getPrivnote()       {return resource.getPrivnote();}
	
	// getter with " and " subtituted by linebreaks - for nicer output in textbox
	public String getAuthorLineBreak () {
		if (resource.getAuthor() != null) {
			return resource.getAuthor().replaceAll(" and ", "\n");
		}
		return resource.getAuthor();
	}

	public String getEditorLineBreak () {
		if (resource.getEditor() != null) {
			return resource.getEditor().replaceAll(" and ", "\n");
		}
		return resource.getEditor();
	}
	
	/*
	 * setter
	 */	 
	public void setCrossref      (String cr) {resource.setCrossref(cr);      }
	public void setType          (String ty) {resource.setType(ty);          }
	public void setTitle         (String ti) {resource.setTitle(ti);         }
	public void setUrl           (String ur) {resource.setUrl(ur);           }
	public void setDescription   (String de) {resource.setDescription(de);   }
	public void setAuthor        (String au) {resource.setAuthor(au);        }
	public void setEditor        (String ed) {resource.setEditor(ed);        }
	public void setJournal       (String jo) {resource.setJournal(jo);	   }
	public void setVolume        (String vo) {resource.setVolume(vo);        }
	public void setChapter       (String ch) {resource.setChapter(ch);       }
	public void setEdition       (String ed) {resource.setEdition(ed);	   }
	public void setYear          (String ye) {resource.setYear(ye);          }
	public void setMisc          (String mi) {resource.setMisc(mi);          }
	public void setMonth         (String mo) {resource.setMonth(mo);   	   }
	public void setDay           (String da) {resource.setDay(da);           }	
	public void setBooktitle     (String bo) {resource.setBooktitle(bo);	   }
	public void setHowpublished  (String ho) {resource.setHowpublished(ho);  }
	public void setInstitution   (String in) {resource.setInstitution(in);   }
	public void setOrganization  (String or) {resource.setOrganization(or);  }
	public void setPublisher     (String pu) {resource.setPublisher(pu);     }
	public void setAddress       (String ad) {resource.setAddress(ad); 	   }
	public void setSchool        (String sc) {resource.setSchool(sc);        }
	public void setSeries        (String se) {resource.setSeries(se); 	   }
	public void setBibtexKey     (String bi) {resource.setBibtexKey(bi);	   }
	public void setGroup         (String ri) {resource.setGroup(ri);         }
	public void setUser          (String un) {resource.setUrl(un);           }
	public void setAnnote        (String an) {resource.setAnnote(an);        }
	public void setKey           (String ke) {resource.setKey(ke);  	       }
	public void setNote          (String no) {resource.setNote(no);          }
	public void setNumber        (String nu) {resource.setNumber(nu);	       }
	public void setPages         (String pa) {resource.setPages(pa);	       }
	public void setBibtexAbstract(String ba) {resource.setBibtexAbstract(ba);}
	public void setEntrytype     (String et) {resource.setEntrytype(et);	   }
	public void setScraperid     (int si)    {resource.setScraperid(si);}
	public void setPrivnote      (String pn) {resource.setPrivnote(pn);}
	
}
