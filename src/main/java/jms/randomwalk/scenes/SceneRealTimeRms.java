package jms.randomwalk.scenes;

import enums.DblSizes;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import jms.randomwalk.datahandling.Data;
import jms.randomwalk.plots.FXPlot;
import jms.randomwalk.ui.GetComponents;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.apache.maven.surefire.shade.booter.org.apache.commons.lang3.SystemUtils;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for Real Time Rms.
 */
public class SceneRealTimeRms extends Data {

    private String language;
    private final boolean isWin;
    private double scalefactor;
    private double linewidth;
    private FXPlot fxplot;
    private GraphicsContext piirturi;
    private boolean running;
    private boolean runtimeRunning;
    private int numParts;
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
    private List<Double> sawLengths;
    private long runs;
    private int initPart;
    private double biggDist;
    private double rmsSum;
    private double rmsData;
    private double sumParts;
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
    private double[] rmsRuns;
    private double[] rmsNorm;
    private ToggleButton setDim1;
    private ToggleButton setDim2;
    private ToggleButton setDim3;
    private ToggleButton setStandPlot;
    private ToggleButton setDiffPlot;
    private ToggleButton setLattice;
    private boolean standPlot;
    private boolean diffPlot;
    private TextField setNumParticles;
    private TextField setNumSteps;
    private VBox plotChoice;
    private HBox dimChoice;
    private Map<String, double[]> others;
    private double sigSeed;
    private HBox isovalikko;
    private Pane pane;

    /**
     * Main class gets vars via this.
     * @return clone of vars array
     */
    public String[] getVars() {
        return this.vars.clone();
    }

    /**
     * Initiating user variable array and other variables.
     * @param language which ui language: finnish or english
     */
    public SceneRealTimeRms(String language) {
        super();
        this.setLanguage(language);
        this.isWin = SystemUtils.IS_OS_WINDOWS;
        this.vars = new String[]{
            "-",    // vars[0] which simulation         USER
            "0",    // vars[1] particles                USER
            "0.1",  // vars[2] diameter                 n/a
            "0",    // vars[3] steps                    USER
            "0",    // vars[4] dimension                USER
            "-",    // vars[5] efficiency or sawplot    n/a
            "f",    // vars[6] fixed(/spread)           n/a
            "-",    // vars[7] (lattice/)free           n/a
            "-"};   // vars[8] save (off)               n/a
        this.running = false;
    }

