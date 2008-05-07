package helpers.picatobibtex;

/**
 * @author C. Kramer
 * @version $Id$
 */
public class SubField {
	private String subtag = null;
	private String subcontent = null;
	
	/**
	 * @param subtag
	 * @param subcontent
	 */
	public SubField(final String subtag, final String subcontent){
		this.subtag = subtag;
		this.subcontent = subcontent;
	}
	
	/**
	 * Returns the subtag i.e. $5
	 * 
	 * @return String
	 */
	public String getSubTag(){
		return this.subtag;
	}
	
	/**
	 * Return the content of this subfield
	 * 
	 * @return String
	 */
	public String getContent(){
		return this.subcontent;
	}
}
