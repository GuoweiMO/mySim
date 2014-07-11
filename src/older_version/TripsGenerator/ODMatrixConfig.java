package older_version.TripsGenerator;

import java.util.*;

/**
 * Created by kwai on 6/11/14.
 */
public class ODMatrixConfig {

    Map<Integer,Object> cent_index;
    List<Object> cent_Set;
    int tag = 0;
    double[][] Cost_Matrix;
    int i = 0;
    int j = 0;
    public ODMatrixConfig(){
        cent_index = new HashMap<Integer, Object>();
        cent_Set = new LinkedList<Object>();

    }

    /***
     * deprecated function
     ***/
    public double[][] simplifyCostMatrix(List<Map<String,Object>> query_result){

        for(Map<String,Object> map:query_result){
            if(!cent_Set.contains(map.get("start_vertex"))) {
                cent_Set.add(map.get("start_vertex"));
                cent_index.put(tag, map.get("start_vertex"));
                tag++;
            }

            if(!cent_Set.contains(map.get("end_vertex"))) {
                cent_Set.add(map.get("end_vertex"));
                cent_index.put(tag, map.get("end_vertex"));
                tag++;
            }
        }

        Cost_Matrix = new double[cent_Set.size()][cent_Set.size()];

        //initialization
        for(int a=0;a<Cost_Matrix.length;a++){
            for(int b=0;b<Cost_Matrix.length;b++){
                Cost_Matrix[a][b] = 0.0d;
            }
        }

        //assignment
        for(Map<String,Object> map2:query_result){
            double distance = (Double) map2.get("primary_path_length");
            Object s_vertex = map2.get("start_vertex");
            Object e_vertex = map2.get("end_vertex");

            for(Integer key:cent_index.keySet()){
                if(cent_index.get(key).equals(s_vertex)){
                    i = key;
                }
                if (cent_index.get(key).equals(e_vertex)){
                    j = key;
                }
            }

            Cost_Matrix[i][j] = distance/240.0; //unit: minutes
            //refer to average speed in central london was 8.98 mph and google maps real time calculation

            //System.out.println("("+i+","+j+") "+ Math.exp(-0.1*Cost_Matrix[i][j]));
        }

        return Cost_Matrix;
    }

    //deprecated function
    public double[][] constructODMatrix(double[][] cost_matrix,double total_trips){
        double [][] ODMatrix = new double[cost_matrix.length][cost_matrix.length]; // 114x114
        double[] sum_col = new double[cost_matrix.length];
        double total = 0.0d;
        for(int s = 0; s < cost_matrix.length; s++) {
            for (int t = 0; t < cost_matrix.length; t++) {
                if(cost_matrix[s][t] != 0.0) {
                    total += Math.exp(-0.1 * cost_matrix[s][t]);
                }
            }
            //System.out.println(sum_col[i]);
             //+=sum_col[s];
        }
        //System.out.println(total);
        // Furness iteration algorithm initialization
        // T<i,j> = exp(-0.1*t<i,j>) ai bj
        for(int m = 0; m<cost_matrix.length;m++){
            for(int n = 0;n<cost_matrix.length;n++){
                if(cost_matrix[m][n] != 0.0) {
                    ODMatrix[m][n] = total_trips * Math.exp(-0.1 * cost_matrix[m][n]) / total;
                    System.out.println("("+m +","+n+") "+"trips: "+Math.round(ODMatrix[m][n]));
                }
                else ODMatrix[m][n] = 0.0;
            }
        }
        //total trips :78443 average trips:688

        return  ODMatrix;
   }

    //avgSpeed (meter/minute)
    public HashMap<String,Long> buildODMatrix(List<Map<String,Object>> query_result,double total_trips,double avgSpeed){
        double total_cost = 0.0d;
        HashMap<String,Long> ODtrips = new HashMap<String, Long>();
        for(Map<String,Object> map:query_result){
            total_cost += Math.exp((Double) map.get("primary_path_length") * (-0.1)/avgSpeed);
        }

        //Random rand = new Random();
        //Math.abs(rand.nextGaussian()+1);
        for(Map<String,Object> map2:query_result){
            ODtrips.put(map2.get("start_vertex")+","+map2.get("end_vertex")
                        ,Math.round(total_trips*Math.exp((-0.1)*(Double)map2.get("primary_path_length")/avgSpeed)/total_cost));
        }

//        for(Map.Entry entry:ODtrips.entrySet()){
//            System.out.println(entry.toString());
//        }

        return ODtrips;
    }

    public List<Object> getCent_Set() {
        return cent_Set;
    }

    public Map<Integer, Object> getCent_index() {
        return cent_index;
    }
}
