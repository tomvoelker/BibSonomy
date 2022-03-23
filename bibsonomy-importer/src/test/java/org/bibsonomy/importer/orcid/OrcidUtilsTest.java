package org.bibsonomy.importer.orcid;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

public class OrcidUtilsTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetListOfWorkIds() {

        JSONParser jsonParser = new JSONParser();
        Set<String> workIds = new HashSet<>();

        try (FileReader reader = new FileReader("src/test/resources/orcid/works.json"))
        {
            JSONObject worksObj = (JSONObject) jsonParser.parse(reader);
            workIds = OrcidUtils.getListOfWorkIds(worksObj);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        assertEquals(146, workIds.size());
    }
}
