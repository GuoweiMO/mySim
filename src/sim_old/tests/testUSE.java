package sim_old.tests;

import sim_old.netgen.GMLFileReader;
import sim_old.routing.KSPTree;
import sim_old.tripasg.SDEAlgo;


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
