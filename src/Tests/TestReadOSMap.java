package Tests;

import RoutesConstructor.BasicLink;
import TrafficNetGenerator.OSMapReader;

import java.util.*;

/**
 * Created by kwai on 25/06/14.
 */
public class TestReadOSMap {
    public static void main(String[] args){
        OSMapReader reader = new OSMapReader();
        reader.read("Data/map.osm");
        //reader.read("Data/milan.xml");

        LinkedList<BasicLink> basicLinks = reader.getMyBasicLinkSet();

//        for(int i =0 ; i< links.size();i++) {
//            int xlen = links.get(i).getLons().size();
//            int ylen = links.get(i).getLats().size();
//            String lid = links.get(i).getLid();
//            System.out.println(lid+" "+ xlen +" " + ylen);
//
//        }

       System.out.println(basicLinks.size()); // 386 links
       //System.out.println(reader.getLinkedNodesID().size()); //419 (without repetition) , in total 772

        //reader.exportAsGraph("OutPut/graph" +(new Random()).nextInt(100) +".txt");
    }
}
