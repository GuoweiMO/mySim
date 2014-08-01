package trs.sim;

import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kwai on 28/07/14.
 */
public class Routing {
    KShortestPaths ksp;
    WeightedGraph<String,DefaultWeightedEdge> graph;
    Map<String,Double> pathinfo_1;
    Map<String,List<DefaultWeightedEdge>> pathList_1;
    Map<String,Double> pathinfo_2;
    Map<String,List<DefaultWeightedEdge>> pathList_2;

    public Routing(WeightedGraph<String,DefaultWeightedEdge> graph){
        this.graph = graph;
        pathinfo_1 = new HashMap<String, Double>();
        pathList_1 = new HashMap<String, List<DefaultWeightedEdge>>();
        pathinfo_2 = new HashMap<String, Double>();
        pathList_2 = new HashMap<String, List<DefaultWeightedEdge>>();

    }

    public void runKSP(String source){
        ksp = new KShortestPaths(graph,source,2);

        for(String vertex:graph.vertexSet()) {
            if (!vertex.equals(source)) {
                //System.out.println("current source:"+source);
                List<GraphPath> path = ksp.getPaths(vertex);
                pathinfo_1.put(path.get(0).getStartVertex() + "," + path.get(0).getEndVertex(), path.get(0).getWeight());
                pathList_1.put(path.get(0).getStartVertex() + "," + path.get(0).getEndVertex(), path.get(0).getEdgeList());

                pathinfo_2.put(path.get(1).getStartVertex() + "," + path.get(1).getEndVertex(), path.get(1).getWeight());
                pathList_2.put(path.get(1).getStartVertex() + "," + path.get(1).getEndVertex(), path.get(1).getEdgeList());

                //System.out.println(pathList_1.get(path.get(0).getStartVertex() + "," + path.get(0).getEndVertex()));
            }
        }

    }

    public Map<String,List<DefaultWeightedEdge>> getPathList_1() {
        return pathList_1;
    }

    public Map<String, Double> getPathinfo_1() {
        return pathinfo_1;
    }

    public Map<String, List<DefaultWeightedEdge>> getPathList_2() {
        return pathList_2;
    }

    public Map<String, Double> getPathinfo_2() {
        return pathinfo_2;
    }
}
