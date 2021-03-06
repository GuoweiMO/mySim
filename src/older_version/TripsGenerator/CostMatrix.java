package older_version.TripsGenerator;

import older_version.DB_Interaction.JdbcUtils;
import older_version.RoutesConstructor.KSPTree;
import org.jgrapht.GraphPath;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by kwai on 6/11/14.
 */
public class CostMatrix {
    KSPTree tree;
    HashSet<Object> CentroidSet;
    CentroidsDesignator cd;
    HashSet<GraphPath> CentPathSet;
    List<Map<String,Object>> QueryResult;

    public CostMatrix(CentroidsDesignator cd){
        this.cd = cd;
        CentPathSet = new HashSet<GraphPath>();
    }

    public HashSet<GraphPath> computeCostMatrix(){

        // note undirected edges are printed as: {<v1>,<v2>}
        CentroidSet = new HashSet<Object>(cd.getCentroidSet());

        tree = cd.getTree();
        //WeightedGraph graph = cd.getGraph();
        //List<Object> centroidList = new ArrayList(CentroidSet);

        //System.out.println(centroidList.size());
        for(Object vtx:CentroidSet) {
            System.out.println("\n computing paths of source:" + vtx+ "  ...");
            tree.runSingleSourceKSP(vtx);// get all possible path from one centroid to another, 139 X 139
            List<GraphPath> pathList = tree.getPathList_1();
                for (GraphPath path : pathList) {
                    if (CentroidSet.contains(path.getStartVertex()) && CentroidSet.contains(path.getEndVertex())) {

                        //pathList.remove(path);
                        CentPathSet.add(path);
                        System.out.println(path.getEdgeList());
                    }
                }
            }

            //CentPathSet.add(pathList);
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


    public List<Map<String,Object>> costMatrixfromDB(String tableName){
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();

        String sql = "select start_vertex,end_vertex,primary_path_length,primary_path_edges from "+tableName;

        List<Object> paras = new ArrayList<Object>();
//        paras.add("start_vertex");
//        paras.add("end_vertex");
//        paras.add("primary_path_length");

            try {
                QueryResult = jdbcUtils.findMultiResult(sql, paras);
                //System.out.println();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return QueryResult;
    }

    public CentroidsDesignator getCentroidDesignator() {
        return cd;
    }

    public HashSet<Object> getCentroidSet() {
        return CentroidSet;
    }
}
