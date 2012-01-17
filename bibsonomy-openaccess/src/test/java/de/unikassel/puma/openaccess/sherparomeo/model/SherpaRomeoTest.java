package de.unikassel.puma.openaccess.sherparomeo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Ignore;
import org.junit.Test;

public class SherpaRomeoTest {

    /*
     * TODO: write a useful test ;)
     */
    @SuppressWarnings("unchecked")
    @Test
    @Ignore
    public void marshalling() {
        try {
            final JAXBContext jc = JAXBContext.newInstance("de.unikassel.puma.openaccess.sherparomeo.model");

            final Unmarshaller unmarshaller = jc.createUnmarshaller();

            
            try {
            	final Romeoapi rp = (Romeoapi) unmarshaller.unmarshal(new URL("http://www.sherpa.ac.uk/romeo/api24.php?issn=1444-1586"));
            	final List<Publisher> publishers = rp.publishers.getPublisher();
            	final JSONObject output = new JSONObject();
                for (final Publisher publisher : publishers) {
                    //System.out.println("PreArchiving: " + publisher.getPreprints().getPrearchiving());
                    //System.out.println("PostArchiving: " + publisher.getPostprints().getPostarchiving());
                	final JSONArray conditions = new JSONArray();
                    for (final Condition condition : publisher.getConditions().getCondition()) 
                        conditions.add(condition.getvalue());
                    
                    output.put("conditions", conditions);
                    output.put("publisher", publisher.getName());
                    output.put("colour", publisher.romeocolour);
                }
                System.out.println(output.toString());
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            fail();
            e.printStackTrace();
        }
    }
    
    @Test
	public void testOffline() throws Exception {
    	
    	final String start = "{\"conditions\":[\"Published source must be acknowledged\"";
    	
    	final JAXBContext jc = JAXBContext.newInstance("de.unikassel.puma.openaccess.sherparomeo.model");
    	final Unmarshaller unmarshaller = jc.createUnmarshaller();
    	final Romeoapi sp = (Romeoapi) unmarshaller.unmarshal(new File("./src/test/resources/data/institute_of_physics.xml"));            
    	final List<Publisher> publishers = sp.publishers.getPublisher();
    	final JSONObject output = new JSONObject();
        for (final Publisher publisher : publishers) {
            //System.out.println("PreArchiving: " + publisher.getPreprints().getPrearchiving());
            //System.out.println("PostArchiving: " + publisher.getPostprints().getPostarchiving());
        	final JSONArray conditions = new JSONArray();
            for (final Condition condition : publisher.getConditions().getCondition()) 
                conditions.add(condition.getvalue());
            
            output.put("conditions", conditions);
            output.put("publisher", publisher.getName());
            output.put("colour", publisher.romeocolour);
        }
        assertEquals(start, output.toString().substring(0, start.length()));
	}

}
