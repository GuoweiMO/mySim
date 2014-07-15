package gml.tests;

import gml.netgen.GMLFileReader;
import gml.routing.KSPTree;
import gml.tripasg.DynamicEquilibrium;
import org.jgrapht.WeightedGraph;

/**
 * Created by kwai on 15/07/14.
 */
public class testUSE {
    public static void main(String[] args){
        GMLFileReader reader = new GMLFileReader();
        reader.read("Data/cen_milan.gml");

        KSPTree tree = new KSPTree(reader);
        tree.constructGraph();

        DynamicEquilibrium de = new DynamicEquilibrium(tree,reader);
        de.runSDEAlgo();
    }
}
