package TrafficNetGenerator;

import RoutesConstructor.BasicNode;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

/**
 * Created by kwai on 10/07/14.
 */
public class GMLFileReader {

    String min_lon;
    String min_lat;
    String max_lon;
    String max_lat;
    List<String> highways;
    List<String> linkIDs;
    Map<String,List<String>> vertLinkSet;
    Set<BasicNode> endVertSet; //only store start and end point
    public GMLFileReader(){
        highways = new ArrayList<String>();
        linkIDs = new ArrayList<String>();
        vertLinkSet = new HashMap<String, List<String>>();
        endVertSet = new HashSet<BasicNode>();
    }

    public void read(String fileDir){
        try{
            File gmlFile = new File(fileDir);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(gmlFile);
            doc.getDocumentElement().normalize();

            Node bounds = doc.getElementsByTagName("boundedBy").item(0);
            if(bounds.getNodeType() == Node.ELEMENT_NODE){
                Element b_ele = (Element) bounds;
                NodeList coords = b_ele.getElementsByTagName("coord");
                min_lon = ((Element) coords.item(0)).getElementsByTagName("X").item(0).getTextContent();
                min_lat = ((Element) coords.item(0)).getElementsByTagName("Y").item(0).getTextContent();

                max_lon = ((Element) coords.item(1)).getElementsByTagName("X").item(0).getTextContent();
                max_lat = ((Element) coords.item(1)).getElementsByTagName("Y").item(0).getTextContent();
            }


            NodeList featureList = doc.getElementsByTagName("featureMember");
            for(int i=0;i<featureList.getLength();i++){
                Node item = featureList.item(i);
                if(item.getNodeType() == Node.ELEMENT_NODE){
                    Element ele = (Element) item;
                    Node data = ele.getElementsByTagName("data").item(0);
                    //System.out.println(data.getNodeType());
                    if(data.getNodeType() == Node.ELEMENT_NODE){
                        Element data_ele = (Element) data;
                        String highway = data_ele.getElementsByTagName("osmtimesta").item(0).getTextContent();
                        highways.add(highway);
                        String linkId = data_ele.getElementsByTagName("osmuid").item(0).getTextContent();
                        linkIDs.add(linkId);

                        Node geometry = data_ele.getElementsByTagName("geometryProperty").item(0);
                        List<String> vertexes = new ArrayList<String>();
                        int uid = 0;
                        if(geometry.getNodeType()==Node.ELEMENT_NODE){
                            Element geo_ele = (Element) geometry;
                            String[] verTemp = (geo_ele.getElementsByTagName("coordinates").item(0).getTextContent()).split(" ");
                            for(BasicNode e_node:endVertSet) {
                                if(!(e_node.getX()+","+e_node.getY()).equals(verTemp[0].trim())) {
                                    endVertSet.add(new BasicNode(verTemp[0].split(",")[0],verTemp[0].split(",")[1],
                                            (String.valueOf(uid++))));
                                }
                                if(!(e_node.getX()+","+e_node.getY()).equals(verTemp[verTemp.length-1].trim())) {
                                    endVertSet.add(new BasicNode(verTemp[verTemp.length-1].split(",")[0],
                                            verTemp[verTemp.length-1].split(",")[1],(String.valueOf(uid++))));
                                }
                            }
                            vertexes.addAll(Arrays.asList(verTemp));
                        }
                        vertLinkSet.put(linkId, vertexes);
                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<String> getHighways() {
        return highways;
    }

    public Map<String, List<String>> getVertLinkSet() {
        return vertLinkSet;
    }

    public List<String> getLinkIDs() {
        return linkIDs;
    }

    public Set<BasicNode> getEndVertSet() {
        return endVertSet;
    }

}
