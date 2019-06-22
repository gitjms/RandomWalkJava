
package randomwalkjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

public class Data {

    public String[] vars;

    public Data() {
        this.vars = new String[]{"0","0.0","0","0","-","-"};
    }

    public void setVars(String[] vars) {
        this.vars = vars;
    }

    public void setVar(Integer i, String var) {
        vars[i]=var;
    }

    public String[] getVars() {
        return vars;
    }

    public String getVar(Integer i) {
        return vars[i];
    }

    public static void createData(String[] vars) {

        try {
            String fileLocation = "C:\\DATA\\";
            
            String[] command = {"cmd","/c","walk.exe",vars[0],vars[1],vars[2],vars[3],vars[4],vars[5]};
            
            Runtime runtime = Runtime.getRuntime();
            // print the state of the program
            System.out.println(" Random Walk calculations begin");

            Process process = runtime.exec(command, null, new File(fileLocation));

            int exitVal;
            try (BufferedReader input = new BufferedReader(new InputStreamReader(
                process.getInputStream()))) {
                StreamGobbler errorGobbler = new
                    StreamGobbler(process.getErrorStream(), "ERROR");
                errorGobbler.start();
                String line = null;
                while ((line = input.readLine()) != null)
                {
                    System.out.println(line);
                }   exitVal = process.waitFor();
                if (exitVal == 0)
                    System.out.println(" Calculations ended with no errors");
                else {
                    System.out.println(" Calculations ended with error code " + exitVal);
                    runtime.exit(exitVal);
                }
            }
            
            runtime.addShutdownHook(new Message());

        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        //Nappi.class.cast("execute").setToggle(false);
    }

    public static Pair<String,List<Pair<Double,Double>>> readData(){
    
        List<Pair<Double,Double>> data = new ArrayList<>();
        boolean first = false;
        String header = "";

        try (Scanner sc = new Scanner(new File("C:\\DATA\\rms_2D.xy"))) {

            while (sc.hasNextLine()) {
                if (!first) {
                    header = sc.nextLine();
                    first = true;
                } else {
                    String datapair = sc.nextLine();
                    data.add(new Pair(Double.valueOf(datapair.split("\t")[0].trim()),Double.valueOf(datapair.split("\t")[1].trim())));
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new Pair(header,data);
    }
}
