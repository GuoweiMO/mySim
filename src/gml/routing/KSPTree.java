package gml.routing;

import gml.netgen.BasicEdge;
import gml.netgen.BasicNode;
import gml.netgen.CoordinateTransfer;
import gml.netgen.GMLFileReader;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
* Created by kwai on 6/11/14.
*/
public class KSPTree {

        WeightedGraph<String,DefaultWeightedEdge> u_graph;
        GMLFileReader reader;
        KShortestPaths ksp;
        List<GraphPath> pathList_1;
        List<GraphPath> pathList_2;

        public KSPTree(GMLFileReader reader){
            u_graph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
            this.reader = reader;
            pathList_1 = new LinkedList<GraphPath>();
            pathList_2 = new LinkedList<GraphPath>();
        }

        public WeightedGraph<String,DefaultWeightedEdge> constructGraph(){

            List<BasicEdge> roadsList = reader.getBasicEdgeSet();

            for(BasicEdge links:roadsList){

                //get end_vertices
                List<String> nodesList = links.getNodeIDs();
                String v0 = nodesList.get(0);
                String vn = nodesList.get(1);

                if(!v0.equals(vn)) { // remove loops
                    u_graph.addVertex(v0);
                    u_graph.addVertex(vn);

                    DefaultWeightedEdge edge = u_graph.addEdge(v0, vn);

                    //compute weight (actual distance) (unit: meters)
                    double a_lon, b_lon,a_lat,b_lat;
                    double weight = 0.0d;
                    for(int i = 0; i<links.getCoords().size()-1;i++) {
                        a_lon = Double.parseDouble(links.getCoords().get(i).split(",")[0]);
                        a_lat = Double.parseDouble(links.getCoords().get(i).split(",")[1]); ;
                        b_lon = Double.parseDouble(links.getCoords().get(i+1).split(",")[0]); ;
                        b_lat = Double.parseDouble(links.getCoords().get(i+1).split(",")[1]); ;
                        weight += Math.round(Math.sqrt(Math.pow((a_lon - b_lon) * 111000 * Math.sin((a_lat + b_lat) / 2), 2.0)
                                + Math.pow((a_lat - b_lat) * 111000, 2.0)));
                    }

                    if(edge != null) {
                        u_graph.setEdgeWeight(edge, weight);
                        //System.out.println("["+v0+","+vn+"]  "+u_graph.getEdgeWeight(edge)+" meters");
                    }
                }

            }
            return u_graph;
        }

    public void runSingleSourceKSP(Object sourceVertex,Set vertexSet){

        System.out.println("\nNow the paths from " + sourceVertex + " are being computed as below:");

        ksp = new KShortestPaths(u_graph,sourceVertex,2);

        for(Object vertex:vertexSet){
            if(!vertex.equals(sourceVertex)) { //exclude the path to self
                List<GraphPath> paths = ksp.getPaths(vertex);
                if (paths != null) {  // some node can not be reached
                    //print all reachable paths
                    System.out.println("From: " + sourceVertex + " To: " + vertex + "  Shortest Length:" + paths.get(0).getWeight());
                    System.out.println("Primary path:  " + paths.get(0));
                    //System.out.println("Secondary path:" + paths.get(1));
                    //System.out.println("");

                    pathList_1.add(paths.get(0));

                    if(paths.size()==2){
                    pathList_2.add(paths.get(1)); //store the second best path for each vertex pair
                    }

                }
            }
        }
        System.out.println("Computation finishes." + " (Source: "+ sourceVertex +" ) \n");

    }

    public GMLFileReader getReader() {
        return reader;
    }

    public double getVertexXCoord(String vertex){
        Set<BasicNode> basicNodeSet;
        basicNodeSet = reader.getBasicNodeSet();

        CoordinateTransfer ct = new CoordinateTransfer(
                                        Double.parseDouble(reader.getMin_lat()),
                                        Double.parseDouble(reader.getMax_lat()),
                                        Double.parseDouble(reader.getMin_lon()),
                                        Double.parseDouble(reader.getMax_lon()));
        double xcoord = 0.0d;
        for(BasicNode node: basicNodeSet){
            if(node.getID().equals(vertex)){
                    xcoord = ct.lonToScreenX(Double.parseDouble(node.getX()));
            }
        }
        return xcoord;
    }

    public double getVertexYCoord(String vertex){
        Set<BasicNode> basicNodeSet;
        basicNodeSet = reader.getBasicNodeSet();

        CoordinateTransfer ct = new CoordinateTransfer(Double.parseDouble(reader.getMin_lat()),
                                        Double.parseDouble(reader.getMax_lat()),
                                        Double.parseDouble(reader.getMin_lon()),
                                        Double.parseDouble(reader.getMax_lon()));
        double ycoord = 0.0d;
        for(BasicNode node: basicNodeSet){
            if(node.getID().equals(vertex)){
                    ycoord = ct.latToScreenY(Double.parseDouble(node.getY()));
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
