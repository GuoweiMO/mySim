package gml.tests;

import gml.netgen.GMLFileReader;
import gml.routing.KSPTree;
import gml.tripgen.CentroidsDesignator;
import gml.tripgen.CostMatrix;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kwai on 11/07/14.
 */
public class testCostMatrix {

    public static void main(String[] args){
        GMLFileReader reader = new GMLFileReader();
        reader.read("Data/cen_milan.gml");

        KSPTree tree = new KSPTree(reader);
        WeightedGraph w_graph = tree.constructGraph();

        CentroidsDesignator cd = new CentroidsDesignator(reader,tree,w_graph,8,8); //62 points
        //cd.designateCentroids();

        //cd.saveCentroids2DB("milan_centroids");

        List<Map<String,Object>> centroids = CentroidsDesignator.queryCentroidsFromDB("milan_centroids");
        Set centSet = new HashSet();
        for(Map cent:centroids){
            centSet.add(cent.get("centroid"));
            System.out.println(cent.get("centroid"));
        }
        CostMatrix cm = new CostMatrix(cd);
        cm.computeCostMatrix(centSet);
        Set<GraphPath> p_pathSet = cm.getPrim_PathSet();
        Set<GraphPath> s_pathSet = cm.getSecond_PathSet();
//
//        StringBuffer sb = new StringBuffer();
//        for(GraphPath path:pathSet){
//            sb.append(path.getStartVertex()+" "+path.getEndVertex()+" "+path.getWeight() +" " +path.getEdgeList()+"\n");
//        }
//        Writer writer;
//        try{
//            writer = new BufferedWriter(new FileWriter(new File("OutPut/path_info.txt")));
//            writer.write(sb.toString());
//        }catch(FileNotFoundException e) {
//            e.printStackTrace();
//        }catch (IOException e){
//            e.printStackTrace();
//        }

        cm.pathInfo2DB(p_pathSet,s_pathSet,"milan_paths");

    }
}
