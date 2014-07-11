package older_version.TripsAssignment;

import older_version.DB_Interaction.JdbcUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by kwai on 6/11/14.
 */
public class BasicAssignment {

    List<Map<String,Object>> QueryResult;
    HashMap<String,Long> ODMatrix;
    HashMap<String,Long> edgeTrips;
    public BasicAssignment(List<Map<String,Object>> QueryResult,HashMap<String,Long> ODMatrix){
        this.QueryResult = QueryResult;
        this.ODMatrix = ODMatrix;
        edgeTrips = new HashMap<String, Long>();
    }

    public BasicAssignment(){

    }

    public void runAssignment(){
        List<String> edgeArr;
        String temStr;
        for(Map<String,Object> g_path:QueryResult) {
            temStr = g_path.get("primary_path_edges").toString().trim();
            temStr = temStr.substring(1, temStr.length() - 1);
            //System.out.println(temStr);
            edgeArr = Arrays.asList(temStr.split(","));

            for(String edge:edgeArr) {
                edge = edge.replace("(","");
                edge = edge.replace(")","");

                String vtx1 = edge.trim().split(":")[0].trim();
                String vtx2 = edge.trim().split(":")[1].trim();
                edge = vtx1+","+vtx2;

                if(edgeTrips.keySet().contains(edge)){
                    edgeTrips.replace(edge,
                            edgeTrips.get(edge)+ ODMatrix.get(g_path.get("start_vertex") + "," + g_path.get("end_vertex")));
                }
                else {
                    edgeTrips.put(edge, ODMatrix.get(g_path.get("start_vertex") + "," + g_path.get("end_vertex")));
                }
            }
        }

        for(Map.Entry entry:edgeTrips.entrySet()){
            System.out.println(entry);
        }

    }


    public void save2DB(String tableName){
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
        for(Map.Entry entry:edgeTrips.entrySet()) {
            String sql = "insert into "+tableName+"(start_vertex,end_vertex,trips) values(?,?,?)";
            List<Object> paras = new ArrayList<Object>();
            paras.add(entry.getKey().toString().split(",")[0]);
            paras.add(entry.getKey().toString().split(",")[1]);
            paras.add(entry.getValue());
            try {
                boolean flag = jdbcUtils.updateByPrepStmt(sql, paras);
                System.out.println(flag);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Map<String,Object>> queryfromDB(String tableName){
        List<Map<String,Object>> ResultList = new ArrayList<Map<String, Object>>();

        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();

        String sql = "select start_vertex,end_vertex,trips from "+ tableName;

        List<Object> paras = new ArrayList<Object>();

        try {
            ResultList = jdbcUtils.findMultiResult(sql, paras);
            //System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ResultList;
    }
}
