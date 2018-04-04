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
    public void parsePomFile(File file, Database db) {
        try {
            // read in xml file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;

            builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            // parent tag
            NodeList parents = document.getElementsByTagName("parent");

            if (parents.getLength() != 0) {
                Element parent = (Element) parents.item(0);

                saveDependency(parent, db);
            }

            // dependency tag
            NodeList dependencies = document.getElementsByTagName("dependency");

            for (int i = 0; i < dependencies.getLength(); i++) {
                Element dependency = (Element) dependencies.item(i);

                saveDependency(dependency, db);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDependency(Element element, Database db) {
        // TODO output results
//                System.out.println("GroupId: " + element.getElementsByTagName("groupId").item(0).getTextContent());
//                System.out.println("artifactId: " + element.getElementsByTagName("artifactId").item(0).getTextContent());
//                if (element.getElementsByTagName("version").getLength() != 0) {
//                    System.out.println("version: " + element.getElementsByTagName("version").item(0).getTextContent());
//                } else {
//                    System.out.println("version: default");
//                }
//
//                System.out.print("\n");

        String groupId = element.getElementsByTagName("groupId").item(0).getTextContent();
        String artifactId = element.getElementsByTagName("artifactId").item(0).getTextContent();
        String version = element.getElementsByTagName("version").getLength() != 0 ? element.getElementsByTagName("version").item(0).getTextContent() : "default";

        // TODO modify project name
        if (!db.checkExistance("test-pom", groupId, artifactId, version)) {
            db.insert("test-pom", groupId, artifactId, version);
        }
    }
}
