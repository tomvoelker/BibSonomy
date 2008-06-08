package helpers.picatobibtex.rules;

import helpers.picatobibtex.PicaRecord;
import helpers.picatobibtex.PicaUtils;

/**
 * @author daill
 * @version $Id$
 */
public class AbstractRule implements Rules {
	private PicaRecord pica = null;
	private PicaUtils utils = null;
	
	/**
	 * @param pica
	 * @param utils
	 */
	public AbstractRule(PicaRecord pica, PicaUtils utils){
		this.pica = pica;
		this.utils = utils;
	}

	public String getContent() {
		String abstr = "";
		abstr = utils.getData("046M", "$a");
		
		return utils.cleanString(abstr);
	}

	public boolean isAvailable() {
		if(pica.isExisting("046M")){
			return true;
		}
		
		return false;
	}

}
