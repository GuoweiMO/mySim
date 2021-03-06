package trs.analy;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import trs.run.AdvRun;
import trs.sim.AoNAssignment;
import trs.sim.Routing;
import trs.sim.SDEAlgo;
import trs.sim.netgen.BasicEdge;
import trs.sim.netgen.GraphingA;
import trs.util.Result2JSON;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by kwai on 29/07/14.
 */
public class PricingModels {

    WeightedGraph<String,DefaultWeightedEdge> graph;
    WeightedGraph<String,DefaultWeightedEdge> graph_1;
    Routing routing_1;
    Set<DefaultWeightedEdge> CgsEdges;
    AoNAssignment aona_1;
    Map<String,List<DefaultWeightedEdge>> pathlist_1;
    Map<String,Double> trips_1;
    SDEAlgo sde;

    public PricingModels(WeightedGraph<String, DefaultWeightedEdge> graph, Routing routing){
        this.graph = graph;
        graph_1 = graph;
        this.routing_1 = routing;
        CgsEdges = new HashSet<DefaultWeightedEdge>();
    }

    public PricingModels(WeightedGraph<String, DefaultWeightedEdge> graph, Map<String, List<DefaultWeightedEdge>> pathlist_1){
        this.graph = graph;
        graph_1 = graph;
        this.pathlist_1 = pathlist_1;
        CgsEdges = new HashSet<DefaultWeightedEdge>();
    }

