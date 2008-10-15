package org.bibsonomy.scraper.converter.picatobibtex.rules;

import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.scraper.converter.picatobibtex.PicaUtils;

/**
 * @author daill
 * @version $Id$
 */
public class ISSNRule implements Rules {
	private PicaRecord pica = null;
	private PicaUtils utils = null;
	
	/**
	 * @param pica
	 * @param utils
	 */
	public ISSNRule(PicaRecord pica, PicaUtils utils){
		this.pica = pica;
		this.utils = utils;
	}

	public String getContent() {
		String res = "";
		
		res = utils.getData("005A", "$0");
		if(res.length() == 0){
			 res = utils.getData("005A", "$A"); 
		}
		
		return utils.cleanString(res);
	}

	public boolean isAvailable() {
		if(pica.isExisting("005A")){
			return true;
		}
		
		return false;
	}

}
