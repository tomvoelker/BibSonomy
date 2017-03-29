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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "parametername",
    "parametervalue"
})
@XmlRootElement(name = "parameter")
public class Parameter {

    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String source;
    protected String parametername;
    protected String parametervalue;

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the parametername property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParametername() {
        return parametername;
    }

    /**
     * Sets the value of the parametername property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParametername(String value) {
        this.parametername = value;
    }

    /**
     * Gets the value of the parametervalue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParametervalue() {
        return parametervalue;
    }

    /**
     * Sets the value of the parametervalue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParametervalue(String value) {
        this.parametervalue = value;
    }

}
