
package randomwalkjava;

import com.sun.glass.ui.Screen;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.jetbrains.annotations.Contract;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.System.arraycopy;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for Real Time Rms
 */
@SuppressWarnings("SameReturnValue")
class SceneRealTimeRms extends Data {

    private double scalefactor;
    private double linewidth;
    private GraphicsContext piirturi;
    private boolean running;
    private boolean runtimeRunning;
    private long runs;
    private double rms_sum;
    private double rms_data;
    private double smallest;
    private double greatest;
    private Runtime runtime;
    private int exitVal;
    private ToggleButton setDim1;
    private ToggleButton setDim2;
    private ToggleButton setDim3;
    private TextField setNumParticles;
    private TextField setNumSteps;

    /**
     * main class gets vars via this
     * @return clone of vars array
     */
    String[] getVars() { return this.vars.clone(); }

    /**
     * initiating user variable array and other variables
     */
    SceneRealTimeRms() {
        super();
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

    /**
     * Real Time Rms
     * @param folder datafolder C:/RWDATA
     * @param executable Fortran executable walk.exe
     * @param piirturi GraphicsContext which draws the animation
     * @param scalefactor scaling is used in different particle amounts
     * @param linewidth width for lines
     * @param fxplot plotting element for graphs
     * @param rms_runs container for runs counting
     * @param newdata true if is a new run with new data
     * @param mincount x-axis min for normal distribution plot
     * @param maxcount x-axis max for normal distribution plot
     * @param standnorm true if standard normal distribution, false otherwise
     * @param measure width and height for drawing area
     */
    void refresh(File folder, String executable,
                 GraphicsContext piirturi, double scalefactor, double linewidth,
                 FXPlot fxplot, double[] rms_runs, boolean newdata,
                 double mincount, double maxcount, boolean standnorm, double measure) {

        int i = 0;
        int j = 0;
        int p = 0;

        this.setPiirturi(piirturi);
        this.setLinewidth(linewidth);
        this.setScalefactor(scalefactor);

        if (newdata) {
            this.setRuns(1);
            this.setRms_sum(0.0);
            this.setRms_data(0.0);
        }

        double centerX = measure/2.0;
        double centerY = measure/2.0;

        int num_part = parseInt(this.vars[0]);
        int num_steps = parseInt(this.vars[3]) + 1;
        double steps = parseDouble(this.vars[3]);
        int dim = parseInt(this.vars[4]);
        double expected = Math.sqrt(steps);
        this.setGreatest(expected);

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
            xAxis[x] = x;

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

        fxplot.setFrameVis();

        this.getPiirturi().setLineWidth(this.getLinewidth());

        String[] command;

        command = new String[]{"cmd","/c",executable,
            this.vars[0], this.vars[1], this.vars[2], this.vars[3],
            this.vars[4], this.vars[5], this.vars[6], this.vars[7],
            this.vars[8]};

        try {
            this.setRuntime(Runtime.getRuntime());
            runtimeStart();

            Process process = this.getRuntime().exec(command, null, folder);

            try (BufferedReader input = new BufferedReader(new InputStreamReader(
                process.getInputStream(), StandardCharsets.UTF_8))) {

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
                                        values[0][i] = Double.parseDouble(line.trim()) + centerX/this.getScalefactor();
                                    } catch (NumberFormatException e) {
                                        continue;
                                    }   values[1][i] = centerY;
                                    break;
                                case 2:
                                    {
                                        String[] valStr = line.split("(\\s+)");
                                        try {
                                            values[0][i] = Double.parseDouble(valStr[0].trim()) + centerX/this.getScalefactor();
                                            values[1][i] = Double.parseDouble(valStr[1].trim()) + centerX/this.getScalefactor();
                                        } catch (NumberFormatException e) {
                                            continue;
                                        }       break;
                                    }
                                case 3:
                                    {
                                        String[] valStr = line.split("(\\s+)");
                                        try {
                                            values[0][i] = Double.parseDouble(valStr[0].trim()) + centerX/this.getScalefactor();
                                            values[1][i] = Double.parseDouble(valStr[1].trim()) + centerX/this.getScalefactor();
                                            values[2][i] = Double.parseDouble(valStr[2].trim()) + 1.2*centerX/this.getScalefactor();
                                        } catch (NumberFormatException e) {
                                            continue;
                                        }       break;
                                    }
                                default:
                                    break;
                            }
                            /*
                            * RED SOURCE DOT
                            */
                            if ( j == 0 && i == 0 ) {
                                this.getPiirturi().setFill(Color.RED);
                                final double widthheight = expected * Math.log10(steps) / (Math.sqrt(steps) * dim);
                                switch (dim) {
                                    case 1:
                                        this.getPiirturi().fillRect(
                                            values[0][i], centerY,
                                            expected / ( 10.0 * Math.sqrt(Math.log10(steps)) ),
                                            Math.sqrt(centerY/2.0));
                                        break;
                                    case 2:
                                        this.getPiirturi().fillRect(
                                            values[0][i], values[1][i],
                                            widthheight,
                                            widthheight);
                                        break;
                                    case 3:
                                        this.getPiirturi().fillRect(
                                            values[0][i] + Math.cos(values[2][i]),
                                            values[1][i] + Math.sin(values[2][i]),
                                            widthheight,
                                            widthheight);
                                        break;
                                    default:
                                        break;
                                }
                                this.getPiirturi().setStroke(Color.YELLOW);
                            }

                            /*
                            * YELLOW LINES
                            */
                            if ( j > 0){
                                for (int k = 0; k < num_part; k++){
                                    if ( dim < 3 ) {
                                        this.getPiirturi().strokeLine(
                                            muistiX[k], muistiY[k], values[0][k], values[1][k]);
                                    } else {
                                        this.setLinewidth(10.0 * Math.log10(steps)
                                            / ( values[2][k] * scalefactor ));
                                        this.getPiirturi().setLineWidth(this.getLinewidth());
                                        this.getPiirturi().strokeLine(
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
                            /*
                            * PLOTS
                            */
                            try {
                                plotData[j] = Double.parseDouble(line.split("(\\s+)")[1].trim());
                            } catch (NumberFormatException e) {
                                continue;
                            }

                            if ( this.getRuns() == 1 ) {
                                sum_parts = 0.0;
                                this.setRms_data(0.0);
                            }
                                    
                            this.setRms_data(this.getRms_data() + plotData[j]);
                            sum[i] = this.getRms_data();
                            j++;
                            p++;

                            if ( p == num_part ) {
                                i = 0;
                                j = 0;
                                p = 0;
                                for (int f = 0; f < 10; f++)
                                    xAxis[f] = (double) (f + this.getRuns());

                                for (int m = 0; m < num_part; m++)
                                    sum_parts += sum[m];

                                double rrms = Math.sqrt((sum_parts/num_part) / this.getRuns());
                                this.setRms_sum(this.getRms_sum() + rrms);

                                if ( this.getRuns() < 11 ) {
                                    rms_runs[(int) this.getRuns() - 1] = rrms;
                                    yAxis = rms_runs.clone();
                                } else {
                                    arraycopy(rms_runs, 1, rms_runs, 0, 9);
                                    rms_runs[9] = rrms;
                                    yAxis = rms_runs.clone();
                                }

                                /*
                                * find greatest and smallest values for y-axis max limit
                                */
                                if ( rrms > this.getGreatest() ) {
                                    this.setGreatest(rrms);
                                    this.setSmallest(this.getGreatest());
                                }
                                if ( rrms > expected ) {
                                    this.setGreatest(rrms);
                                    this.setSmallest(expected);
                                }

                                Thread.sleep(100);
                                fxplot.setMinY(this.getSmallest() * 0.8 );
                                fxplot.setMaxY(this.getGreatest() * 1.2 );
                                fxplot.updateWData("R_rms", xAxis, yAxis);
                                fxplot.updateWData("sqrt(N)", xAxis, y2Axis);

                                double sigma2 = Math.pow(rrms - this.getRms_sum()/this.getRuns(),2.0);
                                double ynorm;
                                double mean;
                                
                                if (standnorm)
                                    mean = 0.0;
                                else
                                    mean = this.getRms_sum()/this.getRuns();
                                
                                double ymax = 1.0;
                                for (int h = 0; h < 1000; h++) {
                                    if (standnorm) {
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
                                fxplot.updateHData(xnormAxis, ynormAxis);
                            }
                    }
                }
                if (!failed)
                    this.setRuns(this.getRuns() + 1);

                this.setExitVal(process.waitFor());
                if (this.getExitVal() != 0) {
                    this.getRuntime().exit(getExitVal());
                    this.getRuntime().exit(this.getExitVal());
                }
            } 

        } catch (IOException | InterruptedException e) {
            this.getRuntime().gc();
            System.out.println(e.getMessage());
        }
    }

    /**
     * method for checking if user input in GUI is an integer
     * @param str GUI input string
     * @return true if input is an integer, false otherwise
     */
    private static boolean isNumInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * Create GUI for Real Time Rms
     * @return REAL TIME RMS SCENE
     */
    Parent getSceneReal(){
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(getPaneWidth());
        asettelu.setVgap(5);
        asettelu.setHgap(10);
        asettelu.setPadding(new Insets(0, 0, 0, 0));
        VBox valikko = new VBox();
        valikko.setPadding(new Insets(20, 10, 0, 0));
        valikko.setSpacing(10);

        DropShadow shadow = new DropShadow();

        /*
        * COMPONENTS...
        */
        Label labNumParticles = new Label("number of particles:");
        this.setNumParticles = new TextField("");
        this.setNumParticles.setOnKeyReleased(e -> {
            if (isNumInteger(this.setNumParticles.getText().trim())){
                if (this.setNumParticles.getText().trim().equals("0")){
                    this.setNumParticles.setText("1");
                    this.vars[0] = "1";
                } else {
                    this.vars[0] = this.setNumParticles.getText().trim();
                }
            } else
                this.vars[0] = "0";
        });

        this.vars[1] = "0.1"; // (diameter of particle)
        this.vars[2] = "0"; // (charge of particles)

        Label labNumSteps = new Label("number of steps:");
        this.setNumSteps = new TextField("");
        this.setNumSteps.setOnKeyReleased(e -> {
            if (isNumInteger(this.setNumSteps.getText().trim())){
                this.vars[3] = this.setNumSteps.getText().trim();
            } else
                this.vars[3] = "0";
        });

        Label labNumDimensions = new Label("dimension:");
        this.setDim1 = new ToggleButton("1");
        this.setDim1.setMinWidth(35);
        this.setDim1.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setDim1.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim1.setEffect(shadow));
        this.setDim1.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim1.setEffect(null));

        this.setDim2 = new ToggleButton("2");
        this.setDim2.setMinWidth(35);
        this.setDim2.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setDim2.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim2.setEffect(shadow));
        this.setDim2.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim2.setEffect(null));

