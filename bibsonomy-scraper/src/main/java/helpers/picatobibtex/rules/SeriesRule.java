package helpers.picatobibtex.rules;

import helpers.picatobibtex.PicaRecord;
import helpers.picatobibtex.PicaUtils;

/**
 * @author daill
 * @version $Id$
 */
public class SeriesRule implements Rules {
	private PicaRecord pica = null;
	private PicaUtils utils = null;
	
	/**
	 * @param pica
	 * @param utils
	 */
	public SeriesRule(PicaRecord pica, PicaUtils utils){
		this.pica = pica;
		this.utils = utils;
	}

	public String getContent() {
		String res = "";
		
		res = utils.getData("036E", "$a");
		
		return utils.cleanString(res);
	}

	public boolean isAvailable() {
		if(pica.isExisting("036E")){
			return true;
		}
		
		return false;
	}

}
