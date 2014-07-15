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
    Set<GraphPath> prim_PathSet;
    Set<GraphPath> second_PathSet;

    public CostMatrix(CentroidsDesignator cd){
        this.cd = cd;
        prim_PathSet = new HashSet<GraphPath>();
        second_PathSet = new HashSet<GraphPath>();
    }

    public void computeCostMatrix(Set CentroidSet){
        double cur_time = System.currentTimeMillis();

        /*** note undirected edges are printed as: {<v1>,<v2>} ***/

        tree = cd.getTree();

        for(Object vtx:CentroidSet) {
            tree.runSingleSourceKSP(vtx,CentroidSet); // get all possible path from one centroid to another
            List<GraphPath> pathList1 = tree.getPathList_1();
                for (GraphPath path1 : pathList1) {
                    if (CentroidSet.contains(path1.getStartVertex()) && CentroidSet.contains(path1.getEndVertex())) {
                        prim_PathSet.add(path1);
                        //System.out.println(path.getEdgeList());
                    }
                }
            List<GraphPath> pathList2 = tree.getPathList_2();
                for(GraphPath path2 : pathList2){
                    if (CentroidSet.contains(path2.getStartVertex()) && CentroidSet.contains(path2.getEndVertex())) {
                        second_PathSet.add(path2);
                        //System.out.println(path.getEdgeList());
                    }
                }
            }

            //prim_PathSet.add(pathList);
        System.out.println("Finish computation. Cost:" + (System.currentTimeMillis() - cur_time)/60000 +" min");
    }

    public void pathInfo2DB(Set<GraphPath> path1Set,Set<GraphPath> path2Set,String tableName){
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
        for(GraphPath p_path:path1Set) {
            String sql = "insert into " + tableName+"(start_vertex,end_vertex,primary_path_length,primary_path_edges) values(?,?,?,?)";
            List<Object> paras = new ArrayList<Object>();
            paras.add(p_path.getStartVertex());
            paras.add(p_path.getEndVertex());
            paras.add(p_path.getWeight());
            paras.add(p_path.getEdgeList().toString());
            try {
                boolean flag = jdbcUtils.updateByPrepStmt(sql, paras);
                System.out.println("Insertion: "+flag);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for(GraphPath s_path:path2Set) {
            String sql = "UPDATE " + tableName+" SET secondary_path_length = ?, secondary_path_edges = ? " +
                    "WHERE start_vertex = ? AND end_vertex = ? ";
            List<Object> paras = new ArrayList<Object>();
            paras.add(s_path.getWeight());
            paras.add(s_path.getEdgeList().toString());
            paras.add(s_path.getStartVertex());
            paras.add(s_path.getEndVertex());
            try {
                boolean flag = jdbcUtils.updateByPrepStmt(sql, paras);
                System.out.println("Update: "+flag);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static List<Map<String,Object>> costMatrixfromDB(String tableName){
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();

        String sql = "select start_vertex,end_vertex,primary_path_length,primary_path_edges," +
                "secondary_path_length,secondary_path_edges from "+tableName;

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
    public static HashMap<String,Double> buildODMatrix(List<Map<String,Object>> query_result,double total_trips,double avgSpeed){
        double total_cost = 0.0d;
        HashMap<String,Double> ODtrips = new HashMap<String, Double>();
        for(Map<String,Object> map:query_result){
            total_cost += Math.exp((Double) map.get("primary_path_length") * (-0.1)/avgSpeed);
        }

        //Random rand = new Random();
        //Math.abs(rand.nextGaussian()+1);
        for(Map<String,Object> map2:query_result){
            ODtrips.put(map2.get("start_vertex")+","+map2.get("end_vertex")
                    ,total_trips*Math.exp((-0.1)*(Double)map2.get("primary_path_length")/avgSpeed)/total_cost);
        }

//        for(Map.Entry entry:ODtrips.entrySet()){
//            System.out.println(entry.toString());
//        }

        return ODtrips;
    }

    public CentroidsDesignator getCentroidDesignator() {
        return cd;
    }

    public Set<GraphPath> getPrim_PathSet() {
        return prim_PathSet;
    }

    public Set<GraphPath> getSecond_PathSet() {
        return second_PathSet;
    }
}
