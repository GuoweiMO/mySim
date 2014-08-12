package trs.run;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import sim_old.db.JdbcUtils;
import trs.sim.AoNAssignment;
import trs.sim.ODMatrix;
import trs.sim.Routing;
import trs.sim.SDEAlgo;
import trs.sim.netgen.BasicEdge;
import trs.sim.netgen.GMLReader;
import trs.sim.netgen.GraphingA;
import trs.util.Result2JSON;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by kwai on 07/08/14.
 */
public class AdvRun {
    final double TOTAL_TRIPS = 25000; //morning peak, milan per hour. after calibration.
    static final double Free_Speed = 500.0;
    Map<String,Double> trips;
    Map<DefaultWeightedEdge,Double> flows;
    Map<String,List<DefaultWeightedEdge>> pathlist_1;
    Map<String,List<DefaultWeightedEdge>> pathlist_2;
    Map<String,Double> pathinfo_1;
    Map<String,Double> pathinfo_2;
    Map<DefaultWeightedEdge,Double> SDE_Flows;
    GraphingA graphingA;
    Routing routing;
    SDEAlgo sde;
    AoNAssignment aona;
    WeightedGraph<String, DefaultWeightedEdge> graph_0;

    public AdvRun(){
        pathlist_1 = new HashMap<String, List<DefaultWeightedEdge>>();
        pathinfo_1 = new HashMap<String, Double>();
        pathlist_2 = new HashMap<String, List<DefaultWeightedEdge>>();
        pathinfo_2 = new HashMap<String, Double>();
    }

    public void runSimulation(){
        runGraphing();
        reConstructPath();
        runODMatrix();
        runAoNAssignment();
        //refineGraph();
        runEquilibrium();
    }

    public void runSim4Pricing(){
        runGraphing();
        reConstructPath();
        runODMatrix();
        runAoNAssignment();
    }

