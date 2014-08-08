package trs.sim.netgen;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kwai on 06/08/14.
 */
public class GraphingA {

    WeightedGraph<String,DefaultWeightedEdge> u_graph;
    GMLReader reader;
    Map<DefaultWeightedEdge,Double> capacity;

    public GraphingA(GMLReader reader){
        u_graph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        this.reader = reader;
        capacity = new HashMap<DefaultWeightedEdge, Double>();
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

                double s_lon = Double.parseDouble(basicEdge.getCoords().get(0).split(",")[0]);
                //double s_lat = Double.parseDouble(basicEdge.getCoords().get(0).split(",")[1]);
                double e_lon = Double.parseDouble(basicEdge.getCoords().get(basicEdge.getCoords().size() -1).split(",")[0]);
                //double e_lat = Double.parseDouble(basicEdge.getCoords().get(basicEdge.getCoords().size() -1).split(",")[1]);

                double mid_lon = (s_lon + e_lon)/2 ;
                //double mid_lat = (s_lat + e_lat)/2 ;
                CoordTransfer ct = new CoordTransfer(Double.parseDouble(reader.getMin_lat()),
                        Double.parseDouble(reader.getMax_lat()),
                        Double.parseDouble(reader.getMin_lon()),
                        Double.parseDouble(reader.getMax_lon()));
                double x = ct.lonToScreenX(mid_lon);
                if(x <1366.0/8.0 || x > 1366.0*7.0/8.0){
                    capacity.put(edge,2200.0);
                }
                else if(x <1366.0/4.0 || x > 1366.0*3.0/4.0){
                    capacity.put(edge,2000.0);
                }
                else if(x <1366.0*3.0/8.0 || x > 1366.0*5.0/8.0){
                    capacity.put(edge,1800.0);
                }
                else {
                    capacity.put(edge,1600.0);
                }
            }
        }

        return u_graph;
    }

    public Map<DefaultWeightedEdge, Double> getCapacity() {
        return capacity;
    }
}
