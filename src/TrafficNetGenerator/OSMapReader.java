package TrafficNetGenerator;
/******
  created by kwai
*****/
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;


import RoutesConstructor.BasicLink;
import RoutesConstructor.BasicNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OSMapReader {

    boolean flag = false;
    BasicNode mynode;
    HashSet<BasicNode> basicNodeSet;
    LinkedList<BasicLink> myBasicLinkSet;
    ArrayList<Double> lons;
    ArrayList<Double> lats;
    ArrayList<String> nodeIDs;
    double minlat;
    double minlon;
    double maxlat;
    double maxlon;
    double currentTime;

    //<bounds minlat="51.5106" minlon="-0.0999" maxlat="51.5181" maxlon="-0.0795"/>

    String edgeType = "";

    public OSMapReader(){
      basicNodeSet = new HashSet<BasicNode>();
      myBasicLinkSet = new LinkedList<BasicLink>();

    }

    public void read(String fileDir){

        try{
            File xmlFile = new File(fileDir);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlFile);

            doc.getDocumentElement().normalize();

            System.out.println("OpenStreetMap: " + fileDir);
            System.out.println("---------------------------");
            System.out.println("reading map data, please wait...");
            currentTime = System.currentTimeMillis();
            /******************Get nodes*******************/
            NodeList nList = doc.getElementsByTagName("node");
            /******************Get Bounds *****************/
            NodeList bounds = doc.getElementsByTagName("bounds");
            Node bound = bounds.item(0);
            minlat = Double.parseDouble(((Element) bound).getAttribute("minlat"));
            minlon = Double.parseDouble(((Element) bound).getAttribute("minlon"));
            maxlat = Double.parseDouble(((Element) bound).getAttribute("maxlat"));
            maxlon = Double.parseDouble(((Element) bound).getAttribute("maxlon"));

            for(int i = 0; i< nList.getLength();i++){
                Node nNode = nList.item(i);

                if(nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element ele = (Element) nNode;

                    //if(ele.hasChildNodes()){ //get nodes only with tags
                        //String tag = ((Element) ele.getElementsByTagName("tag").item(0)).getAttribute("k");
//                        if(tag.equals("highway")) {
//                            System.out.println("node id : " + ele.getAttribute("id"));
//                            System.out.println("x : " + ele.getAttribute("lat"));
//                            System.out.println("y : " + ele.getAttribute("lon"));
                            //System.out.println("key :" + ((Element) ele.getElementsByTagName("tag").item(0)).getAttribute("k"));
                            String x = ele.getAttribute("lon");
                            String y = ele.getAttribute("lat");
                            String id = ele.getAttribute("id");
                            mynode = new BasicNode(Double.parseDouble(x),Double.parseDouble(y),id);

                            basicNodeSet.add(mynode);
//                        }

                   // }
                }
            }
            //System.out.println("total highway nodes: " + vertexSet.size());



            /******************Get highways*******************/
            NodeList wayList = doc.getElementsByTagName("way");

            for(int i = 0; i<wayList.getLength(); i++){ // loop all root tag way
                Node wNode = wayList.item(i);

                 lons = new ArrayList<Double>();
                 lats = new ArrayList<Double>();
                 nodeIDs = new ArrayList<String>();

                String edgeID = "";
                if (wNode.getNodeType() == Node.ELEMENT_NODE){
                    Element wElement = (Element) wNode;

                    //only get those way which are highways
                    if(wElement.hasChildNodes()){
                        NodeList childList = wElement.getChildNodes();

                        /****************************Child Nodes(nd,tag)******************************/
                        for(int j=0;j< childList.getLength();j++){  //loop all the child nodes

                            Node childNode = childList.item(childList.getLength()-1-j);
                            if (childNode.getNodeType() == Node.ELEMENT_NODE){
                                Element childEle = (Element) childNode;
                                String tag = childEle.getTagName();
                                if(tag.equals("tag"))
                                    if(childEle.getAttribute("k").equals("highway")){
                                       if(childEle.getAttribute("v").equals("primary") ||
                                          childEle.getAttribute("v").equals("primary_link")  ||
                                          childEle.getAttribute("v").equals("secondary")  ||
                                          childEle.getAttribute("v").equals("secondary_link")  ||
                                          childEle.getAttribute("v").equals("tertiary")  ||
                                          childEle.getAttribute("v").equals("tertiary_link")  ||
                                          childEle.getAttribute("v").equals("unclassified") ||
                                          childEle.getAttribute("v").equals("residential")){
                                            flag = true;
                                            edgeID = wElement.getAttribute("id");
                                            edgeType = childEle.getAttribute("v");
                                            //System.out.println("way id: " + edgeID);
                                            //System.out.println("highway type: " + edgeType);
                                            //System.out.println("Contains nodes: ");
                                        }
                                    }
                                if(flag && tag.equals("nd")){
                                    String nid = childEle.getAttribute("ref");
                                    //System.out.println("node id:" + nid);
                                    Iterator iter = basicNodeSet.iterator();
                                    while(iter.hasNext()){
                                        BasicNode linkNode = (BasicNode) iter.next();
                                        if(linkNode.getID().equals(nid)) {
                                            nodeIDs.add(nid);
                                            lons.add(linkNode.getX());
                                            lats.add(linkNode.getY());

                                            //System.out.println("    x: " + linkNode.getX() + " y:" + linkNode.getY());
                                        }
                                    }
                                }
                            }

                        } //end of child loop

                    }
                }

                if(!lons.isEmpty() && !lats.isEmpty() && !nodeIDs.isEmpty() && !edgeID.equals("")) {
                    myBasicLinkSet.add(new BasicLink(lons, lats, nodeIDs, edgeID));
//                    System.out.println("Edge "+ edgeID+"  " + "start point:"+nodeIDs.get(nodeIDs.size()-1)
//                            + " end point:" + nodeIDs.get(0));
                }

                flag = false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("finish reading. Time Cost: " +(System.currentTimeMillis()-currentTime)/1000 +"s\n");
    }


//    public void exportStandardGraph(String file_name){
//        CoordinateTransfer ct = new CoordinateTransfer(this.minlat,this.getMaxlat(),this.getMinlon(),this.maxlon);
//
//        int starting_pt_id = 0;
//        int ending_pt_id = 1;
//
//        int tag = 0;
//        HashSet<String> nodesIDSet = new HashSet<String>();
//        HashMap<Integer,String> pt_Set = new HashMap<Integer, String>();
//        //1. prepare the text to export
//        StringBuffer sb = new StringBuffer();
//        sb.append(419+"\n\n");
//        for(Link links:myLinkSet)
//        {
//            if(!nodesIDSet.contains((links.getNodeIDs()).get(links.getNodeIDs().size()-1))) {
//                nodesIDSet.add((String) (links.getNodeIDs()).get(links.getNodeIDs().size() - 1));
//                pt_Set.put(tag,(String) (links.getNodeIDs()).get(links.getNodeIDs().size() - 1));
//                starting_pt_id = tag;
//                tag ++;
//            }
//            else {
//                for(Map.Entry entry1:pt_Set.entrySet()){
//                    if((links.getNodeIDs()).get(links.getNodeIDs().size()-1).equals(entry1.getValue()) ){
//                        starting_pt_id = (Integer) entry1.getKey();
//                    }
//                }
//            }
//
//            if(!nodesIDSet.contains((links.getNodeIDs()).get(0))) {
//                nodesIDSet.add((String) (links.getNodeIDs()).get(0));
//                pt_Set.put(tag,(String) (links.getNodeIDs()).get(0));
//                ending_pt_id = tag;
//                tag++;
//            }else {
//                for(Map.Entry entry2:pt_Set.entrySet()){
//                    if(((links.getNodeIDs()).get(0)).equals(entry2.getValue()) ){
//                        ending_pt_id = (Integer) entry2.getKey();
//                    }
//                }
//            }
//
//                double x0 = ct.lonToScreenX((Double) (links.getLons()).get(0));
//                double xn = ct.lonToScreenX((Double) (links.getLons()).get(links.getLons().size() - 1));
//                double y0 = ct.latToScreenY((Double) (links.getLats()).get(0));
//                double yn = ct.latToScreenY((Double) (links.getLons()).get(links.getLats().size() - 1));
//                double weight = Math.round(Math.sqrt(Math.pow(x0 - xn, 2.0) + Math.pow(y0 - yn, 2.0)) - 5280000.0d);
//
//                sb.append(starting_pt_id + "	" + ending_pt_id + "	" + weight + "\n");
//        }
//        //2. open the file and put the data into the file.
//        Writer output = null;
//        try {
//            // use buffering
//            // FileWriter always assumes default encoding is OK!
//            output = new BufferedWriter(new FileWriter(new File(file_name)));
//            output.write(sb.toString());
//        }catch(FileNotFoundException e)
//        {
//            e.printStackTrace();
//        }catch(IOException e)
//        {
//            e.printStackTrace();
//        }finally {
//            // flush and close both "output" and its underlying FileWriter
//            try
//            {
//                if (output != null) output.close();
//            } catch(IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }

    public LinkedList<BasicLink> getMyBasicLinkSet() {
        return myBasicLinkSet;
    }

    public double getMinlat(){
        return minlat;
    }

    public double getMaxlat() {
        return maxlat;
    }

    public double getMinlon() {
        return minlon;
    }

    public double getMaxlon() {
        return maxlon;
    }

    public HashSet getBasicNodeSet() {
        return basicNodeSet;
    }

    public double getCurrentTime() {
        return currentTime;
    }
}