package gml.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kwai on 6/14/14.
 */

public class JdbcUtils {
    private final String USERNAME = "root";
    private final String PASSWORD = "061837";
    private final String DRIVER = "com.mysql.jdbc.Driver";
    private final String URL = "jdbc:mysql://localhost:3306/mysql";

    private Connection connection;

    //SQL execution statement
    private PreparedStatement pstmt;
    private ResultSet resultSet;

    public JdbcUtils(){
        try {
            Class.forName(DRIVER);
            System.out.println("Driver Loaded!");
        }catch (Exception e){

        }
    }

    public Connection getConnection(){
        try{
            connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("Connected to MySQL!!");
        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }

    public boolean updateByPrepStmt(String sql,List<Object> paras) throws SQLException{
        boolean flag = false;
        int result = -1; // the number of lines influenced
        pstmt = connection.prepareStatement(sql);
        int index = 1;
        if(paras != null && !paras.isEmpty()){
            for (int i= 0;i<paras.size();i++ ){
                pstmt.setObject(index++,paras.get(i));
            }
        }
        result = pstmt.executeUpdate();
        flag = result > 0 ? true :false;
        return flag;
    }

    //return single record
    public Map<String,Object> findSingleResult(String sql,List<Object> paras) throws SQLException{
        Map<String,Object> map = new HashMap<String, Object>();

        pstmt = connection.prepareStatement(sql);

        int index = 1;
        if(paras!=null && paras.isEmpty()){
            for(int i=0;i<paras.size();i++){
                pstmt.setObject(index++,paras.get(i));
            }
        }
        resultSet = pstmt.executeQuery(); // return the query result
        ResultSetMetaData metaData = resultSet.getMetaData();
        int col_len = metaData.getColumnCount(); // get the number of column
        while (resultSet.next()){
            for(int i=0;i<col_len;i++){
                String cols_name = metaData.getColumnName(i+1);
                Object cols_val = resultSet.getObject(cols_name);
                if(cols_val==null ) {
                    cols_val = "";
                }
                map.put(cols_name,cols_val);
            }

        }
        return map;
    }

    //return multiple records
    public List<Map<String,Object>> findMultiResult(String sql,List<Object> paras) throws SQLException{
        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
        pstmt = connection.prepareStatement(sql);
        int index = 1;
        if(paras!=null && paras.isEmpty()){
            for(int i=0;i<paras.size();i++){
                pstmt.setObject(index++,paras.get(i));
            }
        }
        resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int col_len = metaData.getColumnCount();
        while (resultSet.next()){
            Map<String,Object> map = new HashMap<String, Object>();
            for(int i=0; i<col_len;i++){
                String cols_name = metaData.getColumnName(i+1); //start from 1
                Object cols_val = resultSet.getObject(cols_name);
                if(cols_val == null){
                    cols_val = "";
                }
                map.put(cols_name,cols_val);
            }
            list.add(map);
        }

        return list;

    }

    public static void main(String[] args){
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
//        String sql = "insert into Account(Name,Password) values(?,?)";
//        List<Object> paras = new ArrayList<Object>();
//        paras.add("Jamie");
//        paras.add("admin");
//        try{
//            boolean flag = jdbcUtils.updateByPrepStmt(sql,paras);
//            System.out.println(flag);
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
    }


}
