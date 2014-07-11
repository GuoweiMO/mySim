package gml.tripgen;

import gml.db.JdbcUtils;
import gml.netgen.BasicEdge;
import gml.routing.KSPTree;
import gml.netgen.GMLFileReader;
import org.jgrapht.WeightedGraph;

import java.sql.SQLException;
import java.util.*;

/**
* Created by kwai on 27/06/14.
*/
public class CentroidsDesignator {

    KSPTree tree;
    Set<Object> CentroidSet;
    List<BasicEdge> linksSet;
    Set<Object> RemovedCentroids;
    WeightedGraph w_graph;
    GMLFileReader reader;
    double[][] offsetX;
    double[][] offsetY;
    public CentroidsDesignator(GMLFileReader reader,KSPTree kspTree,WeightedGraph w_graph,int rows,int cols){
        this.reader = reader;
        this.tree = kspTree;
        this.w_graph = w_graph;
        CentroidSet = new HashSet<Object>();
        linksSet = new LinkedList();
        RemovedCentroids = new HashSet<Object>();
        offsetX = new double[rows][cols];
        offsetY = new double[rows][cols];
    }

    public CentroidsDesignator(){
        //only for call database-related functions
    }

    public void designateCentroids(){
        linksSet = reader.getBasicEdgeSet();

        System.out.println("\n Start designating centroids, please wait...");
//        for(Object vertex:w_graph.vertexSet()){
//            if(w_graph.edgesOf(vertex).size() == 1) {  //imply this vertex is the endpoint,suitable as centroid
//                  CentroidSet.add(vertex);
//                  //System.out.println(vertex);
//            }
//        }

//        for(Object vertex1:CentroidSet){
//            for(Object vertex2:CentroidSet){
//                if(!vertex1.equals(vertex2)
//                && Math.abs(tree.getVertexXCoord((String) vertex1) - tree.getVertexXCoord((String) vertex2)) < 50
//                && Math.abs(tree.getVertexYCoord((String) vertex1) - tree.getVertexYCoord((String) vertex2)) < 50){
//                    RemovedCentroids.add(vertex1);
//                }
//
////                double x = tree.getVertexXCoord((String) vertex1);
////                double y = tree.getVertexYCoord((String) vertex1);
////                if(x<30 || x >1300 || y<20 || y>740){
////                  if(Math.abs(x - tree.getVertexXCoord((String) vertex2)) > 30
////                  && Math.abs(y - tree.getVertexYCoord((String) vertex2))> 30) {
////                      RemovedCentroids.remove(vertex1);
////                  }
////                }
//            }
//        }

        //using grid to filter vertex as centroids
        int rows = offsetX.length;
        int cols = offsetX[0].length;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                offsetX[i][j] = 1366.0/cols;
                offsetY[i][j] = 768.0/rows;
                for(Object vtx:w_graph.vertexSet()) {
                    double x = tree.getVertexXCoord((String) vtx);
                    double y = tree.getVertexYCoord((String) vtx);
                    if (1366.0*j/cols < x && x <1366.0*(j+1)/cols && 768.0*i/rows < y && y< 768.0*(i+1)/rows){
                        //calculate the closest point to the grid center
                        if(Math.abs(1366.0*(j+0.5)/cols - x)<offsetX[i][j] && Math.abs(768.0*(i+0.5)/rows - y) < offsetY[i][j]) {
                            offsetX[i][j] = Math.abs(1366.0 * (j + 0.5) / cols - x); //offset to grid center point
                            offsetY[i][j] = Math.abs(768.0 * (i + 0.5) / rows - y);
                        }
                    }

                }

                // restore the best centroid
                for(Object vtx2:w_graph.vertexSet()){
                    double x2 = tree.getVertexXCoord((String) vtx2);
                    double y2 = tree.getVertexYCoord((String) vtx2);
                    if(Math.abs(1366.0 * (j + 0.5) / cols - x2) == offsetX[i][j] &&
                       Math.abs(768.0 * (i + 0.5) / rows - y2) == offsetY[i][j]){
                       CentroidSet.add(vtx2);
                    }

                }

                for(Object vtx3:w_graph.vertexSet()) {
                    double x3 = tree.getVertexXCoord((String) vtx3);
                    double y3 = tree.getVertexYCoord((String) vtx3);

                    if (w_graph.edgesOf(vtx3).size() == 1) {
                        if ((x3 > 1366.0 * 9.5 / 10 && 768.0 * i / rows < y3 && y3 < 768.0 * (i + 1) / rows) ||
                            (y3 < 768.0 * 0.5 / 10 && 1366.0 * j / cols < x3 && x3 < 1366.0 * (j + 1) / cols)) {
                            //imply this vertex is the endpoint,suitable as centroid
                            CentroidSet.add(vtx3);
                            break;
                            //System.out.println(vertex);
                        }
                    }
                }

            }
        }

        for(Object cent:CentroidSet){
           System.out.println(cent + " X:" + tree.getVertexXCoord((String) cent)+ " Y:" + tree.getVertexYCoord((String) cent));
        }

        System.out.println("centroids are successfully designated. "+ "Centroids number : "+ CentroidSet.size() +"\n");
        //CentroidSet.removeAll(RemovedCentroids);
    }


    public void saveCentroids2DB(String tableName){
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
        for(Object centroids:CentroidSet) {
            String sql = "insert into " + tableName+"(centroid) values(?)";
            List<Object> paras = new ArrayList<Object>();
            paras.add(centroids);

            try {
                boolean flag = jdbcUtils.updateByPrepStmt(sql, paras);
                System.out.println(flag);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Map<String,Object>> queryCentroidsFromDB(String tableName){
        List<Map<String,Object>> result = new LinkedList<Map<String, Object>>();
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();

        String sql = "select centroid from "+tableName;

        List<Object> paras = new ArrayList<Object>();
//        paras.add("start_vertex");
//        paras.add("end_vertex");
//        paras.add("primary_path_length");

        try {
            result = jdbcUtils.findMultiResult(sql, paras);
            //System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;

    }

    public Set<Object> getCentroidSet() {
        return CentroidSet;
    }

    public KSPTree getTree() {
        return tree;
    }

    public void setTree(KSPTree tree) {
        this.tree = tree;
    }

    public WeightedGraph getGraph() {
        return w_graph;
    }

    public void setGraph(WeightedGraph w_graph) {
        this.w_graph = w_graph;
    }
}
