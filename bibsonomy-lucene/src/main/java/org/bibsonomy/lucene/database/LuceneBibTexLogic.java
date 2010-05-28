package org.bibsonomy.lucene.database;


import org.bibsonomy.lucene.database.params.BibTexParam;
import org.bibsonomy.lucene.database.params.ResourcesParam;
import org.bibsonomy.model.BibTex;

/**
 * class for accessing the bibsonomy database 
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneBibTexLogic extends LuceneDBLogic<BibTex> {	
	/** singleton pattern's instance reference */
	protected static LuceneDBLogic<BibTex> instance;
	
	/**
	 * @return An instance of this implementation of {@link LuceneDBInterface}
	 */
	public static LuceneDBInterface<BibTex> getInstance() {
		if (instance == null) {
			instance = new LuceneBibTexLogic();
		}
		
		return instance;
	}
	
	/**
	 * constructor disabled for enforcing singleton pattern 
	 */
	private LuceneBibTexLogic() {
	}
	
	@Override
	protected ResourcesParam<BibTex> getResourcesParam() {
		return new BibTexParam();
	}
	
	@Override
	protected String getResourceName() {
		return "BibTex";
	}
}
