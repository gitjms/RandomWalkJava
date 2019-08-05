
package randomwalkjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for data handling:
 * creates data folder and copies executales,
 * reads data from file
 */
public class Data {
    
    public String[] vars;

    /**
     * empty constructor
     */
    public Data() {
    }
    
    public Data(String[] vars) {
        this.vars = vars.clone();
    }

    public void setVars(String[] vars) {
        this.vars = vars.clone();
    }

    public void setVar(Integer i, String var) {
        this.vars[i]=var;
    }

    public String[] getVars() {
        return this.vars.clone();
    }

    public String getVar(Integer i) {
        return this.vars[i];
    }

    public Pair< Boolean, String > createData(File folderPath, String executable, boolean save) {
        String teksti = "";
        //String[] command = null;
        boolean ok = true;
        String msg = "";
        /**
        * vars from user:
        * vars[0] = particles,
        * vars[1] = diameter,
        * vars[2] = charge,
        * vars[3] = steps,
        * vars[4] = dimension,
        * vars[5] = mmc,
        * vars[6] = fixed,
        * vars[7] = lattice,
        * vars[8] = save
        */
        String[] command = new String[]{"cmd","/c",executable,
            this.vars[0], this.vars[1], this.vars[2], this.vars[3],
            this.vars[4], this.vars[5], this.vars[6], this.vars[7],
            this.vars[8]};
 
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(command[0]);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
        Runtime runtime = Runtime.getRuntime();

        try {
            if (save == true){
                /**
                * print the state of the program
                */
                System.out.println(" Fortran execution begins...");
            }
            Process process = runtime.exec(command, null, folderPath);
            
            int exitVal;
            try (BufferedReader input = new BufferedReader(new InputStreamReader(
                process.getInputStream(), Charset.defaultCharset()))) {

                StreamGobbler errorGobbler = new StreamGobbler(
                    process.getErrorStream(), "ERROR ");
                errorGobbler.start();
                String line;

                StreamGobbler outputGobbler = new StreamGobbler(
                    process.getInputStream(), "", fos);
                outputGobbler.start();

                while ((line = input.readLine()) != null){
                    System.out.println(line);
                    if (teksti.isEmpty())
                        teksti = line;
                    else
                        teksti = teksti + line + "\n";
                }

                exitVal = process.waitFor();
                if (exitVal == 0) {
                    if (save == true) {
                        msg = " Fortran execution ended with no errors";
                        System.out.println(msg);
                    }
                } else {
                    if (save == true) {
                        msg = " Fortran execution ended with error code " + exitVal;
                        System.out.println(msg);
                        runtime.addShutdownHook(new Message());
                    }
                    runtime.exit(exitVal);
                }
                if ( fos != null ) fos.close();
            } catch (InterruptedException e) {
                ok = false;
                teksti = teksti + "\n" + msg + "\n" + e.getMessage();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            ok = false;
            teksti = teksti + "\n" + e.getMessage();
        }

        try {
            if ( fos != null ) {
                fos.flush();
                fos.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new Pair(ok,teksti);
    }

    public static List<double[]> readDataMMC(File filePath, Integer dim){
    
        double[] values;
        List<double[]> dataList = new ArrayList<>();

        try (Scanner sc = new Scanner(filePath)) {
            while (sc.hasNextLine()) {
                values = new double[dim];
                String data = sc.nextLine();
                String[] osat;
                osat = data.trim().split("(\\s+)");
                for ( int i = 0; i < osat.length; i++)
                    values[i] = Double.valueOf(osat[i]);
                dataList.add(values);
             }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RandomWalk.class.getName()).log(Level.SEVERE, null, ex);
        }

        return dataList;
    }
}
