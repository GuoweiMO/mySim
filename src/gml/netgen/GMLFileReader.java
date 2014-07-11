package gml.netgen;

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
    List<BasicEdge> BasicEdgeSet;
    Set<BasicNode> BasicNodeSet; //only store start and end point
    int uid = 0;
    public GMLFileReader(){
        highways = new ArrayList<String>();
        linkIDs = new ArrayList<String>();
        BasicEdgeSet = new ArrayList<BasicEdge>();
        BasicNodeSet = new HashSet<BasicNode>();
    }

    public void read(String fileDir){
        try{
            File gmlFile = new File(fileDir);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(gmlFile);
            doc.getDocumentElement().normalize();

            System.out.println("Reading data from " + fileDir);
            double cur_time = System.currentTimeMillis();
            /***************get map boundary*******************/
            Node bounds = doc.getElementsByTagName("boundedBy").item(0);
            if(bounds.getNodeType() == Node.ELEMENT_NODE){
                Element b_ele = (Element) bounds;
                NodeList coords = b_ele.getElementsByTagName("coord");
                min_lon = ((Element) coords.item(0)).getElementsByTagName("X").item(0).getTextContent();
                min_lat = ((Element) coords.item(0)).getElementsByTagName("Y").item(0).getTextContent();

                max_lon = ((Element) coords.item(1)).getElementsByTagName("X").item(0).getTextContent();
                max_lat = ((Element) coords.item(1)).getElementsByTagName("Y").item(0).getTextContent();
            }

            /*******************get features**********************/
            NodeList featureList = doc.getElementsByTagName("featureMember");
            for(int i=0;i<featureList.getLength();i++){
                Node item = featureList.item(i);
                if(item.getNodeType() == Node.ELEMENT_NODE){
                    Element ele = (Element) item;
                    Node data = ele.getElementsByTagName("data").item(0);
                    //System.out.println(data.getNodeType());
                    if(data.getNodeType() == Node.ELEMENT_NODE){
                        Element data_ele = (Element) data;
                        /****get road types****/
                        String highway = data_ele.getElementsByTagName("highway").item(0).getTextContent();
                        highways.add(highway);
                        /****get road ids****/
                        String linkId = data_ele.getElementsByTagName("OSMID").item(0).getTextContent();
                        linkIDs.add(linkId);

                        Node geometry = data_ele.getElementsByTagName("geometryProperty").item(0);
                        List<String> vertexes = new ArrayList<String>();

                        if(geometry.getNodeType()==Node.ELEMENT_NODE){
                            Element geo_ele = (Element) geometry;
                            String[] verTemp = (geo_ele.getElementsByTagName("coordinates").item(0).getTextContent()).split(" ");

                            BasicNode s_node = new BasicNode(verTemp[0].split(",")[0], verTemp[0].split(",")[1],
                                                            String.valueOf(uid++));
                            BasicNode e_node = new BasicNode(verTemp[verTemp.length-1].split(",")[0],
                                                             verTemp[verTemp.length-1].split(",")[1],String.valueOf(uid++));
                            if(i == 0) {
                                BasicNodeSet.add(s_node);
                                BasicNodeSet.add(e_node);
                            }
                            else {
                                int s_flag = 1;
                                int e_flag = 1;
                                for (BasicNode node1 : BasicNodeSet) {
                                    if ((node1.getX() + "," + node1.getY()).equals(s_node.getX() + "," + s_node.getY())) {
                                        s_flag = 0;
                                    }

                                    if ((node1.getX() + "," + node1.getY()).equals(e_node.getX() + "," + e_node.getY())) {
                                        e_flag = 0;
                                    }
                                }

                                if (s_flag == 1) {
                                    BasicNodeSet.add(s_node);
                                    //uid++;
                                }

                                if (e_flag == 1) {
                                    BasicNodeSet.add(e_node);
                                   // uid++;
                                }
                            }

                            vertexes.addAll(Arrays.asList(verTemp));

                            List<String> endVerIDs = new ArrayList<String>();
                            for(BasicNode node2: BasicNodeSet) {
                                if((node2.getX()+","+node2.getY()).equals(verTemp[0].trim())) {
                                    endVerIDs.add(node2.getID());
                                }
                                if((node2.getX()+","+node2.getY()).equals(verTemp[verTemp.length-1].trim())) {
                                    endVerIDs.add(node2.getID());
                                }
                            }

                            BasicEdgeSet.add(new BasicEdge(vertexes, endVerIDs, linkId, highway));

                        }
                    }
                }
            }

//           System.out.println(BasicNodeSet.size());
//           //System.out.println(BasicEdgeSet.size());
//            for(BasicEdge edge:BasicEdgeSet){
//                System.out.println(edge.getLid()+": "+edge.getCoords() + edge.getNodeIDs());
//            }
//
//            for(BasicNode nodes:BasicNodeSet){
//                    System.out.println(nodes.getID() + " : " + nodes.getX() + "," + nodes.getY());
//            }

            System.out.println("\n Finished reading. Cost: " + (System.currentTimeMillis() - cur_time)/1000 +"s");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<String> getHighways() {
        return highways;
    }

    public List<BasicEdge> getBasicEdgeSet() {
        return BasicEdgeSet;
    }

    public List<String> getLinkIDs() {
        return linkIDs;
    }

    public Set<BasicNode> getBasicNodeSet() {
        return BasicNodeSet;
    }

    public String getMax_lat() {
        return max_lat;
    }

    public String getMax_lon() {
        return max_lon;
    }

    public String getMin_lat() {
        return min_lat;
    }

    public String getMin_lon() {
        return min_lon;
    }
}
