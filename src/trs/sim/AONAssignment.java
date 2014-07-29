package trs.sim;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kwai on 28/07/14.
 */
public class AONAssignment {
    WeightedGraph<String,DefaultWeightedEdge> graph;
    Routing route;
    Map<DefaultWeightedEdge,Double> link_flow;
    public AONAssignment(WeightedGraph<String,DefaultWeightedEdge> graph,Routing route){
        this.graph=graph;
        this.route = route;
        link_flow = new HashMap<DefaultWeightedEdge, Double>();
    }
    public void runAssignment(Map<String,Double> trips,Map<String,List<DefaultWeightedEdge>> pathlist){

        for(Map.Entry<String,List<DefaultWeightedEdge>> path:pathlist.entrySet()){ //each path
            for(DefaultWeightedEdge edge:path.getValue()){ //each edge in the path
                if(!link_flow.containsKey(edge))
                    link_flow.put(edge,trips.get(path.getKey()));
                else
                    link_flow.replace(edge,link_flow.get(edge)+trips.get(path.getKey()));
            }
        }
    }

    public Map<DefaultWeightedEdge, Double> getLink_flow() {
        return link_flow;
    }
}
