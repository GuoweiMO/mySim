package trs.util;

import com.sun.glass.ui.Window;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Created by kwai on 10/08/14.
 */
public class GUI extends JFrame{

        public GUI() {
            initUI();
        }

        private void initUI() {

            MapPanel mp = new MapPanel();

            add(mp);

            setBackground(Color.white);
            setSize(1260,700);
            setTitle("Traffic Simulation");
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        }

        public static void main(String[] args) {
                   GUI gui = new GUI();
                   gui.setVisible(true);
        }
 }

