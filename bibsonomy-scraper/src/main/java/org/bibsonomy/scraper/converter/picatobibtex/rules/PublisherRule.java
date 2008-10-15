package org.bibsonomy.scraper.converter.picatobibtex.rules;

import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.scraper.converter.picatobibtex.PicaUtils;

/**
 * @author daill
 * @version $Id$
 */
public class PublisherRule implements Rules {
	private PicaRecord pica = null;
	private PicaUtils utils = null;
	
	/**
	 * @param pica
	 * @param utils
	 */
	public PublisherRule(PicaRecord pica, PicaUtils utils){
		this.pica = pica;
		this.utils = utils;
	}

	public String getContent() {
		String res = "";

		res = utils.getData("033A", "$n");
		res += " " + utils.getData("033A", "$p");
		
		return utils.cleanString(res);
	}

	public boolean isAvailable() {
		if(pica.isExisting("033A")){
			return true;
		}
		
		return false;
	}

}
