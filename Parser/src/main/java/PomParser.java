import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class PomParser {
    public void parsePomFile(Document document) {
        NodeList dependencies = document.getElementsByTagName("dependency");

        for (int i = 0; i < dependencies.getLength(); i++) {
            Element dependency = (Element) dependencies.item(i);

            System.out.println("GroupId: " + dependency.getElementsByTagName("groupId").item(0).getTextContent());
            System.out.println("artifactId: " + dependency.getElementsByTagName("artifactId").item(0).getTextContent());
            System.out.println("version: " + dependency.getElementsByTagName("version").item(0).getTextContent());
            System.out.println("\n");

            // TODO output results
        }
    }
}
