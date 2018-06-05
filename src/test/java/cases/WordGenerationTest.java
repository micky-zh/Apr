package cases;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
import org.bouncycastle.math.ec.ScaleYPointMap;
import org.docx4j.Docx4J;
import org.docx4j.TraversalUtil;
import org.docx4j.XmlUtils;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.convert.out.html.AbstractHtmlExporter;
import org.docx4j.convert.out.html.HtmlExporterNG2;
import org.docx4j.finders.ClassFinder;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Body;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.Text;
import org.junit.Test;

public class WordGenerationTest {

    @Test
    // https://stackoverflow.com/questions/13507424/how-to-create-a-word-document-using-apache-poi
    // XWPF Reference(.docx)
    // HWPF Reference(.doc)
    public void create() throws IOException {
        XWPFDocument document = new XWPFDocument();
        //Write the Document in file system
        FileOutputStream out = new FileOutputStream(new File("new.docx"));
        document.write(out);
        out.close();
    }

    @Test
    public void dox4jCreate() throws IOException, Docx4JException {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

        //        String html = "<html><body><p>Hello World!</p></body></html>";
        //        wordMLPackage.getMainDocumentPart().addAltChunk(AltChunkType.Html, html.getBytes());

        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        //        mdp.addParagraphOfText("Paragraph 1");

        // Add the XHTML altChunk
        String xhtml =
                "<html><body><h1>Hello World!</h1><p>Hello World!</p><hr><h1>Hello World!</h1><p>Hello "
                        + "World!</p><hr><h1>Hello World!</h1><p>Hello World!</p><hr></body></html>";
        mdp.addAltChunk(AltChunkType.Xhtml, xhtml.getBytes());

        //        mdp.addParagraphOfText("Paragraph 3");

        wordMLPackage.save(new java.io.File(System.getProperty("user.dir") + "/new.docx"));
    }

    @Test
    public void doc4jRead() throws Exception {
        File doc = new File("new1.docx");
        WordprocessingMLPackage wordMLPackage =
                Docx4J.load(doc);
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
        org.docx4j.wml.Document wmlDocumentEl
                = (org.docx4j.wml.Document) documentPart.getJaxbElement();
        Body body = wmlDocumentEl.getBody();
        for (Object o : wmlDocumentEl.getContent()) {
            //System.out.println(o);
        }
        System.out.println("---------------------------");
        AbstractHtmlExporter exporter = new HtmlExporterNG2();
        HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
        OutputStream os = new ByteArrayOutputStream();
        javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(os);
        exporter.html(wordMLPackage, result, htmlSettings);
        System.out.println( ((ByteArrayOutputStream)os).toString() );



    }

    @Test
    public void readWord() throws XmlException, OpenXML4JException, IOException {
        OPCPackage opcPackage = POIXMLDocument.openPackage("new1.docx");
        POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
        String text2007 = extractor.getText();
        System.out.println(text2007);
    }

}
