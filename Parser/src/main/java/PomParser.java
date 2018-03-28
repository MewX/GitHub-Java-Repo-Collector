import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class PomParser {
    public void parsePomFile(File file) {
        try {
            // read in xml file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;

            builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            NodeList dependencies = document.getElementsByTagName("dependency");

            for (int i = 0; i < dependencies.getLength(); i++) {
                Element dependency = (Element) dependencies.item(i);

                System.out.println("GroupId: " + dependency.getElementsByTagName("groupId").item(0).getTextContent());
                System.out.println("artifactId: " + dependency.getElementsByTagName("artifactId").item(0).getTextContent());
                System.out.println("version: " + dependency.getElementsByTagName("version").item(0).getTextContent());
                System.out.print("\n");

                // TODO output results
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
