package de.unikassel.puma.openaccess.sherparomeo.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "header",
    "journals",
    "publishers"
})
@XmlRootElement(name = "romeoapi")
public class Romeoapi {

    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String version;
    @XmlElement(required = true)
    protected Header header;
    @XmlElement(required = true)
    protected Journals journals;
    @XmlElement(required = true)
    protected Publishers publishers;

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link Header }
     *     
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link Header }
     *     
     */
    public void setHeader(Header value) {
        this.header = value;
    }

    /**
     * Gets the value of the journals property.
     * 
     * @return
     *     possible object is
     *     {@link Journals }
     *     
     */
    public Journals getJournals() {
        return journals;
    }

    /**
     * Sets the value of the journals property.
     * 
     * @param value
     *     allowed object is
     *     {@link Journals }
     *     
     */
    public void setJournals(Journals value) {
        this.journals = value;
    }

    /**
     * Gets the value of the publishers property.
     * 
     * @return
     *     possible object is
     *     {@link Publishers }
     *     
     */
    public Publishers getPublishers() {
        return publishers;
    }

    /**
     * Sets the value of the publishers property.
     * 
     * @param value
     *     allowed object is
     *     {@link Publishers }
     *     
     */
    public void setPublishers(Publishers value) {
        this.publishers = value;
    }

}
