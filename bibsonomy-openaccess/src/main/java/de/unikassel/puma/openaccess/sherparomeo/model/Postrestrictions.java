package de.unikassel.puma.openaccess.sherparomeo.model;

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
    "postrestriction"
})
@XmlRootElement(name = "postrestrictions")
public class Postrestrictions {

    protected List<Postrestriction> postrestriction;

    /**
     * Gets the value of the postrestriction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the postrestriction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPostrestriction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Postrestriction }
     * 
     * 
     */
    public List<Postrestriction> getPostrestriction() {
        if (postrestriction == null) {
            postrestriction = new ArrayList<Postrestriction>();
        }
        return this.postrestriction;
    }

}
