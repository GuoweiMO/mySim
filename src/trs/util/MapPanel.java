package trs.util;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import sim_old.netgen.BasicEdge;
import sim_old.netgen.GMLFileReader;
import sim_old.routing.KSPTree;
import sim_old.tripasg.BasicAssignment;
import sim_old.tripgen.CentroidsDesignator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapPanel extends JPanel {

    ArrayList<String> nID;
    List<String> drawPtList;
    GMLFileReader reader;
    List<Map<String,Object>> centroids;
    List<Map<String,Object>> tripList;
    KSPTree tree;

    public MapPanel() {
        nID = new ArrayList<String>();
        drawPtList = new ArrayList<String>();


        reader = new GMLFileReader();
        reader.read("Data/cen_milan.gml");
        tree = new KSPTree(reader);
        centroids = CentroidsDesignator.queryCentroidsFromDB("milan_centroids");

        tripList = BasicAssignment.queryTripsfromDB("milan_BAtrips_2");

    }

    @Override
    protected void paintComponent(Graphics g) {
        //super.paintComponent(g);
        drawMap(g);
    }


    public void drawMap(Graphics g){

        //final Set centSets = cd.getCentroidSet();

        final List<BasicEdge> basicEdges = reader.getBasicEdgeSet();
        final double minlat = Double.parseDouble(reader.getMin_lat());
        final double maxlat = Double.parseDouble(reader.getMax_lat());
        final double minlon = Double.parseDouble(reader.getMin_lon());
        final double maxlon = Double.parseDouble(reader.getMax_lon());

        final double scaleX = ((maxlon - minlon)*3600)/1366;
        final double scaleY = ((maxlat - minlat)*3600)/768;

            //System.out.println(xList.get(0)+","+yList.get(0));
        for(int i=0; i < basicEdges.size(); i++) {
            List<String> coords = basicEdges.get(i).getCoords();
            List<String> nodeIDs = basicEdges.get(i).getNodeIDs();

            Path2D path = new Path2D.Double();

            Double X0 = ((Double.parseDouble(coords.get(0).split(",")[0]) - minlon) * 3600)/(scaleX * 1366/800.0);
            Double Y0 = ((maxlat - Double.parseDouble(coords.get(0).split(",")[1])) * 3600)/(scaleY * 768/600.0)+30;
            Double XN = ((Double.parseDouble(coords.get(coords.size()-1).split(",")[0]) - minlon) * 3600)/(scaleX);
            Double YN = ((maxlat - Double.parseDouble(coords.get(coords.size()-1).split(",")[1])) * 3600)/(scaleY);

            path.moveTo(X0,Y0);
            for (int j = 1; j < coords.size(); j++) {
                path.lineTo(((Double.parseDouble(coords.get(j).split(",")[0]) - minlon) * 3600) / (scaleX * 1366/800.0),
                        ((maxlat - Double.parseDouble(coords.get(j).split(",")[1])) * 3600) / (scaleY * 768/600.0) +30);
            }
            g.setColor(Color.YELLOW);
            ((Graphics2D) g).setStroke(new BasicStroke(2));

            if((nodeIDs.contains("37")&&nodeIDs.contains("41"))||
                (nodeIDs.contains("37")&&nodeIDs.contains("166"))||

                (nodeIDs.contains("105")&&nodeIDs.contains("94"))||
                (nodeIDs.contains("150")&&nodeIDs.contains("163"))||

                (nodeIDs.contains("323")&&nodeIDs.contains("574"))||
                (nodeIDs.contains("111")&&nodeIDs.contains("108"))||
                (nodeIDs.contains("268")&&nodeIDs.contains("118"))||
                (nodeIDs.contains("103")&&nodeIDs.contains("102"))||
                (nodeIDs.contains("393")&&nodeIDs.contains("206"))||
                (nodeIDs.contains("189")&&nodeIDs.contains("398"))||
                (nodeIDs.contains("588")&&nodeIDs.contains("378"))||
                (nodeIDs.contains("264")&&nodeIDs.contains("116"))||
                (nodeIDs.contains("209")&&nodeIDs.contains("111"))||
                (nodeIDs.contains("323")&&nodeIDs.contains("112"))||
                (nodeIDs.contains("323")&&nodeIDs.contains("574"))||
                (nodeIDs.contains("150")&&nodeIDs.contains("163"))||
                (nodeIDs.contains("147")&&nodeIDs.contains("146"))||
                (nodeIDs.contains("103")&&nodeIDs.contains("393"))||
                (nodeIDs.contains("207")&&nodeIDs.contains("206"))
                    ){
                g.setColor(Color.GREEN);
            ((Graphics2D) g).setStroke(new BasicStroke(3));
            }

            if((nodeIDs.contains("151")&&nodeIDs.contains("32"))||
                    (nodeIDs.contains("38")&&nodeIDs.contains("588"))||
                    (nodeIDs.contains("118")&&nodeIDs.contains("102"))||
                    (nodeIDs.contains("83")&&nodeIDs.contains("82"))||
                    (nodeIDs.contains("38")&&nodeIDs.contains("108"))
                    ) {
                g.setColor(Color.RED);
                ((Graphics2D) g).setStroke(new BasicStroke(3));
            }

            ((Graphics2D) g).draw(path);
        }


//        /*******draw centroid points******/
//        for(Map cent:centroids) {
//            Ellipse2D.Double s_cir = new Ellipse2D.Double(tree.getVertexXCoord((String)cent.get("centroid"))*800.0/1366 - 4,
//                    tree.getVertexYCoord((String) cent.get("centroid"))*600.0/768 +30 - 4, 8, 8);
//
//            g.setColor(Color.ORANGE);
//            ((Graphics2D) g).fill(s_cir);
//            g.drawString((String) cent.get("centroid"), ((Double) s_cir.getX()).intValue()-6,
//                    ((Double) s_cir.getY()).intValue());
//        }

        /******************Draw all vertexes in the graph*****************/
//                    for(Object vtx:w_graph.vertexSet()){
//                        Ellipse2D.Double s_cir = new Ellipse2D.Double(tree.getVertexXCoord((String)vtx)*this.getWidth()/1366 - 4,
//                                tree.getVertexYCoord((String) vtx)*this.getHeight()/768 - 4, 8, 8);
//
//
//                        g.setColor(Color.ORANGE);
//                        ((Graphics2D) g).fill(s_cir);
//                        g.setColor(Color.BLUE);
//                        g.drawString((String) vtx, ((Double) s_cir.getX()).intValue()-6,
//                                ((Double) s_cir.getY()).intValue());
//                    }

         /***************draw trip paths****************/
//        for(Map tripinfo:tripList) {
//            Path2D trip_path = new Path2D.Double();
//            Double t_x0 = tree.getVertexXCoord((String) tripinfo.get("start_vertex")) * 800.0 / 1366;
//            Double t_y0 = tree.getVertexYCoord((String) tripinfo.get("start_vertex")) * 600.0 / 768;
//            Double t_xn = tree.getVertexXCoord((String) tripinfo.get("end_vertex")) * 800.0 / 1366;
//            Double t_yn = tree.getVertexYCoord((String) tripinfo.get("end_vertex")) * 600.0 / 768;
//
//            Double trip = ((Double) tripinfo.get("trips"))/1000.0;
//
//
//            trip_path.moveTo(t_x0, t_y0);
//            trip_path.lineTo(t_xn, t_yn);
//
//            g.setColor(Color.YELLOW);
//            ((Graphics2D) g).setStroke(new BasicStroke(3));
//            ((Graphics2D) g).draw(trip_path);
//            g.setColor(Color.BLUE);
//            g.drawString(new DecimalFormat("#.0").format(trip),(t_x0.intValue()+t_xn.intValue())/2,
//                    (t_y0.intValue()+t_yn.intValue())/2);
//        }

    }



}
