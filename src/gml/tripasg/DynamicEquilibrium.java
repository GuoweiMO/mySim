package gml.tripasg;


import gml.netgen.BasicEdge;
import gml.netgen.BasicNode;
import gml.netgen.GMLFileReader;
import gml.routing.KSPTree;
import gml.tripgen.CostMatrix;
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
    BasicAssignment ba;
    public DynamicEquilibrium(KSPTree tree,GMLFileReader reader){
        this.tree = tree;
        this.reader = reader;
        E_Cost = new HashMap<DefaultWeightedEdge, Double>();
        E_Flow = new HashMap<DefaultWeightedEdge, Double>();
        Aux_Flow = new HashMap<DefaultWeightedEdge, Double>();

    }

    public void runSDEAlgo(){
        WeightedGraph<String, DefaultWeightedEdge> u_graph = tree.getGraph();
        /***** All Or Nothing Assignment to get Auxiliary flows****/
        List<Map<String,Object>> db_trips = BasicAssignment.queryTripsfromDB("milan_BAtrips");
        List<Map<String,Object>> db_pathinfo = CostMatrix.costMatrixfromDB("milan_paths");

        /******Initialization (Iteration 0)******/
        for(DefaultWeightedEdge edge:u_graph.edgeSet()){
            E_Cost.put(edge,u_graph.getEdgeWeight(edge));
            E_Flow.put(edge,0.0);

            for(Map record:db_trips){
                if(edge.equals(u_graph.getEdge((String) record.get("start_vertex"), (String) record.get("end_vertex")))){
                    Aux_Flow.put(edge,(Double)record.get("trips"));
                }
                else Aux_Flow.put(edge,0.0);
            }
        }

        //succeeds!
//        for(Map.Entry entry:Aux_Flow.entrySet()){
//            System.out.println(entry.getKey()+" " + entry.getValue());
//        }

        /****Start Iterations from 1****/

        double theta;
        Map<DefaultWeightedEdge,Double> new_flow = new HashMap<DefaultWeightedEdge, Double>();
        Map<DefaultWeightedEdge,Double> old_flow = new HashMap<DefaultWeightedEdge, Double>();
        List<BasicEdge> edgeSet = reader.getBasicEdgeSet();
        old_flow = E_Flow;
        double density = 67; //vehicles/mile

        for (int i = 1; ; i++) {
            System.out.println("iteration " + i);
            theta = 1.0 / i;
            for(DefaultWeightedEdge edge2:u_graph.edgeSet()) {

                new_flow.put(edge2,(1-theta)*old_flow.get(edge2) + theta*Aux_Flow.get(edge2));

                for(BasicEdge basicEdge:edgeSet){
                    if(basicEdge.getNodeIDs().contains(u_graph.getEdgeSource(edge2)) &&
                       basicEdge.getNodeIDs().contains(u_graph.getEdgeTarget(edge2))){

                        String type = basicEdge.getType();
                        if(type.equals("motorway")||type.equals("motorway_link")){
                            E_Cost.replace(edge2,
                                    E_Cost.get(edge2)/2166*Math.exp(new_flow.get(edge2)*1609/(density*u_graph.getEdgeWeight(edge2))));
                        }
                        else if(type.equals("trunk_link")||type.equals("trunk")){
                            E_Cost.replace(edge2,
                                    E_Cost.get(edge2)/1833*Math.exp(new_flow.get(edge2)*1609/(density*u_graph.getEdgeWeight(edge2))));
                        }
                        else if(type.equals("primary")||type.equals("primary_link")){
                            E_Cost.replace(edge2,
                                    E_Cost.get(edge2)/1500*Math.exp(new_flow.get(edge2)*1609/(density*u_graph.getEdgeWeight(edge2))));
                        }
                        else if(type.equals("secondary")||type.equals("secondary_link")){
                            E_Cost.replace(edge2,
                                    E_Cost.get(edge2)/1166*Math.exp(new_flow.get(edge2)*1609/(density*u_graph.getEdgeWeight(edge2))));
                        }
                        else if(type.equals("tertiary")||type.equals("tertiary_link")){
                            E_Cost.replace(edge2,
                                    E_Cost.get(edge2)/833*Math.exp(new_flow.get(edge2)*1609/(density*u_graph.getEdgeWeight(edge2))));
                        }
                    }
                }

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
                   System.out.println(edge_k+" "+ new_flow.get(edge_k));
               }
            }

            Map<String,Double> e_cost = new HashMap<String, Double>();
            for(DefaultWeightedEdge edge3:u_graph.edgeSet()){

                old_flow.replace(edge3,new_flow.get(edge3));
                e_cost.put(u_graph.getEdgeSource(edge3)+","+u_graph.getEdgeTarget(edge3),E_Cost.get(edge3));
            }

            ba = new BasicAssignment(db_pathinfo,e_cost);
            ba.runAssignment();
            Map<String,Double> update_trips = ba.getEdgeTrips();
            for(DefaultWeightedEdge edge4:u_graph.edgeSet()){
                if(update_trips.get(u_graph.getEdgeSource(edge4)+","+u_graph.getEdgeTarget(edge4)) != null)
                    Aux_Flow.replace(edge4,update_trips.get(u_graph.getEdgeSource(edge4)+","+u_graph.getEdgeTarget(edge4)));
            }

        }



    }


}
