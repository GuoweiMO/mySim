package sim_old.netgen;

/**
 * Created by kwai on 26/06/14.
 */
public class CoordinateTransfer {
    double minlat;
    double maxlat;
    double minlon;
    double maxlon;

    public CoordinateTransfer(double minlat,double maxlat,double minlon,double maxlon){
        this.minlat = minlat;
        this.maxlat = maxlat;
        this.minlon = minlon;
        this.maxlon = maxlon;
    }

    public double getMaxlon() {
        return maxlon;
    }

    public double getMinlon() {
        return minlon;
    }

    public double getMaxlat() {
        return maxlat;
    }

    public double getMinlat() {
        return minlat;
    }

    public void setMaxlat(double maxlat) {
        this.maxlat = maxlat;
    }

    public void setMaxlon(double maxlon) {
        this.maxlon = maxlon;
    }

    public void setMinlat(double minlat) {
        this.minlat = minlat;
    }

    public void setMinlon(double minlon) {
        this.minlon = minlon;
    }


    public double getScaleX(){
        return  ((maxlon - minlon)*3600)/java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth(); //1366.0
    }

    public double getScaleY(){
        return ((maxlat - minlat)*3600)/java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight(); //768.0
    }

    public double lonToScreenX(double lon){

        return  ((lon - minlon)  * 3600) / getScaleX();

    }

    public double latToScreenY(double lat){
        return  ((maxlat - lat) * 3600) / getScaleY();

    }

}
