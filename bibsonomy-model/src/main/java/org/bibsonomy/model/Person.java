/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.model;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.enums.Gender;
import org.bibsonomy.model.extra.AdditionalKey;

/**
 * Entity class of a real person. Note that {@link User} and {@link Author} are
 * not {@link Person} subclasses since they are not modeled as real persons
 * instances.
 *
 * @author jil
 */
@Getter
@Setter
public class Person implements Linkable, Serializable {

    private static final long serialVersionUID = 4578956154246424767L;
    public static final String[] fieldsWithResolvableMergeConflicts = {"mainName", "academicDegree", "orcid", "researcherid", "gender", "college", "email", "homepage"};

    private Integer personChangeId;
    /** null means new non-persistent object */
    private String personId;
    /** usually current real name */
    private PersonName mainName;
    /** other names like former names or pseudonyms */
    private List<PersonName> names;
    /** something like "Dr. rer. nat." */
    private String academicDegree;
    /** researcher id on http://orcid.org/ */
    private String orcid;
    /** researcher id on http://researcherID.com/ */
    private String researcherid;
    /** sameAs relation to a user */
    private String user;
    /** {@link User} who last modified this {@link Person} */
    private String changedBy;
    /** point in time when the last change was made */
    private Date changeDate;
    /** the number of posts in the system, which this {@link Person} as an author */
    private int postCounter;
    /** place to link to the original entries when imported from Deutsche Nationalbibliothek */
    private String dnbPersonId;
    /** the gender */
    private Gender gender;
    /** the college of the person */
    private String college;
    /** the email of the person */
    private String email;
    /** the homepage of the person */
    private URL homepage;

    /** cris links that are connected to this project */
    private List<CRISLink> crisLinks = new LinkedList<>();

    private List<ResourcePersonRelation> resourceRelations = new LinkedList<>();

    /** additional keys for person */
    private List<AdditionalKey> additionalKeys = new LinkedList<>();
    /**
     * default constructor
     */
    public Person() {
        this.names = new ArrayList<>();
    }


    /**
     * @param string synthetic id. null means new non-persistent object
     */
    public void setPersonId(String string) {
        this.personId = string;
        for (PersonName name : this.names)
            name.setPersonId(this.personId);
    }

    /**
     * @return usually current real name
     */
    public PersonName getMainName() {
        if (this.mainName == null) {
            for (PersonName name : this.names) {
                if (name.isMain()) {
                    this.mainName = name;
                }
            }
        }

        // Return empty person name, if main name still null
        if (this.mainName != null) {
            return this.mainName;
        }

        return new PersonName();
    }

    /**
     * @param id usually current real name
     */
    public void setMainName(int id) {
        for (PersonName name : this.names) {
            if (name.getPersonNameChangeId() == id) {
                name.setMain(true);
                this.mainName = name;
            } else {
                name.setMain(false);
            }
        }
    }

    /**
     *
     @param name
     */
    public void setMainName(PersonName name) {
        // Add name, if person doesn't already have the name
        boolean isNewName = this.addName(name);

        // Set selected name as new main name
        Optional<PersonName> selectedName = this.names.stream().filter(n -> n.equals(name)).findFirst();
        if (selectedName.isPresent()) {
            selectedName.get().setMain(true);
            this.mainName = selectedName.get();
        }
    }

    /**
     *
     * @param name the person name to add
     *
     * @return true, if added and false, if already exists
     */
    public boolean addName(PersonName name) {
        if(this.getNames().contains(name)) {
            return false;
        }

        if (name != null) {
            name.setPersonId(this.getPersonId());
            name.setMain(false);
            this.getNames().add(name);
            return true;
        }

        return false;
    }

    /**
     *
     * @param name the person name to remove
     *
     * @return true, if removed and false, if not found
     */
    public boolean removeName(PersonName name) {
        if (!this.getNames().contains(name)) {
            return false;
        }

        if (name != null) {
            this.names.remove(name);
            return true;
        }

        return false;
    }


    @Override
    public boolean equals(Object obj) {
        if (personId == null) {
            return obj == this;
        }

        return ((obj instanceof Person) && (this.getPersonId().equals(((Person) obj).getPersonId())));
    }

    @Override
    public int hashCode() {
        if (personId == null) {
            return System.identityHashCode(this);
        }
        return personId.hashCode();
    }


    @Override
    public String getLinkableId() {
        return this.getPersonId();
    }

    @Override
    public Integer getId() {
        return this.personChangeId;
    }

    /**
     * returns true if specific attributes are equal or at least null for one person
     * @param person
     * @return
     */
    public boolean equalsTo(Person person) {
        return (this.academicDegree == null || person.getAcademicDegree() == null || this.academicDegree.equals(person.getAcademicDegree())) &&
                (this.college == null || person.getCollege() == null || this.college.equals(person.getCollege())) &&
                (this.gender == null || person.getGender() == null || this.gender.equals(person.getGender())) &&
                (this.email == null || person.getEmail() == null || this.email.equals(person.getEmail())) &&
                (this.homepage == null || person.getHomepage() == null || this.homepage.equals(person.getHomepage())) &&
                (this.orcid == null || person.orcid == null || this.orcid.equals(person.orcid)) &&
                (this.researcherid == null || person.researcherid == null || this.researcherid.equals(person.researcherid)) &&
                (this.user == null || person.user == null);
    }
}
