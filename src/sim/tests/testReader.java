package sim.tests;

import sim.netgen.BasicNode;
import sim.netgen.GMLFileReader;

import java.util.Set;

/**
 * Created by kwai on 11/07/14.
 */
public class testReader {

    public static void main(String[] args){
        GMLFileReader reader = new GMLFileReader();
        reader.read("Data/cen_milan.gml");

        Set<BasicNode> nodeset = reader.getBasicNodeSet();
        for(BasicNode node:nodeset){
            System.out.println(node.getID()+" "+node.getX()+"," + node.getY());
        }
        System.out.println(reader.getBasicNodeSet().size());
    }
}
