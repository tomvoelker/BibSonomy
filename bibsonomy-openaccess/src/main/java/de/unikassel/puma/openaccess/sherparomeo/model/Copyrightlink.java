/**
 * BibSonomy-OpenAccess - Check Open Access Policies for Publications
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
