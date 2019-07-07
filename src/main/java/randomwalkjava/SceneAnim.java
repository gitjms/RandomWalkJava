
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
public class SceneAnim extends Data {
    
    final int compwidth = 150;
    final int paneWidth = 200;
    private boolean running;
    private long runs;
    private double rms_data;
    private double smallest;
    private double greatest;

    @Override
    public String[] getVars() {
        return this.vars;
    }
 
    public SceneAnim() {
        this.vars = new String[]{
            "0",    // particles
            "0.0",  // seze
            "0",    // steps
            "0",    // dimension
            "f",    // fixed
            "-",    // lattice
            "-",    // avoid
            "-"};   // save
        this.running = false;
        this.runs = 1;
        this.rms_data = 0.0;
        this.smallest = 0.0;
        this.greatest = 0.0;
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
        FXPlot chart, double[] rms_runs, boolean newdata) {

        if (newdata == true)
            this.runs = 1;

        int width = 900;
        int height = 900;

        double centerX = width/2;
        double centerY = height/2;

        int num_part = Integer.valueOf(this.vars[0]);
        int num_steps = Integer.valueOf(this.vars[2]) + 1;
        int dim = Integer.valueOf(this.vars[3]);

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
        Arrays.fill(y2Axis, Math.sqrt(Double.valueOf(this.vars[2])));

        double[] sum = new double[num_steps];
        double sum_parts = 0.0;

        chart.getFrame().setVisible(true);
        piirturi.setLineWidth(linewidth);

        try {
            command = new String[]{"cmd","/c",executable,
                this.vars[0], this.vars[1], this.vars[2], this.vars[3],
                this.vars[4], this.vars[5], this.vars[6], this.vars[7]};

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

                int i = 0;
                int j = 0;
                int p = 0;
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
                                        Math.sqrt(Double.valueOf(this.vars[2]))
                                            / (4.0 * Math.log10(Double.valueOf(this.vars[2]))),
                                        Math.sqrt(centerY/2.0));
                                } else if (dim == 2) {
                                    piirturi.fillRect(
                                    values[0][i], values[1][i],
                                    Math.sqrt(Double.valueOf(this.vars[2]))
                                        / (3.0 * Math.log10(Double.valueOf(this.vars[2]))),
                                    Math.sqrt(Double.valueOf(this.vars[2]))
                                        / (3.0 * Math.log10(Double.valueOf(this.vars[2]))));
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
                                if ( rrms > Math.sqrt(Double.valueOf(this.vars[2])) ) {
                                     this.greatest = Math.sqrt(Double.valueOf(this.vars[2]));
                                     this.smallest = rrms;
                                }

                                Thread.sleep(100);
                                chart.setMinY( this.smallest * 0.8);
                                chart.setMaxY( this.greatest * 1.2);
                                chart.updateData("R_rms", xAxis, yAxis);
                                chart.updateData("sqrt(N)", xAxis, y2Axis);
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

        Label labSizeParticles = new Label("diameter of particle:");
        TextField setSizeParticles = new TextField("");
        setSizeParticles.setOnKeyReleased(e -> {
            this.vars[1] = setSizeParticles.getText().trim();
        });

        Label labNumSteps = new Label("number of steps:");
        TextField setNumSteps = new TextField("");
        setNumSteps.setOnKeyReleased(e -> {
            this.vars[2] = setNumSteps.getText().trim();
        });

        Label labNumDimensions = new Label("dimensions:");
        TextField setNumDimensions = new TextField("");
        setNumDimensions.setOnKeyReleased(e -> {
            this.vars[3] = setNumDimensions.getText().trim();
        });

        // ...THEIR PLACEMENTS
        GridPane.setHalignment(labNumParticles, HPos.LEFT);
        asettelu.add(labNumParticles, 0, 0);
        GridPane.setHalignment(setNumParticles, HPos.CENTER);
        setNumParticles.setMinWidth(compwidth);
        setNumParticles.setMaxWidth(compwidth);
        asettelu.add(setNumParticles, 0, 1);
        
        GridPane.setHalignment(labSizeParticles, HPos.LEFT);
        asettelu.add(labSizeParticles, 0, 2);
        GridPane.setHalignment(setSizeParticles, HPos.CENTER);
        setSizeParticles.setMinWidth(compwidth);
        setSizeParticles.setMaxWidth(compwidth);
        asettelu.add(setSizeParticles, 0, 3);

        GridPane.setHalignment(labNumSteps, HPos.LEFT);
        asettelu.add(labNumSteps, 0, 4);
        GridPane.setHalignment(setNumSteps, HPos.CENTER);
        setNumSteps.setMinWidth(compwidth);
        setNumSteps.setMaxWidth(compwidth);
        asettelu.add(setNumSteps, 0, 5);
        
        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 6);
        GridPane.setHalignment(setNumDimensions, HPos.CENTER);
        setNumDimensions.setMinWidth(compwidth);
        setNumDimensions.setMaxWidth(compwidth);
        asettelu.add(setNumDimensions, 0, 7);

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 8, 2, 1);

        final Pane empty1 = new Pane();
        GridPane.setHalignment(empty1, HPos.CENTER);
        asettelu.add(empty1, 0, 9, 2, 1);

        final Pane empty2 = new Pane();
        GridPane.setHalignment(empty2, HPos.CENTER);
        asettelu.add(empty2, 0, 10, 2, 1);

        return asettelu;
    }

}
