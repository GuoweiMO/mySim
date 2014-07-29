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
    Map<String,Double> pathinfo;
    Map<String,List<DefaultWeightedEdge>> pathList;

    public Routing(WeightedGraph<String,DefaultWeightedEdge> graph){
        this.graph = graph;
        pathinfo = new HashMap<String, Double>();
        pathList = new HashMap<String, List<DefaultWeightedEdge>>();
    }

    public void runKSP(String source){
        ksp = new KShortestPaths(graph,source,1);

        for(String vertex:graph.vertexSet()) {
            if (!vertex.equals(source)) {
                List<GraphPath> path = ksp.getPaths(vertex);
                pathinfo.put(path.get(0).getStartVertex() + "," + path.get(0).getEndVertex(), path.get(0).getWeight());
                pathList.put(path.get(0).getStartVertex() + "," + path.get(0).getEndVertex(),path.get(0).getEdgeList());
            }
        }
    }

    public Map<String,List<DefaultWeightedEdge>> getPathList() {
        return pathList;
    }

    public Map<String, Double> getPathinfo() {
        return pathinfo;
    }
}
