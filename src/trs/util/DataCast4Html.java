package trs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by kwai on 11/08/14.
 */
public class DataCast4Html {

    public static void readfile(String fileDir){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(new File(fileDir)));
            String line;
            while ((line = reader.readLine()) != null){
                String[] str = line.split("");
                System.out.println();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        readfile("OutPut/simulation/equilibrium/ite1");
    }
}
