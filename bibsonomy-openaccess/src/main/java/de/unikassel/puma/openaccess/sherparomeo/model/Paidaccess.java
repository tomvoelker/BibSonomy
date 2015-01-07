/**
 * BibSonomy-OpenAccess - Check Open Access Policies for Publications
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unikassel.puma.openaccess.sherparomeo.model;

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
