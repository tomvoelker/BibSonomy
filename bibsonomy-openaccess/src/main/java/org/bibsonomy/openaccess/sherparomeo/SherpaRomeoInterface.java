package org.bibsonomy.openaccess.sherparomeo;

public interface SherpaRomeoInterface {
    
    
    /**
     * Performs a search for the publisher. 
     * 
     * @param publisher
     * @param qtype     possible values:
     *                  - all   All the strings in publisher must appear in the publisher’s name, 
     *                          but they may be in any order or location. This is the default qtype
     *                  - any   Publishers’ names must include one or more of the publisher strings
     *                  - exact The publisher string must appear as an intact ‘phrase’ somewhere in the
     *                          pblisher’s name
     * @return
     */
    public String getPolicyForPublisher(String publisher, String qtype);
    
    
    /**
     * Performs a search for the journal title. Qtype can be exact or contains
     * 
     * @param jtitle
     * @param qtype     possible values:
     *                  - starts    The pub string must appear as an intact 'phrase' somewhere in the
     *                              publisher’s name
     *                  - contains  The jtitle string may appear anywhere in the journal title
     *                  - exact     Full title (excluding any added place of publication) must equal jtitle
     * @return
     */
    public String getPolicyForJournal(String jtitle, String qtype);
}
