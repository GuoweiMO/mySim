package trs.sim;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import sim.db.JdbcUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by kwai on 28/07/14.
 */
public class Graphing {

    WeightedGraph<String,DefaultWeightedEdge> w_graph;
    public Graphing(){
        w_graph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
    }

    public WeightedGraph<String,DefaultWeightedEdge> buildGraph(){
        for(int i =1;i<=22;i++) {
            w_graph.addVertex(Integer.toString(i));
        }
        w_graph.addEdge("1", "2");
        w_graph.addEdge("1", "4");
        w_graph.addEdge("1", "12");
        w_graph.addEdge("2", "3");
        w_graph.addEdge("2", "6");
        w_graph.addEdge("3", "4");
        w_graph.addEdge("3", "7");
        w_graph.addEdge("3", "8");
        w_graph.addEdge("4", "10");
        w_graph.addEdge("5", "6");
        w_graph.addEdge("5", "12");
        w_graph.addEdge("5", "13");
        w_graph.addEdge("6", "7");
        w_graph.addEdge("6", "14");
        w_graph.addEdge("7", "8");
        w_graph.addEdge("7", "15");
        w_graph.addEdge("8", "9");
        w_graph.addEdge("8", "16");
        w_graph.addEdge("9", "10");
        w_graph.addEdge("9", "18");
        w_graph.addEdge("10", "11");
        w_graph.addEdge("10", "19");
        w_graph.addEdge("11", "12");
        w_graph.addEdge("11", "20");
        w_graph.addEdge("11", "21");
        w_graph.addEdge("12", "22");
        w_graph.addEdge("13", "14");
        w_graph.addEdge("13", "22");
        w_graph.addEdge("14", "15");
        w_graph.addEdge("15", "16");
        w_graph.addEdge("16", "17");
        w_graph.addEdge("17", "18");
        w_graph.addEdge("18", "19");
        w_graph.addEdge("19", "20");
        w_graph.addEdge("20", "21");
        w_graph.addEdge("21", "22");

        this.generateWeight();

        return w_graph;
    }

    public void generateWeight(){
        int i= 0;
        for(DefaultWeightedEdge edge:w_graph.edgeSet()){
            w_graph.setEdgeWeight(edge,2000*Math.sin((i -Math.PI -1)*Math.PI/23)+5000); //generate distance in [3km,10km]
            i++;
        }
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



    public WeightedGraph<String, DefaultWeightedEdge> getW_graph() {
        return w_graph;
    }
}
