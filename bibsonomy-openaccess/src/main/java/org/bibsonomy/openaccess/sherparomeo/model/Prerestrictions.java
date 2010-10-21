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
    "prerestriction"
})
@XmlRootElement(name = "prerestrictions")
public class Prerestrictions {

    protected List<Prerestriction> prerestriction;

    /**
     * Gets the value of the prerestriction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the prerestriction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrerestriction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Prerestriction }
     * 
     * 
     */
    public List<Prerestriction> getPrerestriction() {
        if (prerestriction == null) {
            prerestriction = new ArrayList<Prerestriction>();
        }
        return this.prerestriction;
    }

}
