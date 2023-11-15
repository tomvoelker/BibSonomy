package de.unikassel.puma.openaccess.sword;

import java.util.HashMap;
import java.util.Map;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class MetsNamespacePrefixMapper extends NamespacePrefixMapper {

    private final Map<String, String> namespaceMap = new HashMap<>();

    // Create namespace prefix mapping
    public MetsNamespacePrefixMapper() {
        namespaceMap.put("http://www.loc.gov/METS/", "mets");
        namespaceMap.put("http://www.loc.gov/mods/", "mods");
        namespaceMap.put("http://www.loc.gov/mods/v3", "mods");
        namespaceMap.put("http://www.bibsonomy.org/2010/11/BibSonomy", "bib");
        namespaceMap.put("http://puma.uni-kassel.de/2010/11/PUMA-SWORD", "puma");
        namespaceMap.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        namespaceMap.put("http://www.w3.org/1999/xlink", "xlink");
    }

    /* (non-Javadoc)
     * Returning null when not found based on spec.
     * @see com.sun.xml.bind.marshaller.NamespacePrefixMapper#getPreferredPrefix(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        return namespaceMap.getOrDefault(namespaceUri, suggestion);
    }
}