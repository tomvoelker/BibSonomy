package org.bibsonomy.webapp.command.ajax;

import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;



/**
 * Command for recommendation ajax requests.
 * 
 * @author fei
 * @version $Id$
 * 
 */
public class AjaxPublicationRecommenderCommand extends AjaxRecommenderCommand<BibTex> {
	
	/*
	 * setters taken from BibtexHandlerBean to support old edit_bibtex.jsp
	 * 
	 * 
	 */	 
	public void setCrossref      (String cr) {getPost().getResource().setCrossref(cr);      }
	public void setType          (String ty) {getPost().getResource().setType(ty);          }
	public void setTitle         (String ti) {getPost().getResource().setTitle(ti);         }
	public void setUrl           (String ur) {getPost().getResource().setUrl(ur);           }
	public void setDescription   (String de) {getPost().setDescription(de);   }
	public void setAuthor        (String au) {getPost().getResource().setAuthor(au);        }
	public void setEditor        (String ed) {getPost().getResource().setEditor(ed);        }
	public void setJournal       (String jo) {getPost().getResource().setJournal(jo);	   }
	public void setVolume        (String vo) {getPost().getResource().setVolume(vo);        }
	public void setChapter       (String ch) {getPost().getResource().setChapter(ch);       }
	public void setEdition       (String ed) {getPost().getResource().setEdition(ed);	   }
	public void setYear          (String ye) {getPost().getResource().setYear(ye);          }
	public void setMisc          (String mi) {getPost().getResource().setMisc(mi);          }
	public void setMonth         (String mo) {getPost().getResource().setMonth(mo);   	   }
	public void setDay           (String da) {getPost().getResource().setDay(da);           }	
	public void setBooktitle     (String bo) {getPost().getResource().setBooktitle(bo);	   }
	public void setHowpublished  (String ho) {getPost().getResource().setHowpublished(ho);  }
	public void setInstitution   (String in) {getPost().getResource().setInstitution(in);   }
	public void setOrganization  (String or) {getPost().getResource().setOrganization(or);  }
	public void setPublisher     (String pu) {getPost().getResource().setPublisher(pu);     }
	public void setAddress       (String ad) {getPost().getResource().setAddress(ad); 	   }
	public void setSchool        (String sc) {getPost().getResource().setSchool(sc);        }
	public void setSeries        (String se) {getPost().getResource().setSeries(se); 	   }
	public void setBibtexKey     (String bi) {getPost().getResource().setBibtexKey(bi);	   }
	public void setGroup         (String ri) {getPost().getGroups().add(new Group(ri));         }
	public void setUser          (String un) {getPost().getResource().setUrl(un);           }
	public void setAnnote        (String an) {getPost().getResource().setAnnote(an);        }
	public void setKey           (String ke) {getPost().getResource().setKey(ke);  	       }
	public void setNote          (String no) {getPost().getResource().setNote(no);          }
	public void setNumber        (String nu) {getPost().getResource().setNumber(nu);	       }
	public void setPages         (String pa) {getPost().getResource().setPages(pa);	       }
	public void setBibtexAbstract(String ba) {getPost().getResource().setAbstract(ba);}
	public void setEntrytype     (String et) {getPost().getResource().setEntrytype(et);	   }
	public void setPrivnote      (String pn) {getPost().getResource().setPrivnote(pn);}
	
	
	public void setRelevantFor(List<String> relevantFor) {
		this.setRelevantGroups(relevantFor);
	}

}
