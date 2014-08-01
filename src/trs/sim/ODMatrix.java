package trs.sim;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kwai on 28/07/14.
 */
public class ODMatrix {

    WeightedGraph<String,DefaultWeightedEdge> graph;
    Routing routing;
    Map<String,Double> grav;
    Map<String,Double> trips;
    Map<String,Double> costs;

    public ODMatrix(WeightedGraph<String,DefaultWeightedEdge> graph,Routing routing){
        this.graph = graph;
        this.routing = routing;
        grav = new HashMap<String, Double>();
        trips = new HashMap<String, Double>();
        costs = new HashMap<String, Double>();
        for(Map.Entry<String,Double> pathinfo:routing.getPathinfo_1().entrySet()){
            costs.put(pathinfo.getKey(),pathinfo.getValue()/1000); // 60 km/h
        }
    }

    public void generateCost(double total_trips){
        double total=0.0;
        for(Map.Entry<String,Double> pathcost:costs.entrySet()){
            grav.put(pathcost.getKey(),Math.exp(pathcost.getValue()*(-0.1)));  //return null if the key is firstly put
            double value = grav.get(pathcost.getKey());
            total += value;
        }

        for(Map.Entry<String,Double> gravinfo:grav.entrySet()){
                trips.put(gravinfo.getKey(),total_trips * gravinfo.getValue()/total);
        }

    }

    public Map<String, Double> getTrips() {
        return trips;
    }

    public void setCosts(Map<String, Double> costs) {
        this.costs = costs;
    }

    public Map<String, Double> getCosts() {
        return costs;
    }
}
