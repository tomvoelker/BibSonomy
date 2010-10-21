package org.bibsonomy.openaccess.sherparomeo.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "paidaccessurl",
    "paidaccessname",
    "paidaccessnotes"
})
@XmlRootElement(name = "paidaccess")
public class Paidaccess {

    protected String paidaccessurl;
    protected String paidaccessname;
    protected String paidaccessnotes;

    /**
     * Gets the value of the paidaccessurl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaidaccessurl() {
        return paidaccessurl;
    }

    /**
     * Sets the value of the paidaccessurl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaidaccessurl(String value) {
        this.paidaccessurl = value;
    }

    /**
     * Gets the value of the paidaccessname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaidaccessname() {
        return paidaccessname;
    }

    /**
     * Sets the value of the paidaccessname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaidaccessname(String value) {
        this.paidaccessname = value;
    }

    /**
     * Gets the value of the paidaccessnotes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaidaccessnotes() {
        return paidaccessnotes;
    }

    /**
     * Sets the value of the paidaccessnotes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaidaccessnotes(String value) {
        this.paidaccessnotes = value;
    }

}
