package sim.tripgen;

import sim.db.JdbcUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by kwai on 27/07/14.
 */
public class RealTrips {

    public static void main(String[] args){

        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();

//        String sql = "SELECT SUM(flows) FROM traffic_flow_06 WHERE time LIKE '07:3%' OR time LIKE '07:4%' OR time LIKE '07:5%' "+
//                     "OR time LIKE '08%' OR time LIKE '09%' OR time LIKE '09:1%' OR time LIKE '09:2%' OR time LIKE '09:30%' ";
        String sql = "SELECT SUM(flows) FROM traffic_flow_06 WHERE time LIKE '16:3%' OR time LIKE '16:4%' OR time LIKE '16:5%' " +
                "OR time LIKE '17%' OR time LIKE '18:0%' OR time LIKE '18:1%' OR time LIKE '18:2%' OR time LIKE '18:30%'";
//        String sql = "SELECT SUM(flows) FROM traffic_flow_06 WHERE time LIKE '09:35%' OR time LIKE '09:4%' OR time LIKE '09:5%' "+
//                "OR time LIKE '10%' OR time LIKE '11%' OR time LIKE '12%' OR time LIKE '13%' OR time LIKE '14%' OR time LIKE '15%' " +
//                "OR time LIKE '16:0%' OR time LIKE '16:1%' OR time LIKE '16:2%'";
//        String sql = "SELECT SUM(flows) FROM traffic_flow_06 WHERE time LIKE '18:35%' OR time LIKE '18:4%' OR time LIKE '18:5%' "+
//                "OR time LIKE '19%' OR time LIKE '20%' OR time LIKE '21%' OR time LIKE '22%' ";
        List<Object> paras = new ArrayList<Object>();

        Map<String,Object> QueryResult;

        try {
            QueryResult = jdbcUtils.findSingleResult(sql, paras);
            System.out.println("Morning Peak Total Flows:"+ QueryResult);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //model calibration
//        String sql_ = "SELECT SUM(trips) FROM milan_BAtrips_3";
//        // String sql2 = "UPDATE milan_BAtrips_3 SET trips=trips*157174/873797 ";
//        List<Object> paras = new ArrayList<Object>();
//
//        try {
//            Map<String,Object> result = jdbcUtils.findSingleResult(sql_, paras);
//            //boolean flag = jdbcUtils.updateByPrepStmt(sql2,paras);
//            System.out.println(result);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


    }
}

// morning peak:  449071; assume 70% on main roads. same for others. (total flow 157174 per hour)
// afternoon peak: 378512; 2 hours (total flow 132479 per hour)
// midday: 997965  7 hours (total flow  99796 per hour)
// evening: 484155  4 hours (total flow 84727 per hour)