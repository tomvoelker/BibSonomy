package beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import resources.Bibtex;


public class WarningBean implements Serializable {
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257854931056922929L;
	
	private LinkedList <Bibtex>duplicate;
	private LinkedList <Bibtex>incomplete;
	private LinkedList <String>warning;
	private HashMap <Bibtex,String>errors;
	
	
	
	public WarningBean() {
		duplicate  = new LinkedList<Bibtex>();
		incomplete = new LinkedList<Bibtex>();
		warning    = new LinkedList<String>();
		errors     = new HashMap<Bibtex, String>();
	}	
	
	/*
	 * setter
	 */
	
	public void addDuplicate(Bibtex bib ) {
		duplicate.add(bib);
	}	

	public void addIncomplete (Bibtex bib) {
		incomplete.add(bib);
	}	
	public void setWarning(String w ) {
		warning.add(w);
	}
	
	/*
	 * getter
	 */
	public int getIncompleteCount () {
		return incomplete.size();
	}
	public int getDuplicateCount () {
		return duplicate.size();
	}

	public LinkedList<Bibtex> getDuplicate() {
		return duplicate;
	}
	public LinkedList<Bibtex> getIncomplete() {
		return incomplete;
	}
	public LinkedList<String> getWarning() {
		return warning;
	}

	public HashMap<Bibtex,String> getErrors() {
		return errors;
	}

	public void setErrors(HashMap<Bibtex,String> errors) {
		this.errors = errors;
	}
}
