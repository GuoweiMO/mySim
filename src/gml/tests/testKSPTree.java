package gml.tests;

import gml.netgen.GMLFileReader;
import gml.routing.KSPTree;
import org.jgrapht.WeightedGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwai on 11/07/14.
 */
public class testKSPTree {

    public static void main(String[] args){
        GMLFileReader reader = new GMLFileReader();
        reader.read("Data/east_milan.gml");

        KSPTree tree = new KSPTree(reader);
        WeightedGraph w_graph = tree.constructGraph();
        List<String> verList = new ArrayList<String>(w_graph.vertexSet());
        tree.runSingleSourceKSP(verList.get(30));
    }
}
