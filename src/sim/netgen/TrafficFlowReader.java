package sim.netgen;

import sim.db.JdbcUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by kwai on 27/07/14.
 */
public class TrafficFlowReader {

    public static void main(String[] args){
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();

        try {
            File file = new File("Data/traffic_flow_06");
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            while((line = br.readLine()) !=null){
                String[] record = line.split("\\s+");
                String sql = "insert into traffic_flow_06 (uid,time,flows,speed) values(?,?,?,?)";
                List<Object> paras = new ArrayList<Object>();
                paras.add(record[0]);
                paras.add(record[2]);
                paras.add(record[3]);
                paras.add(record[4]);
                try {
                    boolean flag = jdbcUtils.updateByPrepStmt(sql, paras);
                    System.out.println(flag);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

//        try {
//            File file = new File("Data/detectors.csv");
//            BufferedReader br2 = new BufferedReader(new FileReader(file));
//
//            String line2;
//            while((line2 = br2.readLine()) !=null){
//                String[] record = line2.split(",");
//                String sql = "insert into detectors (pid,lat,lng) values(?,?,?)";
//                List<Object> paras = new ArrayList<Object>();
//                paras.add(record[0]);
//                paras.add(record[4]);
//                paras.add(record[5]);
//
//                try {
//                    boolean flag = jdbcUtils.updateByPrepStmt(sql, paras);
//                    //System.out.println(flag);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
////                System.out.println("success!");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }

    }
}
