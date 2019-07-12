
package randomwalkjava;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/*
    TODO    3D plot
*/
public class SceneAnimation extends Data {
    
    final int compwidth = 150;
    final int paneWidth = 200;
    private boolean running;
    private long runs;
    private double rms_data;
    private double smallest;
    private double greatest;
    private double rrms_sum;
    private double var_sum;

    @Override
    public String[] getVars() {
        return this.vars;
    }
 
    public SceneAnimation() {
        this.vars = new String[]{
            "0",    // vars[0] particles        USER
            "0.1",  // vars[1] diameter         n/a
            "0",    // vars[2] charge           n/a
            "0",    // vars[3] steps            USER
            "0",    // vars[4] dimension        USER
            "0",    // vars[5] temperature      n/a
            "f",    // vars[6] fixed(/spread)   n/a
            "-",    // vars[7] (lattice/)free   n/a
            "-",    // vars[8] avoid (on/)off   n/a
            "-"};   // vars[9] save (off)       n/a
        this.running = false;
        this.runs = 1;
        this.rrms_sum = 0.0;
        this.rms_data = 0.0;
        this.smallest = 0.0;
        this.var_sum = 0.0;
    }

    public void start() {
        this.running = true;
    }

    public void stop() {
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    public static boolean isNumDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static boolean isNumInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public void refresh(File folderPath, String executable,
        GraphicsContext piirturi, double scalefactor, double linewidth,
        FXPlot fxplot, double[] rms_runs, double[] rms_std, boolean newdata) {

        int i = 0;
        int j = 0;
        int p = 0;

        if (newdata == true)
            this.runs = 1;

        int width = 900;
        int height = 900;

        double centerX = width/2;
        double centerY = height/2;

        int num_part = Integer.valueOf(this.vars[0]);
        int num_steps = Integer.valueOf(this.vars[3]) + 1;
        double steps = Double.valueOf(this.vars[3]);
        int dim = Integer.valueOf(this.vars[4]);
        this.greatest = Math.sqrt(steps);

        double[] muistiX = new double[num_part];
        double[] muistiY = new double[num_part];

        double[][] values = new double[2][num_part];
        String[] command = null;

        double[] plotData = new double[num_steps];

        double[] xAxis = new double[10];
        for (int x = 0; x < 10; x++)
            xAxis[x] = (double) x;

        double[] yAxis = new double[10];
        Arrays.fill(yAxis, 0.0);

        double[] y2Axis = new double[10];
        Arrays.fill(y2Axis, Math.sqrt(steps));

        double[] xnormAxis = new double[1000];
        double mincount;
        if ( Math.sqrt(steps) < 5.0 )
            mincount = 0.0;
        else
            mincount = Math.sqrt(steps) - 5.0;
        double maxcount = Math.sqrt(steps) + 5.0;
        double skip = (maxcount-mincount)/100.0;
        for (int x = 0; x < 1000; x++) {
            xnormAxis[x] = mincount + (double) x/100.0 + skip;
        }
        double[] ynormAxis = new double[1000];
        Arrays.fill(ynormAxis, 0.0);

        double[] sum = new double[num_steps];
        double sum_parts = 0.0;

        fxplot.setFrameVis(true);

        piirturi.setLineWidth(linewidth);

        try {
            command = new String[]{"cmd","/c",executable,
                this.vars[0], this.vars[1], this.vars[2], this.vars[3],
                this.vars[4], this.vars[5], this.vars[6], this.vars[7],
                this.vars[8], this.vars[9]};

            // FOR DEBUGGING
            //FileOutputStream fos = new FileOutputStream(command[0]);

            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec(command, null, folderPath);
            
            int exitVal;
            try (BufferedReader input = new BufferedReader(new InputStreamReader(
                process.getInputStream()))) {

                String line = null;

                // FOR DEBUGGING
                /*StreamGobbler outputGobbler = new StreamGobbler(
                    process.getInputStream(), "", fos);
                outputGobbler.start();*/

                if ( dim < 3 ) {
                    while ((line = input.readLine()) != null){
                        if (line.trim().startsWith("S"))
                            break;
                        if (!line.substring(0,1).matches("([0-9]|-|\\+)"))
                            continue;
                        if (!line.trim().split("(\\s+)")[0].trim().equals("+")) {
                            if (dim == 1) {
                                try {
                                    values[0][i] = Double.parseDouble(line.trim()) + centerX/scalefactor;
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                                values[1][i] = centerY;
                            } else if (dim == 2) {
                                String[] valStr = line.split("(\\s+)");
                                try {
                                    values[0][i] = Double.parseDouble(valStr[0].trim()) + centerX/scalefactor;
                                    values[1][i] = Double.parseDouble(valStr[1].trim()) + centerX/scalefactor;
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                            }

                            if ( j == 0 && i == 0 ) {
                                piirturi.setFill(Color.RED);
                                if (dim == 1) {
                                    piirturi.fillRect(
                                        values[0][i], centerY,
                                        Math.sqrt(steps) / (4.0 * Math.log10(steps)),
                                        Math.sqrt(centerY/2.0));
                                } else if (dim == 2) {
                                    piirturi.fillRect(
                                    values[0][i], values[1][i],
                                    Math.sqrt(steps) / (3.0 * Math.log10(steps)),
                                    Math.sqrt(steps) / (3.0 * Math.log10(steps)));
                                 }
                                piirturi.setStroke(Color.YELLOW);
                            }

                            if ( j > 0){
                                for (int k = 0; k < num_part; k++){
                                    piirturi.strokeLine(
                                        muistiX[k], muistiY[k], values[0][k], values[1][k]);
                                }
                            }
                            muistiX[i] = values[0][i];
                            muistiY[i] = values[1][i];

                            i++;

                            if ( i == num_part ){
                                i = 0;
                                j++;
                            }

                            if ( j == num_steps ) {
                                i = 0;
                                j = 0;
                            }

                        } else {
                            try {
                                plotData[j] = Double.parseDouble(line.split("(\\s+)")[1].trim());
                            } catch (NumberFormatException e) {
                                continue;
                            }

                            if ( this.runs == 1 ) {
                                sum_parts = 0.0;
                                this.rms_data = 0.0;
                            }
                                    
                            this.rms_data = this.rms_data + plotData[j];
                            sum[i] = this.rms_data;
                            j++;
                            p++;

                            if ( p == num_part ) {
                                i = 0;
                                j = 0;
                                p = 0;
                                for (int f = 0; f < 10; f++)
                                    xAxis[f] = (double) (f + this.runs);

                                for (int m = 0; m < num_part; m++)
                                    sum_parts += sum[m];

                                double rrms = Math.sqrt((sum_parts/num_part) / this.runs);
                                this.rrms_sum += rrms;

                                if ( this.runs < 11 ) {
                                    rms_runs[(int) this.runs - 1] = rrms;
                                    yAxis = Arrays.copyOfRange(rms_runs, 0, 10);
                                } else {
                                    for (int h = 0; h < 9; h++)
                                        rms_runs[h] = rms_runs[h+1];
                                    rms_runs[9] = rrms;
                                    yAxis = Arrays.copyOfRange(rms_runs, 0, 10);
                                }

                                // find greatest and smallest values for y-axis max limit
                                if ( rrms > this.greatest ) {
                                    this.greatest = rrms;
                                    this.smallest = this.greatest;
                                }
                                if ( rrms > Math.sqrt(steps) ) {
                                     this.greatest = rrms;
                                     this.smallest = Math.sqrt(steps);
                                }

                                Thread.sleep(100);
                                fxplot.setMinY( this.smallest * 0.8 );
                                fxplot.setMaxY( this.greatest * 1.2 );
                                fxplot.updateWData("R_rms", xAxis, yAxis);
                                fxplot.updateWData("sqrt(N)", xAxis, y2Axis);

                                double diff2 = Math.pow(rrms - Math.sqrt(steps),2.0);
                                this.var_sum += diff2;
                                double sigma2 = Math.pow(rrms - this.rrms_sum/this.runs,2.0);
                                
                                double ynew = 0.0;
                                double ymax = 1.0;

                                for (int h = 0; h < 1000; h++) {
                                    ynew = //1.0/(Math.sqrt(2.0*Math.PI*diff2)) *
                                        Math.exp( -Math.pow( (xnormAxis[h]-rrms),2.0 ) / (2.0 * diff2));
                                    if ( ynew > ymax ) {
                                        ymax = ynew;
                                    }
                                    ynormAxis[h] = ynew;
                                }
                                fxplot.updateHData("norm", xnormAxis, ynormAxis, ymax);
                            }
                         }
                    }
                    this.runs++;
                } else if (dim == 3) {
                    while ((line = input.readLine()) != null){
                        if (line.trim().startsWith("S"))
                                break;
                        if (!line.trim().split("(\\s+)")[0].trim().equals("+")) {
                            /*String[] valStr = line.split("(\\s+)");
                            values[0][i] = Double.valueOf(valStr[0].trim()) + centerX/scalefactor;
                            values[1][i] = Double.valueOf(valStr[1].trim()) + centerX/scalefactor;
                            values[2][i] = Double.valueOf(valStr[2].trim()) + centerX/scalefactor;

                            if ( runs == 0 && j == 0 && i == 0 ) {
                                piirturi.setFill(Color.RED);
                                piirturi.fillRect(
                                    values[0][i], values[1][i],
                                    Math.sqrt(Double.valueOf(this.vars[2]))
                                        / (4.0 * Math.log10(Double.valueOf(this.vars[2]))),
                                    Math.sqrt(Double.valueOf(this.vars[2]))
                                        / (4.0 * Math.log10(Double.valueOf(this.vars[2]))));
                                piirturi.setStroke(Color.YELLOW);
                            }

                            if ( j > 0){
                                for (int k = 0; k < num_part; k++){
                                    piirturi.strokeLine(muistiX[k], muistiY[k], values[0][k], values[1][k]);
                                }
                            }
                            muistiX[i] = values[0][i];
                            muistiY[i] = values[1][i];

                            i++;

                            if ( i == num_part ){
                                i = 0;
                                j++;
                            }

                            if ( j == num_steps ) {
                                i = 0;
                                j = 0;
                            }*/
                            /*if (i%1000 == 0) {
                                piirturi.setFill(javafx.scene.paint.Color.BLACK);
                                piirturi.strokeRect(0, 0, width, height);
                                piirturi.setFill(javafx.scene.paint.Color.YELLOW);
                                piirturi.strokeText("walks: "+i, centerX, 50, 200.0);
                                i = 0;
                            }*/
                        } else {
                            //plotData[j] = Double.valueOf(line.split("(\\s+)")[1].trim());
                            //IntStream.range(0, swingData.length).forEach(val-> swingData[0][val] = (double) val);

                            //Thread.sleep(100);
                            //chart.updateData("RMS", xAxis, plotData[1]);
                            
                            //m++;
                            i = 0;
                            j = 0;
                        }
                        runs++;
                    }
                }
                
                exitVal = process.waitFor();
                if (exitVal != 0) {
                    runtime.exit(exitVal);
                }
                // FOR DEBUGGING
                //fos.flush();
                //fos.close();
            }

        } catch (IOException | InterruptedException e) {
            //System.out.println(e.getMessage());
        }
        // FOR ONE ROUND OPERATION
        //stop();
    }

    // RANDOM WALK ANIMATION
    public Parent getSceneAnim(){
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(paneWidth);
        asettelu.setVgap(5);
        asettelu.setHgap(10);
        asettelu.setPadding(new Insets(0, 0, 0, 0));
        VBox valikko = new VBox();
        valikko.setPadding(new Insets(20, 10, 0, 0));
        valikko.setSpacing(10);

        // COMPONENTS...
        Label labNumParticles = new Label("number of particles:");
        TextField setNumParticles = new TextField("");
        setNumParticles.setOnKeyReleased(e -> {
            if (isNumInteger(setNumParticles.getText().trim())){
                if (setNumParticles.getText().trim().equals("0")){
                    setNumParticles.setText("1");
                    this.vars[0] = "1";
                } else {
                    this.vars[0] = setNumParticles.getText().trim();
                }
            }
        });

        // this.vars[1] = "0.1" (diameter of particl)
        // this.vars[2] = "0" (charge of particles)

        Label labNumSteps = new Label("number of steps:");
        TextField setNumSteps = new TextField("");
        setNumSteps.setOnKeyReleased(e -> {
            this.vars[3] = setNumSteps.getText().trim();
        });

        Label labNumDimensions = new Label("dimensions:");
        TextField setNumDimensions = new TextField("");
        setNumDimensions.setOnKeyReleased(e -> {
            this.vars[4] = setNumDimensions.getText().trim();
        });

        // this.vars[5] = "0" temperature      n/a
        // this.vars[6] = "f" fixed(/spread)   n/a
        // this.vars[7] = "-" (lattice/)free   n/a
        // this.vars[8] = "-" avoid (on/)off   n/a
        // this.vars[9] = "-" save (off)       n/a

        // ...THEIR PLACEMENTS
        GridPane.setHalignment(labNumParticles, HPos.LEFT);
        asettelu.add(labNumParticles, 0, 0);
        GridPane.setHalignment(setNumParticles, HPos.CENTER);
        setNumParticles.setMinWidth(compwidth);
        setNumParticles.setMaxWidth(compwidth);
        asettelu.add(setNumParticles, 0, 1);

        GridPane.setHalignment(labNumSteps, HPos.LEFT);
        asettelu.add(labNumSteps, 0, 2);
        GridPane.setHalignment(setNumSteps, HPos.CENTER);
        setNumSteps.setMinWidth(compwidth);
        setNumSteps.setMaxWidth(compwidth);
        asettelu.add(setNumSteps, 0, 3);
        
        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 4);
        GridPane.setHalignment(setNumDimensions, HPos.CENTER);
        setNumDimensions.setMinWidth(compwidth);
        setNumDimensions.setMaxWidth(compwidth);
        asettelu.add(setNumDimensions, 0, 5);

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 6, 2, 1);

        final Pane empty1 = new Pane();
        GridPane.setHalignment(empty1, HPos.CENTER);
        asettelu.add(empty1, 0, 7, 2, 1);

        final Pane empty2 = new Pane();
        GridPane.setHalignment(empty2, HPos.CENTER);
        asettelu.add(empty2, 0, 8, 2, 1);

        return asettelu;
    }

}
