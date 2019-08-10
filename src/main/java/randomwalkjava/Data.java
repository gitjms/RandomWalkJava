
package randomwalkjava;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for data handling:
 * executes Fortran code for all runs,
 * reads initial particle data for MMC
 */
class Data {

    /**
     * user variables from GUI
     */
    String[] vars;

    /*
      initiating vars array
     */
    Data(@NotNull String[] vars) {
        this.vars = vars.clone();
    }

    @Contract(pure = true)
    Data() {

    }

    /**
     * method executes Fortan code to get data
     * @param folderPath datafolder c:/RWDATA
     * @param executable Fortran executable walk.exe
     * @return true if fortran execution succeeded, false otherwise
     */
    Boolean createData(File folderPath, String executable) {
        StringBuilder teksti = new StringBuilder();
        boolean ok = true;
        String msg = "";
        /*
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

        /*FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(command[0]);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }*/

        Runtime runtime = Runtime.getRuntime();

        try {
            /*
            * print the state of the program
            */
            System.out.println(" Fortran execution begins...");
            Process process = runtime.exec(command, null, folderPath);
            
            int exitVal;
            try (BufferedReader input = new BufferedReader(new InputStreamReader(
                process.getInputStream(), StandardCharsets.UTF_8))) {

                StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream());
                errorGobbler.start();
                String line;

                /*StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), fos);
                outputGobbler.start();*/

                while ((line = input.readLine()) != null){
                    System.out.println(line);
                    if (teksti.length() == 0)
                        teksti = new StringBuilder(line);
                    else
                        teksti.append(line).append("\n");
                }

                exitVal = process.waitFor();
                if (exitVal == 0) {
                    msg = " Fortran execution ended with no errors";
                    System.out.println(msg);
                } else {
                    msg = " Fortran execution ended with error code " + exitVal;
                    System.out.println(msg);
                    runtime.addShutdownHook(new Message());
                    runtime.exit(exitVal);
                }
//                if ( fos != null ) fos.close();
            } catch (InterruptedException e) {
                ok = false;
                teksti.append("\n").append(msg).append("\n").append(e.getMessage());
                System.out.println(teksti);
            }

        } catch (IOException e) {
            ok = false;
            teksti.append("\n").append(e.getMessage());
            System.out.println(teksti);
        }

        /*try {
            if ( fos != null ) fos.flush();
            if ( fos != null ) fos.close();
        } catch (IOException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }*/

        return ok;
    }

    /**
     * method reads the initial data from file for MMC
     * @param filePath datafolder c:/RWDATA
     * @param dim particle field dimension, user choice
     * @return list of initial particle configuration data
     */
    @NotNull
    static List<double[]> readDataMMC(File filePath, Integer dim) {
    
        double[] values;
        List<double[]> dataList = new ArrayList<>();
        
        try ( Scanner sc = new Scanner(filePath) ) {
            while (sc.hasNextLine()) {
                values = new double[dim];
                String data = sc.nextLine();
                String[] osat;
                osat = data.trim().split("(\\s+)");
                if (!osat[0].equals("Start")) {
                    for (int i = 0; i < osat.length; i++) {
                        values[i] = Double.parseDouble(osat[i]);
                    }
                    dataList.add(values);
                }
             }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RandomWalk.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
        }

        return dataList;
    }

    /**
     * method for setting the save parameter in vars array
     * @param var the vars array to set
     */
    void setVar(String var) {
        this.vars[8]=var;
    }

}
