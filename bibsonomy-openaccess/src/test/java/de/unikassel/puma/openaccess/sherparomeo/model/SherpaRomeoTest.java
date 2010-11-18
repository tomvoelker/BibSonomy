package de.unikassel.puma.openaccess.sherparomeo.model;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

public class SherpaRomeoTest {

    /*
     * TODO: write an useful test ;)
     */
    @SuppressWarnings("unchecked")
    @Test
    public void marshalling() {
        try {
            JAXBContext jc = JAXBContext.newInstance("de.unikassel.puma.openaccess.sherparomeo.model");

            Unmarshaller unmarshaller = jc.createUnmarshaller();

            
            try {
                Romeoapi rp = (Romeoapi) unmarshaller.unmarshal(new URL("http://www.sherpa.ac.uk/romeo/api24.php?issn=1444-1586"));
                List<Publisher> publishers = rp.publishers.getPublisher();
                JSONObject output = new JSONObject();
                for (Publisher publisher : publishers) {
                    //System.out.println("PreArchiving: " + publisher.getPreprints().getPrearchiving());
                    //System.out.println("PostArchiving: " + publisher.getPostprints().getPostarchiving());
                    JSONArray conditions = new JSONArray();
                    for (Condition condition : publisher.getConditions().getCondition()) 
                        conditions.add(condition.getvalue());
                    
                    output.put("conditions", conditions);
                    output.put("publisher", publisher.getName());
                    output.put("colour", publisher.romeocolour);
                }
                System.out.println(output.toJSONString());
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Romeoapi sp = (Romeoapi) unmarshaller.unmarshal(new File(
                    "./src/test/resources/data/institute_of_physics.xml"));            
            Marshaller marshaller = jc.createMarshaller();
            System.out.println("------------------------------------------");
            marshaller.marshal(sp, System.out);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            fail();
            e.printStackTrace();
        }
    }

}
