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
    public void parsePomFile(File file, Database db, String projectName, String commitTag) {
        try {
            boolean hasDependency = false;

            // read in xml file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;

            builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            // parent tag
            NodeList parents = document.getElementsByTagName("parent");

            if (parents.getLength() != 0) {
                hasDependency = true;

                Element parent = (Element) parents.item(0);

                saveDependency(parent, db, projectName, commitTag);
            }

            // dependency tag
            NodeList dependencies = document.getElementsByTagName("dependency");

            for (int i = 0; i < dependencies.getLength(); i++) {
                hasDependency = true;

                Element dependency = (Element) dependencies.item(i);

                saveDependency(dependency, db, projectName, commitTag);
            }

            // no dependency found
            if (!hasDependency) {
                if (!db.checkProjectExistance(projectName)) {
                    db.insert(projectName, null,"no dependency", "no dependency", "no dependency");
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDependency(Element element, Database db, String projectName, String commitTag) {
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

        if (!db.checkExistance(projectName, groupId, artifactId, version)) {
            db.insert(projectName, commitTag, groupId, artifactId, version);
        }
    }
}
