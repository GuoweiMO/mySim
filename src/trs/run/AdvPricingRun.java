package trs.run;

import org.jgrapht.graph.DefaultWeightedEdge;
import trs.analy.PricingModels;
import trs.sim.netgen.BasicEdge;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by kwai on 08/08/14.
 */
public class AdvPricingRun extends AdvRun{

    public AdvPricingRun(){
        super();
    }

    public void greedRun(){

        runSim4Pricing();

        PricingModels sgp = new PricingModels(graph_0,routing);
        Map<DefaultWeightedEdge,Double> flows_1 = sgp.runGreedyMode(pathlist_1, trips, flows, 1500);

        double post_flow = 0.0d;
        double post_cost = 0.0d;
        for(Map.Entry<DefaultWeightedEdge,Double> flow:flows_1.entrySet()) {
            System.out.println(flow);
            post_flow += flow.getValue();
            post_cost += flow.getValue()*sgp.getGraph_1().getEdgeWeight(flow.getKey())/Free_Speed
                    *(1+0.15*Math.pow(flow.getValue() / graphingA.getCapacity().get(flow.getKey()), 4.0));
        }
        System.out.println("Total Flow: " +post_flow+"  "+"Total Cost: "+post_cost); //stp.getCgsEdges().size()*22

    }

    public void adjustingRun(){

        runSim4Pricing();
        System.out.println("-------------------------Static Adjusting Pricing----------------------------------");
        PricingModels sap = new PricingModels(graph_0,pathlist_1);
        Map<DefaultWeightedEdge,Double> flows_2 = sap.runAdjustingMode(pathlist_1,
                pathlist_2, trips, flows, graphingA.getCapacity());

        double post_flow = 0.0d;
        double post_cost = 0.0d;
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<DefaultWeightedEdge,Double> flow:flows_2.entrySet()) {
            System.out.println(flow);

            for(BasicEdge edge:graphingA.getGraphEdges()){
                if((graph_0.getEdgeSource(flow.getKey()).equals(edge.getSource()) &&
                    graph_0.getEdgeTarget(flow.getKey()).equals(edge.getTarget())) ||
                   (graph_0.getEdgeSource(flow.getKey()).equals(edge.getTarget()) &&
                    graph_0.getEdgeTarget(flow.getKey()).equals(edge.getSource())) ){
                    sb.append(edge.getLid()+" "+flow.getValue()+ " \n");
                }
            }

            post_flow += flow.getValue();
            post_cost += flow.getValue()*sap.getGraph_1().getEdgeWeight(flow.getKey())/Free_Speed
                    *(1+0.15*Math.pow(flow.getValue()/graphingA.getCapacity().get(flow.getKey()),4.0));
        }
        System.out.println("Total Flow: " +post_flow+"  "+"Total Cost: "+post_cost);

//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("OutPut/4display/static_pricing")));
//            writer.write(sb.toString());
//            writer.flush();
//            writer.close();
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }

    }

    public void variableRun(){
        runSim4Pricing();

        PricingModels vp = new PricingModels(graph_0,pathlist_1);
        vp.runVariableToll(pathinfo_1,flows,graphingA.getCapacity(),graphingA.getGraphEdges(),aona);
    }

    public void fixedRun(){
        runSim4Pricing();

        PricingModels vp = new PricingModels(graph_0,pathlist_1);
        vp.runFixedToll(pathinfo_1,flows,graphingA.getCapacity(),graphingA.getGraphEdges(),aona);
    }

    public static void main(String[] args){
        //(new AdvPricingRun()).greedRun();
        //(new AdvPricingRun()).adjustingRun();
        //(new AdvPricingRun()).fixedRun();
        (new AdvPricingRun()).variableRun();
    }
}
