package trs;

import org.jgrapht.graph.DefaultWeightedEdge;
import trs.analy.Pricing;

import java.util.Map;

/**
 * Created by kwai on 29/07/14.
 */
public class PricingRun extends BasicRun {
    public PricingRun(){
        super();
    }

    public void congestedRun(){
        run(false);

        Pricing stp = new Pricing(super.graphing_0.getW_graph());
        Map<DefaultWeightedEdge,Double> flows_1 = stp.congestedPricing(super.pathlist_0,super.trips_0,super.flows_0,800,false);

        double post_total =0.0d;
        for(Map.Entry<DefaultWeightedEdge,Double> flow:flows_1.entrySet()) {
            System.out.println(flow);
            post_total += flow.getValue();
        }
        System.out.println(post_total);

    }

    public void dynamicRun(){
        run(true);
        Pricing dyp = new Pricing(super.graphing_0.getW_graph());
        //trial 1
        dyp.congestedPricing(super.pathlist_0,super.sde.getFinal_Trips(),super.sde.getNew_Flow(),600,true); //561
        //trial 2
//        dyp.congestedPricing(super.pathlist_0,super.sde.getFinal_Trips(),super.sde.getNew_Flow(),550,true); //547
//        //trial 3
//        dyp.congestedPricing(super.pathlist_0,super.sde.getFinal_Trips(),super.sde.getNew_Flow(),500,true); //532
//        //trial 4
//        dyp.congestedPricing(super.pathlist_0,super.sde.getFinal_Trips(),super.sde.getNew_Flow(),450,true);
    }

    public static void main(String[] args){
        //(new PricingRun()).congestedRun();
        (new PricingRun()).dynamicRun();
    }
}
