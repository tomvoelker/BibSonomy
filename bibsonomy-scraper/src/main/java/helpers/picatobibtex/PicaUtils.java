package helpers.picatobibtex;

/**
 * @author daill
 * @version $Id$
 */
public class PicaUtils {
	private PicaRecord pica;
	// String array with all regex pieces to be replaced
	private String[] cleaning = {"@", "&lt;.+?&gt;", "\\{", "\\}"};
	
	/**
	 * @param pica
	 */
	public PicaUtils(final PicaRecord pica){
		this.pica = pica;
	}
	
	/**
	 * use this method to get the data out of a specific row and subfield
	 * 
	 * @param cat
	 * @param sub
	 * @return string
	 */
	public String getData(final String cat, final String sub){
		Row r = null;
		SubField f = null;
		
		if ((r = pica.getRow(cat)) != null){
			if ((f = r.getSubField(sub)) != null){
				return f.getContent();
			}
		}
		
		return "";
	}
	
	/**
	 * Tries to clean the given String from i.e. internal references like @
	 * 
	 * @param toClean
	 * @return String
	 */
	public String cleanString(String toClean){
		String res = toClean;
		
		for (String s : cleaning){
			res = res.replaceAll(s, "");
		}
		
		
		return res;
	}
	
	/**
	 * Replace "XML=1.0/CHARSET=UTF-8/PRS=PP" in the url
	 * 
	 * @param url
	 * @return formatted url
	 */
	public String prepareUrl(String url){
		String new_url = "";
		
		new_url = url.replaceFirst("XML=1.0/CHARSET=UTF-8/PRS=PP", "");
		
		return new_url;
	}
}
