package gml.tests;

import gml.tripasg.BasicAssignment;
import gml.tripgen.CostMatrix;

import java.util.List;
import java.util.Map;

/**
 * Created by kwai on 14/07/14.
 */
public class testAssigment {

    public static void main(String[] args){
        List<Map<String,Object>> db_pathinfo = CostMatrix.costMatrixfromDB("milan_paths");
        Map<String,Double> db_odmatrix = CostMatrix.buildODMatrix(db_pathinfo,100000,700);
        BasicAssignment ba = new BasicAssignment(db_pathinfo,db_odmatrix);
        ba.runAssignment();
        //ba.saveTrips2DB("milan_BAtrips_2");
    }
}
