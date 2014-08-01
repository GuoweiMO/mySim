package trs;

import org.jgrapht.graph.DefaultWeightedEdge;
import trs.analy.PricingModel;

import java.util.Map;

/**
 * Created by kwai on 29/07/14.
 */
public class PricingRun extends BasicRun {
    public PricingRun(){
        super();
    }

    public void congestedRun(){
        run(false,false);

        PricingModel stp = new PricingModel(super.graphing_0.getW_graph(),super.routing_0);
        Map<DefaultWeightedEdge,Double> flows_1 = stp.staticChange(super.pathlist_0, super.trips_0, super.flows_0, 2000, false);

        double post_flow = 0.0d;
        double post_cost = 0.0d;
        for(Map.Entry<DefaultWeightedEdge,Double> flow:flows_1.entrySet()) {
            System.out.println(flow);
            post_flow += flow.getValue();
            post_cost += flow.getValue()*stp.getGraph_1().getEdgeWeight(flow.getKey())/500
                         *(1+0.15*Math.pow(flow.getValue() / super.graphing_0.getEdge_capacity().get(flow.getKey()), 4.0));
        }
        System.out.println("Total Flow: " +post_flow+"  "+"Total Cost: "+post_cost); //stp.getCgsEdges().size()*22

    }

    public void run(){
        run(false,false);
        PricingModel slp = new PricingModel(super.graphing_0.getW_graph(),super.routing_0);
        Map<DefaultWeightedEdge,Double> flows_2 = slp.selectiveChange(super.routing_0.getPathList_1(),
                                super.routing_0.getPathList_2(),super.trips_0,super.flows_0,2000);

        for(Map.Entry<DefaultWeightedEdge,Double> flow:flows_2.entrySet()) {
            System.out.println(flow);
        }

        double post_flow = 0.0d;
        double post_cost = 0.0d;
        for(Map.Entry<DefaultWeightedEdge,Double> flow:flows_2.entrySet()) {
            System.out.println(flow);
            post_flow += flow.getValue();
            post_cost += flow.getValue()*slp.getGraph_1().getEdgeWeight(flow.getKey())/500
                    *(1+0.15*Math.pow(flow.getValue()/super.graphing_0.getEdge_capacity().get(flow.getKey()),4.0));
        }
        System.out.println("Total Flow: " +post_flow+"  "+"Total Cost: "+post_cost);


    }

    public void dynamicRun(){
        run(true,false);
        PricingModel dyp = new PricingModel(super.graphing_0.getW_graph(),super.routing_0);
        //trial 1
        Map<DefaultWeightedEdge,Double> flows_2
                =dyp.staticChange(super.pathlist_0, super.sde.getFinal_Trips(), super.sde.getNew_Flow(), 1000, true); //561

        double post_flow = 0.0d;
        double post_cost = 0.0d;
        for(Map.Entry<DefaultWeightedEdge,Double> flow:flows_2.entrySet()) {
            System.out.println(flow);
            post_flow += flow.getValue();
            post_cost += flow.getValue()*dyp.getGraph_1().getEdgeWeight(flow.getKey())/500
                        *(1+0.15*Math.pow(flow.getValue()/super.graphing_0.getEdge_capacity().get(flow.getKey()),4.0));
        }
        System.out.println("Total Flow: " +post_flow+"  "+"Total Cost: "+post_cost);

        //trial 2
//        dyp.staticChange(super.pathlist_0,super.sde.getFinal_Trips(),super.sde.getNew_Flow(),550,true); //547
//        //trial 3
//        dyp.staticChange(super.pathlist_0,super.sde.getFinal_Trips(),super.sde.getNew_Flow(),500,true); //532
//        //trial 4
//        dyp.staticChange(super.pathlist_0,super.sde.getFinal_Trips(),super.sde.getNew_Flow(),450,true);
    }

    public static void main(String[] args){
        //(new PricingRun()).congestedRun();
        //(new PricingRun()).dynamicRun();
        (new PricingRun()).run();
    }
}
