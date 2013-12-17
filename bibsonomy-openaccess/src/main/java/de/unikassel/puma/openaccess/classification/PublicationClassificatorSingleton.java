package de.unikassel.puma.openaccess.classification;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * @author philipp
  */
@Deprecated // TODO remove and config 
public class PublicationClassificatorSingleton {
	
	private String classificationFilePath;
	
	
	 /** singleton instance */
    private PublicationClassificator singleton;
    /**
     * Gets singleton
     * @return singleton
     */
    public PublicationClassificator getInstance(){
        
    	if (!present(this.singleton)) {
    		this.singleton = new PublicationClassificator(this.classificationFilePath);
    	}
    	
    	return this.singleton;
    }
    
    public void setClassificationFilePath(final String classPath) {
    	this.classificationFilePath = classPath;
    }


}
