package beans;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.LinkedList;

import resources.Bibtex;
/**
 * @author Serak
 *
 */
public class UploadBean implements Serializable {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3546359539234113720L;
	
	private String file;
	private String description;	
	private String tag;
	private String user_name;
	
	private LinkedList <Bibtex>bibtex;
	
	private final Hashtable <String,String>errors;
	
	public boolean validate() {
	    boolean allOk=true;
	    
	    if (file.equals("") ) {
	        errors.put("file","Please choose a file");
	        file="";
	        allOk=false;
	      }
	    
	    if (description.equals("") ) {
	        errors.put("description","Please enter a description");
	        description="";
	        allOk=false;
	      }
	    
	    
	    if (tag.equals("")) {
	        errors.put("tag","Please enter at least one tag");
	        tag="";
	        allOk=false;
	      }
	
	    return allOk;
	  }
	
	public String getErrorMsg(String s) {
	    String errorMsg =errors.get(s.trim());
	    return (errorMsg == null) ? "":errorMsg;
	  }
	
	public UploadBean() {
	  	
		file="";
	    description=""; 
	    tag="";
	    user_name="";
	    bibtex = new LinkedList<Bibtex>();
	    
	    errors = new Hashtable<String,String>();
	  }
	
	 public String getFile() {
	    return file;
	  }
	 
	 public String getDescription() {
	    return description;
	  }
	 
	 public String getTag() {
	    return tag;
	  }
	 
	 
	 public String getUser_name() {
	    return user_name;
	  }

	 public void setFile(String f) {
	    file=f;
	  }
	 
	 public void setDescription(String d) {
	    description=d;
	  }

	 
	 public void setTag(String t) {
	    tag=t;
	  }

	 
	 public void  setUser_name(String un) {
	    user_name=un;
	  }
	 
	 public void addBibtex (Bibtex bib) {
			bibtex.add(bib);
	 }
	 public void setBibtex (LinkedList<Bibtex> bib) {
			bibtex = bib;
	 }
	 public int getBibtexCount () {
			return bibtex.size();
	 }
	 public LinkedList getBibtex () {
	    	return bibtex;
	 }
	 public String toStringBib() {
			return bibtex.getFirst() + " " + bibtex.getLast();
	 }

	 public void setErrors(String key, String msg) {
	    errors.put(key,msg);
	  }

}
