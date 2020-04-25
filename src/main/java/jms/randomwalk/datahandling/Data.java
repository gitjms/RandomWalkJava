package jms.randomwalk.datahandling;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.maven.surefire.shade.booter.org.apache.commons.lang3.SystemUtils;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for data handling: creates data, reads data, sets some data values for Fortran.
 */
public class Data {

    private final boolean isWin = SystemUtils.IS_OS_WINDOWS;
    
    /**
     * user variables from GUI.
     */
    public String[] vars;

    /*
      initiating vars array
     */
    public Data(String[] vars) {
        this.vars = vars.clone();
    }

    public Data() {
    }

    /**
     * Method executes Fortan code to get data.
     * @param folderPath datafolder "c:/RWDATA" or "home/user/RWDATA"
     * @param executable Fortran executable "walk.exe" or "walk"
     * @param ismcsaw mc-saw or saw
     * @param issaw saw or not
     * @return true if fortran execution succeeded, false otherwise
     */
    public Boolean createData(File folderPath, String executable, boolean ismcsaw, boolean issaw) {
        StringBuilder teksti = new StringBuilder();
        boolean ok = true;
        String msg = "";
        /*
        * vars from user:
        * vars[0] = which simulation,
        * vars[1] = particles,
        * vars[2] = diameter,
        * vars[3] = steps,
        * vars[4] = dimension,
        * vars[5] = calcfix or sawplot,
        * vars[6] = fixed,
        * vars[7] = lattice,
        * vars[8] = save
        */
        String[] command;
        if (this.isWin) {
            command = new String[]{"cmd", "/c", executable, this.vars[0],
                this.vars[1], this.vars[2], this.vars[3], this.vars[4],
                this.vars[5], this.vars[6], this.vars[7], this.vars[8]};
        } else {
            command = new String[]{"./" + executable,
            this.vars[0], this.vars[1], this.vars[2], this.vars[3], this.vars[4],
            this.vars[5], this.vars[6], this.vars[7], this.vars[8]};
        }
        
        Runtime runtime = Runtime.getRuntime();

        try {
            /*
            * print the state of the program
            */
            if (!issaw) {
                System.out.println(" Fortran execution begins...");
            }
            Process process = runtime.exec(command, null, folderPath);
            
            int exitVal;
            try (BufferedReader input = new BufferedReader(new InputStreamReader(
                process.getInputStream(), StandardCharsets.UTF_8))) {

                StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream());
                errorGobbler.start();
                String line;

                while ((line = input.readLine()) != null) {
                    System.out.println(line);
                    if (teksti.length() == 0) {
                        teksti = new StringBuilder(line);
                    } else {
                        teksti.append(line).append("\n");
                    }

                    if (ismcsaw && line.startsWith("F")) {
                        ok = false;
                    }
                }

                exitVal = process.waitFor();
                if (exitVal == 0) {
                    if (!issaw) {
                        msg = " Fortran execution ended.";
                    }
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
     * Method reads the initial data from file for MMC.
     * @param filePath datafolder "c:/RWDATA"
     * @param dim particle field dimension, user choice
     * @return list of initial particle configuration data
     */
    public static List<double[]> readDataDiff(File filePath, Integer dim) {
    
        double[] values;
        List<double[]> dataList = new ArrayList<>();

        try (Scanner sc = new Scanner(filePath)) {
            while (sc.hasNextLine()) {
                values = new double[dim];
                String data = sc.nextLine();
                String[] osat = data.trim().split("(\\s+)");
                if (!osat[0].equals("Start")) {
                    for (int i = 0; i < osat.length; i++) {
                        values[i] = Double.parseDouble(osat[i]);
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
     * Method for setting the parameter for setting saw or mc-saw in vars array.
     * @param var the vars array to set
     */
    public void setSawMc(String var) {
        this.vars[0] = var;
    }

    /**
     * Method for setting the parameter for setting mc-saw efficiency runs in vars array.
     * @param var the vars array to set
     */
    public void setSawMcEff(String var) {
        this.vars[3] = var;
    }

    /**
     * Method for setting the parameter for plotting in vars array.
     * @param var the vars array to set
     */
    public void setSawPlot(String var) {
        this.vars[5] = var;
    }

    /**
     * Method for setting the save parameter in vars array.
     * @param var the vars array to set
     */
    public void setSave(String var) {
        this.vars[8] = var;
    }

}
