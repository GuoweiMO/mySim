package trs;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import trs.sim.*;

import java.util.List;
import java.util.Map;

/**
 * Created by kwai on 29/07/14.
 */
public class BasicRun {
    final double TOTAL_TRIPS = 10000;
    Map<String,Double> trips_0;
    Map<DefaultWeightedEdge,Double> flows_0;
    Map<String,List<DefaultWeightedEdge>> pathlist_0;
    Map<DefaultWeightedEdge,Double> SDE_Flows;
    Graphing graphing_0;
    SDEAlgo sde;

    public BasicRun(){

    }

    public void run(boolean runSDE){
        //construct basic weighted undirected graph
        graphing_0 = new Graphing();
        WeightedGraph<String,DefaultWeightedEdge> graph_0 = graphing_0.buildGraph();
        System.out.println("-------------------------The Network-----------------------------------------------");
        for(DefaultWeightedEdge edge:graph_0.edgeSet()){
            System.out.println(edge+" ["+graph_0.getEdgeWeight(edge)+"]");
        }

        //construct shortest routing trees for each source
        Routing routing_0 = new Routing(graph_0);
        for(String vertex:graph_0.vertexSet()) {
            routing_0.runKSP(vertex);
        }
        pathlist_0 = routing_0.getPathList();

        //construct O-D matrix
        System.out.println("-------------------------The Trips Between 2 Vertexes------------------------------");
        ODMatrix od_0 = new ODMatrix(graph_0,routing_0);
        od_0.generateCost(TOTAL_TRIPS);
        trips_0 = od_0.getTrips();
//        for(Map.Entry trip:trips_0.entrySet()) {
//            System.out.println(trip);
//        }


        //run All-or-Nothing Assignment
        System.out.println("-------------------------All-or-Nothing Assignment----------------------------------");
        AONAssignment aona_0 = new AONAssignment(graph_0,routing_0);
        aona_0.runAssignment(trips_0,pathlist_0);
        flows_0 = aona_0.getLink_flow();
        double pre_total=0.0d;
        for(Map.Entry<DefaultWeightedEdge,Double> flow:flows_0.entrySet()) {
            System.out.println(flow);
            pre_total += flow.getValue();
        }
        System.out.println("Total Flow :"+pre_total);

        //run Dynamic Equilibrium Assignment
        if(runSDE) {
            System.out.println("-------------------------Dynamic Equilibrium Assignment-----------------------------");
            sde = new SDEAlgo(graph_0, routing_0, aona_0);
            sde.algoInit();
            sde.runAlgo();
            SDE_Flows = sde.getNew_Flow();
        }

    }

    public static void main(String[] args){
        (new BasicRun()).run(true);
    }

    public Map<String, Double> getTrips_0() {
        return trips_0;
    }

    public Map<DefaultWeightedEdge, Double> getFlows_0() {
        return flows_0;
    }

    public Map<String, List<DefaultWeightedEdge>> getPathlist_0() {
        return pathlist_0;
    }
}
