import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class Parser {
    public static void main(String[] args) {
        try {
            // TODO modify file path
            File testFile = new File("test/pom.xml");

            // parser for pom.xml
            PomParser pomParser = new PomParser();

            // read in xml file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(testFile);

            document.getDocumentElement().normalize();

            // call pom parser
            if (testFile.getName().contains("pom.xml")) {
                pomParser.parsePomFile(document);
            }

            // call gradle parser

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
