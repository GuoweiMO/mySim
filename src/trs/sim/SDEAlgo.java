package trs.sim;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import trs.sim.netgen.BasicEdge;
import trs.sim.netgen.Edge_ID;

import java.util.*;

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
    AoNAssignment aona;
    Map<String,List<DefaultWeightedEdge>> pathList_1;
    Map<String,Double> pathInfo_1;
    Map<DefaultWeightedEdge,Double> edge_Capacity;
    Set<DefaultWeightedEdge> CgsEdges;
    Set<BasicEdge> edgeSet;
    StringBuilder sb;
    List<Double> ite_flows = new ArrayList<Double>();
    List<Double> ite_cost = new ArrayList<Double>();

    public static enum PricingType{
        FixedRoads, VariableRoads, None
    }

    public SDEAlgo(WeightedGraph<String,DefaultWeightedEdge> graph,
                   Map<DefaultWeightedEdge,Double> edge_Capacity,
                   Map<String,List<DefaultWeightedEdge>> pathList_1,
                   Map<String,Double> pathInfo_1,AoNAssignment aona,
                   Set<BasicEdge> edgeSet,
                   Set<DefaultWeightedEdge> CgsEdges){

        this.graph = graph;
        this.pathList_1 = pathList_1;
        this.pathInfo_1 = pathInfo_1;
        this.aona = aona;
        this.edge_Capacity = edge_Capacity;
        this.CgsEdges = CgsEdges;
        this.edgeSet = edgeSet;

        Aux_Flow = new HashMap<DefaultWeightedEdge, Double>();
        E_Cost = new HashMap<DefaultWeightedEdge, Double>();
        Old_Flow = new HashMap<DefaultWeightedEdge, Double>();
        New_Flow = new HashMap<DefaultWeightedEdge, Double>();
        Ini_Cost = new HashMap<DefaultWeightedEdge, Double>();
        sb = new StringBuilder();
    }


    public SDEAlgo(WeightedGraph<String,DefaultWeightedEdge> graph,
                   Map<DefaultWeightedEdge,Double> edge_Capacity,
                   Map<String,List<DefaultWeightedEdge>> pathList_1,
                   Set<BasicEdge> edgeSet,
                   Map<String,Double> pathInfo_1,AoNAssignment aona){

        this.graph = graph;
        this.pathList_1 = pathList_1;
        this.pathInfo_1 = pathInfo_1;
        this.aona = aona;
        this.edge_Capacity = edge_Capacity;
        this.edgeSet = edgeSet;

        Aux_Flow = new HashMap<DefaultWeightedEdge, Double>();
        E_Cost = new HashMap<DefaultWeightedEdge, Double>();
        Old_Flow = new HashMap<DefaultWeightedEdge, Double>();
        New_Flow = new HashMap<DefaultWeightedEdge, Double>();
        Ini_Cost = new HashMap<DefaultWeightedEdge, Double>();
        sb = new StringBuilder();
    }


    // initialization of the algorithm
    public void algoInit(){
        for (DefaultWeightedEdge edge:graph.edgeSet()){

            Aux_Flow.put(edge,aona.getLink_flow().get(edge));

            E_Cost.put(edge,graph.getEdgeWeight(edge)/500.0); //initial cost
            Ini_Cost.put(edge,graph.getEdgeWeight(edge)/500.0);

            Old_Flow.put(edge,0.0);  //initial flow
            New_Flow.put(edge,0.0);
        }
    }


    public void runAlgo(PricingType type, float rate){
            float theta;
            for(int i = 1;;i++){ //start the iteration
                System.out.println("iteration "+ i);
                sb.append("iteration " + i + "\n");
                theta = (float) 1.0/i;
                double total_flows=0.0d;
                double total_cost = 0.0d;

                int k =0; //k to count the number of charging roads
                double extra_tolls = 0.0d;
                Set<DefaultWeightedEdge> CgsSet = new HashSet<DefaultWeightedEdge>();
                for(DefaultWeightedEdge edge1:graph.edgeSet()){

                    New_Flow.replace(edge1,(1.0-theta)*Old_Flow.get(edge1)+theta*Aux_Flow.get(edge1));

                    //System.out.print("current capacity: " + edge_Capacity.get(edge1) + "  ");

                    System.out.println( edge1+" ["+Old_Flow.get(edge1)+" --> "+ New_Flow.get(edge1)+"]");

                    Edge_ID ei = new Edge_ID(graph,edgeSet);
                    sb.append(ei.getEdgeID(edge1)+" "+ New_Flow.get(edge1)+"\n");


                    if(type == PricingType.VariableRoads) {
                        if (New_Flow.get(edge1) > edge_Capacity.get(edge1) * rate) { //0.8 is better than both 0.7 and 0.9
                            //variable pricing (20p/km)
                           // System.out.println(edge1);
                            CgsSet.add(edge1);
                            k++;
                            E_Cost.replace(edge1, 0.0002*graph.getEdgeWeight(edge1) +
                                    Ini_Cost.get(edge1) * (1+0.15*Math.pow(New_Flow.get(edge1) / edge_Capacity.get(edge1),4.0)));
                            extra_tolls += New_Flow.get(edge1)*0.0002*graph.getEdgeWeight(edge1);

                        } else
                            E_Cost.replace(edge1, Ini_Cost.get(edge1)*(1+0.15*Math.pow(New_Flow.get(edge1)/edge_Capacity.get(edge1),4.0)));
                    }
                    else if (type == PricingType.FixedRoads){
                        if(CgsEdges.contains(edge1)){
                            E_Cost.replace(edge1, 0.0002*graph.getEdgeWeight(edge1) +
                                    Ini_Cost.get(edge1) * (1+0.15*Math.pow(New_Flow.get(edge1) / edge_Capacity.get(edge1),4.0)));
                        }else
                            E_Cost.replace(edge1, Ini_Cost.get(edge1)*(1+0.15*Math.pow(New_Flow.get(edge1)/edge_Capacity.get(edge1),4.0)));
                    }
                    else
                        E_Cost.replace(edge1, Ini_Cost.get(edge1)*(1+0.15*Math.pow(New_Flow.get(edge1)/edge_Capacity.get(edge1),4.0)));

                    total_flows +=New_Flow.get(edge1);
                    double actual_cost =  Ini_Cost.get(edge1) * (1+0.15*Math.pow(New_Flow.get(edge1) / edge_Capacity.get(edge1), 4.0));
                    total_cost += actual_cost*New_Flow.get(edge1);

                }

                System.out.println("Total flows:" +total_flows +"| Total Cost: " + total_cost);
                ite_flows.add(total_flows);
                ite_cost.add(total_cost);

                double RG;
                double cur_sum =0.0;
                double std_sum =0.0;
                for(DefaultWeightedEdge edge_n:graph.edgeSet()){

                    cur_sum += New_Flow.get(edge_n)*E_Cost.get(edge_n);
                    std_sum += Aux_Flow.get(edge_n)*E_Cost.get(edge_n);
                }

                RG = (cur_sum-std_sum)/cur_sum;
                System.out.println("RelGap: "+ RG);
                if( i>1 && Math.abs(RG) <0.0001){
                    System.out.println("Social Equilibrium Reaches. (iteration "+i +" )");
                    if(type == PricingType.VariableRoads) {
                        CgsEdges.clear();
                        for (DefaultWeightedEdge eg : CgsSet)
                            CgsEdges.add(eg);
                    }
                    //System.out.println(extra_tolls);
                    break;
                }

                int flag = 0;
                for(DefaultWeightedEdge edge_n:graph.edgeSet()){
                    if(Math.abs(New_Flow.get(edge_n) - Old_Flow.get(edge_n)) > 1){
                        flag++;
                    }
                }
                if(flag == 0){
                   // System.out.println("User Equilibrium Reaches. (iteration "+i +" )");
                    //break;
                }

                Map<DefaultWeightedEdge,Double> updated_trips = this.reRunAON(25000,pathList_1,pathInfo_1);
                this.update_AuxFlow(updated_trips);

                for(DefaultWeightedEdge edge2:graph.edgeSet()){
                    Old_Flow.replace(edge2,New_Flow.get(edge2));
                }
         }
    }

    public Map<DefaultWeightedEdge, Double> reRunAON(double total_trips,Map<String,List<DefaultWeightedEdge>> pathList_1,
            Map<String,Double> pathInfo_1){

            ODMatrix od2 = new ODMatrix(graph,pathInfo_1);
            Map<String,Double> New_Cost = new HashMap<String, Double>();

            //initialise the new_cost
            for(Map.Entry<String,List<DefaultWeightedEdge>> path_a:pathList_1.entrySet()){
                New_Cost.put(path_a.getKey(),0.0);
            }

            for(DefaultWeightedEdge edge:graph.edgeSet()) {
                for(Map.Entry<String,List<DefaultWeightedEdge>> path_b:pathList_1.entrySet()){
                    if(path_b.getValue().contains(edge)){
                        New_Cost.replace(path_b.getKey(),New_Cost.get(path_b.getKey())+E_Cost.get(edge));
                    }

                }
            }

            od2.setCosts(New_Cost);
            od2.generateCost(total_trips);
            final_Trips = od2.getTrips();
            AoNAssignment aona2 = new AoNAssignment(graph);
            aona2.runAssignment(od2.getTrips(), pathList_1);
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

    public StringBuilder getSb() {
        return sb;
    }

    public List<Double> getIte_cost() {
        return ite_cost;
    }

    public List<Double> getIte_flows() {
        return ite_flows;
    }

    public Set<DefaultWeightedEdge> getCgsEdges() {
        return CgsEdges;
    }
}
