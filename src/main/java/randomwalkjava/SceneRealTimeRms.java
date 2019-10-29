
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
import org.jetbrains.annotations.NotNull;

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

    private String language;
    private double scalefactor;
    private double linewidth;
    private FXPlot fxplot;
    private GraphicsContext piirturi;
    private boolean running;
    private boolean runtimeRunning;
    private int num_parts;
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
    private double[] histoXAxis;
    private double[] histoYAxis;
    private double[] sum;
    private List<Double> saw_lengths;
    private long runs;
    private int init_part;
    private double bigg_dist;
    private double rms_sum;
    private double rms_data;
    private double sum_parts;
    private double smallest;
    private double greatest;
    private double greatestdn;
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
    private VBox plotChoice;
    private HBox dim_choice;
    private Map<String,double[]> others;
    private double sigSeed;

    /**
     * main class gets vars via this
     * @return clone of vars array
     */
    String[] getVars() { return this.vars.clone(); }

    /**
     * initiating user variable array and other variables
     */
    SceneRealTimeRms(String language){
        super();
        this.setLanguage(language);
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
     * @param folder datafolder "C:/RWDATA"
     * @param executable Fortran executable "walk.exe"
     * @param piirturi GraphicsContext which draws the animation
     * @param scalefactor scaling is used in different particle amounts
     * @param linewidth width for lines
     * @param newdata true if is a new run with new data
     * @param measure width and height for drawing area
     */
    void refresh(File folder, String executable, GraphicsContext piirturi, double scalefactor,
                 double linewidth, boolean newdata, double measure) {

        int i = 0;
        int j = 0;
        int p = 0;

        this.setPiirturi(piirturi);
        this.setLinewidth(linewidth);
        this.setScalefactor(scalefactor);
        this.setRms_sum(0.0);
        this.setRms_data(0.0);
        this.setSmallest();
        this.setOthers(new HashMap<>());
        this.getOthers().clear();

        if (newdata) {
            this.setBiggDist(0.0);
            this.setRuns(1);
            this.setSigSeed(0.0);
            this.setInitPart(0);
            this.setRms_runs();
            this.setRms_norm();
            this.setMeasure(measure);
            this.numParts(parseInt(this.vars[0]));
            this.setSteps(parseDouble(this.vars[3]));
            this.dim(parseInt(this.vars[4]));
            this.setExpected(Math.sqrt(this.getSteps()));
            this.setGreatest(this.getExpected() + Math.log10(this.getSteps()));
            String standdiff = "";
            this.setOthers(new HashMap<>());
            this.getOthers().clear();

            if ( this.isStandPlot() ){
                /*this.setMincount(-3.0);
                this.setMaxcount(3.0);*/
                this.setMincount(-this.getExpected());
                this.setMaxcount(this.getExpected());
            } else if ( this.isDiffPlot() ){
                /*this.setMincount(-(7.0+65.0*(1.0-Math.exp(-0.001*this.getSteps()))));
                this.setMaxcount(7.0+65.0*(1.0-Math.exp(-0.001*this.getSteps())));*/
                this.setMincount(-this.getExpected());
                this.setMaxcount(this.getExpected());
            }

            if (this.isStandPlot()) standdiff = "stand";
            else if (this.isDiffPlot()) standdiff = "diff";

            this.setGreatestDN(0.0);

            this.setMuistiX(new double[this.getNumParts()]);
            this.setMuistiY(new double[this.getNumParts()]);

            if( this.getDim() < 3 ) this.setValues(new double[2][this.getNumParts()]);
            else this.setValues(new double[3][this.getNumParts()]);

            this.setPlotData(new double[this.getNumParts()]);

            this.setXAxis(new double[10]);
            for (int x = 0; x < 10; x++) this.getXAxis()[x] = x;

            this.setYAxis(new double[10]);
            Arrays.fill(this.getYAxis(), 0.0);

            this.setY2Axis(new double[10]);
            Arrays.fill(this.getY2Axis(), this.getExpected());

            this.setXnormAxis(new double[100]);
            double skip = (this.getMaxcount()-this.getMincount())/100.0;
            this.getXnormAxis()[0] = this.getMincount() + skip/2.0;
            for (int x = 0; x < 99; x++) this.getXnormAxis()[x+1] = this.getXnormAxis()[x] + skip;

            this.setYnormAxis(new double[100]);
            Arrays.fill(this.getYnormAxis(), 0.0);

            int sizeEx = ((int) this.getExpected() * 2 + 6);
            this.setHistoXAxis(new double[sizeEx]);
            for (int x = 0; x < sizeEx; x++) this.getHistoXAxis()[x] = x;

            this.setHistoYAxis(new double[sizeEx]);
            for (int x = 0; x < sizeEx; x++) this.getHistoYAxis()[x] = 0;

            this.setSawLengths(new ArrayList<>());
            for (int x = 0; x < this.getNumParts(); x++) this.getSawLengths().add(0.0);

            this.setSum(new double[this.getNumParts()]);
            this.setSumParts(0.0);

            this.setCenterX(this.getMeasure()/2.0);
            this.setCenterY(this.getMeasure()/2.0);

            this.getFxplot().setWData(this.getRms_runs(), this.getRms_runs(), this.getExpected());
            this.getFxplot().setNData(this.getRms_norm(), this.getRms_norm(), this.getMincount(), this.getMaxcount(), standdiff);
            this.getFxplot().setHData(this.getHistoXAxis(), this.getHistoYAxis());

            this.getFxplot().setFrameVis();
        }

        this.getPiirturi().setLineWidth(this.getLinewidth());

        String[] command;

        command = new String[]{"cmd","/c",executable,
            this.vars[0], this.vars[1], this.vars[2], this.vars[3], this.vars[4],
            this.vars[5], this.vars[6], this.vars[7], this.vars[8]};

        try {
            this.setRuntime(Runtime.getRuntime());
            runtimeStart();

            Process process = this.getRuntime().exec(command, null, folder);

            try (BufferedReader input = new BufferedReader(new InputStreamReader(
                process.getInputStream(), StandardCharsets.UTF_8))) {

                String line;

                while ((line = input.readLine()) != null) {
                    if (line.trim().startsWith("S") || line.isEmpty()) continue;
                    if (!line.substring(0,1).matches("([0-9]|-|\\+)")) continue;
                    if ( !(line.trim().split("(\\s+)")[0].trim().equals("+")) ) {
                        switch (this.getDim()) {
                            case 1: {
                                try {
                                    this.getValues()[0][i] = Double.parseDouble(line.trim()) + this.getCenterX()/this.getScalefactor();
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
                                     this.getValues()[1][i] = Double.parseDouble(valStr[1].trim()) + this.getCenterY()/this.getScalefactor();
                                 } catch (NumberFormatException e) {
                                     continue;
                                 }
                                 break;
                             }
                             case 3: {
                                 String[] valStr = line.split("(\\s+)");
                                 try {
                                     this.getValues()[0][i] = Double.parseDouble(valStr[0].trim()) + this.getCenterX()/this.getScalefactor();
                                     this.getValues()[1][i] = Double.parseDouble(valStr[1].trim()) + this.getCenterY()/this.getScalefactor();
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
                         * YELLOW LINES
                         */
                        if ( j > 0){
                            this.getPiirturi().setStroke(Color.YELLOW);
                            for (int k = 0; k < this.getNumParts(); k++){
                                if ( this.getDim() < 3 ) {
                                    this.getPiirturi().strokeLine(
                                        this.getMuistiX()[k], this.getMuistiY()[k], this.getValues()[0][k], this.getValues()[1][k]);
                                } else {
                                    this.getPiirturi().strokeLine(
                                        this.getMuistiX()[k], this.getMuistiY()[k],
                                        this.getValues()[0][k] + Math.cos(this.getValues()[2][k]),
                                        this.getValues()[1][k] + Math.sin(this.getValues()[2][k]));
                                }
                            }
                        }
                        if ( this.getDim() < 3 ) {
                            this.getMuistiX()[i] = this.getValues()[0][i];
                            this.getMuistiY()[i] = this.getValues()[1][i];
                        } else {
                            this.getMuistiX()[i] = this.getValues()[0][i] + Math.cos(this.getValues()[2][i]);
                            this.getMuistiY()[i] = this.getValues()[1][i] + Math.sin(this.getValues()[2][i]);
                        }

                        /*
                         * RED SOURCE DOT
                         */
                        if ( j == 0 && i == 0 ) {
                            this.getPiirturi().setFill(Color.RED);
                            if (this.getDim() == 1) {
                                final double width = this.getExpected() / (10.0 * Math.sqrt(Math.log10(this.getSteps())));
                                final double height = Math.sqrt(this.getCenterY() / 2.0);
                                this.getPiirturi().fillRect(
                                    this.getCenterX()/this.getScalefactor(),
                                    this.getCenterY() - height/2.0,
                                    width, height);
                            } else {
                                final double widthheight = 20.0 / this.getScalefactor();
                                final double scalecenter = this.getCenterX()/this.getScalefactor();
                                this.getPiirturi().fillRect(
                                    (this.getDim() == 2 ? scalecenter - widthheight/2.0 : scalecenter - 0.0),
                                    (this.getDim() == 2 ? scalecenter - widthheight/2.0 : scalecenter - 2.0*widthheight/Math.log10(this.getSteps())),
                                    widthheight, widthheight);
                            }
                        }

                        i++;

                        if ( i == this.getNumParts() ){
                            i = 0;
                            j++;
                        }

                        if ( j == this.getSteps() + 1) {
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

                            this.setBiggDist(Math.max(this.getBiggDist(), Math.sqrt(dataVal)));

                            if (this.getRuns() > 1) this.getSawLengths().add(Math.sqrt(dataVal));
                            else this.getSawLengths().set(this.getInitPart(), Math.sqrt(dataVal));
                            this.setInitPart(this.getInitPart() + 1);

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

                            double rrms_walk = Math.sqrt(this.getSumParts()/(this.getNumParts()*this.getRuns()));
                            this.setRms_sum(this.getRms_sum() + rrms_walk);
                            double avg = this.getRms_sum()/this.getRuns();
                            this.setSigSeed(this.getSigSeed() + Math.pow(rrms_walk - avg, 2.0));

                            if ( this.getRuns() < 11 ) {
                                this.getRms_runs()[(int) this.getRuns() - 1] = rrms_walk;
                            } else {
                                arraycopy(this.getRms_runs(), 1, this.getRms_runs(), 0, 9);
                                this.getRms_runs()[9] = rrms_walk;
                            }
                            this.setYAxis(this.getRms_runs().clone());

                            /*
                             * find greatest value for y-axis max limit
                             */
                            if ( rrms_walk + Math.log10(this.getSteps()) > this.getGreatest() || rrms_walk > this.getExpected() )
                                this.setGreatest(rrms_walk + Math.log10(this.getSteps()));

                            this.getFxplot().setWMinY(this.getSmallest());
                            this.getFxplot().setWMaxY(this.getGreatest() + 1.0 );
                            this.getFxplot().updateWData("Rrms", this.getXAxis(), this.getYAxis(), this.getExpected(), rrms_walk);
                            this.getFxplot().updateWData(this.getLanguage().equals("fin") ? "\u221Aaskeleet" : "\u221Asteps",
                                this.getXAxis(), this.getY2Axis(), this.getExpected(), rrms_walk);

                            double ynorm = 0.0;

                            for (int h = 0; h < 100; h++) {
                                if (this.isStandPlot()) {
                                    double sigma = Math.sqrt(this.getSigSeed()/this.getRuns());
                                    ynorm = 1.0 / (Math.sqrt(2.0 * Math.PI * sigma))
                                        * Math.exp(-Math.pow(this.getXnormAxis()[h]*3.0, 2.0) / (2.0 * Math.pow(sigma,2)));
                                    this.setGreatestDN(ynorm > this.getGreatestDN() ? ynorm : this.getGreatestDN());
                                    this.getFxplot().setNMaxY(this.getGreatestDN());
                                } else if (this.isDiffPlot()) {
                                    /*double factor = 0.0;
                                    if (this.getDim() == 1) factor = 2.0;
                                    else if (this.getDim() == 2) factor = 4.0;
                                    else if (this.getDim() == 3) factor = 6.0;*/
                                    //double rrms = this.getSumParts()/this.getNumParts();
                                    double diff = rrms_walk / (2.0 * this.getDim() * this.getSteps());
                                    double sigma = Math.sqrt(4.0 * Math.PI * diff * this.getSteps());
                                    ynorm = 1.0 / sigma//Math.sqrt(4.0 * Math.PI * sigma)
                                        * Math.exp(-Math.pow(this.getXnormAxis()[h], 2.0) / (4.0 *diff * this.getSteps()));
                                        //* Math.exp(-Math.pow(this.getXnormAxis()[h]*3.0/sigma, 2.0) / 2.0);
                                    if ( ynorm > this.getGreatestDN() ) this.setGreatestDN(ynorm);
                                    this.getFxplot().setNMaxY(this.getGreatestDN());
                                }
                                this.getYnormAxis()[h] = ynorm;
                            }

                            /*
                             * DISTANCE HISTOGRAM
                             */
                            int size = (int) this.getBiggDist();
                            if ( size > this.getHistoXAxis().length ) {
                                this.setHistoYAxis(calcHistogram(this.getSawLengths(), size+2, size+1));
                                Thread.sleep(100);
                                this.setHistoXAxis(new double[size+1]);
                                for (int x = 0; x < size+1; x++) this.getHistoXAxis()[x] = x;
                            } else if ( size > (int) this.getExpected() * 2 + 6 ) {
                                this.setHistoYAxis(calcHistogram(this.getSawLengths(), size+2, this.getHistoYAxis().length));
                                Thread.sleep(100);
                            } else this.setHistoYAxis(calcHistogram( this.getSawLengths(),
                                (int) this.getExpected() * 2 + 7, (int) this.getExpected() * 2 + 6));
                        }
                    }
                }

                if ( this.getRuns() < 6 ) {
                    String mapkey;
                    String mapkey1 = "\u03C1(r,t\u2081)";
                    String mapkey2 = "\u03C1(r,t\u2082)";
                    String mapkey3 = "\u03C1(r,t\u2083)";
                    String mapkey4 = "\u03C1(r,t\u2084)";
                    String mapkey5 = "\u03C1(r,t\u2085)";
                    if (this.getRuns() == 1) mapkey = mapkey1;
                    else if (this.getRuns() == 2) mapkey = mapkey2;
                    else if (this.getRuns() == 3) mapkey = mapkey3;
                    else if (this.getRuns() == 4) mapkey = mapkey4;
                    else mapkey = mapkey5;
                    if (!this.getOthers().containsKey(mapkey)) {
                        this.getOthers().put(mapkey, this.getYnormAxis().clone());
                        for (Map.Entry<String, double[]> longEntry : this.getOthers().entrySet()) {
                            this.getFxplot().updateNData(String.valueOf(((Map.Entry) longEntry).getKey()), this.getXnormAxis(), (double[]) ((Map.Entry) longEntry).getValue());
                        }
                    }
                }

                this.getFxplot().updateNData("\u03C1(r,t)", this.getXnormAxis(), this.getYnormAxis());

                for (Map.Entry<String, double[]> longEntry : this.getOthers().entrySet()) {
                    this.getFxplot().updateNData(String.valueOf(((Map.Entry) longEntry).getKey()), this.getXnormAxis(), (double[]) ((Map.Entry) longEntry).getValue());
                }

                this.getFxplot().updateHData( this.getHistoXAxis(), this.getHistoYAxis() );

                this.setRuns(this.getRuns() + 1);

                this.setExitVal(process.waitFor());
                if (this.getExitVal() != 0) this.getRuntime().exit(this.getExitVal());
            }

        } catch (IOException | InterruptedException e) {
            this.getRuntime().gc();
            System.out.println(e.getMessage());
        }
    }

    /**
     * method for creating histogram
     * @param data input data
     * @param max max value
     * @param numBins number of bins
     * @return bin data array
     */
    @NotNull
    @Contract(pure = true)
    private static double[] calcHistogram(@NotNull List<Double> data, double max, int numBins) {
        final double[] result = new double[numBins];
        final double binSize = max / numBins;

        for (double d : data) {
            int bin = (int) Math.ceil(d / binSize);
            if (bin >= 0 && bin < numBins) result[bin] += 1;
        }
        return result;
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
        Label labNumParticles = new Label(this.getLanguage().equals("fin") ? "hiukkasten lukumäärä:" : "number of particles:");
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

        Label labNumSteps = new Label(this.getLanguage().equals("fin") ? "askelten lukumäärä:" : "number of steps:");
        this.setNumSteps = new TextField("");
        this.setNumSteps.setOnKeyReleased(e -> {
            if (isNumInteger(this.setNumSteps.getText().trim())){
                this.vars[3] = this.setNumSteps.getText().trim();
            } else
                this.vars[3] = "0";
        });

        Label labNumDimensions = new Label(this.getLanguage().equals("fin") ? "ulottuvuus:" : "dimension:");
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

        this.setDimension(new HBox(this.setDim1,this.setDim2,this.setDim3));
        this.getDimension().setSpacing(20);
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

        Label labPlotChoice = new Label(this.getLanguage().equals("fin") ? "Kuvaaja:" : "Plot type:");
        this.setStandPlot = new ToggleButton(this.getLanguage().equals("fin") ? "NORM. JAKAUMA" : "NORM. DISTRIB.");
        this.setStandPlot.setMinWidth(this.getCompwidth());
        this.setStandPlot.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setStandPlot.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setStandPlot.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setStandPlot.setEffect(shadow));
        this.setStandPlot.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setStandPlot.setEffect(null));

        this.setDiffPlot = new ToggleButton(this.getLanguage().equals("fin") ? "DIFF. JAKAUMA" : "DIFF. DISTRIB.");
        this.setDiffPlot.setMinWidth(this.getCompwidth());
        this.setDiffPlot.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setDiffPlot.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setDiffPlot.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDiffPlot.setEffect(shadow));
        this.setDiffPlot.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDiffPlot.setEffect(null));

        this.setPlotChoice(new VBox(this.setStandPlot,this.setDiffPlot));
        this.getPlotChoice().setSpacing(10);
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
        GridPane.setHalignment(this.getDimension(), HPos.CENTER);
        this.getDimension().setMinWidth(getCompwidth());
        this.getDimension().setMaxWidth(getCompwidth());
        asettelu.add(this.getDimension(), 0, 5);

        final Pane empty1 = new Pane();
        GridPane.setHalignment(empty1, HPos.CENTER);
        asettelu.add(empty1, 0, 6, 2, 2);

        GridPane.setHalignment(labPlotChoice, HPos.LEFT);
        asettelu.add(labPlotChoice, 0, 7);
        GridPane.setHalignment(this.getPlotChoice(), HPos.CENTER);
        this.getPlotChoice().setMinWidth(getCompwidth());
        this.getPlotChoice().setMaxWidth(getCompwidth());
        asettelu.add(this.getPlotChoice(), 0, 8);

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 9, 2, 1);

        final Pane empty3 = new Pane();
        GridPane.setHalignment(empty3, HPos.CENTER);
        asettelu.add(empty3, 0, 10, 2, 2);

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
     * @return the init_part
     */
    @Contract(pure = true)
    private int getInitPart() { return this.init_part; }

    /**
     * @param init_part the init_part to set
     */
    private void setInitPart(int init_part) { this.init_part = init_part; }

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
     * @param histoXAxis x-axis data array for normal distribution plots to set
     */
    private void setHistoXAxis(double[] histoXAxis) { this.histoXAxis = histoXAxis; }

    /**
     * @return x-axis data array for normal distribution plots
     */
    @Contract(pure = true)
    private double[] getHistoXAxis() { return this.histoXAxis; }

    /**
     * @param histoYAxis y-axis data array for normal distribution plots to set
     */
    private void setHistoYAxis(double[] histoYAxis) { this.histoYAxis = histoYAxis; }

    /**
     * @return y-axis data array for normal distribution plots
     */
    @Contract(pure = true)
    private double[] getHistoYAxis() { return this.histoYAxis; }

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
     * @return the greatestdn
     */
    @Contract(pure = true)
    private double getGreatestDN() { return this.greatestdn; }

    /**
     * @param greatestdn the greatestdn to set
     */
    private void setGreatestDN(double greatestdn) { this.greatestdn = greatestdn; }

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

    /**
     * @return the language
     */
    @Contract(pure = true)
    private String getLanguage() { return this.language; }

    /**
     * @param language the language to set
     */
    private void setLanguage(String language) { this.language = language; }

    /**
     * @return the plotChoice
     */
    @Contract(pure = true)
    VBox getPlotChoice() { return this.plotChoice; }

    /**
     * @param plotChoice the plotChoice to set
     */
    private void setPlotChoice(VBox plotChoice) { this.plotChoice = plotChoice; }

    /**
     * @return the dim_choice
     */
    @Contract(pure = true)
    HBox getDimension() { return this.dim_choice; }

    /**
     * @param dim_choice the dim_choice to set
     */
    private void setDimension(HBox dim_choice) { this.dim_choice = dim_choice; }

    /**
     * @return the fxplot
     */
    @Contract(pure = true)
    FXPlot getFxplot() { return fxplot; }

    /**
     * @param fxplot the fxplot to set
     */
    void setFxplot( FXPlot fxplot ) { this.fxplot = fxplot; }

    /**
     * @return the saw_lengths
     */
    @Contract(pure = true)
    private List <Double> getSawLengths() { return this.saw_lengths; }

    /**
     * @param saw_lengths the saw_lengths to set
     */
    private void setSawLengths(List<Double> saw_lengths) { this.saw_lengths = saw_lengths; }

    /**
     * @return the bigg_dist
     */
    @Contract(pure = true)
    private double getBiggDist() { return this.bigg_dist; }

    /**
     * bigg_dist to set
     */
    private void setBiggDist(double bigg_dist) { this.bigg_dist = bigg_dist; }

    /**
     * @return the sigSeed
     */
    @Contract(pure = true)
    private double getSigSeed() { return this.sigSeed; }

    /**
     * sigSeed to set
     */
    private void setSigSeed(double sigSeed) { this.sigSeed = sigSeed; }
}