    /**
     * Real Time Rms.
     * @param folder datafolder "C:/RWDATA"
     * @param executable Fortran executable "walk.exe"
     * @param piirturi GraphicsContext which draws the animation
     * @param scalefactor scaling is used in different particle amounts
     * @param linewidth width for lines
     * @param newdata true if is a new run with new data
     * @param measure width and height for drawing area
     */
    public void refresh(File folder, String executable, GraphicsContext piirturi,
        double scalefactor, double linewidth, boolean newdata, double measure) {

        int i = 0;
        int j = 0;
        int p = 0;

        this.setPiirturi(piirturi);
        this.setLinewidth(linewidth);
        this.setScalefactor(scalefactor);
        this.setRmsSum(0.0);
        this.setRmsData(0.0);
        this.setSmallest();
        this.setOthers(new HashMap<>());
        this.getOthers().clear();

        if (newdata) {
            this.setBiggDist(0.0);
            this.setRuns(1);
            if (this.isStandPlot()) {
                this.setSigSeed(1.0);
            } else {
                this.setSigSeed(0.0);
            }
            this.setInitPart(0);
            this.setRmsRuns();
            this.setRmsNorm();
            this.setMeasure(measure);
            this.numParts(Integer.parseInt(this.vars[1]));
            this.setSteps(Double.parseDouble(this.vars[3]));
            this.dim(Integer.parseInt(this.vars[4]));
            this.setExpected(Math.sqrt(this.getSteps()));
            this.setGreatest(this.getExpected() + Math.log10(this.getSteps()));
            String standdiff = "";
            this.setOthers(new HashMap<>());
            this.getOthers().clear();

            if (this.isStandPlot()) {
                this.setMincount(-this.getExpected() * 2.0);
                this.setMaxcount(this.getExpected() * 2.0);
            } else if (this.isDiffPlot()) {
                this.setMincount(-this.getExpected());
                this.setMaxcount(this.getExpected());
            }

            if (this.isStandPlot()) {
                standdiff = "stand";
            } else if (this.isDiffPlot()) {
                standdiff = "diff";
            }

            this.setGreatestDN(0.0);

            this.setMuistiX(new double[this.getNumParts()]);
            this.setMuistiY(new double[this.getNumParts()]);

            if (this.getDim() < 3) {
                this.setValues(new double[2][this.getNumParts()]);
            } else {
                this.setValues(new double[3][this.getNumParts()]);
            }

            this.setPlotData(new double[this.getNumParts()]);

            this.setXAxis(new double[10]);
            for (int x = 0; x < 10; x++) {
                this.getXAxis()[x] = x;
            }

            this.setYAxis(new double[10]);
            Arrays.fill(this.getYAxis(), 0.0);

            this.setY2Axis(new double[10]);
            Arrays.fill(this.getY2Axis(), this.getExpected());

            this.setXnormAxis(new double[100]);
            double skip = (this.getMaxcount() - this.getMincount()) / 100.0;
            this.getXnormAxis()[0] = this.getMincount() + skip / 2.0;
            for (int x = 0; x < 99; x++) {
                this.getXnormAxis()[x + 1] = this.getXnormAxis()[x] + skip;
            }

            this.setYnormAxis(new double[100]);
            Arrays.fill(this.getYnormAxis(), 0.0);

            int sizeEx = ((int) this.getExpected() * 2 + 6);
            this.setHistoXAxis(new double[sizeEx]);
            for (int x = 0; x < sizeEx; x++) {
                this.getHistoXAxis()[x] = x;
            }

            this.setHistoYAxis(new double[sizeEx]);
            for (int x = 0; x < sizeEx; x++) {
                this.getHistoYAxis()[x] = 0;
            }

            this.setSawLengths(new ArrayList<>());
            for (int x = 0; x < this.getNumParts(); x++) {
                this.getSawLengths().add(0.0);
            }

            this.setSum(new double[this.getNumParts()]);
            this.setSumParts(0.0);

            this.setCenterX(this.getMeasure() / 2.0);
            this.setCenterY(this.getMeasure() / 2.0);

            this.getFxplot().setWData(this.getRmsRuns(), this.getRmsRuns(), this.getExpected());
            this.getFxplot().setNData(this.getRmsNorm(), this.getRmsNorm(), this.getMincount(), this.getMaxcount(), standdiff);
            this.getFxplot().setHData(this.getHistoXAxis(), this.getHistoYAxis());

            this.getFxplot().setFrameVis();
        }

        this.getPiirturi().setLineWidth(this.getLinewidth());

        String[] command;

        if (this.isWin) {
            command = new String[]{"cmd", "/c", executable,
                this.vars[0], this.vars[1], this.vars[2], this.vars[3], this.vars[4],
                this.vars[5], this.vars[6], this.vars[7], this.vars[8]};
        } else {
            command = new String[]{"./" + executable,
                this.vars[0], this.vars[1], this.vars[2], this.vars[3], this.vars[4],
                this.vars[5], this.vars[6], this.vars[7], this.vars[8]};
        }

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
                    if (!line.substring(0, 1).matches("([0-9]|-|\\+)")) {
                        continue;
                    }
                    if (!(line.trim().split("(\\s+)")[0].trim().equals("+"))) {
                        switch (this.getDim()) {
                            case 1: {
                                this.getValues()[0][i] = Double.parseDouble(line.trim()) + this.getCenterX() / this.getScalefactor();
                                this.getValues()[1][i] = this.getCenterY();
                                break;
                            }
                            case 2: {
                                String[] valStr = line.split("(\\s+)");
                                this.getValues()[0][i] = Double.parseDouble(valStr[0].trim()) + this.getCenterX() / this.getScalefactor();
                                this.getValues()[1][i] = Double.parseDouble(valStr[1].trim()) + this.getCenterY() / this.getScalefactor();
                                break;
                            }
                            case 3: {
                                String[] valStr = line.split("(\\s+)");
                                this.getValues()[0][i] = Double.parseDouble(valStr[0].trim()) + this.getCenterX() / this.getScalefactor();
                                this.getValues()[1][i] = Double.parseDouble(valStr[1].trim()) + this.getCenterY() / this.getScalefactor();
                                this.getValues()[2][i] = Double.parseDouble(valStr[2].trim()) + 1.2 * this.getCenterX() / this.getScalefactor();
                                break;
                            }
                            default:
                                break;
                        }

                        /*
                         * YELLOW LINES
                         */
                        if (j > 0) {
                            this.getPiirturi().setStroke(Color.YELLOW);
                            for (int k = 0; k < this.getNumParts(); k++) {
                                if (this.getDim() < 3) {
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
                        if (this.getDim() < 3) {
                            this.getMuistiX()[i] = this.getValues()[0][i];
                            this.getMuistiY()[i] = this.getValues()[1][i];
                        } else {
                            this.getMuistiX()[i] = this.getValues()[0][i] + Math.cos(this.getValues()[2][i]);
                            this.getMuistiY()[i] = this.getValues()[1][i] + Math.sin(this.getValues()[2][i]);
                        }

                        /*
                         * RED SOURCE DOT
                         */
                        if (j == 0 && i == 0) {
                            this.getPiirturi().setFill(Color.RED);
                            if (this.getDim() == 1) {
                                final double width = this.getExpected() / (10.0 * Math.sqrt(Math.log10(this.getSteps())));
                                final double height = Math.sqrt(this.getCenterY() / 2.0);
                                this.getPiirturi().fillRect(
                                    this.getCenterX() / this.getScalefactor(),
                                    this.getCenterY() - height / 2.0,
                                    width, height);
                            } else {
                                final double widthheight = 20.0 / this.getScalefactor();
                                final double scalecenter = this.getCenterX() / this.getScalefactor();
                                this.getPiirturi().fillRect(
                                    (this.getDim() == 2 ? scalecenter - widthheight / 2.0 : scalecenter - 0.0),
                                    (this.getDim() == 2 ? scalecenter - widthheight / 2.0 : scalecenter - 2.0 * widthheight / Math.log10(this.getSteps())),
                                    widthheight, widthheight);
                            }
                        }

                        i++;

                        if (i == this.getNumParts()) {
                            i = 0;
                            j++;
                        }

                        if (j == this.getSteps() + 2) {
                            i = 0;
                            j = 0;
                        }

                    } else {
                        /*
                         * PLOTS
                         */
                        double dataVal;
                        // Fortran input starts with "+", therefore [1] and not [0] which is a plus-sign
                        dataVal = Double.parseDouble(line.split("(\\s+)")[1].trim());
                        this.getPlotData()[j] = dataVal;
                        this.setBiggDist(Math.max(this.getBiggDist(), Math.sqrt(dataVal)));

                        if (this.getRuns() > 1) {
                            this.getSawLengths().add(Math.sqrt(dataVal));
                        } else {
                            this.getSawLengths().set(this.getInitPart(), Math.sqrt(dataVal));
                        }
                        this.setInitPart(this.getInitPart() + 1);

                        if (this.getRuns() == 1) {
                            this.setSumParts(0.0);
                            this.setRmsData(0.0);
                        }

                        this.setRmsData(this.getRmsData() + this.getPlotData()[j]);
                        this.getSum()[i] = this.getRmsData();
                        j++;
                        p++;

                        if (p == this.getNumParts()) {
                            i = 0;
                            j = 0;
                            p = 0;
                            for (int f = 0; f < 10; f++) {
                                this.getXAxis()[f] = (double) (f + this.getRuns());
                            }

                            for (int m = 0; m < this.getNumParts(); m++) {
                                this.setSumParts(this.getSumParts() + this.getSum()[m]);
                            }

                            double rrmsWalk = Math.sqrt(this.getSumParts() / (this.getNumParts() * this.getRuns()));
                            this.setRmsSum(this.getRmsSum() + rrmsWalk);
                            double avg = this.getRmsSum() / (this.getRuns());
                            this.setSigSeed(this.getSigSeed() + (rrmsWalk - avg));

                            if (this.getRuns() < 11) {
                                this.getRmsRuns()[(int) this.getRuns() - 1] = rrmsWalk;
                            } else {
                                System.arraycopy(this.getRmsRuns(), 1, this.getRmsRuns(), 0, 9);
                                this.getRmsRuns()[9] = rrmsWalk;
                            }
                            this.setYAxis(this.getRmsRuns().clone());

                            /*
                             * find greatest value for y-axis max limit
                             */
                            if (rrmsWalk + Math.log10(this.getSteps()) > this.getGreatest() || rrmsWalk > this.getExpected()) {
                                this.setGreatest(rrmsWalk + Math.log10(this.getSteps()));
                            }

                            this.getFxplot().setWMinY(this.getSmallest());
                            this.getFxplot().setWMaxY(this.getGreatest() + 1.0);
                            this.getFxplot().updateWData("Rrms", this.getXAxis(), this.getYAxis(), this.getExpected(), rrmsWalk);
                            this.getFxplot().updateWData(this.getLanguage().equals("fin") ? "\u221AS" : "\u221AS",
                                this.getXAxis(), this.getY2Axis(), this.getExpected(), rrmsWalk);

                            double ynorm = 0.0;
                            for (int h = 0; h < 100; h++) {
                                if (this.isStandPlot()) {
                                    double sigma = this.getSigSeed() / this.getRuns();
                                    ynorm = 1.0 / (Math.sqrt(2.0 * Math.PI) * sigma)
                                        * Math.exp(-Math.pow(this.getXnormAxis()[h], 2.0) / (2.0 * Math.pow(sigma, 2)));
                                    this.setGreatestDN(ynorm > this.getGreatestDN() ? ynorm : this.getGreatestDN());
                                    this.getFxplot().setNMaxY(this.getGreatestDN());
                                } else if (this.isDiffPlot()) {
                                    double sigma2 = this.getRuns() / this.getSteps() * Math.pow(rrmsWalk, 2.0);
                                    ynorm = 1.0 / Math.sqrt(2.0 * Math.PI * sigma2)
                                        * Math.exp(-Math.pow(this.getXnormAxis()[h], 2.0) / (2.0 * sigma2));
                                    if (ynorm > this.getGreatestDN()) {
                                        this.setGreatestDN(ynorm);
                                    }
                                    this.getFxplot().setNMaxY(this.getGreatestDN());
                                }
                                this.getYnormAxis()[h] = ynorm;
                            }

                            /*
                             * DISTANCE HISTOGRAM
                             */
                            int size = (int) this.getBiggDist();
                            if (size > this.getHistoXAxis().length) {
                                this.setHistoYAxis(calcHistogram(this.getSawLengths(), size + 2, size + 1));
                                TimeUnit.MILLISECONDS.sleep(100);
                                this.setHistoXAxis(new double[size + 1]);
                                for (int x = 0; x < size + 1; x++) {
                                    this.getHistoXAxis()[x] = x;
                                }
                            } else if (size > (int) this.getExpected() * 2 + 6) {
                                this.setHistoYAxis(calcHistogram(this.getSawLengths(), size + 2, this.getHistoYAxis().length));
                                TimeUnit.MILLISECONDS.sleep(100);
                            } else {
                                this.setHistoYAxis(calcHistogram(this.getSawLengths(),
                                    (int) this.getExpected() * 2 + 7, (int) this.getExpected() * 2 + 6));
                            }

                        }
                    }
                }

                // FIRST THREE DISTRIBUTION GRAPHS
                if (this.getRuns() < 4) {
                    String mapkey;
                    String mapkey1 = "\u03C1(r,t\u2081)";
                    String mapkey2 = "\u03C1(r,t\u2082)";
                    String mapkey3 = "\u03C1(r,t\u2083)";
                    switch ((int) this.getRuns()) {
                        case 1:
                            mapkey = mapkey1;
                            break;
                        case 2:
                            mapkey = mapkey2;
                            break;
                        default:
                            mapkey = mapkey3;
                            break;
                    }
                    if (!this.getOthers().containsKey(mapkey)) {
                        this.getOthers().put(mapkey, this.getYnormAxis().clone());
                        this.getOthers().entrySet().forEach((longEntry) -> {
                            this.getFxplot().updateNData(String.valueOf(((Map.Entry) longEntry).getKey()), this.getXnormAxis(), (double[]) ((Map.Entry) longEntry).getValue());
                        });
                    }
                }

                this.getFxplot().updateNData("\u03C1(r,t)", this.getXnormAxis(), this.getYnormAxis());

                this.getOthers().entrySet().forEach((longEntry) -> {
                    this.getFxplot().updateNData(String.valueOf(((Map.Entry) longEntry).getKey()), this.getXnormAxis(), (double[]) ((Map.Entry) longEntry).getValue());
                });

                this.getFxplot().updateHData(this.getHistoXAxis(), this.getHistoYAxis());
                this.setRuns(this.getRuns() + 1);

                this.setExitVal(process.waitFor());
                if (this.getExitVal() != 0) {
                    this.getRuntime().exit(this.getExitVal());
                }
            }

        } catch (IOException | InterruptedException e) {
            this.getRuntime().gc();
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method for creating histogram.
     * @param data input data
     * @param max max value
     * @param numBins number of bins
     * @return bin data array
     */
    private static double[] calcHistogram(List<Double> data, double max, int numBins) {
        final double[] result = new double[numBins];
        final double binSize = max / numBins;

        data.stream().map((d) -> (int) Math.ceil(d / binSize) - 1).filter((bin) -> (bin >= 0 && bin < numBins)).forEachOrdered((bin) -> {
            result[bin] += 1;
        });
        return result;
    }

    /**
     * Method for checking if user input in GUI is an integer.
     * @param str GUI input string
     * @return true if input is an integer, false otherwise
     */
    private static boolean isNumInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Create GUI for Real Time Rms
     * @param isovalikko GUI component
     * @param pane GUI component
     * @param ivRms1FI image object
     * @param ivRms1EN image object
     * @param ivRms2FI image object
     * @param ivRms2EN image object
     * @return REAL TIME RMS SCENE
     */
    public Parent getSceneReal(HBox isovalikko, Pane pane, ImageView ivRms1FI, ImageView ivRms1EN, ImageView ivRms2FI,
        ImageView ivRms2EN) {
        
        this.setIsoValikko(isovalikko);
        this.setPane(pane);
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(DblSizes.PANEW.getDblSize());
        asettelu.setVgap(2.5);
        asettelu.setHgap(10);
        asettelu.setPadding(new Insets(0, 0, 0, 0));
        VBox valikko = new VBox();
        valikko.setPadding(new Insets(0, 0, 0, 0));

        DropShadow shadow = new DropShadow();
        GetComponents getComponents = new GetComponents();

        /*
        * COMPONENTS...
        */
        Label labNumParticles = new Label(this.getLanguage().equals("fin") ? "hiukkasten lukumäärä:" : "number of particles:");
        this.setNumParticles = new TextField("");
        this.setNumParticles.setOnKeyReleased(e -> {
            if (isNumInteger(this.setNumParticles.getText().trim())) {
                if (this.setNumParticles.getText().trim().equals("0")) {
                    this.setNumParticles.setText("1");
                    this.vars[1] = "1";
                } else {
                    this.vars[1] = this.setNumParticles.getText().trim();
                }
            } else {
                this.vars[1] = "0";
            }
        });

        this.vars[2] = "0"; // (diameter of particle)

        Label labNumSteps = new Label(this.getLanguage().equals("fin") ? "askelten lukumäärä:" : "number of steps:");
        this.setNumSteps = new TextField("");
        this.setNumSteps.setOnKeyReleased(e -> {
            if (isNumInteger(this.setNumSteps.getText().trim())) {
                this.vars[3] = this.setNumSteps.getText().trim();
            } else {
                this.vars[3] = "0";
            }
        });

        Label labNumDimensions = new Label(this.getLanguage().equals("fin") ? "ulottuvuus:" : "dimension:");
        this.setDim1 = new ToggleButton("1");
        this.setDim1.setMinWidth(DblSizes.SMLBUTW.getDblSize());
        this.setDim1.setMaxWidth(DblSizes.SMLBUTW.getDblSize());
        this.setDim1.setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.setDim1.getFont().getSize()));
        this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setDim1.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim1.setEffect(shadow));
        this.setDim1.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim1.setEffect(null));

        this.setDim2 = new ToggleButton("2");
        this.setDim2.setMinWidth(DblSizes.SMLBUTW.getDblSize());
        this.setDim2.setMaxWidth(DblSizes.SMLBUTW.getDblSize());
        this.setDim2.setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.setDim2.getFont().getSize()));
        this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setDim2.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim2.setEffect(shadow));
        this.setDim2.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim2.setEffect(null));

        this.setDim3 = new ToggleButton("3");
        this.setDim3.setMinWidth(DblSizes.SMLBUTW.getDblSize());
        this.setDim3.setMaxWidth(DblSizes.SMLBUTW.getDblSize());
        this.setDim3.setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.setDim3.getFont().getSize()));
        this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setDim3.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim3.setEffect(shadow));
        this.setDim3.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim3.setEffect(null));

        this.setDimension(new HBox(this.setDim1, this.setDim2, this.setDim3));
        this.getDimension().setSpacing(20);
        this.setDim1.setOnMouseClicked(f -> {
            this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.vars[4] = "1";
        });
        this.setDim2.setOnMouseClicked(f -> {
            this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.vars[4] = "2";
        });
        this.setDim3.setOnMouseClicked(f -> {
            this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
            this.vars[4] = "3";
        });

        this.vars[5] = "-"; // efficiency or sawplot    n/a
        this.vars[6] = "f"; // fixed(/spread)           n/a

        /*
         * BUTTON: LATTICE
         */
        this.setLattice = new ToggleButton(this.getLanguage().equals("fin") ? "VAPAA" : "FREE");
        this.setLattice.setMinWidth(DblSizes.BUTW.getDblSize());
        this.setLattice.setMaxWidth(DblSizes.BUTW.getDblSize());
        this.setLattice.setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.setLattice.getFont().getSize()));
        this.setLattice.setBackground(new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setLattice.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.setLattice.setEffect(shadow));
        this.setLattice.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.setLattice.setEffect(null));
        this.setLattice.setOnMouseClicked((MouseEvent event) -> {
            if (SceneRealTimeRms.this.setLattice.getText().equals("LATTICE") || SceneRealTimeRms.this.setLattice.getText().equals("HILA")) {
                SceneRealTimeRms.this.setLattice.setText(SceneRealTimeRms.this.getLanguage().equals("fin") ? "VAPAA" : "FREE");
                SceneRealTimeRms.this.setLattice.setBackground(new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY)));
                SceneRealTimeRms.this.vars[7] = "-";
            } else if (SceneRealTimeRms.this.setLattice.getText().equals("FREE") || SceneRealTimeRms.this.setLattice.getText().equals("VAPAA")) {
                SceneRealTimeRms.this.setLattice.setText(SceneRealTimeRms.this.getLanguage().equals("fin") ? "HILA" : "LATTICE");
                SceneRealTimeRms.this.setLattice.setBackground(new Background(new BackgroundFill(Color.LIGHTSALMON, CornerRadii.EMPTY, Insets.EMPTY)));
                SceneRealTimeRms.this.vars[7] = "l";
            }
        });
        //this.vars[7] = "-";   // (lattice/)free       n/a

        this.vars[8] = "-";     // save (off)           n/a

        Label labPlotChoice = new Label(this.getLanguage().equals("fin") ? "Kuvaaja:" : "Plot type:");
        this.setStandPlot = new ToggleButton(this.getLanguage().equals("fin") ? "RMS JAKAUMA" : "RMS DISTRIB.");
        this.setStandPlot.setMinWidth(DblSizes.BUTW.getDblSize());
        this.setStandPlot.setMaxWidth(DblSizes.BUTW.getDblSize());
        this.setStandPlot.setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.setStandPlot.getFont().getSize()));
        this.setStandPlot.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setStandPlot.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setStandPlot.setEffect(shadow));
        this.setStandPlot.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setStandPlot.setEffect(null));

        this.setDiffPlot = new ToggleButton(this.getLanguage().equals("fin") ? "DIFF. JAKAUMA" : "DIFF. DISTRIB.");
        this.setDiffPlot.setMinWidth(DblSizes.BUTW.getDblSize());
        this.setDiffPlot.setMaxWidth(DblSizes.BUTW.getDblSize());
        this.setDiffPlot.setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.setDiffPlot.getFont().getSize()));
        this.setDiffPlot.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setDiffPlot.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDiffPlot.setEffect(shadow));
        this.setDiffPlot.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDiffPlot.setEffect(null));

        Label labNormDiff = new Label(this.getLanguage().equals("fin") ? "jakauma:" : "distribution:");
        this.setPlotChoice(new VBox(this.setStandPlot, this.setDiffPlot));
        this.getPlotChoice().setSpacing(10);
        this.setStandPlot.setOnMouseClicked(f -> {
            this.setStandPlot.setBackground(new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDiffPlot.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.getIsoValikko().getChildren().remove(1);
            this.setPane(this.getLanguage().equals("fin")
                ? getComponents.getPane2(ivRms1FI, DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize())
                : getComponents.getPane2(ivRms1EN, DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize()));
            this.getIsoValikko().getChildren().addAll(this.getPane());
            this.standPlot = true;
            this.diffPlot = false;
            labNormDiff.setVisible(true);
        });
        this.setDiffPlot.setOnMouseClicked(f -> {
            this.setStandPlot.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDiffPlot.setBackground(new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY)));
            this.getIsoValikko().getChildren().remove(1);
            this.setPane(this.getLanguage().equals("fin")
                ? getComponents.getPane2(ivRms2FI, DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize())
                : getComponents.getPane2(ivRms2EN, DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize()));
            this.getIsoValikko().getChildren().addAll(this.getPane());
            this.diffPlot = true;
            this.standPlot = false;
            labNormDiff.setVisible(true);
        });
        labNormDiff.setVisible(false);

        /*
        * ...THEIR PLACEMENTS
        */
        GridPane.setHalignment(labNumParticles, HPos.LEFT);
        asettelu.add(labNumParticles, 0, 0);
        GridPane.setHalignment(this.setNumParticles, HPos.CENTER);
        this.setNumParticles.setMinWidth(DblSizes.BUTW.getDblSize());
        this.setNumParticles.setMaxWidth(DblSizes.BUTW.getDblSize());
        asettelu.add(this.setNumParticles, 0, 1);

        GridPane.setHalignment(labNumSteps, HPos.LEFT);
        asettelu.add(labNumSteps, 0, 2);
        GridPane.setHalignment(this.setNumSteps, HPos.CENTER);
        this.setNumSteps.setMinWidth(DblSizes.BUTW.getDblSize());
        this.setNumSteps.setMaxWidth(DblSizes.BUTW.getDblSize());
        asettelu.add(this.setNumSteps, 0, 3);
        
        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 4);
        GridPane.setHalignment(this.getDimension(), HPos.CENTER);
        this.getDimension().setMinWidth(DblSizes.BUTW.getDblSize());
        this.getDimension().setMaxWidth(DblSizes.BUTW.getDblSize());
        asettelu.add(this.getDimension(), 0, 5);

        final Pane empty1 = new Pane();
        GridPane.setHalignment(empty1, HPos.CENTER);
        asettelu.add(empty1, 0, 6, 2, 2);

        final Pane empty2 = new Pane();
        GridPane.setHalignment(empty2, HPos.CENTER);
        asettelu.add(empty2, 0, 7, 2, 2);

        GridPane.setHalignment(this.setLattice, HPos.CENTER);
        this.setLattice.setMinWidth(DblSizes.BUTW.getDblSize());
        this.setLattice.setMaxWidth(DblSizes.BUTW.getDblSize());
        asettelu.add(this.setLattice, 0, 8);

        final Pane empty3 = new Pane();
        GridPane.setHalignment(empty3, HPos.CENTER);
        asettelu.add(empty3, 0, 9, 2, 2);

        GridPane.setHalignment(labPlotChoice, HPos.LEFT);
        asettelu.add(labPlotChoice, 0, 10);
        GridPane.setHalignment(this.getPlotChoice(), HPos.CENTER);
        this.getPlotChoice().setMinWidth(DblSizes.BUTW.getDblSize());
        this.getPlotChoice().setMaxWidth(DblSizes.BUTW.getDblSize());
        asettelu.add(this.getPlotChoice(), 0, 11);

        final Pane empty4 = new Pane();
        GridPane.setHalignment(empty4, HPos.CENTER);
        asettelu.add(empty4, 0, 12, 2, 2);

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 13, 2, 1);

        return asettelu;
    }

    /**
     * the setRunning to set to true
     */
    public void start() {
        this.setRunning(true);
    }

    /**
     * the setRunning to set to false
     */
    public void stop() {
        this.setRunning(false);
    }

    /**
     * @return running
     */
    public boolean isRunning() {
        return this.running;
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
    public boolean runtimeIsRunning() {
        return this.runtimeRunning;
    }

    public void stopRuntime() {
        this.setRuntimeRunning(false);
        this.runtime.exit(this.getExitVal());
    }

    /**
     * @return the scalefactor
     */
    private double getScalefactor() {
        return this.scalefactor;
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
    private double getLinewidth() {
        return this.linewidth;
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
    private GraphicsContext getPiirturi() {
        return this.piirturi;
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
    private long getRuns() {
        return this.runs;
    }

    /**
     * @param runs the runs to set
     */
    private void setRuns(long runs) {
        this.runs = runs;
    }

    /**
     * @return the init_part
     */
    private int getInitPart() {
        return this.initPart;
    }

    /**
     * @param initPart the init_part to set
     */
    private void setInitPart(int initPart) {
        this.initPart = initPart;
    }

    /**
     * @return the rmsSum
     */
    private double getRmsSum() {
        return this.rmsSum;
    }

    /**
     * @param rmsSum the rmsSum to set
     */
    private void setRmsSum(double rmsSum) {
        this.rmsSum = rmsSum;
    }

    /**
     * @return the rmsData
     */
    private double getRmsData() {
        return this.rmsData;
    }

    /**
     * @param rmsData the rmsData to set
     */
    private void setRmsData(double rmsData) {
        this.rmsData = rmsData;
    }

    /**
     * @return the rmsRuns
     */
    private double[] getRmsRuns() {
        return this.rmsRuns;
    }

    /**
     * rmsRuns to fill with zeros
     */
    private void setRmsRuns() {
        this.rmsRuns = new double[10];
        Arrays.fill(this.rmsRuns, 0.0);
    }

    /**
     * @return the rmsNorm
     */
    private double[] getRmsNorm() {
        return this.rmsNorm;
    }

    /**
     * rmsNorm to fill with zeros
     */
    private void setRmsNorm() {
        this.rmsNorm = new double[10];
        Arrays.fill(this.rmsNorm, 0.0);
    }

    /**
     * @param numParts amount of particles to set
     */
    private void numParts(int numParts) {
        this.numParts = numParts;
    }

    /**
     * @return the amount of particles
     */
    private int getNumParts() {
        return this.numParts;
    }

    /**
     * @param steps amount of steps to set
     */
    private void setSteps(double steps) {
        this.steps = steps;
    }

    /**
     * @return amount of steps
     */
    private double getSteps() {
        return this.steps;
    }

    /**
     * @param dim dimension of field to set
     */
    private void dim(int dim) {
        this.dim = dim;
    }

    /**
     * @return dimension of field
     */
    private int getDim() {
        return this.dim;
    }

    /**
     * @param muistiX x-data values to store for later use
     */
    private void setMuistiX(double[] muistiX) {
        this.muistiX = muistiX;
    }

    /**
     * @return x-data values
     */
    private double[] getMuistiX() {
        return this.muistiX;
    }

    /**
     * @param muistiY y-data values to store for later use
     */
    private void setMuistiY(double[] muistiY) {
        this.muistiY = muistiY;
    }

    /**
     * @return y-data values
     */
    private double[] getMuistiY() {
        return this.muistiY;
    }

    /**
     * @param values data array from InputStream to set
     */
    private void setValues(double[][] values) {
        this.values = values;
    }

    /**
     * @return data array from InputStream
     */
    private double[][] getValues() {
        return this.values;
    }

    /**
     * @param plotdata data for plotting to set
     */
    private void setPlotData(double[] plotdata) {
        this.plotdata = plotdata;
    }

    /**
     * @return data for plotting
     */
    private double[] getPlotData() {
        return this.plotdata;
    }

    /**
     * @param xAxis x-axis data array for walk plot to set
     */
    private void setXAxis(double[] xAxis) {
        this.xAxis = xAxis;
    }

    /**
     * @return x-axis data array for walk plot
     */
    private double[] getXAxis() {
        return this.xAxis;
    }

    /**
     * @param yAxis y-axis data array for walk plot to set
     */
    private void setYAxis(double[] yAxis) {
        this.yAxis = yAxis;
    }

    /**
     * @return y-axis data array for walk plot
     */
    private double[] getYAxis() {
        return this.yAxis;
    }

    /**
     * @param y2Axis y-axis data array for normal distribution plots to set
     */
    private void setY2Axis(double[] y2Axis) {
        this.y2Axis = y2Axis;
    }

    /**
     * @return y-axis data array for normal distribution plots
     */
    private double[] getY2Axis() {
        return this.y2Axis;
    }

    /**
     * @param xnormAxis x-axis data array for normal distribution plots to set
     */
    private void setXnormAxis(double[] xnormAxis) {
        this.xnormAxis = xnormAxis;
    }

    /**
     * @return x-axis data array for normal distribution plots
     */
    private double[] getXnormAxis() {
        return this.xnormAxis;
    }

    /**
     * @param ynormAxis y-axis data array for normal distribution plots to set
     */
    private void setYnormAxis(double[] ynormAxis) {
        this.ynormAxis = ynormAxis;
    }

    /**
     * @return y-axis data array for normal distribution plots
     */
    private double[] getYnormAxis() {
        return this.ynormAxis;
    }

    /**
     * @param histoXAxis x-axis data array for normal distribution plots to set
     */
    private void setHistoXAxis(double[] histoXAxis) {
        this.histoXAxis = histoXAxis;
    }

    /**
     * @return x-axis data array for normal distribution plots
     */
    private double[] getHistoXAxis() {
        return this.histoXAxis;
    }

    /**
     * @param histoYAxis y-axis data array for normal distribution plots to set
     */
    private void setHistoYAxis(double[] histoYAxis) {
        this.histoYAxis = histoYAxis;
    }

    /**
     * @return y-axis data array for normal distribution plots
     */
    private double[] getHistoYAxis() {
        return this.histoYAxis;
    }

    /**
     * @param expected expected value to set
     */
    private void setExpected(double expected) {
        this.expected = expected;
    }

    /**
     * @return expected value
     */
    private double getExpected() {
        return this.expected;
    }

    /**
     * @param sum array for rms data to set
     */
    private void setSum(double[] sum) {
        this.sum = sum;
    }

    /**
     * @return array for rms data
     */
    private double[] getSum() {
        return this.sum;
    }

    /**
     * @param sumParts array for rms sum data to set
     */
    private void setSumParts(double sumParts) {
        this.sumParts = sumParts;
    }

    /**
     * @return array for rms sum data
     */
    private double getSumParts() {
        return this.sumParts;
    }

    /**
     * @param centerX drawing area center in x-axis to set
     */
    private void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    /**
     * @return drawing area center in x-axis
     */
    private double getCenterX() {
        return this.centerX;
    }

    /**
     * @param centerY drawing area center in y-axis to set
     */
    private void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    /**
     * @return drawing area center in y-axis
     */
    private double getCenterY() {
        return this.centerY;
    }

    /**
     * @param measure particle area limit from Fortran to set
     */
    private void setMeasure(double measure) {
        this.measure = measure;
    }

    /**
     * @return particle area limit from Fortran
     */
    private double getMeasure() {
        return this.measure;
    }

    /**
     * @return the mincount
     */
    private double getMincount() {
        return this.mincount;
    }

    /**
     * @param mincount the mincount to set
     */
    private void setMincount(double mincount) {
        this.mincount = mincount;
    }

    /**
     * @return the maxcount
     */
    private double getMaxcount() {
        return this.maxcount;
    }

    /**
     * @param maxcount the maxcount to set
     */
    private void setMaxcount(double maxcount) {
        this.maxcount = maxcount;
    }

    /**
     * @return whether to plot standard normal distribution
     */
    private boolean isStandPlot() {
        return this.standPlot;
    }

    /**
     * @return whether to plot diffusion normal distribution
     */
    private boolean isDiffPlot() {
        return this.diffPlot;
    }

    /**
     * @return the smallest
     */
    private double getSmallest() {
        return this.smallest;
    }

    /**
     */
    private void setSmallest() {
        this.smallest = 0.0;
    }

    /**
     * @return the greatest
     */
    private double getGreatest() {
        return this.greatest;
    }

    /**
     * @param greatest the greatest to set
     */
    private void setGreatest(double greatest) {
        this.greatest = greatest;
    }

    /**
     * @return the greatestdn
     */
    private double getGreatestDN() {
        return this.greatestdn;
    }

    /**
     * @param greatestdn the greatestdn to set
     */
    private void setGreatestDN(double greatestdn) {
        this.greatestdn = greatestdn;
    }

    /**
     * @return the runtime
     */
    private Runtime getRuntime() {
        return this.runtime;
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
    private int getExitVal() {
        return this.exitVal;
    }

    /**
     * @param exitVal the exitVal to set
     */
    private void setExitVal(int exitVal) {
        this.exitVal = exitVal;
    }

    /**
     *
     * @param others the others to set
     */
    private void setOthers(Map<String, double[]> others) {
        this.others = others;
    }

    /**
     * @return the others
     */
    private Map<String, double[]> getOthers() {
        return others;
    }

    /**
     * @return the language
     */
    private String getLanguage() {
        return this.language;
    }

    /**
     * @param language the language to set
     */
    private void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the plotChoice
     */
    public VBox getPlotChoice() {
        return this.plotChoice;
    }

    /**
     * @param plotChoice the plotChoice to set
     */
    private void setPlotChoice(VBox plotChoice) {
        this.plotChoice = plotChoice;
    }

    /**
     * @return the dim_choice
     */
    public HBox getDimension() {
        return this.dimChoice;
    }

    /**
     * @param dimChoice the dim_choice to set
     */
    private void setDimension(HBox dimChoice) {
        this.dimChoice = dimChoice;
    }

    /**
     * @return the fxplot
     */
    public FXPlot getFxplot() {
        return fxplot;
    }

    /**
     * @param fxplot the fxplot to set
     */
    public void setFxplot(FXPlot fxplot) {
        this.fxplot = fxplot;
    }

    /**
     * @return the saw_lengths
     */
    private List<Double> getSawLengths() {
        return this.sawLengths;
    }

    /**
     * @param sawLengths the saw_lengths to set
     */
    private void setSawLengths(List<Double> sawLengths) {
        this.sawLengths = sawLengths;
    }

    /**
     * @return the bigg_dist
     */
    private double getBiggDist() {
        return this.biggDist;
    }

    /**
     * bigg_dist to set
     */
    private void setBiggDist(double biggDist) {
        this.biggDist = biggDist;
    }

    /**
     * @return the sigSeed
     */
    private double getSigSeed() {
        return this.sigSeed;
    }

    /**
     * sigSeed to set
     */
    private void setSigSeed(double sigSeed) {
        this.sigSeed = sigSeed;
    }

    /**
     * @return the pane
     */
    private Pane getPane() {
        return this.pane;
    }

    /**
     * @param pane the pane to set
     */
    private void setPane(Pane pane) {
        this.pane = pane;
    }

    /**
     * @return the isovalikko
     */
    private HBox getIsoValikko() {
        return this.isovalikko;
    }

    /**
     * @param isovalikko the isovalikko to set
     */
    private void setIsoValikko(HBox isovalikko) {
        this.isovalikko = isovalikko;
    }
}
