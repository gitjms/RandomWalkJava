
package randomwalkjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    }
    
    public Data(String[] vars) {
        this.vars = vars;
    }

    public void setVars(String[] vars) {
        this.vars = vars;
    }

    public void setVar(Integer i, String var) {
        this.vars[i]=var;
    }

    public String[] getVars() {
        return this.vars;
    }

    public String getVar(Integer i) {
        return this.vars[i];
    }

    public String createData(File folderPath, String executable, boolean save) {
        String teksti = "";
        String[] command = null;

        try {
            command = new String[]{"cmd","/c",executable,
                this.vars[0],this.vars[1],this.vars[2],this.vars[3],
                this.vars[4],this.vars[5],this.vars[6],this.vars[7]};

            FileOutputStream fos = new FileOutputStream(command[0]);
            Runtime runtime = Runtime.getRuntime();

            if (save == true){
                // print the state of the program
                System.out.println(" Fortran execution begins...");
            }
            Process process = runtime.exec(command, null, folderPath);
            
            int exitVal;
            try (BufferedReader input = new BufferedReader(new InputStreamReader(
                process.getInputStream()))) {

                StreamGobbler errorGobbler = new StreamGobbler(
                    process.getErrorStream(), "ERROR ");
                errorGobbler.start();
                String line = null;

                StreamGobbler outputGobbler = new StreamGobbler(
                    process.getInputStream(), "", fos);
                outputGobbler.start();

                while ((line = input.readLine()) != null){
                    System.out.println(line);
                    if (teksti.isEmpty())
                        teksti = line;
                    else
                        teksti = teksti + "\n" + line;
                }

                exitVal = process.waitFor();
                if (exitVal == 0) {
                    if (save == true){
                        System.out.println(" Fortran execution ended with no errors");
                    } else {
                        System.out.println(" Fortran execution ended with error code " + exitVal);
                        runtime.exit(exitVal);
                    }
                }
                fos.flush();
                fos.close();
            }
            
            //runtime.addShutdownHook(new Message());

        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }

        return teksti;
    }

    public static Pair<String,List<Pair<Double,Double>>> readDataCalc(File filePath){
    
        List<Pair<Double,Double>> data = new ArrayList<>();
        boolean first = false;
        String header = "";

        try (Scanner sc = new Scanner(filePath)) {
            while (sc.hasNextLine()) {
                if (!first) {
                    header = sc.nextLine();
                    first = true;
                } else {
                    String datapair = sc.nextLine();
                    data.add(new Pair(
                        Double.valueOf(datapair.split("\t")[0].trim()),
                        Double.valueOf(datapair.split("\t")[1].trim())
                    ));
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new Pair(header,data);
    }

    public static Pair<String,List<Double>> readDataNoCalcX(File filePath){
    
        List<Double> dataList = new ArrayList<>();
        boolean first = false;
        String header = "";

        try (Scanner sc = new Scanner(filePath)) {
            while (sc.hasNextLine()) {
                if (!first) {
                    header = sc.nextLine();
                    first = true;
                } else {
                    String data = sc.nextLine();
                    dataList.add(Double.valueOf(data.trim()));
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new Pair(header,dataList);
    }

    public static Pair<String,List<Double>> readDataNoCalcY(File filePath){
    
        List<Double> dataList = new ArrayList<>();
        boolean first = false;
        String header = "";

        try (Scanner sc = new Scanner(filePath)) {
            while (sc.hasNextLine()) {
                if (!first) {
                    header = sc.nextLine();
                    first = true;
                } else {
                    String data = sc.nextLine();
                    dataList.add(Double.valueOf(data.trim()));
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new Pair(header,dataList);
    }

    public static Pair<String,List<Double>> readDataNoCalcZ(File filePath){
    
        List<Double> dataList = new ArrayList<>();
        boolean first = false;
        String header = "";

        try (Scanner sc = new Scanner(filePath)) {
            while (sc.hasNextLine()) {
                if (!first) {
                    header = sc.nextLine();
                    first = true;
                } else {
                    String data = sc.nextLine();
                    dataList.add(Double.valueOf(data.trim()));
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new Pair(header,dataList);
    }
}
