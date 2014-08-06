package trs.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by kwai on 02/08/14.
 */
public class Data2JSON {

    public static void writeAsJson(String fileDir,int rowNum){

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

        for(int i=0;i<rowNum;i++){
            JSONObject cells = new JSONObject();
            JSONArray cellList = new JSONArray();
                JSONObject cell_1 = new JSONObject();
                cell_1.put("v",i);
                JSONObject cell_2 = new JSONObject();
                cell_2.put("v",5+i);
                JSONObject cell_3 = new JSONObject();
                cell_3.put("v",5*i);
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

    public static void main(String[] args){
        writeAsJson("OutPut/data.json",10);
    }
}
