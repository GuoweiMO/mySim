package sim_old.tests;

import sim_old.netgen.GMLFileReader;
import sim_old.routing.KSPTree;
import org.jgrapht.WeightedGraph;

/**
 * Created by kwai on 11/07/14.
 */
public class testKSPTree {

    public static void main(String[] args){
        GMLFileReader reader = new GMLFileReader();
        reader.read("Data/cen_milan.gml");

        KSPTree tree = new KSPTree(reader);
        WeightedGraph w_graph = tree.constructGraph();
        for(Object edge:w_graph.edgeSet()){
            System.out.println(edge);
        }
//        List<String> verList = new ArrayList<String>(w_graph.vertexSet());
//        Set<String> testSet = new HashSet<String>();
//        testSet.add(verList.get(1));
//        testSet.add(verList.get(2));
//        tree.runSingleSourceKSP(verList.get(0),testSet);
    }
}
