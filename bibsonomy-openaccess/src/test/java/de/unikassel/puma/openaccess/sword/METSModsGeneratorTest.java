package de.unikassel.puma.openaccess.sword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import bibtex.parser.ParseException;
import de.unikassel.puma.openaccess.sword.renderer.xml.Mets;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@Ignore
@RunWith(Parameterized.class)
public class METSModsGeneratorTest extends TestCase {
    private static final Log log = LogFactory.getLog(METSModsGeneratorTest.class);

    private UrlRenderer urlRenderer;
    private METSModsGenerator metsModsGenerator;
    private METSRenderer xmlRenderer;

    private final PumaData<BibTex> pumaData;
    private final String expectedMets;

    public METSModsGeneratorTest(PumaData<BibTex> pumaData, String expectedMets) {
        this.urlRenderer = new UrlRenderer("https://bibsonomy.org/api/");
        this.metsModsGenerator = new METSModsGenerator(this.urlRenderer);
        this.xmlRenderer = new METSRenderer(urlRenderer);
        this.xmlRenderer.init();

        this.pumaData = pumaData;
        this.expectedMets = expectedMets;
    }

    @Test
    public void testGenerateMods() {
        Mets mets = this.metsModsGenerator.generateMETS(this.pumaData);
        try {
            Writer output = new StringWriter();
            this.xmlRenderer.serializeMETSMods(output, mets);
            String result = output.toString();
            Assert.assertEquals(this.expectedMets, result);
            output.close();
        } catch (Exception e) {
            log.error(e);
        }
    }

    // Provide data
    @Parameters
    public static List<Object[]> data() throws IOException, ParseException {
        List<Object[]> list = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            ClassLoader classLoader = METSModsGeneratorTest.class.getClassLoader();
            File bibFile = new File(classLoader.getResource(String.format("sword/bibtex/test-%d.bib", i)).getFile());
            PumaData<BibTex> pumaData = getBibTexPumaData(bibFile, i);

            File metsFile = new File(classLoader.getResource(String.format("sword/mets/mets-%d.xml", i)).getFile());
            String metsString = FileUtils.readFileToString(metsFile, "UTF-8");

            list.add(new Object[] {pumaData, metsString});
        }

        return list;
    }

    private static PumaData<BibTex> getBibTexPumaData(File bibFile, int i) throws ParseException, IOException {
        BufferedReader bibReader = new BufferedReader(new FileReader(bibFile.getAbsolutePath()));

        SimpleBibTeXParser parser = new SimpleBibTeXParser();
        List<BibTex> bibTexes = parser.parseInternal(bibReader, true);
        BibTex bibtex = bibTexes.get(0);

        List<Document> documents = new ArrayList<>();
        Document document = new Document();
        document.setFileName(String.format("testdocument-%d.pdf", i));
        document.setMd5hash("TEST_MD5_HASH");
        documents.add(document);
        bibtex.setDocuments(documents);

        Post<BibTex> post = new Post<>();
        post.setResource(bibtex);
        post.setUser(new User("testuser"));

        PumaData<BibTex> pumaData = new PumaData<>();
        pumaData.setPost(post);
        return pumaData;
    }
}
