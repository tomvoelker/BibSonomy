package org.bibsonomy.openaccess.sherparomeo.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "copyrightlink"
})
@XmlRootElement(name = "copyrightlinks")
public class Copyrightlinks {

    protected List<Copyrightlink> copyrightlink;

    /**
     * Gets the value of the copyrightlink property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the copyrightlink property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCopyrightlink().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Copyrightlink }
     * 
     * 
     */
    public List<Copyrightlink> getCopyrightlink() {
        if (copyrightlink == null) {
            copyrightlink = new ArrayList<Copyrightlink>();
        }
        return this.copyrightlink;
    }

}
