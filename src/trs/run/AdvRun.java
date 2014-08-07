package trs.run;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import sim_old.db.JdbcUtils;
import trs.sim.AoNAssignment;
import trs.sim.ODMatrix;
import trs.sim.Routing;
import trs.sim.SDEAlgo;
import trs.sim.netgen.GMLReader;
import trs.sim.netgen.GraphingA;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by kwai on 07/08/14.
 */
public class AdvRun {
    final double TOTAL_TRIPS = 25000; //morning peak, milan per hour. after calibration.
    Map<String,Double> trips;
    Map<DefaultWeightedEdge,Double> flows;
    Map<String,List<DefaultWeightedEdge>> pathlist_1;
    Map<String,Double> pathinfo_1;
    Map<DefaultWeightedEdge,Double> SDE_Flows;
    GraphingA graphingA;
    Routing routing;
    SDEAlgo sde;

    public AdvRun(){
        pathlist_1 = new HashMap<String, List<DefaultWeightedEdge>>();
        pathinfo_1 = new HashMap<String, Double>();
    }

    public void run(){

        /*********************NETWORK READING*************/
        GMLReader reader = new GMLReader();
        reader.read("Data/cen_milan.gml");

        graphingA = new GraphingA(reader);
        WeightedGraph<String,DefaultWeightedEdge> graph_0 = graphingA.constructGraph();
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

        /********************TREE BUILDING*************************/
//        routing_0 = new Routing(graph_0);
//        for(String vertex:graph_0.vertexSet()) {
//            routing_0.runKSP(vertex);
//        }
//        pathlist_1 = routing_0.getPathList_1();

        /********************Query from MySQL and Put into PathList/PathInfo/Centroids********************/
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();

        String sql = "select start_vertex,end_vertex,primary_path_length,primary_path_edges from milan_paths";
        String sql_2 = "select centroid from milan_centroids";
        List<Object> paras = new ArrayList<Object>();
        List<Map<String,Object>> QueryResult = new LinkedList<Map<String, Object>>();
        List<Map<String,Object>> CentroidList = new LinkedList<Map<String, Object>>();
        try {
            QueryResult = jdbcUtils.findMultiResult(sql, paras);
            CentroidList = jdbcUtils.findMultiResult(sql_2, paras);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(Map<String,Object> record:QueryResult){
            String key = record.get("start_vertex")+","+record.get("end_vertex");
            List<DefaultWeightedEdge> edgeList = new ArrayList<DefaultWeightedEdge>();
            String pathedges = record.get("primary_path_edges").toString().trim();
            pathedges = pathedges.substring(1, pathedges.length() - 1);
            String[] edgeArr = pathedges.split(",");
            for(String edge:edgeArr){
                edge = edge.trim();
                edge = edge.substring(1,edge.length()-1);
                String[] vtx = edge.split(":");
                for(DefaultWeightedEdge edge1:graph_0.edgeSet()){
                    if((graph_0.getEdgeSource(edge1).equals(vtx[0].trim()) && graph_0.getEdgeTarget(edge1).equals(vtx[1].trim())) ||
                       (graph_0.getEdgeTarget(edge1).equals(vtx[0].trim()) && graph_0.getEdgeSource(edge1).equals(vtx[1].trim())) ){
                        edgeList.add(edge1);
                    }
                }
            }
            pathlist_1.put(key, edgeList);
            pathinfo_1.put(key, (Double) record.get("primary_path_length"));
        }

        // System.out.println(QueryResult.size()+" " + pathlist_1.size()); test pass!



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


        /*****************run All-or-Nothing Assignment****************/
        System.out.println("-------------------------All-or-Nothing Assignment----------------------------------");
        AoNAssignment aona = new AoNAssignment(graph_0);
        aona.runAssignment(trips,pathlist_1);
        flows = aona.getLink_flow();

//        StringBuilder sb = new StringBuilder();
//        sb.append("-------------------------All-or-Nothing Assignment----------------------------------\n");
        double pre_total=0.0d;
        double pre_cost =0.0d;
        Set<DefaultWeightedEdge> zeroEdges = new HashSet<DefaultWeightedEdge>();
        for(Map.Entry<DefaultWeightedEdge,Double> flow:flows.entrySet()) {
            System.out.println(flow);
            if(flow.getValue() == 0.0){
                zeroEdges.add(flow.getKey());
            }
//            sb.append(flow+"\n");
            pre_total += flow.getValue();
            pre_cost += flow.getValue()*graph_0.getEdgeWeight(flow.getKey())/500.0*
                    (1+0.15*Math.pow(flow.getValue() / graphingA.getCapacity().get(flow.getKey()), 4.0));
        }
        System.out.println("Total Flow: "+pre_total + "  Total Cost: "+pre_cost);

        graph_0.removeAllEdges(zeroEdges); // remove the links that are 0 flows after assignment;

//        sb.append("Total Flow: "+pre_total + "  Total Cost: "+pre_cost);
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("OutPut/AoNA_Flows")));
//            writer.write(sb.toString());
//            writer.flush();
//            writer.close();
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }

        System.out.println("-------------------------Dynamic Equilibrium Assignment-----------------------------");
        sde = new SDEAlgo(graph_0, graphingA.getCapacity(), pathlist_1,pathinfo_1, aona);
        sde.algoInit();
        sde.runAlgo(false); //false for not running the part of pricing;
        SDE_Flows = sde.getNew_Flow();

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
        (new AdvRun()).run();
    }
}
