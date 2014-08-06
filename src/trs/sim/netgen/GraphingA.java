package trs.sim.netgen;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import java.util.List;

/**
 * Created by kwai on 06/08/14.
 */
public class GraphingA {

    WeightedGraph<String,DefaultWeightedEdge> u_graph;
    GMLReader reader;

    public GraphingA(GMLReader reader){
        u_graph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        this.reader = reader;
    }

    public WeightedGraph<String,DefaultWeightedEdge> constructGraph(){

        List<BasicEdge> roadsList = reader.getBasicEdgeSet();

        for(BasicEdge basicEdge:roadsList){

            //get end_vertices
            List<String> nodesList = basicEdge.getNodeIDs();
            String v0 = nodesList.get(0);
            String vn = nodesList.get(1);

            if(!v0.equals(vn)) { // remove loops
                u_graph.addVertex(v0);
                u_graph.addVertex(vn);

                DefaultWeightedEdge edge = u_graph.addEdge(v0, vn);

                //compute weight (actual distance) (unit: meters)
                double a_lon, b_lon,a_lat,b_lat;
                double weight = 0.0d;
                for(int i = 0; i<basicEdge.getCoords().size()-1;i++) {
                    a_lon = Double.parseDouble(basicEdge.getCoords().get(i).split(",")[0]);
                    a_lat = Double.parseDouble(basicEdge.getCoords().get(i).split(",")[1]);
                    b_lon = Double.parseDouble(basicEdge.getCoords().get(i+1).split(",")[0]);
                    b_lat = Double.parseDouble(basicEdge.getCoords().get(i+1).split(",")[1]);
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
}
