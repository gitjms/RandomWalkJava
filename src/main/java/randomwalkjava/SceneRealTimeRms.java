
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
import java.util.*;

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
    private int num_parts;
    private int num_steps;
    private double steps;
    private int dim;
    private double expected;
    private double[] muistiX;
    private double[] muistiY;
    private double[][] values;
    private double[] plotdata;
    private double[] xAxis;
    private double[] yAxis;
    private double[] y2Axis;
    private double[] xnormAxis;
    private double[] ynormAxis;
    private double[] yotherAxis;
    private double[] sum;
    private long runs;
    private double rms_sum;
    private double rms_data;
    private double sum_parts;
    private double smallest;
    private double greatest;
    private double greatestdiff;
    private double centerX;
    private double centerY;
    private double measure;
    private Runtime runtime;
    private int exitVal;
    private double mincount;
    private double maxcount;
    private double[] rms_runs;
    private double[] rms_norm;
    private ToggleButton setDim1;
    private ToggleButton setDim2;
    private ToggleButton setDim3;
    private ToggleButton setStandPlot;
    private ToggleButton setDiffPlot;
    private boolean standPlot;
    private boolean diffPlot;
    private TextField setNumParticles;
    private TextField setNumSteps;
    private VBox setPlotChoice;
    private Map<String,double[]> others;

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
    }

    /**
     * Real Time Rms
     * @param folder datafolder C:/RWDATA
     * @param executable Fortran executable walk.exe
     * @param piirturi GraphicsContext which draws the animation
     * @param scalefactor scaling is used in different particle amounts
     * @param linewidth width for lines
     * @param fxplot plotting element for graphs
     * @param newdata true if is a new run with new data
     * @param measure width and height for drawing area
     */
    void refresh(File folder, String executable, GraphicsContext piirturi, double scalefactor,
                 double linewidth, FXPlot fxplot, boolean newdata, double measure) {

        int i = 0;
        int j = 0;
        int p = 0;

        this.setPiirturi(piirturi);
        this.setLinewidth(linewidth);
        this.setScalefactor(scalefactor);
        this.setPlotChoice.setDisable(true);
        this.setRms_sum(0.0);
        this.setRms_data(0.0);
        this.setSmallest();

        if (newdata) {
            this.setRuns(1);
            this.setRms_runs();
            this.setRms_norm();
            this.setMeasure(measure);
            this.numParts(parseInt(this.vars[0]));
            this.numSteps(parseInt(this.vars[3]) + 1);
            this.setSteps(parseDouble(this.vars[3]));
            this.dim(parseInt(this.vars[4]));
            this.setExpected(Math.sqrt(this.getSteps()));
            this.setGreatest(this.getExpected());
            this.setGreatestDiff(0.0);
            String standdiff = "";
            this.setOthers(new HashMap<>());
            this.getOthers().clear();

            if ( this.isStandPlot() ){
                this.setMincount(-4.0);
                this.setMaxcount(4.0);
            } else if ( this.isDiffPlot() ){
                this.setMincount(-(Math.sqrt(this.getSteps())*2));
                this.setMaxcount(Math.sqrt(this.getSteps())*2);
            }
            fxplot.setHDiffMaxY(0.0);

            if (this.isStandPlot()) standdiff = "stand";
            else if (this.isDiffPlot()) standdiff = "diff";

            fxplot.setWData(this.getRms_runs(), this.getRms_runs(), this.getExpected());
            fxplot.setHData(this.getRms_norm(), this.getRms_norm(), this.getMincount(), this.getMaxcount(), standdiff);

            this.setMuistiX(new double[this.getNumParts()]);
            this.setMuistiY(new double[this.getNumParts()]);

            if( this.getDim() < 3 )
                this.setValues(new double[2][this.getNumParts()]);
            else
                this.setValues(new double[3][this.getNumParts()]);

            this.setPlotData(new double[this.getNumParts()]);

            this.setXAxis(new double[10]);
            for (int x = 0; x < 10; x++)
                this.getXAxis()[x] = x;

            this.setYAxis(new double[10]);
            Arrays.fill(this.getYAxis(), 0.0);

            this.setY2Axis(new double[10]);
            Arrays.fill(this.getY2Axis(), this.getExpected());

            this.setXnormAxis(new double[1000]);
            double skip = (this.getMaxcount()-this.getMincount())/1000.0;
            this.getXnormAxis()[0] = this.getMincount() + skip/2.0;
            for (int x = 0; x < 999; x++) {
                this.getXnormAxis()[x+1] = this.getXnormAxis()[x] + skip;
            }

            this.setYnormAxis(new double[1000]);
            Arrays.fill(this.getYnormAxis(), 0.0);
            this.setYotherAxis(new double[1000]);
            Arrays.fill(this.getYotherAxis(), 0.0);

            this.setSum(new double[this.getNumParts()]);
            this.setSumParts(0.0);

            this.setCenterX(this.getMeasure()/2.0);
            this.setCenterY(this.getMeasure()/2.0);

            fxplot.setFrameVis();
        }

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

                while ((line = input.readLine()) != null) {
                    if (line.trim().startsWith("S") || line.isEmpty()) {
                        continue;
                    }
                    if (!line.substring(0,1).matches("([0-9]|-|\\+)"))
                        continue;
                    if ( !(line.trim().split("(\\s+)")[0].trim().equals("+")) ) {
                        switch (this.getDim()) {
                            case 1: {
                                try {
                                    this.getValues()[0][i] = Double.parseDouble(line.trim()) + this.getCenterX() / this.getScalefactor();
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                                this.getValues()[1][i] = this.getCenterY();
                                break;
                            }
                             case 2: {
                                 String[] valStr = line.split("(\\s+)");
                                 try {
                                     this.getValues()[0][i] = Double.parseDouble(valStr[0].trim()) + this.getCenterX()/this.getScalefactor();
                                     this.getValues()[1][i] = Double.parseDouble(valStr[1].trim()) + this.getCenterX()/this.getScalefactor();
                                 } catch (NumberFormatException e) {
                                     continue;
                                 }
                                 break;
                             }
                             case 3: {
                                 String[] valStr = line.split("(\\s+)");
                                 try {
                                     this.getValues()[0][i] = Double.parseDouble(valStr[0].trim()) + this.getCenterX()/this.getScalefactor();
                                     this.getValues()[1][i] = Double.parseDouble(valStr[1].trim()) + this.getCenterX()/this.getScalefactor();
                                     this.getValues()[2][i] = Double.parseDouble(valStr[2].trim()) + 1.2*this.getCenterX()/this.getScalefactor();
                                 } catch (NumberFormatException e) {
                                     continue;
                                 }
                                 break;
                             }
                             default:
                                 break;
                        }
                        /*
                           * RED SOURCE DOT
                           */
                        if ( j == 0 && i == 0 ) {
                            this.getPiirturi().setFill(Color.RED);
                            final double widthheight = this.getExpected() * Math.log10(this.getSteps()) / (Math.sqrt(this.getSteps()) * this.getDim());
                            if (this.getDim() == 1) {
                                this.getPiirturi().fillRect(
                                    this.getCenterX() / this.getScalefactor(), this.getCenterY(),
                                    this.getExpected() / (10.0 * Math.sqrt(Math.log10(this.getSteps()))),
                                    Math.sqrt(this.getCenterY() / 2.0));
                            } else {
                                this.getPiirturi().fillRect(
                                    this.getCenterX()/this.getScalefactor(), this.getCenterY()/this.getScalefactor(),
                                    widthheight, widthheight);
                            }
                            this.getPiirturi().setStroke(Color.YELLOW);
                        }

                        /*
                         * YELLOW LINES
                         */
                        if ( j > 0){
                            for (int k = 0; k < this.getNumParts(); k++){
                                if ( this.getDim() < 3 ) {
                                    this.getPiirturi().strokeLine(
                                        this.getMuistiX()[k], this.getMuistiY()[k], this.getValues()[0][k], this.getValues()[1][k]);
                                } else {
                                    this.setLinewidth(10.0 * Math.log10(this.getSteps())
                                        / ( this.getValues()[2][k] * this.getScalefactor() ));
                                    this.getPiirturi().setLineWidth(this.getLinewidth());
                                    this.getPiirturi().strokeLine(
                                        this.getMuistiX()[k], this.getMuistiY()[k],
                                        this.getValues()[0][k]
                                            + Math.cos(this.getValues()[2][k]),
                                        this.getValues()[1][k]
                                            + Math.sin(this.getValues()[2][k]));
                                }
                            }
                        }
                        if ( this.getDim() < 3 ) {
                            this.getMuistiX()[i] = this.getValues()[0][i];
                            this.getMuistiY()[i] = this.getValues()[1][i];
                        } else {
                            this.getMuistiX()[i] = this.getValues()[0][i]
                                + Math.cos(this.getValues()[2][i]);
                            this.getMuistiY()[i] = this.getValues()[1][i]
                                + Math.sin(this.getValues()[2][i]);
                        }

                        i++;

                        if ( i == this.getNumParts() ){
                            i = 0;
                            j++;
                        }

                        if ( j == this.getNumSteps() ) {
                            i = 0;
                            j = 0;
                        }

                    } else {
                        /*
                         * PLOTS
                         */
                        double dataVal;
                        try {
                            dataVal = Double.parseDouble(line.split("(\\s+)")[1].trim());
                            this.getPlotData()[j] = dataVal;
                        } catch (NumberFormatException e) {
                            continue;
                        }

                        if ( this.getRuns() == 1 ) {
                            this.setSumParts(0.0);
                            this.setRms_data(0.0);
                        }

                        this.setRms_data(this.getRms_data() + this.getPlotData()[j]);
                        this.getSum()[i] = this.getRms_data();
                        j++;
                        p++;

                        if ( p == this.getNumParts() ) {
                            i = 0;
                            j = 0;
                            p = 0;
                            for (int f = 0; f < 10; f++)
                                this.getXAxis()[f] = (double) (f + this.getRuns());

                            for (int m = 0; m < this.getNumParts(); m++)
                                this.setSumParts(this.getSumParts() + this.getSum()[m]);

                            double dist_rms = this.getSumParts()/this.getNumParts();
                            double rrms = Math.sqrt((this.getSumParts()/this.getNumParts()) / this.getRuns());
                            this.setRms_sum(this.getRms_sum() + rrms);

                            if ( this.getRuns() < 11 ) {
                                this.getRms_runs()[(int) this.getRuns() - 1] = rrms;
                            } else {
                                arraycopy(this.getRms_runs(), 1, this.getRms_runs(), 0, 9);
                                this.getRms_runs()[9] = rrms;
                            }
                            this.setYAxis(this.getRms_runs().clone());

                            /*
                             * find greatest value for y-axis max limit
                             */
                            if ( rrms > this.getGreatest() || rrms > this.getExpected() ) this.setGreatest(rrms);

                            fxplot.setWMinY(this.getSmallest());
                            fxplot.setWMaxY(this.getGreatest() * 2.0 );
                            fxplot.updateWData("R_rms", this.getXAxis(), this.getYAxis());
                            fxplot.updateWData("sqrt(steps)", this.getXAxis(), this.getY2Axis());

                            double sigma = Math.pow(rrms - this.getRms_sum() / this.getRuns(), 2.0)/this.getSteps();

                            double ynorm = 0.0;
                            //double mean;
                            double diff;

                            //mean = this.getRms_sum() / this.getRuns();

                            for (int h = 0; h < 1000; h++) {
                                if (this.isStandPlot()) {
                                    ynorm = Math.exp(-Math.pow(this.getXnormAxis()[h], 2.0) / (2.0 * sigma));
                                } else if (this.isDiffPlot()) {
                                    if (this.getDim() == 1) {
                                        diff = dist_rms / (2.0 * this.getRuns());
                                        ynorm = //this.getNumParts() / (2.0 * Math.sqrt(Math.PI * diff * this.getRuns()))
                                            Math.exp(-Math.pow(this.getXnormAxis()[h], 2.0) / (2.0 * diff * this.getRuns()));
                                    } else if (this.getDim() == 2) {
                                        diff = Math.pow(rrms, 2.0) / (4.0 * this.getRuns());
                                        ynorm = 1.0 / (2.0 * Math.sqrt(Math.PI * diff * this.getRuns()))
                                            * Math.exp(-Math.pow(this.getXnormAxis()[h], 2.0) / (4.0 * diff * this.getRuns()));
                                    } else if (this.getDim() == 3) {
                                        diff = dist_rms / (6.0 * this.getRuns());
                                        ynorm = //this.getNumParts() / (2.0 * Math.sqrt(Math.PI * diff * this.getRuns()))
                                            Math.exp(-Math.pow(this.getXnormAxis()[h], 2.0) / (6.0 * diff * this.getRuns()));
                                    }
                                    if ( ynorm > this.getGreatestDiff() ) this.setGreatestDiff(ynorm);
                                    fxplot.setHDiffMaxY(this.getGreatestDiff());
                                }
                                if (this. isDiffPlot()) {
                                    this.getYnormAxis()[h] = ynorm;
                                    this.getYotherAxis()[h] = ynorm;
                                } else if (this.isStandPlot())
                                    this.getYnormAxis()[h] = ynorm;
                            }
                        }
                    }
                }
                if ( this. isDiffPlot() && this.getRuns() < 6 ) {
                    if (!this.getOthers().containsKey(String.valueOf(this.getRuns()))) {
                        this.getOthers().put(String.valueOf(this.getRuns()), this.getYotherAxis().clone());
                        for (Map.Entry<String, double[]> longEntry : this.getOthers().entrySet()) {
                            fxplot.updateHDiffData(String.valueOf(((Map.Entry) longEntry).getKey()), this.getXnormAxis(), (double[]) ((Map.Entry) longEntry).getValue());
                        }
                    }
                }
                if (this.isDiffPlot()) {
                    fxplot.updateHDiffData("diff", this.getXnormAxis(), this.getYotherAxis());
                    for (Map.Entry<String, double[]> longEntry : this.getOthers().entrySet()) {
                        fxplot.updateHDiffData(String.valueOf(((Map.Entry) longEntry).getKey()), this.getXnormAxis(), (double[]) ((Map.Entry) longEntry).getValue());
                    }
                } else if (this.isStandPlot()) {
                    fxplot.updateHData(this.getXnormAxis(), this.getYnormAxis());
                }
                this.setRuns(this.getRuns() + 1);

                this.setExitVal(process.waitFor());
                if (this.getExitVal() != 0) {
                    this.setPlotChoice.setDisable(false);
                    this.getRuntime().exit(getExitVal());
                    this.getRuntime().exit(this.getExitVal());
                }
            } 

        } catch (IOException | InterruptedException e) {
            this.getRuntime().gc();
            System.out.println(e.getMessage());
        }

        this.setPlotChoice.setDisable(false);
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

        Label labPlotChoice = new Label("Plot type:");
        this.setStandPlot = new ToggleButton("STANDARD");
        this.setStandPlot.setMinWidth(this.getCompwidth());
        this.setStandPlot.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setStandPlot.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setStandPlot.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setStandPlot.setEffect(shadow));
        this.setStandPlot.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setStandPlot.setEffect(null));

        this.setDiffPlot = new ToggleButton("DIFFUSION");
        this.setDiffPlot.setMinWidth(this.getCompwidth());
        this.setDiffPlot.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setDiffPlot.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setDiffPlot.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDiffPlot.setEffect(shadow));
        this.setDiffPlot.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDiffPlot.setEffect(null));

        this.setPlotChoice = new VBox(this.setStandPlot,this.setDiffPlot);
        this.setPlotChoice.setSpacing(10);
        this.setStandPlot.setOnMouseClicked(f -> {
            this.setStandPlot.setBackground(new Background(new BackgroundFill(Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDiffPlot.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.standPlot = true; this.diffPlot = false;
        });
        this.setDiffPlot.setOnMouseClicked(f -> {
            this.setStandPlot.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDiffPlot.setBackground(new Background(new BackgroundFill(Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
            this.diffPlot = true; this.standPlot = false;
        });

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

        final Pane empty1 = new Pane();
        GridPane.setHalignment(empty1, HPos.CENTER);
        asettelu.add(empty1, 0, 6, 2, 1);

        final Pane empty2 = new Pane();
        GridPane.setHalignment(empty2, HPos.CENTER);
        asettelu.add(empty2, 0, 7, 2, 1);

        GridPane.setHalignment(labPlotChoice, HPos.LEFT);
        asettelu.add(labPlotChoice, 0, 8);
        GridPane.setHalignment(this.setPlotChoice, HPos.CENTER);
        this.setPlotChoice.setMinWidth(getCompwidth());
        this.setPlotChoice.setMaxWidth(getCompwidth());
        asettelu.add(this.setPlotChoice, 0, 9);

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 10, 2, 1);

        final Pane empty3 = new Pane();
        GridPane.setHalignment(empty3, HPos.CENTER);
        asettelu.add(empty3, 0, 11, 2, 1);

        final Pane empty4 = new Pane();
        GridPane.setHalignment(empty4, HPos.CENTER);
        asettelu.add(empty4, 0, 12, 2, 1);

        return asettelu;
    }

    /**
     * the setRunning to set to true
     */
    void start() { this.setRunning(true); }

    /**
     * the setRunning to set to false
     */
    void stop() { this.setRunning(false); }

    /**
     * @return running
     */
    boolean isRunning() { return this.running; }

    /**
     * the setRunning to set
     */
    private void runtimeStart() { this.setRuntimeRunning(true); }

    /**
     * @return isRunning
     */
    boolean runtimeIsRunning() { return this.runtimeRunning; }

    void stopRuntime() {
        this.setRuntimeRunning(false);
        this.runtime.exit(this.getExitVal());
    }

    /**
     * @return the compwidth
     */
    @Contract(pure = true)
    private double getCompwidth() { return 150.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the paneWidth
     */
    @Contract(pure = true)
    private double getPaneWidth() { return 200.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the scalefactor
     */
    @Contract(pure = true)
    private double getScalefactor() { return this.scalefactor; }

    /**
     * @param scalefactor the scalefactor to set
     */
    private void setScalefactor(double scalefactor) { this.scalefactor = scalefactor; }

    /**
     * @return the linewidth
     */
    @Contract(pure = true)
    private double getLinewidth() { return this.linewidth; }

    /**
     * @param linewidth the linewidth to set
     */
    private void setLinewidth(double linewidth) { this.linewidth = linewidth; }

    /**
     * @return the piirturi
     */
    @Contract(pure = true)
    private GraphicsContext getPiirturi() { return this.piirturi; }

    /**
     * @param piirturi the piirturi to set
     */
    private void setPiirturi(GraphicsContext piirturi) { this.piirturi = piirturi; }

    /**
     * @param running the running to set
     */
    private void setRunning(boolean running) { this.running = running; }

    /**
     * @param running the running to set
     */
    private void setRuntimeRunning(boolean running) { this.runtimeRunning = running; }

    /**
     * @return the runs
     */
    @Contract(pure = true)
    private long getRuns() { return this.runs; }

    /**
     * @param runs the runs to set
     */
    private void setRuns(long runs) { this.runs = runs; }

    /**
     * @return the rms_sum
     */
    @Contract(pure = true)
    private double getRms_sum() { return this.rms_sum; }

    /**
     * @param rms_sum the rms_sum to set
     */
    private void setRms_sum(double rms_sum) { this.rms_sum = rms_sum; }

    /**
     * @return the rms_data
     */
    @Contract(pure = true)
    private double getRms_data() { return this.rms_data; }

    /**
     * @param rms_data the rms_data to set
     */
    private void setRms_data(double rms_data) { this.rms_data = rms_data; }

    /**
     * @return the rms_runs
     */
    @Contract(pure = true)
    private double[] getRms_runs() { return this.rms_runs; }

    /**
     * rms_runs to fill with zeros
     */
    private void setRms_runs() {
        this.rms_runs = new double[10];
        Arrays.fill(this.rms_runs, 0.0);
    }

    /**
     * @return the rms_norm
     */
    @Contract(pure = true)
    private double[] getRms_norm() { return this.rms_norm; }

    /**
     * rms_norm to fill with zeros
     */
    private void setRms_norm() {
        this.rms_norm = new double[10];
        Arrays.fill(this.rms_norm, 0.0);
    }

    /**
     * @param numParts amount of particles to set
     */
    private void numParts(int numParts) { this.num_parts = numParts; }

    /**
     * @return the amount of particles
     */
    @Contract(pure = true)
    private int getNumParts() { return this. num_parts; }

    /**
     * @param numSteps amount of steps plus 1 for array to set
     */
    private void numSteps(int numSteps) { this.num_steps = numSteps; }

    /**
     * @return amount of steps plus 1 for array
     */
    @Contract(pure = true)
    private int getNumSteps() { return this.num_steps; }

    /**
     * @param steps amount of steps to set
     */
    private void setSteps(double steps) { this.steps = steps; }

    /**
     * @return amount of steps
     */
    @Contract(pure = true)
    private double getSteps() { return this.steps; }

    /**
     * @param dim dimension of field to set
     */
    private void dim(int dim) { this.dim = dim; }

    /**
     * @return dimension of field
     */
    @Contract(pure = true)
    private int getDim() { return this.dim; }

    /**
     * @param muistiX x-data values to store for later use
     */
    private void setMuistiX(double[] muistiX) { this.muistiX = muistiX; }

    /**
     * @return x-data values
     */
    @Contract(pure = true)
    private double[] getMuistiX() { return this.muistiX; }

    /**
     * @param muistiY y-data values to store for later use
     */
    private void setMuistiY(double[] muistiY) { this.muistiY = muistiY; }

    /**
     * @return y-data values
     */
    @Contract(pure = true)
    private double[] getMuistiY() { return this.muistiY; }

    /**
     * @param values data array from InputStream to set
     */
    private void setValues(double[][] values) { this.values = values; }

    /**
     * @return data array from InputStream
     */
    @Contract(pure = true)
    private double[][] getValues() { return this.values; }

    /**
     * @param plotdata data for plotting to set
     */
    private void setPlotData(double[] plotdata) { this.plotdata = plotdata; }

    /**
     * @return data for plotting
     */
    @Contract(pure = true)
    private double[] getPlotData() { return this.plotdata; }

    /**
     * @param xAxis x-axis data array for walk plot to set
     */
    private void setXAxis(double[] xAxis) { this.xAxis = xAxis; }

    /**
     * @return x-axis data array for walk plot
     */
    @Contract(pure = true)
    private double[] getXAxis() { return this.xAxis; }

    /**
     * @param yAxis y-axis data array for walk plot to set
     */
    private void setYAxis(double[] yAxis) { this.yAxis = yAxis; }

    /**
     * @return y-axis data array for walk plot
     */
    @Contract(pure = true)
    private double[] getYAxis() { return this.yAxis; }

    /**
     * @param y2Axis y-axis data array for normal distribution plots to set
     */
    private void setY2Axis(double[] y2Axis) { this.y2Axis = y2Axis; }

    /**
     * @return y-axis data array for normal distribution plots
     */
    @Contract(pure = true)
    private double[] getY2Axis() { return this.y2Axis; }

    /**
     * @param yotherAxis y-axis data array for normal distribution plots to set
     */
    private void setYotherAxis(double[] yotherAxis) { this.yotherAxis = yotherAxis; }

    /**
     * @return y-axis data array for normal distribution plots
     */
    @Contract(pure = true)
    private double[] getYotherAxis() { return this.yotherAxis; }

    /**
     * @param xnormAxis x-axis data array for normal distribution plots to set
     */
    private void setXnormAxis(double[] xnormAxis) { this.xnormAxis = xnormAxis; }

    /**
     * @return x-axis data array for normal distribution plots
     */
    @Contract(pure = true)
    private double[] getXnormAxis() { return this.xnormAxis; }

    /**
     * @param ynormAxis y-axis data array for normal distribution plots to set
     */
    private void setYnormAxis(double[] ynormAxis) { this.ynormAxis = ynormAxis; }

    /**
     * @return y-axis data array for normal distribution plots
     */
    @Contract(pure = true)
    private double[] getYnormAxis() { return this.ynormAxis; }

    /**
     * @param expected expected value to set
     */
    private void setExpected(double expected) { this.expected = expected; }

    /**
     * @return expected value
     */
    @Contract(pure = true)
    private double getExpected() { return this.expected; }

    /**
     * @param sum array for rms data to set
     */
    private void setSum(double[] sum) { this.sum = sum; }

    /**
     * @return array for rms data
     */
    @Contract(pure = true)
    private double[] getSum() { return this.sum; }

    /**
     * @param sumParts array for rms sum data to set
     */
    private void setSumParts(double sumParts) { this.sum_parts = sumParts; }

    /**
     * @return array for rms sum data
     */
    @Contract(pure = true)
    private double getSumParts() { return this.sum_parts; }

    /**
     * @param centerX drawing area center in x-axis to set
     */
    private void setCenterX(double centerX) { this.centerX = centerX; }

    /**
     * @return drawing area center in x-axis
     */
    @Contract(pure = true)
    private double getCenterX() { return this.centerX; }

    /**
     * @param centerY drawing area center in y-axis to set
     */
    private void setCenterY(double centerY) { this.centerY = centerY; }

    /**
     * @return drawing area center in y-axis
     */
    @Contract(pure = true)
    private double getCenterY() { return this.centerY; }

    /**
     * @param measure particle area limit from Fortran to set
     */
    private void setMeasure(double measure) { this.measure = measure; }

    /**
     * @return particle area limit from Fortran
     */
    @Contract(pure = true)
    private double getMeasure() { return this.measure; }

    /**
     * @return the mincount
     */
    @Contract(pure = true)
    private double getMincount() { return this.mincount; }

    /**
     * @param mincount the mincount to set
     */
    private void setMincount(double mincount) { this.mincount = mincount; }

    /**
     * @return the maxcount
     */
    @Contract(pure = true)
    private double getMaxcount() { return this.maxcount; }

    /**
     * @param maxcount the maxcount to set
     */
    private void setMaxcount(double maxcount) { this.maxcount = maxcount; }

    /**
     * @return whether to plot standard normal distribution
     */
    @Contract(pure = true)
    private boolean isStandPlot() { return this.standPlot; }

    /**
     * @return whether to plot diffusion normal distribution
     */
    @Contract(pure = true)
    private boolean isDiffPlot() { return this.diffPlot; }

    /**
     * @return the smallest
     */
    @Contract(pure = true)
    private double getSmallest() { return this.smallest; }

    /**
     */
    private void setSmallest() { this.smallest = 0.0; }

    /**
     * @return the greatest
     */
    @Contract(pure = true)
    private double getGreatest() { return this.greatest; }

    /**
     * @param greatest the greatest to set
     */
    private void setGreatest(double greatest) { this.greatest = greatest; }

    /**
     * @return the greatestdiff
     */
    @Contract(pure = true)
    private double getGreatestDiff() { return this.greatestdiff; }

    /**
     * @param greatestdiff the greatestdiff to set
     */
    private void setGreatestDiff(double greatestdiff) { this.greatestdiff = greatestdiff; }

    /**
     * @return the runtime
     */
    @Contract(pure = true)
    private Runtime getRuntime() { return this.runtime; }

    /**
     * @param runtime the runtime to set
     */
    private void setRuntime(Runtime runtime) { this.runtime = runtime; }

    /**
     * @return the exitVal
     */
    @Contract(pure = true)
    private int getExitVal() { return this.exitVal; }

    /**
     * @param exitVal the exitVal to set
     */
    private void setExitVal(int exitVal) { this.exitVal = exitVal; }

    /**
     *
     * @param others the others to set
     */
    private void setOthers( Map<String,double[]> others ) { this.others = others; }

    /**
     * @return the others
     */
    @Contract(pure = true)
    private Map<String,double[]> getOthers() { return others; }
}
