package de.unikassel.puma.openaccess.sword;

import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import de.unikassel.puma.openaccess.sword.renderer.xml.Mets;
import de.unikassel.puma.openaccess.sword.renderer.xml.ObjectFactory;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.rest.renderer.UrlRenderer;

public class METSModsGeneratorTest extends TestCase {
    private static final Log log = LogFactory.getLog(METSModsGeneratorTest.class);
    private final String API_URL = "https://bibsonomy.org/api/";

    private UrlRenderer urlRenderer;
    private METSModsGenerator metsModsGenerator;
    private METSRenderer xmlRenderer;
    private PumaData<BibTex> pumaData;

    private static List<PersonName> generateAuthors() {
        final List<PersonName> authors = new ArrayList<PersonName>();
        authors.add(new PersonName("Folke", "Mitzlaff"));
        authors.add(new PersonName("Martin", "Atzmueller"));
        authors.add(new PersonName("Gerd", "Stumme"));
        authors.add(new PersonName("Andreas", "Hotho"));
        return authors;
    }

    private static Post<BibTex> generateInproceedings() {
        final Post<BibTex> inproceedingsPost = new Post<>();
        final BibTex inproceedings = new BibTex();
        inproceedings.setAuthor(generateAuthors());
        inproceedings.setAddress("Bamberg, Germany");
        inproceedings.setBooktitle("Proc. LWA 2013 (KDML Special Track)");
        inproceedings.setEntrytype(BibTexUtils.INPROCEEDINGS);
        inproceedings.setInterHash("73088600a500f7d06768615d6e1c2b3d");
        inproceedings.setIntraHash("820ffb2166b330bf60bb30b16e426553");
        inproceedings.setKey("MASH:13b");
        inproceedings.setPublisher("University of Bamberg");
        inproceedings.setSeries("Lecture Notes in Computer Science");
        inproceedings.setTitle("On the Semantics of User Interaction in Social Media (Extended Abstract, Resubmission)");
        inproceedings.setYear("2011");
        inproceedings.setAbstract("Lorem ipsum bla bla bla...");
        inproceedingsPost.addTag("test");
        inproceedingsPost.addTag("inproceedings");
        inproceedingsPost.addTag("open access");
        inproceedingsPost.addTag("sword");
        inproceedingsPost.addTag("dissemin");

        List<Document> documents = new ArrayList<>();
        Document document = new Document();
        document.setFileName("test.pdf");
        document.setMd5hash("testmd5hash");
        documents.add(document);
        inproceedings.setDocuments(documents);

        inproceedingsPost.setResource(inproceedings);
        inproceedingsPost.setUser(new User("test"));

        return inproceedingsPost;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.urlRenderer = new UrlRenderer(API_URL);
        this.metsModsGenerator = new METSModsGenerator(this.urlRenderer);
        this.xmlRenderer = new METSRenderer(urlRenderer);
        this.xmlRenderer.init();
        this.pumaData = new PumaData<>();
        this.pumaData.setPost(generateInproceedings());
    }

    public void testGenerateMods() {
        Mets mets = this.metsModsGenerator.generateMETS(this.pumaData);
        try {
            // Creates a Writer using FileWriter
            Writer output = new FileWriter("output.xml");
            // Writes string to the file
            this.xmlRenderer.serializeMETSMods(output, mets);
            // Closes the writer
            output.close();
        } catch (Exception e) {
            log.error(e);
        }
    }
}
