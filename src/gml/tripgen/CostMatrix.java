package gml.tripgen;

import gml.db.JdbcUtils;
import gml.routing.KSPTree;
import org.jgrapht.GraphPath;

import java.sql.SQLException;
import java.util.*;

/**
* Created by kwai on 6/11/14.
*/
public class CostMatrix {
    KSPTree tree;
    //HashSet<Object> CentroidSet;
    CentroidsDesignator cd;
    HashSet<GraphPath> CentPathSet;

    public CostMatrix(CentroidsDesignator cd){
        this.cd = cd;
        CentPathSet = new HashSet<GraphPath>();
    }

    public HashSet<GraphPath> computeCostMatrix(Set CentroidSet){
        double cur_time = System.currentTimeMillis();

        /*** note undirected edges are printed as: {<v1>,<v2>} ***/
        //CentroidSet = new HashSet<Object>(cd.getCentroidSet());

        tree = cd.getTree();

        for(Object vtx:CentroidSet) {
            //System.out.println("\nComputing paths of source: " + vtx+ "  ...");
            tree.runSingleSourceKSP(vtx,CentroidSet); // get all possible path from one centroid to another
            List<GraphPath> pathList = tree.getPathList_1();
                for (GraphPath path : pathList) {
                    if (CentroidSet.contains(path.getStartVertex()) && CentroidSet.contains(path.getEndVertex())) {
                        CentPathSet.add(path);
                        //System.out.println(path.getEdgeList());
                    }
                }
            }

            //CentPathSet.add(pathList);
        System.out.println("Finish computation. Cost:" + (System.currentTimeMillis() - cur_time)/60000 +" min");
        return CentPathSet;
    }

    public void pathInfo2DB(Set<GraphPath> pathSet,String tableName){
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
        for(GraphPath p_path:pathSet) {
            String sql = "insert into " + tableName+"(start_vertex,end_vertex,primary_path_length,primary_path_edges) values(?,?,?,?)";
            List<Object> paras = new ArrayList<Object>();
            paras.add(p_path.getStartVertex());
            paras.add(p_path.getEndVertex());
            paras.add(p_path.getWeight());
            paras.add(p_path.getEdgeList().toString());
            try {
                boolean flag = jdbcUtils.updateByPrepStmt(sql, paras);
                System.out.println(flag);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static List<Map<String,Object>> costMatrixfromDB(String tableName){
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();

        String sql = "select start_vertex,end_vertex,primary_path_length,primary_path_edges from "+tableName;

        List<Object> paras = new ArrayList<Object>();
//        paras.add("start_vertex");
//        paras.add("end_vertex");
//        paras.add("primary_path_length");
        List<Map<String,Object>> QueryResult = new LinkedList<Map<String, Object>>();

            try {
                QueryResult = jdbcUtils.findMultiResult(sql, paras);
                //System.out.println();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return QueryResult;
    }


    //avgSpeed (meter/minute)
    public static HashMap<String,Long> buildODMatrix(List<Map<String,Object>> query_result,double total_trips,double avgSpeed){
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

    public CentroidsDesignator getCentroidDesignator() {
        return cd;
    }

}
