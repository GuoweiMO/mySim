package older_version.GUI;

import older_version.RoutesConstructor.BasicLink;
import older_version.TrafficNetGenerator.OSMapReader;
import older_version.TripsGenerator.CentroidsDesignator;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.*;
import java.util.List;

public class Display_2 extends JFrame {

    static ArrayList<Double> xList;
    static ArrayList<Double> yList;
    static ArrayList<String> nID;

    public Display_2() {
        xList = new ArrayList<Double>();
        yList = new ArrayList<Double>();
        nID = new ArrayList<String>();
    }

    public static void main(String[] args) {


        OSMapReader reader = new OSMapReader();
        reader.read("Data/east_milan.osm");


//        KSPTree tree = new KSPTree(reader);
//        WeightedGraph<String,DefaultWeightedEdge> w_graph = tree.constructGraph();
//
//        CentroidsDesignator cd = new CentroidsDesignator(reader,tree,w_graph,10,10);
        //cd.designateCentroids();

        CentroidsDesignator cd = new CentroidsDesignator();
        final List<Map<String,Object>> centroids = cd.queryCentroidsFromDB("Centroids");

        final Set centSets = new HashSet();
        for(Map map:centroids){
            centSets.add(map.get("centroid"));
        }


//        BasicAssignment ba = new BasicAssignment();
//        final List<Map<String,Object>> q_result = ba.queryfromDB("BasicAssignment");

        Display_2 d = new Display_2();

        final LinkedList<BasicLink> basicLinks = reader.getMyBasicLinkSet();
        final double minlat = reader.getMinlat();
        final double maxlat = reader.getMaxlat();
        final double minlon = reader.getMinlon();
        final double maxlon = reader.getMaxlon();

        final double scaleX = ((maxlon - minlon)*3600)/1300;
        final double scaleY = ((maxlat - minlat)*3600)/730;


            //System.out.println(xList.get(0)+","+yList.get(0));

            d.add(new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    //super.paintComponent(g);
                    for(int i=0; i < basicLinks.size(); i++) {
                        xList = basicLinks.get(i).getLons();
                        yList = basicLinks.get(i).getLats();


                        Path2D path = new Path2D.Double();

                        Double X0 = ((xList.get(0) - minlon) * 3600)/(1.25*scaleX) + 100.0;
                        Double Y0 = ((maxlat - yList.get(0)) * 3600) / (1.35*scaleY) +80.0;
                        Double XN = ((xList.get(xList.size()-1) - minlon) * 3600)/(1.25*scaleX) + 100.0;
                        Double YN = ((maxlat - yList.get(yList.size()-1)) * 3600) / (1.35*scaleY) +80.0;

                        path.moveTo(X0,Y0);
                        for (int j = 1; j < xList.size(); j++) {
                            path.lineTo(((xList.get(j) - minlon) * 3600) / (1.25 * scaleX) + 100,
                                        ((maxlat - yList.get(j)) * 3600) / (1.35 * scaleY)+80);
                        }
                        g.setColor(Color.GREEN);
                        ((Graphics2D) g).draw(path);


                        //draw centroid points
                        Object sPt = basicLinks.get(i).getNodeIDs().get(0);
                        Object ePt = basicLinks.get(i).getNodeIDs().get(basicLinks.get(i).getNodeIDs().size()-1);

                        Ellipse2D.Double s_cir = new Ellipse2D.Double(X0 -4,Y0 -4,8,8);
                        Ellipse2D.Double e_cir = new Ellipse2D.Double(XN -4,YN -4,8,8);

                        if(centSets.contains(sPt)){
                           g.setColor(Color.ORANGE);

                           ((Graphics2D) g).fill(s_cir);

                        }

                        if(centSets.contains(ePt)){
                            g.setColor(Color.BLUE);
                            ((Graphics2D) g).fill(e_cir);

                        }
                    }


//                    Double xa_edge = 0.0d;
//                    Double ya_edge = 0.0d;
//                    Double xb_edge = 0.0d;
//                    Double yb_edge = 0.0d;
//                    for(Map map:q_result){
//                        for (int k=0; k <basicLinks.size(); k++) {
//                            xList = basicLinks.get(k).getLons();
//                            yList = basicLinks.get(k).getLats();
//                            nID = basicLinks.get(k).getNodeIDs();
//                            for (int t = 0; t < nID.size(); t++) {
//                                if (nID.get(t).equals(map.get("start_vertex"))) {
//                                    xa_edge = ((xList.get(t) - minlon) * 3600) / (1.25 * scaleX) + 100.0;
//                                    ya_edge = ((maxlat - yList.get(t)) * 3600) / (1.35 * scaleY) + 80.0;
//                                }
//                                if (nID.get(t).equals(map.get("end_vertex"))) {
//                                    xb_edge = ((xList.get(t) - minlon) * 3600) / (1.25 * scaleX) + 100.0;
//                                    yb_edge = ((maxlat - yList.get(t)) * 3600) / (1.35 * scaleY) + 80.0;
//                                }
//                            }
//                        }
//                        Path2D path2 = new Path2D.Double();
//                        path2.moveTo(xa_edge,ya_edge);
//                        path2.lineTo(xb_edge,yb_edge);
//                        g.setColor(Color.YELLOW);
//                        ((Graphics2D) g).draw(path2);
//
//                        g.drawString(map.get("trips").toString(),(xa_edge.intValue()+xb_edge.intValue())/2,
//                                (ya_edge.intValue()+yb_edge.intValue())/2);
//                    }

                }
            });

        d.setSize(1240, 720);
        d.setLocation(new Point(0, 0));
        d.setVisible(true);
        d.setResizable(true);
        d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
