package org.bibsonomy.database.params;


/**
 * @author philipp
 * @version $Id$
 */
public class RepositoryParam {    
    
    private String intraHash;
    
    private String interHash;
    
    private String userName;
    
    private String repositoryName;
        
    /**
     * @return the repositoryName
     */
    public String getRepositoryName() {
        return this.repositoryName;
    }

    /**
     * @param repositoryName the repositoryName to set
     */
    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    /**
     * @return the intraHash
     */
    public String getIntraHash() {
        return this.intraHash;
    }

    /**
     * @param intraHash the intraHash to set
     */
    public void setIntraHash(String intraHash) {
        this.intraHash = intraHash;
    }

    /**
     * @return the interHash
     */
    public String getInterHash() {
        return this.interHash;
    }

    /**
     * @param interHash the interHash to set
     */
    public void setInterHash(String interHash) {
        this.interHash = interHash;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
