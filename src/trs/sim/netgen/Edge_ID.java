package trs.sim.netgen;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import java.util.Set;

/**
 * Created by kwai on 12/08/14.
 */
public class Edge_ID {

    WeightedGraph<String,DefaultWeightedEdge> graph;
    Set<BasicEdge> edgeSet;
    public Edge_ID(WeightedGraph<String,DefaultWeightedEdge> graph,Set<BasicEdge> edgeSet){
       this.graph = graph;
        this.edgeSet = edgeSet;
    }

    public String getEdgeID(DefaultWeightedEdge edge){
        String lid = "";
        for(BasicEdge bedge:edgeSet){
            if((graph.getEdgeSource(edge).equals(bedge.getSource()) &&
                graph.getEdgeTarget(edge).equals(bedge.getTarget())) ||
               (graph.getEdgeSource(edge).equals(bedge.getTarget()) &&
                graph.getEdgeTarget(edge).equals(bedge.getSource())) ){
                lid = bedge.getLid();
            }
        }
        return lid;
    }
}
