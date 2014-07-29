package sim.gui;

import sim.netgen.BasicEdge;
import sim.netgen.GMLFileReader;
import sim.routing.KSPTree;
import sim.tripasg.BasicAssignment;
import sim.tripgen.CentroidsDesignator;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class Display_2 extends JFrame {

    static ArrayList<String> nID;
    static List<String> drawPtList;

    public Display_2() {
        nID = new ArrayList<String>();
        drawPtList = new ArrayList<String>();
    }

    public static void main(String[] args) {


        final GMLFileReader reader = new GMLFileReader();
        reader.read("Data/cen_milan.gml");


        final KSPTree tree = new KSPTree(reader);
        final WeightedGraph<String,DefaultWeightedEdge> w_graph = tree.constructGraph();

        //CentroidsDesignator cd = new CentroidsDesignator(reader,tree,w_graph,8,8);
        //cd.designateCentroids();

        final List<Map<String,Object>> centroids = CentroidsDesignator.queryCentroidsFromDB("milan_centroids");

        final List<Map<String,Object>> tripList = BasicAssignment.queryTripsfromDB("milan_BAtrips_2");

        //final Set centSets = cd.getCentroidSet();

        Display_2 d = new Display_2();

        final List<BasicEdge> basicEdges = reader.getBasicEdgeSet();
        final double minlat = Double.parseDouble(reader.getMin_lat());
        final double maxlat = Double.parseDouble(reader.getMax_lat());
        final double minlon = Double.parseDouble(reader.getMin_lon());
        final double maxlon = Double.parseDouble(reader.getMax_lon());

        final double scaleX = ((maxlon - minlon)*3600)/1366;
        final double scaleY = ((maxlat - minlat)*3600)/768;

            //System.out.println(xList.get(0)+","+yList.get(0));

            d.add(new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {

                    for(int i=0; i < basicEdges.size(); i++) {
                      List<String> coords = basicEdges.get(i).getCoords();

                        Path2D path = new Path2D.Double();

                        Double X0 = ((Double.parseDouble(coords.get(0).split(",")[0]) - minlon) * 3600)/(scaleX * 1366/this.getWidth());
                        Double Y0 = ((maxlat - Double.parseDouble(coords.get(0).split(",")[1])) * 3600)/(scaleY * 768/this.getHeight());
                        Double XN = ((Double.parseDouble(coords.get(coords.size()-1).split(",")[0]) - minlon) * 3600)/(scaleX);
                        Double YN = ((maxlat - Double.parseDouble(coords.get(coords.size()-1).split(",")[1])) * 3600)/(scaleY);

                        path.moveTo(X0,Y0);
                        for (int j = 1; j < coords.size(); j++) {
                            path.lineTo(((Double.parseDouble(coords.get(j).split(",")[0]) - minlon) * 3600) / (scaleX * 1366/this.getWidth()),
                                        ((maxlat - Double.parseDouble(coords.get(j).split(",")[1])) * 3600) / (scaleY * 768/this.getHeight()));
                        }
                        g.setColor(Color.MAGENTA);
                        ((Graphics2D) g).draw(path);
                    }

                    //draw centroid points
                    for(Map cent:centroids) {
                        Ellipse2D.Double s_cir = new Ellipse2D.Double(tree.getVertexXCoord((String)cent.get("centroid"))*this.getWidth()/1366 - 4,
                                                        tree.getVertexYCoord((String) cent.get("centroid"))*this.getHeight()/768 - 4, 8, 8);

                        g.setColor(Color.ORANGE);
                        ((Graphics2D) g).fill(s_cir);
                        g.drawString((String) cent.get("centroid"), ((Double) s_cir.getX()).intValue()-6,
                                                    ((Double) s_cir.getY()).intValue());
                    }

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


                    for(Map tripinfo:tripList) {
                        Path2D trip_path = new Path2D.Double();
                        Double t_x0 = tree.getVertexXCoord((String) tripinfo.get("start_vertex")) * this.getWidth() / 1366;
                        Double t_y0 = tree.getVertexYCoord((String) tripinfo.get("start_vertex")) * this.getHeight() / 768;
                        Double t_xn = tree.getVertexXCoord((String) tripinfo.get("end_vertex")) * this.getWidth() / 1366;
                        Double t_yn = tree.getVertexYCoord((String) tripinfo.get("end_vertex")) * this.getHeight() / 768;

                        Double trip = ((Double) tripinfo.get("trips"))/1000.0;


                        trip_path.moveTo(t_x0, t_y0);
                        trip_path.lineTo(t_xn, t_yn);

                        g.setColor(Color.YELLOW);
                        ((Graphics2D) g).draw(trip_path);
                        g.setColor(Color.BLUE);
                        g.drawString(new DecimalFormat("#.0").format(trip),(t_x0.intValue()+t_xn.intValue())/2,
                                                (t_y0.intValue()+t_yn.intValue())/2);
                    }

                }
            });

        d.setSize(800, 680);
        d.setLocation(new Point(0, 0));
        d.setVisible(true);
        d.setResizable(true);
        d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
