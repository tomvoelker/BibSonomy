package helpers.picatobibtex.rules;

import helpers.picatobibtex.PicaRecord;
import helpers.picatobibtex.PicaUtils;

/**
 * @author daill
 * @version $Id$
 */
public class URNRule implements Rules {
	private PicaRecord pica = null;
	private PicaUtils utils = null;
	
	/**
	 * @param pica
	 * @param utils
	 */
	public URNRule(PicaRecord pica, PicaUtils utils){
		this.pica = pica;
		this.utils = utils;
	}

	public String getContent() {
		String res = "";
		
		res = utils.getData("004U", "$0");
		
		return utils.cleanString(res);
	}

	public boolean isAvailable() {
		if(pica.isExisting("004U")){
			return true;
		}
		
		return false;
	}

}
