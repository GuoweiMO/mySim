package trs.analy;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import trs.sim.AONAssignment;
import trs.sim.Graphing;
import trs.sim.Routing;
import trs.sim.SDEAlgo;

import java.util.*;

/**
 * Created by kwai on 29/07/14.
 */
public class Pricing {

    WeightedGraph<String,DefaultWeightedEdge> graph;
    WeightedGraph<String,DefaultWeightedEdge> graph_1;
    Set<DefaultWeightedEdge> CgsEdges;

    public Pricing(WeightedGraph<String, DefaultWeightedEdge> graph){
        this.graph = graph;
        graph_1 = graph;
        CgsEdges = new HashSet<DefaultWeightedEdge>();
    }

    public Map<DefaultWeightedEdge,Double> congestedPricing(
                                Map<String, List<DefaultWeightedEdge>> pathlist_0,
                                Map<String,Double> trips_0,
                                Map<DefaultWeightedEdge, Double> flows_0,
                                double cgsBound,boolean dynamic){
        for(DefaultWeightedEdge edge:graph_1.edgeSet()){
            if(flows_0.get(edge)>=cgsBound){
               CgsEdges.add(edge);
               //graph_1.removeEdge(edge); //reconstruct graph after pricing
               // flows_0.replace(edge,cgsBound);
            }
        }

        graph_1.removeAllEdges(CgsEdges);

        System.out.println("\nCongested Edges: "+ CgsEdges);

        Routing routing_1 = new Routing(graph_1);
        for(String vertex:graph_1.vertexSet()) {
            routing_1.runKSP(vertex);
        }

        Map<String,List<DefaultWeightedEdge>> pathlist_1 = routing_1.getPathList();

        Map<String,Double> trips_1 = trips_0;

        //compute how many path passing the congested edges
        Map<DefaultWeightedEdge,Double> CgsCnt = new HashMap<DefaultWeightedEdge, Double>();
        double sum_edges;
        for(DefaultWeightedEdge edge_2: CgsEdges){
            sum_edges = 0.0;
            for(Map.Entry<String, List<DefaultWeightedEdge>> path_m:pathlist_0.entrySet()){
                 if(path_m.getValue().contains(edge_2)){
                     sum_edges +=trips_1.get(path_m.getKey());
                 }
            }
            CgsCnt.put(edge_2,sum_edges);
        }

        //reduce trips that pass the charged edges
        for(DefaultWeightedEdge edge_3: CgsEdges){
            for(Map.Entry<String, List<DefaultWeightedEdge>> path_n:pathlist_0.entrySet()){
                if(path_n.getValue().contains(edge_3)){
                    double decremental = (flows_0.get(edge_3) - cgsBound);
                    double percentage = trips_1.get(path_n.getKey())/ CgsCnt.get(edge_3);
                    //System.out.println("trips: "+trips_1.get(path_n.getKey()));
                    //System.out.println("decremental: "+ decremental*percentage);
                    trips_1.replace(path_n.getKey(), trips_1.get(path_n.getKey())-decremental*percentage);
                }
            }
        }

        Map<DefaultWeightedEdge,Double> unCgsCnt = new HashMap<DefaultWeightedEdge, Double>();
        for(DefaultWeightedEdge edge_4: CgsEdges){
            String source = graph.getEdgeSource(edge_4);
            String target = graph.getEdgeTarget(edge_4);
            for(DefaultWeightedEdge edge_5: graph_1.edgeSet()){
                if(graph_1.getEdgeSource(edge_5).equals(source) || graph_1.getEdgeTarget(edge_5).equals(source) ||
                        graph_1.getEdgeSource(edge_5).equals(target) || graph_1.getEdgeTarget(edge_5).equals(target) ){
                    double sum_edges_2 = 0.0;
                    for(Map.Entry<String, List<DefaultWeightedEdge>> path_1:pathlist_1.entrySet()){
                        if(path_1.getValue().contains(edge_5)){
                            sum_edges_2 += trips_1.get(path_1.getKey());
                        }
                    }
                    unCgsCnt.put(edge_5,sum_edges_2);
                }
            }
        }



        //increase trips that pass the edges adjacent to charged edges
        for(DefaultWeightedEdge edge_6: CgsEdges){
            String source = graph.getEdgeSource(edge_6);
            String target = graph.getEdgeTarget(edge_6);
            for(DefaultWeightedEdge edge_7: graph_1.edgeSet()){
                if(graph_1.getEdgeSource(edge_7).equals(source) || graph_1.getEdgeTarget(edge_7).equals(source) ||
                   graph_1.getEdgeSource(edge_7).equals(target) || graph_1.getEdgeTarget(edge_7).equals(target) ){
                    for(Map.Entry<String, List<DefaultWeightedEdge>> path_1:pathlist_1.entrySet()){
                        if(path_1.getValue().contains(edge_7)){
                            double incremental = (flows_0.get(edge_6) - cgsBound)/5;
                            double percentage = trips_1.get(path_1.getKey())/CgsCnt.get(edge_6);
                            //System.out.println("trips: "+trips_1.get(path_1.getKey()));
                            //System.out.println("incremental: "+ incremental*percentage);
                            trips_1.replace(path_1.getKey(), trips_1.get(path_1.getKey())+incremental*percentage);
                        }
                    }
                }
            }
        }


        AONAssignment aona_1 = new AONAssignment(graph_1,routing_1);
        aona_1.runAssignment(trips_1,pathlist_1);

        if(dynamic){
            SDEAlgo sde_1 = new SDEAlgo(graph_1,routing_1,aona_1);
            sde_1.algoInit();
            sde_1.runAlgo();

        }

        return  aona_1.getLink_flow();
    }

    public void cordonPricing(){

    }

    public WeightedGraph<String, DefaultWeightedEdge> getGraph_1() {
        return graph_1;
    }
}
