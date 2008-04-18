package org.bibsonomy.webapp.command;

/**
 * @author mwa
 * @version $Id$
 */
public class BibtexResourceViewCommand extends ResourceViewCommand {
	
	/** the intrahash of a publication **/
	private String requBibtex = "";
	
	/** the hash-type **/
	private String requSim = "";
	
	/**
	 * @return the hash of a bibtex
	 */
	public String getRequBibtex(){
		return this.requBibtex;
	}
	
	/**
	 * @return the hash-type
	 */
	public String getRequSim(){
		return this.requSim;
	}

	public void setRequBibtex(String requBibtex) {
		this.requBibtex = requBibtex;
	}

	public void setRequSim(String requSim) {
		this.requSim = requSim;
	}
}