    public void runGraphing() {
        /*********************NETWORK READING*************/
        GMLReader reader = new GMLReader();
        reader.read("Data/cen_milan.gml");

        graphingA = new GraphingA(reader);
        graph_0 = graphingA.constructGraph();
//        StringBuffer sb = new StringBuffer();
//        sb.append("-------------------------The Network-----------------------------------------------\n");
//        for(DefaultWeightedEdge edge:graph_0.edgeSet()){
//            sb.append(edge + " [" + graph_0.getEdgeWeight(edge) + "] " +"Capacity:"+graphingA.getCapacity().get(edge)+"\n");
//        }
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("OutPut/network")));
//            writer.write(sb.toString());
//            writer.flush();
//            writer.close();
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
     }

     public void runKSPTree() {
         /********************TREE BUILDING*************************/
//        routing_0 = new Routing(graph_0);
//        for(String vertex:graph_0.vertexSet()) {
//            routing_0.runKSP(vertex);
//        }
//        pathlist_1 = routing_0.getPathList_1();

     }

    public void reConstructPath() {
        /********************Query from MySQL and Put into PathList/PathInfo********************/
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();

        String sql = "select start_vertex,end_vertex,primary_path_length,primary_path_edges," +
                        "secondary_path_length,secondary_path_edges from milan_paths";

        List<Object> paras = new ArrayList<Object>();
        List<Map<String, Object>> QueryResult = new LinkedList<Map<String, Object>>();
        try {
            QueryResult = jdbcUtils.findMultiResult(sql, paras);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        recoverPath(QueryResult,"primary_path_edges","primary_path_length",1);
        //System.out.println("starting for the second paths:");
        recoverPath(QueryResult, "secondary_path_edges", "secondary_path_length", 2);
    }

    public void recoverPath(List<Map<String, Object>> QueryResult, String attr1,String attr2,int tag){
         for (Map<String, Object> record : QueryResult) {
             String key = record.get("start_vertex") + "," + record.get("end_vertex");
             List<DefaultWeightedEdge> edgeList = new ArrayList<DefaultWeightedEdge>();
             String pathedges = record.get(attr1).toString().trim();
             if(pathedges.equals(null) || pathedges.equals("")){
                 pathedges = record.get("primary_path_edges").toString().trim();
             }
             pathedges = pathedges.substring(1, pathedges.length() - 1);
             //System.out.println(pathedges);
             String[] edgeArr = pathedges.split(",");
             for (String edge : edgeArr) {
                 edge = edge.trim();
                 edge = edge.substring(1, edge.length() - 1);
                 String[] vtx = edge.split(":");
                 for (DefaultWeightedEdge edge1 : graph_0.edgeSet()) {
                     if ((graph_0.getEdgeSource(edge1).equals(vtx[0].trim()) && graph_0.getEdgeTarget(edge1).equals(vtx[1].trim()))||
                         (graph_0.getEdgeTarget(edge1).equals(vtx[0].trim()) && graph_0.getEdgeSource(edge1).equals(vtx[1].trim()))){
                         edgeList.add(edge1);
                     }
                 }
             }
             if(tag == 1) {
                 pathlist_1.put(key, edgeList);
                 pathinfo_1.put(key, (Double) record.get(attr2));
             }
             if (tag == 2){
                 pathlist_2.put(key, edgeList);
                 if(record.get(attr2) == null || record.get(attr2) == "") {
                     pathinfo_2.put(key, (Double) record.get("primary_path_length"));
                 }else {
                     pathinfo_2.put(key, Double.parseDouble(record.get(attr2).toString()));
                 }
             }
         }

         // System.out.println(QueryResult.size()+" " + pathlist_1.size()); test pass!
    }


    public void runODMatrix() {
         /****************construct O-D matrix********************/
         System.out.println("-------------------------The Trips Between 2 Vertexes------------------------------");
         ODMatrix od_0 = new ODMatrix(graph_0, pathinfo_1);
         od_0.generateCost(TOTAL_TRIPS);
         trips = od_0.getTrips();

//        StringBuilder sb = new StringBuilder();
//        sb.append("-------------------------The O-D Matrix-----------------------------------------------\n");
//        for(Map.Entry trip: trips.entrySet()) {
//            sb.append(trip+"\n");
//        }
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("OutPut/OD_matrix")));
//            writer.write(sb.toString());
//            writer.flush();
//            writer.close();
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
    }

    public void runAoNAssignment() {
        /*****************run All-or-Nothing Assignment****************/
        System.out.println("-------------------------All-or-Nothing Assignment----------------------------------");
        aona = new AoNAssignment(graph_0);
        aona.runAssignment(trips, pathlist_1);
        flows = aona.getLink_flow();

//        StringBuilder sb = new StringBuilder();
        //sb.append("-------------------------All-or-Nothing Assignment----------------------------------\n");
        double pre_total = 0.0d;
        double pre_cost = 0.0d;

        for (Map.Entry<DefaultWeightedEdge, Double> flow : flows.entrySet()) {
            System.out.println(flow);
//            for(BasicEdge edge:graphingA.getGraphEdges()){
//                if((graph_0.getEdgeSource(flow.getKey()).equals(edge.getSource()) &&
//                    graph_0.getEdgeTarget(flow.getKey()).equals(edge.getTarget())) ||
//                   (graph_0.getEdgeSource(flow.getKey()).equals(edge.getTarget()) &&
//                    graph_0.getEdgeTarget(flow.getKey()).equals(edge.getSource())) ){
//                    sb.append(flow.getKey()+" "+ edge.getLid()+ " \n");
//                }
//            }

            pre_total += flow.getValue();
            pre_cost += flow.getValue() * graph_0.getEdgeWeight(flow.getKey()) / Free_Speed *
                    (1 + 0.15 * Math.pow(flow.getValue() / graphingA.getCapacity().get(flow.getKey()), 4.0));
        }
        System.out.println("Total Flow: " + pre_total + "  Total Cost: " + pre_cost);

//        sb.append("Total Flow: "+pre_total + "  Total Cost: "+pre_cost);
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("OutPut/Edge_ID")));
//            writer.write(sb.toString());
//            writer.flush();
//            writer.close();
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
    }

    public void refineGraph(){
        Set<DefaultWeightedEdge> zeroEdges = new HashSet<DefaultWeightedEdge>();
        for (Map.Entry<DefaultWeightedEdge, Double> flow : flows.entrySet()) {
            if (flow.getValue() == 0.0) {
                zeroEdges.add(flow.getKey());
            }
        }
        graph_0.removeAllEdges(zeroEdges); // remove the links that are 0 flows after assignment;

    }

    public void runEquilibrium(){
        System.out.println("-------------------------Dynamic Equilibrium Assignment-----------------------------");
        sde = new SDEAlgo(graph_0, graphingA.getCapacity(), pathlist_1,graphingA.getGraphEdges(),pathinfo_1, aona);
        sde.algoInit();
        sde.runAlgo(SDEAlgo.PricingType.None); //false for not running the part of pricing;
        SDE_Flows = sde.getNew_Flow();

        Result2JSON.writeAsJson("OutPut/4display/sde.json",sde.getIte_flows(),sde.getIte_cost());

//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("OutPut/equilibrium/ude")));
//            writer.write(sde.getSb().toString());
//            writer.flush();
//            writer.close();
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }

//        double post_flow = 0.0d;
//        double post_cost = 0.0d;
//        for(Map.Entry<DefaultWeightedEdge,Double> flow:SDE_Flows.entrySet()) {
//
//            post_flow += flow.getValue();
//            post_cost += flow.getValue()*graph_0.getEdgeWeight(flow.getKey())/500.0*
//                     (1+0.15*Math.pow(flow.getValue()/graphingA.getCapacity().get(flow.getKey()),4.0));
//        }
//        System.out.println("Total Flow: " +post_flow+"  "+"Total Cost: "+post_cost);

    }

    public static void main(String[] args){
        (new AdvRun()).runSimulation();
    }
}
