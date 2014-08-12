package trs.util;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trs.sim.netgen.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Created by kwai on 02/08/14.
 */
public class Data2JSON {

    public static void writeAsJson(String fileDir){
        GMLReader reader = new GMLReader();
        reader.read("Data/cen_milan.gml");

        GraphingA graphingA = new GraphingA(reader);
        WeightedGraph<String, DefaultWeightedEdge> graph_0 = graphingA.constructGraph();

        CoordTransfer ct = new CoordTransfer(Double.parseDouble(reader.getMin_lat()),
                Double.parseDouble(reader.getMax_lat()),
                Double.parseDouble(reader.getMin_lon()),
                Double.parseDouble(reader.getMax_lon()));


        JSONObject data = new JSONObject();
        JSONArray nodeArray = new JSONArray();
        Set<BasicNode> graphNodes= graphingA.getGraphNodes();
        for(BasicNode node:graphNodes) {
            JSONObject nodeObj = new JSONObject();
            nodeObj.put("id", node.getID());
            nodeObj.put("label", node.getID());
            nodeObj.put("x",ct.lonToScreenX(Double.parseDouble(node.getX())));
            nodeObj.put("y",ct.latToScreenY(Double.parseDouble(node.getY())));
            nodeObj.put("size","1.0");
            nodeArray.add(nodeObj);
        }

        data.put("nodes", nodeArray);

        JSONArray edgeArray = new JSONArray();
        Set<BasicEdge> graphEdges = graphingA.getGraphEdges();
        System.out.println(graphEdges.size());
        for(BasicEdge edge:graphEdges){
            JSONObject edgeObj = new JSONObject();
            edgeObj.put("id",edge.getLid());
            edgeObj.put("source",edge.getSource());
            edgeObj.put("target",edge.getTarget());
            edgeArray.add(edgeObj);
        }

        data.put("edges",edgeArray);

        try {
            FileWriter writer = new FileWriter(fileDir);
            writer.write(data.toJSONString());
            writer.flush();
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        writeAsJson("OutPut/map_data.json");
    }
}
