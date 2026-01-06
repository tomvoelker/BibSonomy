package org.bibsonomy.database.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for {@link WikiTemplateLoader}
 */
public class WikiTemplateLoaderTest {
    
    @Test
    public void testLoadUserTemplate() {
        String template = WikiTemplateLoader.getTemplate("user1en");
        assertNotNull("User template should not be null", template);
        assertTrue("User template should contain personal data section", 
                   template.contains("== personal data =="));
        assertTrue("User template should contain publications tag", 
                   template.contains("<publications"));
    }
    
    @Test
    public void testLoadGroupTemplate() {
        String template = WikiTemplateLoader.getTemplate("group1en");
        assertNotNull("Group template should not be null", template);
        assertTrue("Group template should contain group page header", 
                   template.contains("Grouppage of the group"));
        assertTrue("Group template should contain members section", 
                   template.contains("==Members=="));
    }
    
    @Test
    public void testTemplateCaching() {
        String template1 = WikiTemplateLoader.getTemplate("user1en");
        String template2 = WikiTemplateLoader.getTemplate("user1en");
        assertTrue("Templates should be cached and return same instance", 
                   template1 == template2);
    }
    
    @Test
    public void testNonExistentTemplate() {
        String template = WikiTemplateLoader.getTemplate("nonexistent");
        assertTrue("Non-existent template should return null", template == null);
    }
}

