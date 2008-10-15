package org.bibsonomy.scraper.converter.picatobibtex.rules;


import java.util.LinkedList;

import org.bibsonomy.scraper.converter.picatobibtex.PicaRecord;
import org.bibsonomy.scraper.converter.picatobibtex.PicaUtils;
import org.bibsonomy.scraper.converter.picatobibtex.Row;

/**
 * @author daill
 * @version $Id$
 */
public class TagsRule implements Rules {
	private PicaRecord pica = null;
	private PicaUtils utils = null;
	
	/**
	 * @param pica
	 * @param utils
	 */
	public TagsRule(PicaRecord pica, PicaUtils utils){
		this.pica = pica;
		this.utils = utils;
	}

	public String getContent() {
		String tags = "";
		
		LinkedList<Row> list = null;
		Row row = null;
		
		if((list = pica.getRows("044K")) != null){
			for(Row r : list){
				if(r.isExisting("$8")){
					tags += r.getSubField("$8").getContent() + " ";
				}
			}
		} else if(pica.isExisting("041A")){
			String cat = "041A";
			tags += utils.getData(cat, "$8") + " ";
			
			int ctr = 1;
			
			row = pica.getRow(cat + "/0" + Integer.toString(ctr));
			
			while(row != null){
				String newCat = cat + "/0" + Integer.toString(ctr);
				
				if(row.isExisting("$8")){
					tags += utils.getData(newCat, "$8") + " ";
				}
				
				ctr++;
	
				if (ctr < 10){
					row = pica.getRow(cat + "/0" + Integer.toString(ctr));
				} else {
					row = pica.getRow(cat + "/" + Integer.toString(ctr));
				}
			}
		}

		return utils.cleanString(tags);
	}

	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

}
