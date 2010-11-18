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
    "mandate"
})
@XmlRootElement(name = "mandates")
public class Mandates {

    protected List<Mandate> mandate;

    /**
     * Gets the value of the mandate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mandate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMandate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Mandate }
     * 
     * 
     */
    public List<Mandate> getMandate() {
        if (mandate == null) {
            mandate = new ArrayList<Mandate>();
        }
        return this.mandate;
    }

}
