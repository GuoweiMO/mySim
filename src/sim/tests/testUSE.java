package sim.tests;

import sim.netgen.GMLFileReader;
import sim.routing.KSPTree;
import sim.tripasg.SDEAlgo;


/**
 * Created by kwai on 15/07/14.
 */
public class testUSE {
    public static void main(String[] args){
        GMLFileReader reader = new GMLFileReader();
        reader.read("Data/cen_milan.gml");

        KSPTree tree = new KSPTree(reader);
        tree.constructGraph();
        SDEAlgo sdeAlgo = new SDEAlgo(tree);
        sdeAlgo.reConstructGraph();
        sdeAlgo.algoInit();
        sdeAlgo.runAlgo();

        SDEAlgo.saveSDE2DB("SDE_Flows",sdeAlgo.getNew_Flow());

    }
}
