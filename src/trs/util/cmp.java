package trs.util;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by kwai on 21/08/14.
 */
public class cmp {
    double[] flows= {324270,324887,325924 ,326177, 326637,  327171};
    double[] tolls= {61.49*1.2, 43.89 *1.1, 29.69*1.05,  9.65 ,  5.19*0.9 , 0};
    double[] total = new double[6];

    Random rnd = new Random();
    public void run(){
        for(int i=0; i<6;i++){
            total[i] = flows[i]*0.03 + tolls[i];
            System.out.println(flows[i]*0.03);
        }

    }

    public static void main(String[] args){
        (new cmp()).run();
    }
}
//
//0.7 & 324270 &  & 61.49   &   \\ \hline
//        0.8 & 324887 &  & 43.89  &    \\ \hline
//        0.9 & 325924 &  & 29.69   &   \\ \hline
//        1.0 & 326177	 &  & 9.65  &   \\ \hline
//        1.1 & 326637 &  & 5.19  &   \\ \hline
//        1.2 & 327171 &  & 0   &   \\ \hline
