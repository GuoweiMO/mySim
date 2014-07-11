package older_version.RoutesConstructor;

import older_version.TrafficNetGenerator.CoordinateTransfer;
import older_version.TrafficNetGenerator.OSMapReader;

import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;

/**
 * Created by kwai on 6/11/14.
 */
public class KSPTree {

        WeightedGraph<String,DefaultWeightedEdge> u_graph;
        OSMapReader reader;
        KShortestPaths ksp;
        List<GraphPath> pathList_1;
        List<GraphPath> pathList_2;

        public KSPTree(OSMapReader reader){
            u_graph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
            this.reader = reader;
            pathList_1 = new LinkedList<GraphPath>();
            pathList_2 = new LinkedList<GraphPath>();
        }

        public WeightedGraph<String,DefaultWeightedEdge> constructGraph(){

            LinkedList<BasicLink> roadsList = reader.getMyBasicLinkSet();

            for(BasicLink links:roadsList){
                //get end_vertices
                ArrayList nodesArr = links.getNodeIDs();
                String v0 = (String) nodesArr.get(nodesArr.size()-1);
                String vn = (String) nodesArr.get(0);

                if(!v0.equals(vn)) { // remove loops
                    u_graph.addVertex(v0);
                    u_graph.addVertex(vn);

                    DefaultWeightedEdge edge = u_graph.addEdge(v0, vn);

                    //compute weight (actual distance) (unit: meters)
                    double a_lon, b_lon,a_lat,b_lat;
                    double weight = 0.0d;
                    for(int i = 0; i<links.getLats().size()-1;i++) {
                        a_lon = (Double)(links.getLons()).get(i);
                        b_lon = (Double)(links.getLons()).get(i+1);
                        a_lat = (Double)(links.getLats()).get(i);
                        b_lat = (Double)(links.getLats()).get(i+1);
                        weight += Math.round(Math.sqrt(Math.pow((a_lon - b_lon) * 111000 * Math.sin((a_lat + b_lat) / 2), 2.0)
                                + Math.pow((a_lat - b_lat) * 111000, 2.0)));
                    }

                    if(edge != null) {
                        u_graph.setEdgeWeight(edge, weight);
                        System.out.println("["+v0+","+vn+"]  "+u_graph.getEdgeWeight(edge)+" meters");
                    }
                }

            }
            return u_graph;
        }

    public void runSingleSourceKSP(Object sourceVertex){

        System.out.println("\n Now the paths from " + sourceVertex + " are being computed as below:");

        ksp = new KShortestPaths(u_graph,sourceVertex,2);
        //int cnt = 0;
        for(Object vertex:u_graph.vertexSet()){
            if(!vertex.equals(sourceVertex)) { //exclude the path to self
                List<GraphPath> paths = ksp.getPaths(vertex);
                if (paths != null) {  // some node can not be reached
                    //print all reachable paths

                    //System.out.println("From: " + sourceVertex + " To: " + vertex + "  Shortest Length:" + paths.get(0).getWeight());
                    //System.out.println("Primary path:  " + paths.get(0));
                    //System.out.println("Secondary path:" + paths.get(1));
                    //System.out.println("");

                    pathList_1.add(paths.get(0));

                    if(paths.size()==2){
                    pathList_2.add(paths.get(1)); //store the second best path for each vertex pair
                    }
                    //cnt++;
                }
            }
        }
        System.out.println("Computation finishes." + " (Source: "+ sourceVertex +" ) \n");
        //System.out.println("");
    }

    public OSMapReader getOSMapReader() {
        return reader;
    }

    public void setOSMapReader(OSMapReader reader) {
        this.reader = reader;
    }

    public double getVertexXCoord(String vertex){
        LinkedList<BasicLink> basicLinkSet;
        basicLinkSet = reader.getMyBasicLinkSet();
        ArrayList<String> nodeIDs;
        CoordinateTransfer ct = new CoordinateTransfer(reader.getMinlat(),reader.getMaxlat(),reader.getMinlon(),reader.getMaxlon());
        double xcoord = 0.0d;
        for(BasicLink links: basicLinkSet){
            nodeIDs = links.getNodeIDs();
            for(int i = 0 ;i < nodeIDs.size(); i++){
                if(nodeIDs.get(i).equals(vertex)){
                    xcoord = ct.lonToScreenX((Double) links.getLons().get(i));
                }
            }
        }
        return xcoord;
    }

    public double getVertexYCoord(String vertex){
        LinkedList<BasicLink> basicLinkSet;
        basicLinkSet = reader.getMyBasicLinkSet();
        ArrayList<String> nodeIDs;
        CoordinateTransfer ct = new CoordinateTransfer(reader.getMinlat(),reader.getMaxlat(),reader.getMinlon(),reader.getMaxlon());
        double ycoord = 0.0d;
        for(BasicLink links: basicLinkSet){
            nodeIDs = links.getNodeIDs();
            for(int i = 0 ;i < nodeIDs.size(); i++){
                if(nodeIDs.get(i).equals(vertex)){
                    ycoord = ct.latToScreenY((Double) links.getLats().get(i));
                }
            }
        }
        return ycoord;
    }

    public List<GraphPath> getPathList_1(){
        return this.pathList_1;
    }

    public List<GraphPath> getPathList_2() {
        return pathList_2;
    }
}
