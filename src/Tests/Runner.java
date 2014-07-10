package Tests;

import RoutesConstructor.KSPTree;
import TrafficNetGenerator.OSMapReader;
import TripsAssignment.BasicAssignment;
import TripsGenerator.CentroidsDesignator;
import TripsGenerator.CostMatrix;
import TripsGenerator.ODMatrixConfig;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kwai on 03/07/14.
 */
public class Runner {

    public static void main(String[] args){
        OSMapReader reader = new OSMapReader();
        reader.read("Data/east_milan.osm"); //45.4749 up  45.4463 down 9.2017 left  9.2444 right

        KSPTree tree = new KSPTree(reader);
        WeightedGraph<String,DefaultWeightedEdge> w_graph = tree.constructGraph();

        CentroidsDesignator cd = new CentroidsDesignator(reader,tree,w_graph,10,10);
        cd.designateCentroids();

        CostMatrix cm = new CostMatrix(cd);
        Set<GraphPath> pathSet = cm.computeCostMatrix();
        cm.pathInfo2DB(pathSet,"ShortestPathInfo_milan");

//        List<Map<String,Object>> queryResult = cm.costMatrixfromDB("ShortestPathInfo_milan");
//
//        ODMatrixConfig odmc = new ODMatrixConfig();
//        HashMap<String,Long> odMatrix= odmc.buildODMatrix(queryResult, 50000, 240);
//
//        BasicAssignment ba = new BasicAssignment(queryResult,odMatrix);
//        ba.runAssignment();
//
//        ba.save2DB("BasicAssignment");

    }
}
