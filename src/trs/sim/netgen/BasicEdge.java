package trs.sim.netgen;

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
    private String source;
    private String target;

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

    public BasicEdge(String source,String target,String lid){
        this.source = source;
        this.target = target;
        this.lid = lid;
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

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }
}
