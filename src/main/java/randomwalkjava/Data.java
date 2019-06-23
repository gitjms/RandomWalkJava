
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
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Pair;

public class Data {
    
    public String[] vars;
    final int textwidth = 600;
    final int textheight = 450;
    
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

    public TextArea createData(String path) {
        TextArea textArea = new TextArea();
        textArea.setMinWidth(this.textwidth);
        textArea.setMaxWidth(this.textwidth);
        textArea.setMinHeight(this.textheight);
        textArea.setMaxHeight(this.textheight);
        textArea.setFont(Font.font("Verdana",FontWeight.BOLD, 15));
        textArea.setBorder(null);
        textArea.setEditable(false);
        textArea.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        textArea.setBlendMode(BlendMode.DIFFERENCE);
        String teksti = "";
        
        try {
            String[] command = {"cmd","/c","walk.exe",
                this.vars[0],this.vars[1],this.vars[2],
                this.vars[3],this.vars[4],this.vars[5]};
            
            Runtime runtime = Runtime.getRuntime();
            // print the state of the program
            System.out.println(" Random Walk calculation begins");
            Process process = runtime.exec(command, null, new File(path));
            
            int exitVal;
            try (BufferedReader input = new BufferedReader(new InputStreamReader(
                process.getInputStream()))) {
                StreamGobbler errorGobbler = new
                    StreamGobbler(process.getErrorStream(), "ERROR");
                errorGobbler.start();
                String line = null;
                
                while ((line = input.readLine()) != null){
                    System.out.println(line);
                    if (teksti.isEmpty())
                        teksti = line;
                    else
                        teksti = teksti + "\n" + line;
                }
                exitVal = process.waitFor();
                if (exitVal == 0) {
                    System.out.println(" Calculation ended with no errors");
                } else {
                    System.out.println(" Calculation ended with error code " + exitVal);
                    runtime.exit(exitVal);
                }
            }
            
            //runtime.addShutdownHook(new Message());

        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        textArea.setText(teksti);System.out.println(teksti);
        return textArea;
    }

    public static Pair<String,List<Pair<Double,Double>>> readData(String path){
    
        List<Pair<Double,Double>> data = new ArrayList<>();
        boolean first = false;
        String header = "";

        try (Scanner sc = new Scanner(new File(path + "\\rms_2D.xy"))) {
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
