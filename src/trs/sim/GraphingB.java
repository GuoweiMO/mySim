package trs.sim;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import sim_old.db.JdbcUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by kwai on 28/07/14.
 */
public class GraphingB {

    WeightedGraph<String,DefaultWeightedEdge> w_graph;
    Map<DefaultWeightedEdge,Double> edge_capacity;
    public GraphingB(){
        w_graph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        edge_capacity = new HashMap<DefaultWeightedEdge, Double>();
    }

    public WeightedGraph<String,DefaultWeightedEdge> buildGraph(){
        for(int i =1;i<=22;i++) {
            w_graph.addVertex(Integer.toString(i));
        }
        DefaultWeightedEdge edge1 = w_graph.addEdge("1", "2");   w_graph.setEdgeWeight(edge1,2000);
        DefaultWeightedEdge edge2 = w_graph.addEdge("1", "4");   w_graph.setEdgeWeight(edge2,2600);
        DefaultWeightedEdge edge3 = w_graph.addEdge("1", "12");  w_graph.setEdgeWeight(edge3,3400);
        DefaultWeightedEdge edge4 = w_graph.addEdge("2", "3");   w_graph.setEdgeWeight(edge4,1000);
        DefaultWeightedEdge edge5 = w_graph.addEdge("2", "6");   w_graph.setEdgeWeight(edge5,4000);
        DefaultWeightedEdge edge6 = w_graph.addEdge("3", "4");   w_graph.setEdgeWeight(edge6,1500);
        DefaultWeightedEdge edge7 = w_graph.addEdge("3", "7");   w_graph.setEdgeWeight(edge7,4600);
        DefaultWeightedEdge edge8 = w_graph.addEdge("3", "8");   w_graph.setEdgeWeight(edge8,4800);
        DefaultWeightedEdge edge9 = w_graph.addEdge("4", "10");  w_graph.setEdgeWeight(edge9,5200);
        DefaultWeightedEdge edge10 = w_graph.addEdge("5", "6");  w_graph.setEdgeWeight(edge10,4500);
        DefaultWeightedEdge edge11 = w_graph.addEdge("5", "12"); w_graph.setEdgeWeight(edge11,5500);
        DefaultWeightedEdge edge12 = w_graph.addEdge("5", "13"); w_graph.setEdgeWeight(edge12,3800);
        DefaultWeightedEdge edge13 = w_graph.addEdge("6", "7");  w_graph.setEdgeWeight(edge13,5000);
        DefaultWeightedEdge edge14 = w_graph.addEdge("6", "14"); w_graph.setEdgeWeight(edge14,3400);
        DefaultWeightedEdge edge15 = w_graph.addEdge("7", "8");  w_graph.setEdgeWeight(edge15,5700);
        DefaultWeightedEdge edge16 = w_graph.addEdge("7", "15"); w_graph.setEdgeWeight(edge16,6000);
        DefaultWeightedEdge edge17 = w_graph.addEdge("8", "9");  w_graph.setEdgeWeight(edge17,3600);
        DefaultWeightedEdge edge18 = w_graph.addEdge("8", "16"); w_graph.setEdgeWeight(edge18,2400);
        DefaultWeightedEdge edge19 = w_graph.addEdge("9", "10"); w_graph.setEdgeWeight(edge19,6200);
        DefaultWeightedEdge edge20 = w_graph.addEdge("9", "18"); w_graph.setEdgeWeight(edge20,4700);
        DefaultWeightedEdge edge21 = w_graph.addEdge("10", "11");w_graph.setEdgeWeight(edge21,6300);
        DefaultWeightedEdge edge22 = w_graph.addEdge("10", "19");w_graph.setEdgeWeight(edge22,2800);
        DefaultWeightedEdge edge23 = w_graph.addEdge("11", "12");w_graph.setEdgeWeight(edge23,5000);
        DefaultWeightedEdge edge24 = w_graph.addEdge("11", "20");w_graph.setEdgeWeight(edge24,3400);
        DefaultWeightedEdge edge25 = w_graph.addEdge("11", "21");w_graph.setEdgeWeight(edge25,4700);
        DefaultWeightedEdge edge26 = w_graph.addEdge("12", "22");w_graph.setEdgeWeight(edge26,5000);
        DefaultWeightedEdge edge27 = w_graph.addEdge("13", "14");w_graph.setEdgeWeight(edge27,5400);
        DefaultWeightedEdge edge28 = w_graph.addEdge("13", "22");w_graph.setEdgeWeight(edge28,7000);
        DefaultWeightedEdge edge29 = w_graph.addEdge("14", "15");w_graph.setEdgeWeight(edge29,5900);
        DefaultWeightedEdge edge30 = w_graph.addEdge("15", "16");w_graph.setEdgeWeight(edge30,6200);
        DefaultWeightedEdge edge31 = w_graph.addEdge("16", "17");w_graph.setEdgeWeight(edge31,3700);
        DefaultWeightedEdge edge32 = w_graph.addEdge("17", "18");w_graph.setEdgeWeight(edge32,4600);
        DefaultWeightedEdge edge33 = w_graph.addEdge("18", "19");w_graph.setEdgeWeight(edge33,6000);
        DefaultWeightedEdge edge34 = w_graph.addEdge("19", "20");w_graph.setEdgeWeight(edge34,5800);
        DefaultWeightedEdge edge35 = w_graph.addEdge("20", "21");w_graph.setEdgeWeight(edge35,6400);
        DefaultWeightedEdge edge36 = w_graph.addEdge("21", "22");w_graph.setEdgeWeight(edge36,5600);

        edge_capacity.put(edge1,1000.0);
        edge_capacity.put(edge2,1000.0);
        edge_capacity.put(edge3,1200.0);
        edge_capacity.put(edge4,1000.0);
        edge_capacity.put(edge5,1200.0);
        edge_capacity.put(edge6,1000.0);
        edge_capacity.put(edge7,1200.0);
        edge_capacity.put(edge8,1200.0);
        edge_capacity.put(edge9,1200.0);
        edge_capacity.put(edge10,1500.0);
        edge_capacity.put(edge11,1500.0);
        edge_capacity.put(edge12,1700.0);
        edge_capacity.put(edge13,1500.0);
        edge_capacity.put(edge14,1700.0);
        edge_capacity.put(edge15,1500.0);
        edge_capacity.put(edge16,1700.0);
        edge_capacity.put(edge17,1500.0);
        edge_capacity.put(edge18,1700.0);
        edge_capacity.put(edge19,1500.0);
        edge_capacity.put(edge20,1700.0);
        edge_capacity.put(edge21,1500.0);
        edge_capacity.put(edge22,1700.0);
        edge_capacity.put(edge23,1500.0);
        edge_capacity.put(edge24,1700.0);
        edge_capacity.put(edge25,1700.0);
        edge_capacity.put(edge26,1700.0);
        edge_capacity.put(edge27,2000.0);
        edge_capacity.put(edge28,2000.0);
        edge_capacity.put(edge29,2000.0);
        edge_capacity.put(edge30,2000.0);
        edge_capacity.put(edge31,2000.0);
        edge_capacity.put(edge32,2000.0);
        edge_capacity.put(edge33,2000.0);
        edge_capacity.put(edge34,2000.0);
        edge_capacity.put(edge35,2000.0);
        edge_capacity.put(edge36,2000.0);

        return w_graph;
    }


    public void saveGraph2DB(String tableName){
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
        for(DefaultWeightedEdge edge:w_graph.edgeSet()) {
            String sql = "insert into "+tableName+"(source,target,weight) values(?,?,?)";
            List<Object> paras = new ArrayList<Object>();
            paras.add(w_graph.getEdgeSource(edge));
            paras.add(w_graph.getEdgeTarget(edge));
            paras.add(w_graph.getEdgeWeight(edge));
            try {
                boolean flag = jdbcUtils.updateByPrepStmt(sql, paras);
                System.out.println(flag);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<DefaultWeightedEdge, Double> getEdge_capacity() {
        return edge_capacity;
    }

    public WeightedGraph<String, DefaultWeightedEdge> getW_graph() {
        return w_graph;
    }
}
