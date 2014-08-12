package trs.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

/**
 * Created by kwai on 12/08/14.
 */
public class JSONReader {

    public static void read(String jsonFile,List<Double> flows){
        JSONParser parser = new JSONParser();
        try{
            Object obj = parser.parse(new FileReader(jsonFile)); //"OutPut/4display/sde.json"

            JSONObject jobj = (JSONObject) obj;

            JSONArray rowArr = (JSONArray) jobj.get("rows");
            for(int i =0; i<rowArr.size();i++){
                JSONObject row = (JSONObject) rowArr.get(i);
                JSONArray cellList = (JSONArray) row.get("c");
                JSONObject cell_flow = (JSONObject) cellList.get(2);
                Double flow = (Double) cell_flow.get("v");
                flows.add(flow);
            }

        }catch (IOException e){
            e.printStackTrace();
        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        List<Double> sde_flows = new ArrayList<Double>();
        List<Double> ftp_flows = new ArrayList<Double>();
        List<Double> vtp_flows = new ArrayList<Double>();
        read("OutPut/4display/sde.json",sde_flows);
        read("OutPut/4display/fixed_toll.json",ftp_flows);
        read("OutPut/4display/variable_toll.json",vtp_flows);

        Result2JSON.writeAsJson("OutPut/4display/multi_costs.json",sde_flows,ftp_flows,vtp_flows);

    }
}
