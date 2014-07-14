//package gml.tripgen;
//
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
///**
//* Created by kwai on 6/11/14.
//*/
//public class ODMatrixConfig {
//
//    Map<Integer,Object> cent_index;
//    List<Object> cent_Set;
//    int tag = 0;
//    double[][] Cost_Matrix;
//    int i = 0;
//    int j = 0;
//    public ODMatrixConfig(){
//        cent_index = new HashMap<Integer, Object>();
//        cent_Set = new LinkedList<Object>();
//
//    }
//
//    //avgSpeed (meter/minute)
//    public HashMap<String,Long> buildODMatrix(List<Map<String,Object>> query_result,double total_trips,double avgSpeed){
//        double total_cost = 0.0d;
//        HashMap<String,Long> ODtrips = new HashMap<String, Long>();
//        for(Map<String,Object> map:query_result){
//            total_cost += Math.exp((Double) map.get("primary_path_length") * (-0.1)/avgSpeed);
//        }
//
//        //Random rand = new Random();
//        //Math.abs(rand.nextGaussian()+1);
//        for(Map<String,Object> map2:query_result){
//            ODtrips.put(map2.get("start_vertex")+","+map2.get("end_vertex")
//                        ,Math.round(total_trips*Math.exp((-0.1)*(Double)map2.get("primary_path_length")/avgSpeed)/total_cost));
//        }
//
////        for(Map.Entry entry:ODtrips.entrySet()){
////            System.out.println(entry.toString());
////        }
//
//        return ODtrips;
//    }
//
//    public List<Object> getCent_Set() {
//        return cent_Set;
//    }
//
//    public Map<Integer, Object> getCent_index() {
//        return cent_index;
//    }
//}
