package trs.util;

/**
 * Created by kwai on 12/08/14.
 */

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Result2JSON {

    public static void writeAsJson(String fileDir,List<Double> flows,List<Double> cost){

        JSONObject data = new JSONObject();
        JSONArray colArray = new JSONArray();
        JSONObject col_obj_1 = new JSONObject();
        col_obj_1.put("label", "Iteration");
        col_obj_1.put("type", "number");
        JSONObject col_obj_2 = new JSONObject();
        col_obj_2.put("label", "Traffic Flow");
        col_obj_2.put("type", "number");
        JSONObject col_obj_3 = new JSONObject();
        col_obj_3.put("label", "Traffic Cost");
        col_obj_3.put("type", "number");

        colArray.add(col_obj_1);
        colArray.add(col_obj_2);
        colArray.add(col_obj_3);

        data.put("cols", colArray);

        JSONArray rowArray = new JSONArray();

        for(int i=0;i<flows.size();i++){
            JSONObject cells = new JSONObject();
            JSONArray cellList = new JSONArray();
            JSONObject cell_1 = new JSONObject();
            cell_1.put("v",i+1);
            JSONObject cell_2 = new JSONObject();
            cell_2.put("v",flows.get(i));
            JSONObject cell_3 = new JSONObject();
            cell_3.put("v",cost.get(i));
            cellList.add(cell_1);
            cellList.add(cell_2);
            cellList.add(cell_3);
            cells.put("c",cellList);
            rowArray.add(cells);
        }

        data.put("rows",rowArray);

        try {
            FileWriter writer = new FileWriter(fileDir);
            writer.write(data.toJSONString());
            writer.flush();
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void writeAsJson(String fileDir,List<Double> flows1,List<Double> flows2,List<Double> flows3){

        JSONObject data = new JSONObject();
        JSONArray colArray = new JSONArray();
        JSONObject col_obj_1 = new JSONObject();
        col_obj_1.put("label", "Iteration");
        col_obj_1.put("type", "number");
        JSONObject col_obj_2 = new JSONObject();
        col_obj_2.put("label", "SDE");
        col_obj_2.put("type", "number");
        JSONObject col_obj_3 = new JSONObject();
        col_obj_3.put("label", "Fixed Toll");
        col_obj_3.put("type", "number");
        JSONObject col_obj_4 = new JSONObject();
        col_obj_4.put("label", "Variable Toll");
        col_obj_4.put("type", "number");

        colArray.add(col_obj_1);
        colArray.add(col_obj_2);
        colArray.add(col_obj_3);
        colArray.add(col_obj_4);

        data.put("cols", colArray);

        JSONArray rowArray = new JSONArray();

        for(int i=0;i<flows1.size();i++){
            JSONObject cells = new JSONObject();

            JSONArray cellList = new JSONArray();
            JSONObject cell_1 = new JSONObject();
            cell_1.put("v",i+1);
            JSONObject cell_2 = new JSONObject();
            cell_2.put("v",flows1.get(i));
            JSONObject cell_3 = new JSONObject();
            cell_3.put("v",flows2.get(i));
            JSONObject cell_4 = new JSONObject();
            cell_4.put("v",flows3.get(i));
            cellList.add(cell_1);
            cellList.add(cell_2);
            cellList.add(cell_3);
            cellList.add(cell_4);
            cells.put("c",cellList);
            rowArray.add(cells);
        }

        data.put("rows",rowArray);

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

    }

}
