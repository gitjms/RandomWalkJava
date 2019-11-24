
package randomwalkjava;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
     * @param folderPath datafolder "c:/RWDATA"
     * @param executable Fortran executable "walk.exe"
     * @param iscbmc cbmc or saw
     * @param issaw saw or not
     * @return true if fortran execution succeeded, false otherwise
     */
    Boolean createData(File folderPath, String executable, boolean iscbmc, boolean issaw) {
        StringBuilder teksti = new StringBuilder();
        boolean ok = true;
        String msg = "";
        /*
        * vars from user:
        * vars[0] = which simulation,
        * vars[1] = particles,
        * vars[2] = diameter,
        * vars[3] = charge,
        * vars[4] = steps,
        * vars[5] = dimension,
        * vars[6] = calcfix or sawplot,
        * vars[7] = fixed,
        * vars[8] = lattice
          vars[9] = save
        */
        String[] command = new String[]{"cmd","/c",executable,
            this.vars[0], this.vars[1], this.vars[2], this.vars[3], this.vars[4],
            this.vars[5], this.vars[6], this.vars[7], this.vars[8], this.vars[9]};

        Runtime runtime = Runtime.getRuntime();

        try {
            /*
            * print the state of the program
            */
            if (!issaw) System.out.println(" Fortran execution begins...");
            Process process = runtime.exec(command, null, folderPath);
            
            int exitVal;
            try (BufferedReader input = new BufferedReader(new InputStreamReader(
                process.getInputStream(), StandardCharsets.UTF_8))) {

                StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream());
                errorGobbler.start();
                String line;

                while ((line = input.readLine()) != null){
                    System.out.println(line);
                    if (teksti.length() == 0)
                        teksti = new StringBuilder(line);
                    else
                        teksti.append(line).append("\n");

                    if (iscbmc && line.startsWith("F")) ok = false;
                }

                exitVal = process.waitFor();
                if (exitVal == 0) {
                    if (!issaw) msg = " Fortran execution ended.";
                    System.out.println(msg);
                } else {
                    msg = " Fortran execution ended with error code " + exitVal + ".";
                    System.out.println(msg);
                    runtime.addShutdownHook(new Message());
                    runtime.exit(exitVal);
                }
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
        return ok;
    }

    /**
     * method reads the initial data from file for MMC
     * @param filePath datafolder "c:/RWDATA"
     * @param dim particle field dimension, user choice
     * @return list of initial particle configuration data
     */
    @NotNull
    static List<double[]> readDataDiff(File filePath, Integer dim) {
    
        double[] values;
        List<double[]> dataList = new ArrayList<>();

        try ( Scanner sc = new Scanner(filePath) ) {
            while (sc.hasNextLine()) {
                values = new double[dim+1];
                String data = sc.nextLine();
                String[] osat;
                osat = data.trim().split("(\\s+)");
                if (!osat[0].equals("Start")) {
                    for (int i = 1; i < osat.length; i++) {
                        values[i-1] = Double.parseDouble(osat[i]);
                    }
                    switch (osat[0].trim()) {
                        case "+":
                            values[dim] = 1.0;
                            break;
                        case "-":
                            values[dim] = 2.0;
                            break;
                        case "*":
                            values[dim] = 3.0;
                            break;
                    }
                    dataList.add(values);
                }
             }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

        return dataList;
    }

    /**
     * method for setting the save parameter in vars array
     * @param var the vars array to set
     */
    void setSave(String var) { this.vars[9]=var; }

    /**
     * method for setting the parameter for setting saw or cbmc in vars array
     * @param var the vars array to set
     */
    void setSawCbmc(String var) { this.vars[0]=var; }

    /**
     * method for setting the parameter for plotting in vars array
     * @param var the vars array to set
     */
    void setSawPlot(String var) { this.vars[6]=var; }

}
