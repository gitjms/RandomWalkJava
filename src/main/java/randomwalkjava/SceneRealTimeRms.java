
package randomwalkjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import java.util.Arrays;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for Real Time Rms
 */
public class SceneRealTimeRms extends Data {

    private final int compwidth = 150;
    private final int paneWidth = 200;
    private double scalefactor;
    private double linewidth;
    private GraphicsContext piirturi;
    private boolean running;
    private long runs;
    private double rms_sum;
    private double rms_data;
    private double smallest;
    private double greatest;
    private Runtime runtime;
    private int exitVal;

    @Override
    public String[] getVars() {
        return this.vars;
    }
 
    public SceneRealTimeRms() {
        this.vars = new String[]{
            "0",    // vars[0] particles        USER
            "0.1",  // vars[1] diameter         n/a
            "0",    // vars[2] charge           n/a
            "0",    // vars[3] steps            USER
            "0",    // vars[4] dimension        USER
            "-",    // vars[5] mmc              n/a
            "f",    // vars[6] fixed(/spread)   n/a
            "-",    // vars[7] (lattice/)free   n/a
            "-"};   // vars[8] save (off)       n/a
        this.running = false;
        this.runs = 1;
        this.rms_sum = 0.0;
        this.rms_data = 0.0;
        this.smallest = 0.0;
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

    public void runtimeStart() {
        this.running = true;
    }
    public boolean runtimeIsRunning() {
        return this.running;
    }
    public void stopRuntime() {
        this.running = false;
        this.runtime.exit(this.exitVal);
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

    public void refresh(File folder, String executable,
        GraphicsContext piirturi, double scalefactor, int rtrmswidth,
        double linewidth, FXPlot fxplot, double[] rms_runs, double[] rms_norm,
        boolean newdata, double mincount, double maxcount, boolean standnorm) {

        int i = 0;
        int j = 0;
        int p = 0;

        this.piirturi = piirturi;
        this.linewidth = linewidth;
        this.scalefactor = scalefactor;

        if (newdata == true) {
            this.runs = 1;
            this.rms_sum = 0.0;
            this.rms_data = 0.0;
        }

        int width = 900;
        int height = 900;

        double centerX = width/2;
        double centerY = height/2;

        int num_part = parseInt(this.vars[0]);
        int num_steps = parseInt(this.vars[3]) + 1;
        double steps = parseDouble(this.vars[3]);
        int dim = parseInt(this.vars[4]);
        double expected = Math.sqrt(steps);
        this.greatest = expected;

        double[] muistiX = new double[num_part];
        double[] muistiY = new double[num_part];

        double[][] values;
        if( dim < 3 )
            values = new double[2][num_part];
        else
            values = new double[3][num_part];

        double[] plotData = new double[num_part];

        double[] xAxis = new double[10];
        for (int x = 0; x < 10; x++)
            xAxis[x] = (double) x;

        double[] yAxis = new double[10];
        Arrays.fill(yAxis, 0.0);

        double[] y2Axis = new double[10];
        Arrays.fill(y2Axis, expected);

        double[] xnormAxis = new double[1000];

        double skip = (maxcount-mincount)/100.0;
        for (int x = 0; x < 1000; x++) {
            xnormAxis[x] = mincount + (double) x/100.0 + skip;
        }
        
        double[] ynormAxis = new double[1000];
        Arrays.fill(ynormAxis, 0.0);

        double[] sum = new double[num_part];
        double sum_parts = 0.0;

        fxplot.setFrameVis(true);

        this.piirturi.setLineWidth(this.linewidth);

        String[] command = null;

        command = new String[]{"cmd","/c",executable,
            this.vars[0], this.vars[1], this.vars[2], this.vars[3],
            this.vars[4], this.vars[5], this.vars[6], this.vars[7],
            this.vars[8]};

        try {
            this.runtime = Runtime.getRuntime();
            runtimeStart();

            Process process = this.runtime.exec(command, null, folder);

            try (BufferedReader input = new BufferedReader(new InputStreamReader(
                process.getInputStream()))) {

                String line;
                boolean failed = false;

                while ((line = input.readLine()) != null){
                        if (line.trim().startsWith("S")) {
                            failed = true;
                            break;
                        }
                        if (!line.substring(0,1).matches("([0-9]|-|\\+)"))
                            continue;
                        if (!line.trim().split("(\\s+)")[0].trim().equals("+")) {
                            switch (dim) {
                                case 1:
                                    try {
                                        values[0][i] = Double.parseDouble(line.trim()) + centerX/this.scalefactor;
                                    } catch (NumberFormatException e) {
                                        continue;
                                    }   values[1][i] = centerY;
                                    break;
                                case 2:
                                    {
                                        String[] valStr = line.split("(\\s+)");
                                        try {
                                            values[0][i] = Double.parseDouble(valStr[0].trim()) + centerX/this.scalefactor;
                                            values[1][i] = Double.parseDouble(valStr[1].trim()) + centerX/this.scalefactor;
                                        } catch (NumberFormatException e) {
                                            continue;
                                        }       break;
                                    }
                                case 3:
                                    {
                                        String[] valStr = line.split("(\\s+)");
                                        try {
                                            values[0][i] = Double.parseDouble(valStr[0].trim()) + centerX/this.scalefactor;
                                            values[1][i] = Double.parseDouble(valStr[1].trim()) + centerX/this.scalefactor;
                                            values[2][i] = Double.parseDouble(valStr[2].trim()) + 1.2*centerX/this.scalefactor;
                                        } catch (NumberFormatException e) {
                                            continue;
                                        }       break;
                                    }
                                default:
                                    break;
                            }
                            /**
                            * RED SOURCE DOT
                            */
                            if ( j == 0 && i == 0 ) {
                                this.piirturi.setFill(Color.RED);
                                switch (dim) {
                                    case 1:
                                        this.piirturi.fillRect(
                                            values[0][i], centerY,
                                            expected / ( 10.0 * Math.sqrt(Math.log10(steps)) ),
                                            Math.sqrt(centerY/2.0));
                                        break;
                                    case 2:
                                        this.piirturi.fillRect(
                                            values[0][i], values[1][i],
                                            expected * Math.log10(steps) / ( Math.sqrt(steps) * dim ),
                                            expected * Math.log10(steps) / ( Math.sqrt(steps) * dim ));
                                        break;
                                    case 3:
                                        this.piirturi.fillRect(
                                            values[0][i] + Math.cos(values[2][i]),
                                            values[1][i] + Math.sin(values[2][i]),
                                            expected * Math.log10(steps) / ( Math.sqrt(steps) * dim ),
                                            expected * Math.log10(steps) / ( Math.sqrt(steps) * dim ));
                                        break;
                                    default:
                                        break;
                                }
                                this.piirturi.setStroke(Color.YELLOW);
                            }

                            /**
                            * YELLOW LINES
                            */
                            if ( j > 0){
                                for (int k = 0; k < num_part; k++){
                                    if ( dim < 3 ) {
                                        this.piirturi.strokeLine(
                                            muistiX[k], muistiY[k], values[0][k], values[1][k]);
                                    } else {
                                        this.linewidth = 10.0 * Math.log10(steps)
                                            / ( values[2][k] * scalefactor );
                                        this.piirturi.setLineWidth(this.linewidth);
                                        this.piirturi.strokeLine(
                                            muistiX[k], muistiY[k],
                                            values[0][k]
                                                + Math.cos(values[2][k]),
                                            values[1][k]
                                                + Math.sin(values[2][k]));
                                    }
                                }
                            }
                            if ( dim < 3 ) {
                                muistiX[i] = values[0][i];
                                muistiY[i] = values[1][i];
                            } else {
                                muistiX[i] = values[0][i]
                                    + Math.cos(values[2][i]);
                                muistiY[i] = values[1][i]
                                    + Math.sin(values[2][i]);
                            }

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
                            /**
                            * PLOTS
                            */
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
                                this.rms_sum += rrms;

                                if ( this.runs < 11 ) {
                                    rms_runs[(int) this.runs - 1] = rrms;
                                    yAxis = Arrays.copyOfRange(rms_runs, 0, 10);
                                } else {
                                    for (int h = 0; h < 9; h++)
                                        rms_runs[h] = rms_runs[h+1];
                                    rms_runs[9] = rrms;
                                    yAxis = Arrays.copyOfRange(rms_runs, 0, 10);
                                }

                                /**
                                * find greatest and smallest values for y-axis max limit
                                */
                                if ( rrms > this.greatest ) {
                                    this.greatest = rrms;
                                    this.smallest = this.greatest;
                                }
                                if ( rrms > expected ) {
                                     this.greatest = rrms;
                                     this.smallest = expected;
                                }

                                Thread.sleep(100);
                                fxplot.setMinY( this.smallest * 0.8 );
                                fxplot.setMaxY( this.greatest * 1.2 );
                                fxplot.updateWData("R_rms", xAxis, yAxis);
                                fxplot.updateWData("sqrt(N)", xAxis, y2Axis);

                                double sigma2 = Math.pow(rrms - this.rms_sum/this.runs,2.0);
                                double ynorm;
                                double mean;
                                
                                if ( standnorm == true )
                                    mean = 0.0;
                                else
                                    mean = this.rms_sum/this.runs;
                                
                                double ymax = 1.0;
                                for (int h = 0; h < 1000; h++) {
                                    if ( standnorm ) {
                                        ynorm = Math.exp( - Math.pow( ( xnormAxis[h] - mean ), 2.0 ) / (2.0 * sigma2) );
                                    
                                    } else {
                                        ynorm = 1.0/(Math.sqrt(2.0*Math.PI*sigma2))
                                            * Math.exp( - Math.pow( ( xnormAxis[h] - mean ), 2.0 ) / (2.0 * sigma2) );
                                        if ( ynorm > ymax && ynorm > 1.0 ) {
                                            ymax = ynorm;
                                            fxplot.setHMaxY(ymax);
                                        } else if ( ynorm < 1.0 && ymax < 1.0 )
                                            fxplot.setHMaxY(1.0);
                                        else
                                            fxplot.setHMaxY(ymax);
                                    }
                                    ynormAxis[h] = ynorm;
                                }
                                fxplot.updateHData("norm", xnormAxis, ynormAxis, expected);
                            }
                    }
                }
                if ( failed == false )
                    this.runs++;

                this.exitVal = process.waitFor();
                if (this.exitVal != 0) {
                    this.runtime.gc();
                    this.runtime.exit(exitVal);
                    this.runtime.exit(this.exitVal);
                }
            } 

        } catch (IOException | InterruptedException e) {
            this.runtime.gc();
            System.out.println(e.getMessage());
        }
    }

    /**
     * 
     * @return REAL TIME RMS SCENE
     */
    public Parent getSceneReal(){
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(paneWidth);
        asettelu.setVgap(5);
        asettelu.setHgap(10);
        asettelu.setPadding(new Insets(0, 0, 0, 0));
        VBox valikko = new VBox();
        valikko.setPadding(new Insets(20, 10, 0, 0));
        valikko.setSpacing(10);

        DropShadow shadow = new DropShadow();

        /**
        * COMPONENTS...
        */
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
            } else
                this.vars[0] = "0";
        });

        this.vars[1] = "0.1"; // (diameter of particle)
        this.vars[2] = "0"; // (charge of particles)

        Label labNumSteps = new Label("number of steps:");
        TextField setNumSteps = new TextField("");
        setNumSteps.setOnKeyReleased(e -> {
            if (isNumInteger(setNumSteps.getText().trim())){
                this.vars[3] = setNumSteps.getText().trim();
            } else
                this.vars[3] = "0";
        });

        Label labNumDimensions = new Label("dimension:");
        ToggleButton setDim1 = new ToggleButton("1");
        setDim1.setMinWidth(35);
        setDim1.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setDim1.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setDim1.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                setDim1.setEffect(shadow);
        });
        setDim1.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                setDim1.setEffect(null);
        });
        ToggleButton setDim2 = new ToggleButton("2");
        setDim2.setMinWidth(35);
        setDim2.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setDim2.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setDim2.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                setDim2.setEffect(shadow);
        });
        setDim2.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                setDim2.setEffect(null);
        });
        ToggleButton setDim3 = new ToggleButton("3");
        setDim3.setMinWidth(35);
        setDim3.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setDim3.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setDim3.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                setDim3.setEffect(shadow);
        });
        setDim3.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                setDim3.setEffect(null);
        });
        HBox setDimension = new HBox(setDim1,setDim2,setDim3);
        setDimension.setSpacing(20);
        setDim1.setOnMouseClicked(f -> {
            setDim1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim3.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "1";
        });
        setDim2.setOnMouseClicked(f -> {
            setDim1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim3.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "2";
        });
        setDim3.setOnMouseClicked(f -> {
            setDim1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim3.setBackground(new Background(new BackgroundFill(
                Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "3";
        });

        this.vars[5] = "-"; // mmc              n/a
        this.vars[6] = "f"; // fixed(/spread)   n/a
        this.vars[7] = "-"; // (lattice/)free   n/a
        this.vars[8] = "-"; // save (off)       n/a

        /**
        * ...THEIR PLACEMENTS
        */
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
        GridPane.setHalignment(setDimension, HPos.CENTER);
        setDimension.setMinWidth(compwidth);
        setDimension.setMaxWidth(compwidth);
        asettelu.add(setDimension, 0, 5);

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