        this.setDim3 = new ToggleButton("3");
        this.setDim3.setMinWidth(35);
        this.setDim3.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setDim3.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim3.setEffect(shadow));
        this.setDim3.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim3.setEffect(null));

        HBox setDimension = new HBox(this.setDim1,this.setDim2,this.setDim3);
        setDimension.setSpacing(20);
        this.setDim1.setOnMouseClicked(f -> {
            this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "1";
        });
        this.setDim2.setOnMouseClicked(f -> {
            this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "2";
        });
        this.setDim3.setOnMouseClicked(f -> {
            this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "3";
        });

        this.vars[5] = "-"; // mmc              n/a
        this.vars[6] = "f"; // fixed(/spread)   n/a
        this.vars[7] = "-"; // (lattice/)free   n/a
        this.vars[8] = "-"; // save (off)       n/a

        /*
        * ...THEIR PLACEMENTS
        */
        GridPane.setHalignment(labNumParticles, HPos.LEFT);
        asettelu.add(labNumParticles, 0, 0);
        GridPane.setHalignment(this.setNumParticles, HPos.CENTER);
        this.setNumParticles.setMinWidth(getCompwidth());
        this.setNumParticles.setMaxWidth(getCompwidth());
        asettelu.add(this.setNumParticles, 0, 1);

        GridPane.setHalignment(labNumSteps, HPos.LEFT);
        asettelu.add(labNumSteps, 0, 2);
        GridPane.setHalignment(this.setNumSteps, HPos.CENTER);
        this.setNumSteps.setMinWidth(getCompwidth());
        this.setNumSteps.setMaxWidth(getCompwidth());
        asettelu.add(this.setNumSteps, 0, 3);
        
        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 4);
        GridPane.setHalignment(setDimension, HPos.CENTER);
        setDimension.setMinWidth(getCompwidth());
        setDimension.setMaxWidth(getCompwidth());
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

    /**
     * the setRunning to set to true
     */
    void start() {
        this.setRunning(true);
    }

    /**
     * the setRunning to set to false
     */
    void stop() {
        this.setRunning(false);
    }

    /**
     * @return running
     */
    boolean isRunning() {
        return running;
    }

    /**
     * the setRunning to set
     */
    private void runtimeStart() {
        this.setRuntimeRunning(true);
    }

    /**
     * @return isRunning
     */
    boolean runtimeIsRunning() {
        return runtimeRunning;
    }

    void stopRuntime() {
        this.setRuntimeRunning(false);
        this.runtime.exit(this.getExitVal());
    }

    /**
     * @return the compwidth
     */
    @Contract(pure = true)
    private int getCompwidth() { return 150 / (int) Screen.getMainScreen().getPlatformScaleX(); }

    /**
     * @return the paneWidth
     */
    @Contract(pure = true)
    private int getPaneWidth() { return 200 / (int) Screen.getMainScreen().getPlatformScaleX(); }

    /**
     * @return the scalefactor
     */
    @Contract(pure = true)
    private double getScalefactor() {
        return scalefactor;
    }

    /**
     * @param scalefactor the scalefactor to set
     */
    private void setScalefactor(double scalefactor) {
        this.scalefactor = scalefactor;
    }

    /**
     * @return the linewidth
     */
    @Contract(pure = true)
    private double getLinewidth() {
        return linewidth;
    }

    /**
     * @param linewidth the linewidth to set
     */
    private void setLinewidth(double linewidth) {
        this.linewidth = linewidth;
    }

    /**
     * @return the piirturi
     */
    @Contract(pure = true)
    private GraphicsContext getPiirturi() {
        return piirturi;
    }

    /**
     * @param piirturi the piirturi to set
     */
    private void setPiirturi(GraphicsContext piirturi) {
        this.piirturi = piirturi;
    }

    /**
     * @param running the running to set
     */
    private void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * @param running the running to set
     */
    private void setRuntimeRunning(boolean running) {
        this.runtimeRunning = running;
    }

    /**
     * @return the runs
     */
    @Contract(pure = true)
    private long getRuns() {
        return runs;
    }

    /**
     * @param runs the runs to set
     */
    private void setRuns(long runs) {
        this.runs = runs;
    }

    /**
     * @return the rms_sum
     */
    @Contract(pure = true)
    private double getRms_sum() {
        return rms_sum;
    }

    /**
     * @param rms_sum the rms_sum to set
     */
    private void setRms_sum(double rms_sum) {
        this.rms_sum = rms_sum;
    }

    /**
     * @return the rms_data
     */
    @Contract(pure = true)
    private double getRms_data() {
        return rms_data;
    }

    /**
     * @param rms_data the rms_data to set
     */
    private void setRms_data(double rms_data) {
        this.rms_data = rms_data;
    }

    /**
     * @return the smallest
     */
    @Contract(pure = true)
    private double getSmallest() {
        return smallest;
    }

    /**
     * @param smallest the smallest to set
     */
    private void setSmallest(double smallest) {
        this.smallest = smallest;
    }

    /**
     * @return the greatest
     */
    @Contract(pure = true)
    private double getGreatest() {
        return greatest;
    }

    /**
     * @param greatest the greatest to set
     */
    private void setGreatest(double greatest) {
        this.greatest = greatest;
    }

    /**
     * @return the runtime
     */
    @Contract(pure = true)
    private Runtime getRuntime() {
        return runtime;
    }

    /**
     * @param runtime the runtime to set
     */
    private void setRuntime(Runtime runtime) {
        this.runtime = runtime;
    }

    /**
     * @return the exitVal
     */
    @Contract(pure = true)
    private int getExitVal() {
        return exitVal;
    }

    /**
     * @param exitVal the exitVal to set
     */
    private void setExitVal(int exitVal) {
        this.exitVal = exitVal;
    }

}
