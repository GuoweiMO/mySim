package older_version.Tests;

import older_version.RoutesConstructor.KSPTree;
import older_version.TrafficNetGenerator.OSMapReader;
import older_version.TripsGenerator.CentroidsDesignator;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Created by kwai on 27/06/14.
 */

public class TestCentroids {

    public TestCentroids() {}

    public static void main(String[] args){
        OSMapReader reader = new OSMapReader();
        reader.read("Data/east_milan.osm");

        KSPTree tree = new KSPTree(reader);
        WeightedGraph<String,DefaultWeightedEdge> w_graph = tree.constructGraph();

        CentroidsDesignator cd = new CentroidsDesignator(reader,tree,w_graph,10,10);
        cd.designateCentroids();
        cd.saveCentroids2DB("Centroids");
        System.out.println(cd.getCentroidSet().size());
    }
}
