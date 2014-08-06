package sim_old.tripasg;


import sim_old.netgen.GMLFileReader;
import sim_old.routing.KSPTree;
import sim_old.tripgen.CostMatrix;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;

/**
 * Created by kwai on 6/11/14.
 */
public class DynamicEquilibrium {
    GMLFileReader reader;
    KSPTree tree;
    Map<DefaultWeightedEdge,Double> E_Cost;
    Map<DefaultWeightedEdge,Double> E_Flow;
    Map<DefaultWeightedEdge,Double> Aux_Flow;
    Map<DefaultWeightedEdge,Double> Max_Capacity;
    BasicAssignment ba;
    public DynamicEquilibrium(KSPTree tree,GMLFileReader reader){
        this.tree = tree;
        this.reader = reader;
        E_Cost = new HashMap<DefaultWeightedEdge, Double>();
        E_Flow = new HashMap<DefaultWeightedEdge, Double>();
        Aux_Flow = new HashMap<DefaultWeightedEdge, Double>();
        Max_Capacity = new HashMap<DefaultWeightedEdge, Double>();

    }

    public void runSDEAlgo(){
        WeightedGraph<String, DefaultWeightedEdge> u_graph = tree.getGraph();
        /***** All Or Nothing Assignment to get Auxiliary flows****/
        List<Map<String,Object>> db_trips = BasicAssignment.queryTripsfromDB("milan_BAtrips_2");
        List<Map<String,Object>> db_pathinfo = CostMatrix.costMatrixfromDB("milan_paths");

        Map<String,Double> db_odmatrix = CostMatrix.buildODMatrix(db_pathinfo,100000,700);

        /******Initialization (Iteration 0)******/
        for(DefaultWeightedEdge edge:u_graph.edgeSet()){
            if(db_odmatrix.get(u_graph.getEdgeSource(edge)+","+u_graph.getEdgeTarget(edge)) != null) {
                E_Cost.put(edge, db_odmatrix.get(u_graph.getEdgeSource(edge) + "," + u_graph.getEdgeTarget(edge)));
            }else {
                E_Cost.put(edge,u_graph.getEdgeWeight(edge)/700);
            }
            E_Flow.put(edge,0.0);

            for(Map record:db_trips){
                if(edge.equals(u_graph.getEdge((String) record.get("start_vertex"), (String) record.get("end_vertex")))){
                    Aux_Flow.put(edge,(Double) record.get("trips"));
                    //System.out.println(edge+" "+ record.get("trips"));
                }
            }

            if(Aux_Flow.get(edge) == null){
                Aux_Flow.put(edge,0.0);
            }
        }

        //succeeds!
//        for(Map.Entry entry:Aux_Flow.entrySet()){
//            System.out.println(entry.getKey()+" " + entry.getValue());
//        }

        /****Start Iterations from 1****/

        double theta;
        Map<DefaultWeightedEdge,Double> new_flow = new HashMap<DefaultWeightedEdge, Double>();
        for(DefaultWeightedEdge n_edge:u_graph.edgeSet()){
            new_flow.put(n_edge,0.0);
        }
        Map<DefaultWeightedEdge,Double> old_flow ;

        old_flow = E_Flow;
        Max_Capacity = Aux_Flow;
        for (int i = 1; i<3; i++) {
            System.out.println("iteration " + i);
            theta = 1.0 / i;
            for(DefaultWeightedEdge edge2:u_graph.edgeSet()) {

                new_flow.replace(edge2,(1-theta)*old_flow.get(edge2) + theta*Aux_Flow.get(edge2));

                //System.out.println("old cost: "+ E_Cost.get(edge2));
                if (Max_Capacity.get(edge2) != 0.0) {
                    E_Cost.replace(edge2, E_Cost.get(edge2) * Math.exp(new_flow.get(edge2) / (5 * Max_Capacity.get(edge2))));
                }
                //System.out.println("new cost: "+E_Cost.get(edge2));

            }

            int flag = 0;
            for(DefaultWeightedEdge edge_n:u_graph.edgeSet()){
                if(Math.abs(new_flow.get(edge_n) - old_flow.get(edge_n)) < 10){
                    flag++;
                }
            }
            if(flag == 0){
                System.out.println("finished at iteration " + i);
                break;
            }else{
               for(DefaultWeightedEdge edge_k:u_graph.edgeSet()){
                   System.out.println("Flow: "+edge_k+" "+ new_flow.get(edge_k));
               }
            }

            Map<String,Double> e_cost = new HashMap<String, Double>();
            double total_cost =0.0d;
            for(DefaultWeightedEdge edge3:u_graph.edgeSet()){

                old_flow.replace(edge3,new_flow.get(edge3));
                if(db_odmatrix.containsKey(u_graph.getEdgeSource(edge3)+","+u_graph.getEdgeTarget(edge3))){
                    total_cost += Math.exp((-0.1)*E_Cost.get(edge3));
                }
            }

            for(Map.Entry record:db_odmatrix.entrySet()){

            }

            for(DefaultWeightedEdge edge4:u_graph.edgeSet()){
                e_cost.put(u_graph.getEdgeSource(edge4)+","+u_graph.getEdgeTarget(edge4),
                        100000*Math.exp((-0.1)*E_Cost.get(edge4))/total_cost);
                System.out.println("New ODTrips:"+ e_cost.get(u_graph.getEdgeSource(edge4)+","+u_graph.getEdgeTarget(edge4)));
            }


            ba = new BasicAssignment(db_pathinfo,e_cost);
            ba.runAssignment();
            Map<String,Double> update_trips = ba.getEdgeTrips();
            for(DefaultWeightedEdge edge5:u_graph.edgeSet()){
                if(update_trips.get(u_graph.getEdgeSource(edge5)+","+u_graph.getEdgeTarget(edge5)) != null)
                    Aux_Flow.replace(edge5,update_trips.get(u_graph.getEdgeSource(edge5)+","+u_graph.getEdgeTarget(edge5)));
            }

        }



    }


}
