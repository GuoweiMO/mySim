package gml.netgen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwai on 25/06/14.
 * A class defining the edges getting from gml files.
 */
public class BasicEdge {
    private List<String> coords;
    private List<String> endNodeIDs;
    private String lid;
    private String type;

    public BasicEdge(List coords, List endNodeIDs, String lid){
        this.coords = coords;
        this.endNodeIDs = endNodeIDs;
        this.lid = lid;
    }

    public BasicEdge(List coords, List endNodeIDs,String lid, String type){
        this.coords = coords;
        this.endNodeIDs = endNodeIDs;
        this.lid = lid;
        this.type = type;
    }

    public String getLid(){
        return this.lid;
    }

    public void setLid(String lid){
        this.lid = lid;
    }

    public String getType(){ return this.type; }

    public void setType(String type){ this.type = type;}

    public List<String> getNodeIDs() {
        return endNodeIDs;
    }

    public void setEndNodeIDs(List<String> endNodeIDs) {
        this.endNodeIDs = endNodeIDs;
    }

    public List<String> getCoords() {
        return coords;
    }

    public void setCoords(List<String> coords) {
        this.coords = coords;
    }
}
