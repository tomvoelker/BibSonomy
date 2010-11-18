package de.unikassel.puma.openaccess.sherparomeo.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "copyrightlinktext",
    "copyrightlinkurl"
})
@XmlRootElement(name = "copyrightlink")
public class Copyrightlink {

    @XmlElement(required = true)
    protected String copyrightlinktext;
    @XmlElement(required = true)
    protected String copyrightlinkurl;

    /**
     * Gets the value of the copyrightlinktext property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCopyrightlinktext() {
        return copyrightlinktext;
    }

    /**
     * Sets the value of the copyrightlinktext property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCopyrightlinktext(String value) {
        this.copyrightlinktext = value;
    }

    /**
     * Gets the value of the copyrightlinkurl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCopyrightlinkurl() {
        return copyrightlinkurl;
    }

    /**
     * Sets the value of the copyrightlinkurl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCopyrightlinkurl(String value) {
        this.copyrightlinkurl = value;
    }

}
