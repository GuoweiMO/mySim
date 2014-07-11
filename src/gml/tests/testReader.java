package gml.tests;

import gml.netgen.GMLFileReader;

/**
 * Created by kwai on 11/07/14.
 */
public class testReader {

    public static void main(String[] args){
        GMLFileReader reader = new GMLFileReader();
        reader.read("Data/east_milan.gml");
    }
}
