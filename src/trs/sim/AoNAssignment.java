package trs.sim;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kwai on 28/07/14.
 */
public class AoNAssignment {
    WeightedGraph<String,DefaultWeightedEdge> graph;
    Map<DefaultWeightedEdge,Double> link_flow;
    public AoNAssignment(WeightedGraph<String, DefaultWeightedEdge> graph){
        this.graph=graph;
        link_flow = new HashMap<DefaultWeightedEdge, Double>();
        for(DefaultWeightedEdge edge:graph.edgeSet()){
            link_flow.put(edge,0.0); // initialise the flow of the network (possibly no flow on some links )
        }
    }
    public void runAssignment(Map<String,Double> trips,Map<String,List<DefaultWeightedEdge>> pathlist){

        for(Map.Entry<String,List<DefaultWeightedEdge>> path:pathlist.entrySet()){ //each path
            //System.out.println(path.getKey());
            for(DefaultWeightedEdge edge:path.getValue()){ //each edge in the path
                if(!link_flow.containsKey(edge))
                    link_flow.replace(edge,trips.get(path.getKey()));
                else
                    link_flow.replace(edge,link_flow.get(edge)+trips.get(path.getKey()));
            }
        }
    }


    public Map<DefaultWeightedEdge, Double> getLink_flow() {
        return link_flow;
    }
}
