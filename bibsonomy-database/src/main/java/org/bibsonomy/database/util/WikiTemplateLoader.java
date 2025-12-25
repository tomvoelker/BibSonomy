package org.bibsonomy.database.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Loads wiki templates from classpath resources.
 * 
 * <p>This class was created to remove the dependency on {@code bibsonomy-wiki} module,
 * which transitively brought in Spring WebMVC 3.2 and Spring Security 3.2 dependencies
 * that conflict with Spring Boot 3.x. The templates are now stored directly in this module.
 * 
 * <p>For reference, the original templates were loaded via {@code org.bibsonomy.wiki.TemplateManager}
 * from the {@code bibsonomy-wiki} module. This implementation uses plain Java resource loading
 * to avoid Spring dependencies.
 * 
 * @see org.bibsonomy.wiki.TemplateManager (in bibsonomy-wiki module)
 */
public class WikiTemplateLoader {
    private static final Log log = LogFactory.getLog(WikiTemplateLoader.class);

    private static final String TEMPLATE_DIR = "/org/bibsonomy/database/templates/";
    private static final Map<String, String> templateCache = new ConcurrentHashMap<>();

    /**
     * Sentinel value for missing templates to avoid repeated failed lookups.
     * ConcurrentHashMap doesn't support null values, so we use this marker.
     */
    private static final String TEMPLATE_NOT_FOUND = "";
    
    /**
     * Gets a wiki template by name.
     * Templates are loaded from classpath and cached using thread-safe atomic operations.
     *
     * @param templateName the template name (e.g., "user1en", "group1en")
     * @return the template content, or null if not found
     */
    public static String getTemplate(String templateName) {
        // Atomically load and cache the template if not present
        String content = templateCache.computeIfAbsent(templateName, key -> {
            String resourcePath = TEMPLATE_DIR + key + ".wikitemplate";
            try (InputStream is = WikiTemplateLoader.class.getResourceAsStream(resourcePath)) {
                if (is == null) {
                    log.warn("Template file not found: " + resourcePath);
                    return TEMPLATE_NOT_FOUND; // Use sentinel for missing templates
                }

                return loadTemplateFromStream(is);
            } catch (IOException e) {
                log.error("Error loading template: " + key, e);
                return TEMPLATE_NOT_FOUND; // Use sentinel for failed loads
            }
        });

        // Return null for sentinel value (maintains original API contract)
        return TEMPLATE_NOT_FOUND.equals(content) ? null : content;
    }
    
    private static String loadTemplateFromStream(InputStream is) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}

