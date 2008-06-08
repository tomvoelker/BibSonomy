package helpers.picatobibtex.rules;

import helpers.picatobibtex.PicaRecord;
import helpers.picatobibtex.PicaUtils;

/**
 * @author daill
 * @version $Id$
 */
public class YearRule implements Rules {
	private PicaRecord pica = null;
	private PicaUtils utils = null;
	
	/**
	 * @param pica
	 * @param utils
	 */
	public YearRule(PicaRecord pica, PicaUtils utils){
		this.pica = pica;
		this.utils = utils;
	}

	public String getContent() {
		String year = "";
		
		year = utils.getData("011@", "$a");

		if (year.length() == 0){
			year = utils.getData("011@", "$n");
		}
		
		return utils.cleanString(year);
	}

	public boolean isAvailable() {
		if(pica.isExisting("011@")){
			return true;
		}
		
		return false;
	}

}
