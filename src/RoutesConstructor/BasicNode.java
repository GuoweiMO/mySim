package RoutesConstructor;


/**
 * Created by kwai on 24/06/14.
 */
public class BasicNode {
    String x;
    String y;
    String id;
    String attr;
    public BasicNode(String x, String y, String id){
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public String getX(){
        return this.x;
    }

    public String getY(){
        return this.y;
    }
    public String getID(){
        return this.id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String a){
        this.attr = a;
    }
}
