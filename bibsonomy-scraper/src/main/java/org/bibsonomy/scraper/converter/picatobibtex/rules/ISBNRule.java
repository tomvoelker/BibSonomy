package org.bibsonomy.scraper.converter.picatobibtex.rules;

import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.scraper.converter.picatobibtex.PicaUtils;

/**
 * @author daill
 * @version $Id$
 */
public class ISBNRule implements Rules {
	private PicaRecord pica = null;
	private PicaUtils utils = null;
	
	/**
	 * @param pica
	 * @param utils
	 */
	public ISBNRule(PicaRecord pica, PicaUtils utils){
		this.pica = pica;
		this.utils = utils;
	}

	public String getContent() {
		String res = "";
		
		res = utils.getData("004A", "$0");
		if (res.length() == 0){
			res = utils.getData("004A", "$A");
		}
		
		return utils.cleanString(res);
	}

	public boolean isAvailable() {
		if(pica.isExisting("004A")){
			return true;
		}
		
		return false;
	}

}
