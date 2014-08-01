package trs.sim;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kwai on 28/07/14.
 */
public class SDEAlgo {

    WeightedGraph<String,DefaultWeightedEdge> graph;
    Map<DefaultWeightedEdge,Double> Aux_Flow;
    Map<DefaultWeightedEdge,Double> Old_Flow;
    Map<DefaultWeightedEdge,Double> New_Flow;
    Map<DefaultWeightedEdge,Double> E_Cost;
    Map<DefaultWeightedEdge,Double> Ini_Cost;
    Map<String,Double> final_Trips;
    AONAssignment aona;
    Routing routing;
    Map<DefaultWeightedEdge,Double> edge_Capacity;
    boolean dynpricing = false;

    public SDEAlgo(WeightedGraph<String,DefaultWeightedEdge> graph,
                   Map<DefaultWeightedEdge,Double> edge_Capacity,
                   Routing routing,AONAssignment aona){

        this.graph = graph;
        this.routing = routing;
        this.aona = aona;
        this.edge_Capacity = edge_Capacity;

        Aux_Flow = new HashMap<DefaultWeightedEdge, Double>();
        E_Cost = new HashMap<DefaultWeightedEdge, Double>();
        Old_Flow = new HashMap<DefaultWeightedEdge, Double>();
        New_Flow = new HashMap<DefaultWeightedEdge, Double>();
        Ini_Cost = new HashMap<DefaultWeightedEdge, Double>();
    }

    // initialization of the algorithm
    public void algoInit(){
        for (DefaultWeightedEdge edge:graph.edgeSet()){

            Aux_Flow.put(edge,aona.getLink_flow().get(edge));

            E_Cost.put(edge,graph.getEdgeWeight(edge)/500); //initial cost
            Ini_Cost.put(edge,graph.getEdgeWeight(edge)/500);

            Old_Flow.put(edge,0.0);  // initial flow
            New_Flow.put(edge,0.0);
        }
    }


    public void runAlgo(){
            float theta;
            boolean reached= false;
            for(int i = 1;;i++){ //start the iteration
                System.out.println("iteration "+ i);
                theta = (float) 1.0/i;
                double total_flows=0.0d;
                double total_cost = 0.0d;
                for(DefaultWeightedEdge edge1:graph.edgeSet()){
                    New_Flow.replace(edge1,(1.0-theta)*Old_Flow.get(edge1)+theta*Aux_Flow.get(edge1));

                    //System.out.print("current capacity: " + edge_Capacity.get(edge1) + "  ");

                    System.out.println( edge1+" ["+Old_Flow.get(edge1)+" --> "+ New_Flow.get(edge1)+"]");
//                    if(New_Flow.get(edge1) > edge_Capacity.get(edge1)*0.8){
//                        E_Cost.replace(edge1, Ini_Cost.get(edge1) * (1+0.35*Math.pow(New_Flow.get(edge1) / edge_Capacity.get(edge1), 4.0)));
//                    }
//                    else
                    E_Cost.replace(edge1, Ini_Cost.get(edge1) * (1+0.15*Math.pow(New_Flow.get(edge1) / edge_Capacity.get(edge1), 4.0)));

                    total_flows +=New_Flow.get(edge1);
                    double actual_cost =  Ini_Cost.get(edge1) * (1+0.15*Math.pow(New_Flow.get(edge1) / edge_Capacity.get(edge1), 4.0));
                    total_cost += actual_cost*New_Flow.get(edge1);

                }

                System.out.println("Total flows:" +total_flows +"| Total Cost: " + total_cost);

                double RG;
                double cur_sum =0.0;
                double std_sum =0.0;
                for(DefaultWeightedEdge edge_n:graph.edgeSet()){

                    cur_sum += New_Flow.get(edge_n)*E_Cost.get(edge_n);
                    std_sum += Aux_Flow.get(edge_n)*E_Cost.get(edge_n);
                }

                RG = (cur_sum-std_sum)/cur_sum;
                System.out.println("RelGap: "+ RG);
                if( i>1 && Math.abs(RG) <0.001){
                    System.out.println("Social Equilibrium Reaches. (iteration "+i +" )");
                    break;
                }

                int flag = 0;
                for(DefaultWeightedEdge edge_n:graph.edgeSet()){
                    if(Math.abs(New_Flow.get(edge_n) - Old_Flow.get(edge_n)) > 1){
                        flag++;
                    }
                }
                if(flag == 0){
                    System.out.println("User Equilibrium Reaches. (iteration "+i +" )");
                    break;
                }

                Map<DefaultWeightedEdge,Double> updated_trips = this.reRunAON(10000,routing);
                this.update_AuxFlow(updated_trips);

                for(DefaultWeightedEdge edge2:graph.edgeSet()){
                    Old_Flow.replace(edge2,New_Flow.get(edge2));
                }
         }
    }

    public Map<DefaultWeightedEdge, Double> reRunAON(double total_trips,Routing routing){
            ODMatrix od2 = new ODMatrix(graph,routing);
            Map<String,Double> New_Cost = new HashMap<String, Double>();

            //initialise the new_cost
            for(Map.Entry<String,List<DefaultWeightedEdge>> pathlist_0:routing.getPathList_1().entrySet()){
                New_Cost.put(pathlist_0.getKey(),0.0);
            }

            for(DefaultWeightedEdge edge:graph.edgeSet()) {
                for(Map.Entry<String,List<DefaultWeightedEdge>> pathlist:routing.getPathList_1().entrySet()){
                    if(pathlist.getValue().contains(edge)){
                        New_Cost.replace(pathlist.getKey(),New_Cost.get(pathlist.getKey())+E_Cost.get(edge));
                    }

                }
            }

            od2.setCosts(New_Cost);
            od2.generateCost(total_trips);
            final_Trips = od2.getTrips();
            AONAssignment aona2 = new AONAssignment(graph,routing);
            aona2.runAssignment(od2.getTrips(),routing.getPathList_1());
            return aona2.getLink_flow();
    }

    public void update_AuxFlow(Map<DefaultWeightedEdge,Double> updated_trips){
           for(DefaultWeightedEdge edge_k:graph.edgeSet()){
               Aux_Flow.replace(edge_k,updated_trips.get(edge_k));
           }
    }

    public Map<DefaultWeightedEdge, Double> getNew_Flow() {
        return New_Flow;
    }

    public Map<String, Double> getFinal_Trips() {
        return final_Trips;
    }

    public void setDynpricing(boolean dynpricing) {
        this.dynpricing = dynpricing;
    }

    public void setNew_Flow(Map<DefaultWeightedEdge, Double> new_Flow) {
        New_Flow = new_Flow;
    }

    public void setAux_Flow(Map<DefaultWeightedEdge, Double> aux_Flow) {
        for(DefaultWeightedEdge edge:graph.edgeSet()) {
            Aux_Flow.replace(edge,aux_Flow.get(edge));
            System.out.println(Aux_Flow.get(edge));
        }
    }

    public void resetOld_Flow(){
        for(DefaultWeightedEdge edge:graph.edgeSet()){
            Old_Flow.replace(edge,0.0);
        }
    }

    public void resetCost(){
        for(DefaultWeightedEdge edge:graph.edgeSet()){
            E_Cost.replace(edge,graph.getEdgeWeight(edge)/500);
        }
    }
}
