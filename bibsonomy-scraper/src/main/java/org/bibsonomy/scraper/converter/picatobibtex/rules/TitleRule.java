package org.bibsonomy.scraper.converter.picatobibtex.rules;

import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.scraper.converter.picatobibtex.PicaUtils;

/**
 * @author daill
 * @version $Id$
 */
public class TitleRule implements Rules {
	private PicaRecord pica = null;
	private PicaUtils utils = null;
	
	/**
	 * @param pica
	 * @param utils
	 */
	public TitleRule(PicaRecord pica, PicaUtils utils){
		this.pica = pica;
		this.utils = utils;
	}
	
	public String getContent() {
		String res = "";
		
		res = utils.getData("021A", "$a");
		
		return utils.cleanString(res);
	}

	public boolean isAvailable() {
		if(pica.isExisting("021A")){
			return true;
		}
		
		return false;
	}

}
