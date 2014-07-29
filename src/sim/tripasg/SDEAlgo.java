package sim.tripasg;

import sim.db.JdbcUtils;
import sim.routing.KSPTree;
import sim.tripgen.CostMatrix;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by kwai on 16/07/14.
 */
public class SDEAlgo {
    List<Map<String,Object>> db_trips = BasicAssignment.queryTripsfromDB("milan_BAtrips_3"); //initial trips
    List<Map<String,Object>> db_pathinfo = CostMatrix.costMatrixfromDB("milan_paths");
    WeightedGraph<String,DefaultWeightedEdge> w_graph;
    KSPTree tree;
    WeightedGraph<String,DefaultWeightedEdge> u_graph;
    Map<DefaultWeightedEdge,Double> Aux_Flow;
    Map<DefaultWeightedEdge,Double> E_Cost;
    Map<DefaultWeightedEdge,Double> Old_Flow;
    Map<DefaultWeightedEdge,Double> New_Flow;
    Map<DefaultWeightedEdge,Double> Ini_Cost;
    Map<DefaultWeightedEdge,Double> AON_Flow;


    public SDEAlgo(KSPTree tree){
        w_graph = new SimpleWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class);
        this.tree = tree;
        u_graph = tree.getGraph();
        Aux_Flow = new HashMap<DefaultWeightedEdge, Double>();
        E_Cost = new HashMap<DefaultWeightedEdge, Double>();
        Old_Flow = new HashMap<DefaultWeightedEdge, Double>();
        New_Flow = new HashMap<DefaultWeightedEdge, Double>();
        Ini_Cost = new HashMap<DefaultWeightedEdge, Double>();
        AON_Flow = new HashMap<DefaultWeightedEdge, Double>();
    }

    //reconstruct the graphs containing only the links cover by the path.
    public void reConstructGraph(){
        for(Map trip:db_trips){
            String v0 = (String) trip.get("start_vertex");
            String vn = (String) trip.get("end_vertex");
            w_graph.addVertex(v0);
            w_graph.addVertex(vn);
            DefaultWeightedEdge w_edge = w_graph.addEdge(v0,vn);
            for(DefaultWeightedEdge u_edge:u_graph.edgeSet()) {
                if(u_edge.toString().equals(w_edge.toString())){
                w_graph.setEdgeWeight(w_edge, u_graph.getEdgeWeight(u_edge));
                    //System.out.println(u_graph.getEdgeWeight(u_edge));
                }
            }
        }
    }

    // initialization of the algorithm
    public void algoInit(){
        for (Map record:db_trips){
            Aux_Flow.put(w_graph.getEdge((String) record.get("start_vertex"),
                        (String)record.get("end_vertex")),(Double) record.get("trips"));

            AON_Flow.put(w_graph.getEdge((String) record.get("start_vertex"),
                    (String)record.get("end_vertex")),(Double) record.get("trips"));
        }

        for (DefaultWeightedEdge edge:w_graph.edgeSet()){
            E_Cost.put(edge,w_graph.getEdgeWeight(edge)/500); //initial cost (free speed meter/min), equivalent to 30kmh
            Ini_Cost.put(edge,w_graph.getEdgeWeight(edge)/500);
            //System.out.println(w_graph.getEdgeWeight(edge));
            Old_Flow.put(edge,0.0);  // initial flow
            New_Flow.put(edge,0.0);

        }
    }


    public void runAlgo(){
        //final Map<DefaultWeightedEdge,Double> Ste_Flow = Aux_Flow;
        float theta;
        for(int i = 1;;i++){ //start the iteration
            System.out.println("iteration "+ i);
            theta = (float) 1.0/i;
            double total_flows=0.0d;
            for(DefaultWeightedEdge edge1:w_graph.edgeSet()){
                New_Flow.replace(edge1,(1.0-theta)*Old_Flow.get(edge1)+theta*Aux_Flow.get(edge1));
                total_flows +=New_Flow.get(edge1);
               // System.out.println(Aux_Flow.get(edge1));
               // double arg = (New_Flow.get(edge1)-Old_Flow.get(edge1))/(New_Flow.get(edge1)+Old_Flow.get(edge1));
                //System.out.println("Initial Cost:"+ Ini_Cost.get(edge1));

               // System.out.print("Old Cost: " + E_Cost.get(edge1) + "  ");
                E_Cost.replace(edge1, Ini_Cost.get(edge1) * Math.exp(New_Flow.get(edge1)/1500.0));
               // System.out.println("New Cost: "+ E_Cost.get(edge1));

                System.out.println( "Old flow:"+ Old_Flow.get(edge1)+"   "+ "New Flow:"+ New_Flow.get(edge1));
            }

            System.out.println("Total flows:" +total_flows);

            double RG;
            double cur_sum =0.0;
            double std_sum =0.0;
            for(DefaultWeightedEdge edge_n:w_graph.edgeSet()){

                cur_sum += New_Flow.get(edge_n)*E_Cost.get(edge_n);
                std_sum += AON_Flow.get(edge_n)*E_Cost.get(edge_n);
            }

            RG = (cur_sum-std_sum)/cur_sum;
            //System.out.println("std_sum: "+std_sum +" cur_sum: "+ cur_sum + " RG: " + RG);

            if( i>1 && Math.abs(RG) <0.001){
                System.out.println("Equilibrium Reaches. (iteration "+i +" )");
                break;
            }

            int flag = 0;

            for(DefaultWeightedEdge edge_n:w_graph.edgeSet()){
                if(Math.abs(New_Flow.get(edge_n) - Old_Flow.get(edge_n)) > 1){
                    flag++;
                }

            }

            if(flag == 0){
                System.out.println("Equilibrium Reaches. (iteration "+i +" )");
                break;
            }

            Map<String,Double> new_ODTrips = this.rebuildODMatrix();
            BasicAssignment ba = new BasicAssignment(db_pathinfo,new_ODTrips);
            ba.runAssignment();
            Map<String,Double> update_trips = ba.getEdgeTrips();

            this.update_AuxFlow(update_trips);

            for(DefaultWeightedEdge edge2:w_graph.edgeSet()){
                Old_Flow.replace(edge2,New_Flow.get(edge2));
            }
        }
    }

    //for each iteration , rebuild the O-D matrix basing on the last iteration.
    public Map<String,Double> rebuildODMatrix(){
        String temStr;
        List<String> edgeArr;
        double path_cost =0.0;
        double total_cost = 0.0d;
        Map<String,Double> ODCosts = new HashMap<String, Double>();
        Map<String,Double> ODTrips = new HashMap<String, Double>();
        for(Map<String,Object> path:db_pathinfo){
            temStr = path.get("primary_path_edges").toString().trim();
            temStr = temStr.substring(1, temStr.length() - 1);

            edgeArr = Arrays.asList(temStr.split(","));

            for(String edge:edgeArr) {
                edge = edge.replace("(", "");
                edge = edge.replace(")", "");

                String vtx1 = edge.trim().split(":")[0].trim();
                String vtx2 = edge.trim().split(":")[1].trim();
                for(DefaultWeightedEdge edge2:w_graph.edgeSet()){
                    if(edge2.toString().contains(vtx1) && edge2.toString().contains(vtx2)){
                        path_cost += E_Cost.get(edge2);
                    }
                }
                ODCosts.put(path.get("start_vertex") + "," + path.get("end_vertex"), path_cost);
                path_cost = 0.0;
            }

            //System.out.println(ODCosts.get(path.get("start_vertex") + "," + path.get("end_vertex")));
        }

        for(Map.Entry<String,Double> record:ODCosts.entrySet()){
            total_cost += Math.exp((-0.1)*record.getValue());
        }

        for(Map.Entry<String,Double> record2:ODCosts.entrySet()){
            ODTrips.put(record2.getKey(), 100000 * Math.exp((-0.1) * record2.getValue()) / total_cost);
        }

        //System.out.println(total_cost);
        return ODTrips;
    }

    //update the current flow of last iteration to the auxiliary flow of this iteration
    public void update_AuxFlow(Map<String,Double> update_trips){
        double total_trips = 0.0;
        for (Map.Entry<String, Double> trip : update_trips.entrySet()) {
                total_trips += trip.getValue();
        }

        //System.out.println(total_trips);
        for(DefaultWeightedEdge edge_k:w_graph.edgeSet()) {
            for (Map.Entry<String, Double> trip : update_trips.entrySet()) {
                if(trip.getKey().contains(w_graph.getEdgeSource(edge_k)) && trip.getKey().contains(w_graph.getEdgeSource(edge_k))){
                    Aux_Flow.replace(edge_k,trip.getValue()*157174.0/total_trips); //update links with new flow
                    //System.out.println(trip.getValue()*157174.0/total_trips);
                }
            }
        }
    }

    public static void saveSDE2DB(String tableName,Map<DefaultWeightedEdge,Double> final_flows){
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
        for(Map.Entry entry:final_flows.entrySet()) {
            String sql = "insert into "+tableName+"(edge,flows) values(?,?)";
            List<Object> paras = new ArrayList<Object>();
            paras.add(entry.getKey().toString());
            paras.add(entry.getValue());
            try {
                boolean flag = jdbcUtils.updateByPrepStmt(sql, paras);
                System.out.println(flag);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public Map<DefaultWeightedEdge, Double> getNew_Flow() {
        return New_Flow;
    }
}
