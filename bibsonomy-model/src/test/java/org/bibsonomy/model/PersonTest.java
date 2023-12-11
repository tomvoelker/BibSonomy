package org.bibsonomy.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class PersonTest {

    @Test
    public void testSetPersonId() {
        String newPersonId = "m.mustermann";
        Person person = new Person();
        List<PersonName> personNames = Collections.singletonList(new PersonName("Max", "Mustermann"));
        person.setNames(personNames);
        person.setPersonId(newPersonId);

        Assert.assertEquals(newPersonId, person.getPersonId());
        for (PersonName name : person.getNames()) {
            Assert.assertEquals(newPersonId, name.getPersonId());
        }
    }

    @Test
    public void testSetMainName() {
        Person person1 = new Person();
        Assert.assertEquals(person1.getMainName(), new PersonName());
        person1.setMainName(new PersonName("Max", "Mustermann"));
        Assert.assertEquals("Max", person1.getMainName().getFirstName());
        Assert.assertEquals("Mustermann", person1.getMainName().getLastName());
        Assert.assertTrue(person1.getMainName().isMain());


        Person person2 = new Person();
        List<PersonName> personNames2 = Arrays.asList(new PersonName("Max", "Mustermann"), new PersonName("Maximilian", "Mustermann"));
        person2.setNames(personNames2);
        person2.setMainName(new PersonName("Max", "Mustermann"));
        Assert.assertEquals("Max", person2.getMainName().getFirstName());
        Assert.assertEquals("Mustermann", person2.getMainName().getLastName());
        Assert.assertTrue(person2.getMainName().isMain());
        Assert.assertEquals(2, person2.getNames().size());
    }

    @Test
    public void testGetMainName() {
        Person person1 = new Person();
        Assert.assertEquals(new PersonName(), person1.getMainName());

        Person person2 = new Person();
        person2.setMainName(new PersonName("Max", "Mustermann"));
        Assert.assertEquals("Mustermann", person2.getMainName().getLastName());
        Assert.assertEquals("Max", person2.getMainName().getFirstName());

        Person person3 = new Person();
        PersonName name3 = new PersonName("Max", "Mustermann");
        name3.setMain(true);
        List<PersonName> personNames3 = Collections.singletonList(name3);
        person3.setNames(personNames3);
        Assert.assertEquals("Mustermann", person3.getMainName().getLastName());
        Assert.assertEquals("Max", person3.getMainName().getFirstName());

        Person person4 = new Person();
        List<PersonName> personNames4 = Collections.singletonList(new PersonName("Max", "Mustermann"));
        person4.setNames(personNames4);
        Assert.assertNull(person4.getMainName().getLastName());
        Assert.assertNull(person4.getMainName().getFirstName());
    }

    @Test
    public void testAddAndRemoveName() {
        Person person = new Person();
        List<PersonName> names = person.getNames();
        Assert.assertEquals(0, names.size());

        boolean success1 = person.addName(new PersonName("Max", "Mustermann"));
        Assert.assertTrue(success1);
        Assert.assertEquals(1, names.size());

        boolean success2 = person.addName(new PersonName("Max", "Mustermann"));
        Assert.assertFalse(success2);
        Assert.assertEquals(1, names.size());

        boolean success3 = person.addName(new PersonName("Maxi", "Mustermann"));
        Assert.assertTrue(success3);
        boolean success4 = person.addName(new PersonName("Maximilian", "Mustermann"));
        Assert.assertTrue(success4);
        Assert.assertEquals(3, names.size());

        boolean success5 = person.removeName(new PersonName("Maxi", "Mustermann"));
        Assert.assertTrue(success5);
        Assert.assertEquals(2, names.size());

        boolean success6 = person.removeName(new PersonName("Maxi", "Mustermann"));
        Assert.assertFalse(success6);
        Assert.assertEquals(2, names.size());
    }
}
