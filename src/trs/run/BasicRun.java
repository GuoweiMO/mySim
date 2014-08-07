package trs.run;

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
    GraphingB graphing_B_0;
    Routing routing_0;
    SDEAlgo sde;

    public BasicRun(){

    }

    public void run(boolean runSDE,boolean SDEPricing){
        //construct basic weighted undirected graph
        graphing_B_0 = new GraphingB();
        WeightedGraph<String,DefaultWeightedEdge> graph_0 = graphing_B_0.buildGraph();
        System.out.println("-------------------------The Network-----------------------------------------------");
        for(DefaultWeightedEdge edge:graph_0.edgeSet()){
            System.out.println(edge+" ["+graph_0.getEdgeWeight(edge)+"]");
        }

        //construct shortest routing trees for each source
        routing_0 = new Routing(graph_0);
        for(String vertex:graph_0.vertexSet()) {
            routing_0.runKSP(vertex);
        }
        pathlist_0 = routing_0.getPathList_1();

        //construct O-D matrix
        System.out.println("-------------------------The Trips Between 2 Vertexes------------------------------");
        ODMatrix od_0 = new ODMatrix(graph_0,routing_0.getPathinfo_1());
        od_0.generateCost(TOTAL_TRIPS);
        trips_0 = od_0.getTrips();
//        for(Map.Entry trip:trips.entrySet()) {
//            System.out.println(trip);
//        }


        //run All-or-Nothing Assignment
        System.out.println("-------------------------All-or-Nothing Assignment----------------------------------");
        AoNAssignment aona_0 = new AoNAssignment(graph_0);
        aona_0.runAssignment(trips_0,pathlist_0);
        flows_0 = aona_0.getLink_flow();
        double pre_total=0.0d;
        double pre_cost =0.0d;
        for(Map.Entry<DefaultWeightedEdge,Double> flow:flows_0.entrySet()) {
            System.out.println(flow);
            pre_total += flow.getValue();
            pre_cost += flow.getValue()*graph_0.getEdgeWeight(flow.getKey())/500*
                    (1+0.15*Math.pow(flow.getValue() / graphing_B_0.getEdge_capacity().get(flow.getKey()), 4.0));
        }
        System.out.println("Total Flow: "+pre_total + "Total Cost: "+pre_cost);

        if(runSDE) {
            System.out.println("-------------------------Dynamic Equilibrium Assignment-----------------------------");
            sde = new SDEAlgo(graph_0, graphing_B_0.getEdge_capacity(), pathlist_0,routing_0.getPathinfo_1(), aona_0);
            sde.algoInit();
            sde.runAlgo(false);
            SDE_Flows = sde.getNew_Flow();

//            int flag = 36;
//            while(flag != 0) {
//                for (DefaultWeightedEdge edge_x : graph_0.edgeSet()) {
//                    if (SDE_Flows.get(edge_x) / graphing_0.getEdge_capacity().get(edge_x) > 0.8) {
//                        double surplus = SDE_Flows.get(edge_x) - 0.8 * graphing_0.getEdge_capacity().get(edge_x);
//                        SDE_Flows.replace(edge_x, 0.8 * graphing_0.getEdge_capacity().get(edge_x));
//                        for (DefaultWeightedEdge ngb_edge : graph_0.edgeSet()) {
//                            if (!ngb_edge.equals(edge_x) &&
//                                    graph_0.getEdgeSource(ngb_edge).equals(graph_0.getEdgeSource(edge_x)) ||
//                                    graph_0.getEdgeSource(ngb_edge).equals(graph_0.getEdgeTarget(edge_x)) ||
//                                    graph_0.getEdgeTarget(ngb_edge).equals(graph_0.getEdgeSource(edge_x)) ||
//                                    graph_0.getEdgeTarget(ngb_edge).equals(graph_0.getEdgeTarget(edge_x))) {
//                                SDE_Flows.replace(ngb_edge, SDE_Flows.get(ngb_edge) + surplus / 5.0);
//                            }
//                        }
//                    }
//                }
//
//                flag = 0;
//                for(DefaultWeightedEdge edge_$ : graph_0.edgeSet()){
//                    if(SDE_Flows.get(edge_$) > graphing_0.getEdge_capacity().get(edge_$)*0.9)
//                        flag++;
//                }
//            }
//
//
//            sde.resetOld_Flow();
//            sde.resetCost();
//            sde.setAux_Flow(SDE_Flows);
//            sde.runAlgo();
        }

    }

    public static void main(String[] args){
        //run Basic Assignment
        //(new BasicRun()).run(false,false);
        //run SDE algorithm without pricing
        (new BasicRun()).run(true,false);
        //run SDE Algorithm with pricing.
        //(new BasicRun()).run(true,true);
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
