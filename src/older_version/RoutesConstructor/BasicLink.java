package older_version.RoutesConstructor;

import java.util.ArrayList;

/**
 * Created by kwai on 25/06/14.
 */
public class BasicLink {
    private ArrayList lons;
    private ArrayList lats;
    private ArrayList nodeIDs;
    private String lid;
    private String type;

    public BasicLink(ArrayList lons, ArrayList lats, ArrayList nodeIDs, String lid){
        this.lons = lons;
        this.lats = lats;
        this.nodeIDs = nodeIDs;
        this.lid = lid;
    }

    public BasicLink(ArrayList lons, ArrayList lats, ArrayList nodeIDs, String lid, String type){
        this.lons = lons;
        this.lats = lats;
        this.lid = lid;
        this.nodeIDs = nodeIDs;
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

    public ArrayList getNodeIDs() {
        return nodeIDs;
    }

    public void setNodeIDs(ArrayList nodeIDs) {
        this.nodeIDs = nodeIDs;
    }

    public ArrayList getLons() {
        return lons;
    }

    public void setLons(ArrayList lons) {
        this.lons = lons;
    }

    public ArrayList getLats() {
        return lats;
    }

    public void setLats(ArrayList lats) {
        this.lats = lats;
    }
}