    public Map<DefaultWeightedEdge,Double> runGreedyMode(
            Map<String, List<DefaultWeightedEdge>> pathlist_0,
            Map<String, Double> trips_0,
            Map<DefaultWeightedEdge, Double> flows_0,
            double cgsBound){
        for(DefaultWeightedEdge edge:graph_1.edgeSet()){
            if(flows_0.get(edge)>=cgsBound){
               CgsEdges.add(edge);
               //graph_1.removeEdge(edge); //reconstruct graph after pricing
               // flows_0.replace(edge,cgsBound);
            }
        }

        graph_1.removeAllEdges(CgsEdges);

        System.out.println("\nCongested Edges: "+ CgsEdges);

        routing_1 = new Routing(graph_1);
        for(String vertex:graph_1.vertexSet()) {
            routing_1.runKSP(vertex);
        }

        pathlist_1 = routing_1.getPathList_1();

        trips_1 = trips_0;

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
                    trips_1.replace(path_n.getKey(), trips_1.get(path_n.getKey()) - decremental * percentage);
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
                            trips_1.replace(path_1.getKey(), trips_1.get(path_1.getKey()) + incremental*percentage);
                        }
                    }
                }
            }
        }


        aona_1 = new AoNAssignment(graph_1);
        aona_1.runAssignment(trips_1,pathlist_1);


        return  aona_1.getLink_flow();
    }

    public Map<DefaultWeightedEdge,Double> runAdjustingMode(Map<String, List<DefaultWeightedEdge>> pathlist_a,
                                                            Map<String, List<DefaultWeightedEdge>> pathlist_b,
                                                            Map<String, Double> trips_a,
                                                            Map<DefaultWeightedEdge, Double> flows_0,
                                                            Map<DefaultWeightedEdge,Double> capacity){
        //reset the removing edges set
        CgsEdges.clear();
        for(DefaultWeightedEdge edge:graph_1.edgeSet()){

            if(flows_0.get(edge)>= capacity.get(edge)){
                CgsEdges.add(edge);
            }
        }
        System.out.println("\nCongested Edges: "+ CgsEdges);

        return switchAssign(trips_a, flows_0, pathlist_a, pathlist_b, capacity);
    }

    public Map<DefaultWeightedEdge,Double> switchAssign(Map<String, Double> trips,
                                                        Map<DefaultWeightedEdge, Double> flows_0,
                                                        Map<String, List<DefaultWeightedEdge>> pathlist_1,
                                                        Map<String, List<DefaultWeightedEdge>> pathlist_2,
                                                        Map<DefaultWeightedEdge,Double> capacity ){
        Map<DefaultWeightedEdge,Double> link_flow = new HashMap<DefaultWeightedEdge, Double>();

        //initialise link_flow
        for(DefaultWeightedEdge edge_i:graph.edgeSet()){
            link_flow.put(edge_i,0.0);
        }

        //update the involved edges
        for(Map.Entry<String,List<DefaultWeightedEdge>> path:pathlist_1.entrySet()){ //each path

            double percentage = 1;
            for(DefaultWeightedEdge edge:path.getValue()){ //each edge in the path

                if(CgsEdges.contains(edge)) {
                   percentage  = flows_0.get(edge)/capacity.get(edge); //1.xx
                }

                if (!link_flow.containsKey(edge))
                        link_flow.put(edge, trips.get(path.getKey())/percentage);
                else
                        link_flow.replace(edge, link_flow.get(edge) + trips.get(path.getKey())/percentage);
            }
        }

        for(DefaultWeightedEdge edge:CgsEdges) {
            for (Map.Entry<String, List<DefaultWeightedEdge>> path_1 : pathlist_1.entrySet()) { //each path
                    if (path_1.getValue().contains(edge)) {
                        List<DefaultWeightedEdge> alter_path = pathlist_2.get(path_1.getKey()); // get the alternate path
                        for(DefaultWeightedEdge al_edge:alter_path) {

                        link_flow.replace(al_edge,link_flow.get(al_edge)+trips.get(path_1.getKey())*
                                                    (flows_0.get(edge)/capacity.get(edge) - 1));

                        }
                    }
                }
            }

        return  link_flow;

    }

    public Map<String,Double> runVariableToll(Map<String,Double> pathinfo_1,
                                Map<DefaultWeightedEdge, Double> flows_0,
                                Map<DefaultWeightedEdge,Double> capacity,
                                Set<BasicEdge> edgeSet,
                                AoNAssignment aona,float rate) {

        //reset the removing edges set
        CgsEdges.clear();
        for (DefaultWeightedEdge edge : graph_1.edgeSet()) {

            if (flows_0.get(edge) >= capacity.get(edge)) {
                CgsEdges.add(edge);
            }
        }

        sde = new SDEAlgo(graph, capacity, pathlist_1, pathinfo_1, aona, edgeSet, CgsEdges);
        sde.algoInit();
        sde.runAlgo(SDEAlgo.PricingType.VariableRoads, rate); //true for running the part of pricing;
        Map<DefaultWeightedEdge, Double> SDE_Flows = sde.getNew_Flow();
        //Result2JSON.writeAsJson("OutPut/4display/variable_toll.json", sde.getIte_flows(), sde.getIte_cost());


        AdvRun run = new AdvRun();
        Map<DefaultWeightedEdge,Double> tmp_Flows = run.runSimulation();
        System.out.println(sde.getCgsEdges());
        
        Double sde_value = 0.0;
        for(DefaultWeightedEdge edge:sde.getCgsEdges()){
            for(Map.Entry<DefaultWeightedEdge,Double> tmp:tmp_Flows.entrySet()){
                if(edge.toString().equals(tmp.getKey().toString())){
                   sde_value =  tmp.getValue();
                }
            }
            System.out.println(edge+" "+ sde_value.intValue() +" "+ SDE_Flows.get(edge).intValue() +
                    " " + (sde_value - SDE_Flows.get(edge))/sde_value );
        }


        Map<String, Double> vertex_flows = new HashMap<String, Double>();
        Map<String, Set<DefaultWeightedEdge>> vertex_edges = new HashMap<String, Set<DefaultWeightedEdge>>();

        for (String vertex : graph.vertexSet()) {
            Set<DefaultWeightedEdge> s_edges = graph.edgesOf(vertex);
            vertex_edges.put(vertex, s_edges);
        }
        for (Map.Entry<String, Set<DefaultWeightedEdge>> entry : vertex_edges.entrySet()) {
            Double v_flows = 0.0;
            for (DefaultWeightedEdge x_edge : entry.getValue()) {
                v_flows += SDE_Flows.get(x_edge);
            }
            vertex_flows.put(entry.getKey(), v_flows);
        }


//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("OutPut/4display/variable_toll_pricing")));
//            writer.write(sde.getSb().toString());
//            writer.flush();
//            writer.close();
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
        return vertex_flows;
    }

    public Map<String,Double> runFixedToll(Map<String,Double> pathinfo_1,
                                Map<DefaultWeightedEdge, Double> flows_0,
                                Map<DefaultWeightedEdge,Double> capacity,
                                Set<BasicEdge> edgeSet,
                                AoNAssignment aona,
                                float rate){

        //reset the removing edges set
        CgsEdges.clear();
        for(DefaultWeightedEdge edge:graph_1.edgeSet()){

            if(flows_0.get(edge)>= rate * capacity.get(edge)){
                CgsEdges.add(edge);
            }
        }

        //System.out.println("# of Charging Roads:" + CgsEdges.size());
        sde = new SDEAlgo(graph, capacity, pathlist_1,pathinfo_1, aona,edgeSet,CgsEdges);
        sde.algoInit();
        sde.runAlgo(SDEAlgo.PricingType.FixedRoads,rate); //true for running the part of pricing;
        Map<DefaultWeightedEdge,Double> SDE_Flows = sde.getNew_Flow();

        double total_tolls = 0.0;
        for(Map.Entry<DefaultWeightedEdge,Double> flow:SDE_Flows.entrySet()){
            if(CgsEdges.contains(flow.getKey())){
                total_tolls += SDE_Flows.get(flow.getKey())*0.0002*graph.getEdgeWeight(flow.getKey());
            }
        }

        System.out.println(total_tolls);
//        AdvRun run = new AdvRun();
//        Map<DefaultWeightedEdge,Double> tmp_Flows = run.runSimulation();
//        System.out.println(CgsEdges);
//        Double sde_value = 0.0;
//        for(DefaultWeightedEdge edge:CgsEdges){
//            for(Map.Entry<DefaultWeightedEdge,Double> tmp:tmp_Flows.entrySet()){
//                if(edge.toString().equals(tmp.getKey().toString())){
//                   sde_value =  tmp.getValue();
//                }
//            }
//            System.out.println(edge+" "+ sde_value.intValue() +" "+ SDE_Flows.get(edge).intValue() +
//                    " " + (sde_value - SDE_Flows.get(edge))/sde_value );
//        }



        Map<String,Double> vertex_flows = new HashMap<String, Double>();
        Map<String,Set<DefaultWeightedEdge>> vertex_edges = new HashMap<String, Set<DefaultWeightedEdge>>();

        for(String vertex:graph.vertexSet()){
            Set<DefaultWeightedEdge> s_edges = graph.edgesOf(vertex);
            vertex_edges.put(vertex,s_edges);
        }
        for(Map.Entry<String, Set<DefaultWeightedEdge>> entry:vertex_edges.entrySet()){
            Double v_flows = 0.0;
            for(DefaultWeightedEdge x_edge:entry.getValue()){
                v_flows+=SDE_Flows.get(x_edge);
            }
            vertex_flows.put(entry.getKey(),v_flows);
        }

        //Result2JSON.writeAsJson("OutPut/4display/fixed_toll_0.7.json", sde.getIte_flows(), sde.getIte_cost());

//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("OutPut/4display/fixed_toll_pricing")));
//            writer.write(sde.getSb().toString());
//            writer.flush();
//            writer.close();
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
            return vertex_flows;
    }


    public WeightedGraph<String, DefaultWeightedEdge> getGraph_1() {
        return graph_1;
    }

    public AoNAssignment getAona_1() {
        return aona_1;
    }

    public Routing getRouting_1() {
        return routing_1;
    }

    public Set<DefaultWeightedEdge> getCgsEdges() {
        return CgsEdges;
    }

    public Map<String, List<DefaultWeightedEdge>> getPathlist_1() {
        return pathlist_1;
    }

    public Map<String, Double> getTrips_1() {
        return trips_1;
    }

    public void setGraph_1(WeightedGraph<String, DefaultWeightedEdge> graph_1) {
        this.graph_1 = graph_1;
    }
}
