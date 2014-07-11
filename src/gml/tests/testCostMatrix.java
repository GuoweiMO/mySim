package gml.tests;

import gml.netgen.GMLFileReader;
import gml.routing.KSPTree;
import gml.tripgen.CentroidsDesignator;
import gml.tripgen.CostMatrix;
import gml.tripgen.ODMatrixConfig;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kwai on 11/07/14.
 */
public class testCostMatrix {

    public static void main(String[] args){
        GMLFileReader reader = new GMLFileReader();
        reader.read("Data/east_milan.gml");

        KSPTree tree = new KSPTree(reader);
        WeightedGraph w_graph = tree.constructGraph();

        CentroidsDesignator cd = new CentroidsDesignator(reader,tree,w_graph,10,10);
        cd.designateCentroids();

        cd.saveCentroids2DB("milan_centroids");
        List<Map<String,Object>> centroids = cd.queryCentroidsFromDB("milan_centroids");

        CostMatrix cm = new CostMatrix(cd);
        Set<GraphPath> pathSet = cm.computeCostMatrix();
        cm.pathInfo2DB(pathSet,"milan_paths");

    }
}
