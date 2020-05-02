package jms.randomwalk.scenes;

import enums.DblSizes;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jms.randomwalk.datahandling.Data;
import jms.randomwalk.plots.FXPlot;
import jms.randomwalk.ui.GetComponents;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.apache.maven.surefire.shade.booter.org.apache.commons.lang3.SystemUtils;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for Real Time Saw.
 */
public class SceneRealTimeSaw extends Data {

    private String language;
    private final boolean isWin;
    private VBox stepcomps;
    private VBox dimcomps;
    private HBox comps;
    private boolean issaw;
    private boolean iseff;
    private boolean running;
    private boolean runtimeRunning;
    private boolean firstdata;
    private FXPlot fxplot;
    private int dim;
    private int steps;
    private long runs;
    private long failed;
    private double biggDist;
    private List<Double> sawLengths;
    private List<Double> sawExpd;
    private List<Double> sawRms;
    private List<Double> expdRuns;
    private List<Double> rmsRuns;
    private List<Double> effRuns;
    private List<Double> succRuns;
    private List<Integer> xAxis;
    private List<Integer> xhistAxis;
    private double[] greatestY;
    private double greatestY2;
    private Runtime runtime;
    private int exitVal;
    private ToggleButton setDim2;
    private ToggleButton setDim3;
    private HBox dimChoice;
    private Slider aaSlider;
    private double mcSumweight;
    private int maxRuns;

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
    public SceneRealTimeSaw(String language) {
        super();
        this.setLanguage(language);
        this.isWin = SystemUtils.IS_OS_WINDOWS;
        this.vars = new String[]{
            "E",    // vars[0] which simulation         n/a
            "0",    // vars[1] particles                n/a
            "0",    // vars[2] diameter                 n/a
            "0",    // vars[3] steps                    USER
            "0",    // vars[4] dimension                USER
            "-",    // vars[5] efficiency or sawplot    n/a
            "-",    // vars[6] fixed(/spread)           n/a
            "-",    // vars[7] lattice/(free)           n/a
            "-"};   // vars[8] save (off)               n/a
        this.running = false;
        this.setIsSaw(true);
    }

    /**
     * Real Time Rms.
     * @param folder datafolder "C:/RWDATA"
     * @param executable Fortran executable "walk.exe" or "walkLx"
     * @param firstdata true if is first run
     * @param sawLengths data container
     * @param sawExpd data container
     * @param sawRms data container
     * @param expdRuns data container
     * @param rmsRuns data container
     * @param effRuns data container
     * @param succRuns data container
     * @param xAxis data container
     * @param xHistAxis data container
     * @param issaw saw or mc-saw
     * @param iseff efficiency
     * @param aaSlider Slider for factor A
     * @param maxRuns max runs for efficiency run
     */
    public void refresh(File folder, String executable, boolean firstdata, List<Double> sawLengths,
        List<Double> sawExpd, List<Double> sawRms, List<Double> expdRuns, List<Double> rmsRuns,
        List<Double> effRuns, List<Double> succRuns, List<Integer> xAxis, List<Integer> xHistAxis,
        boolean issaw, boolean iseff, Slider aaSlider, int maxRuns) {

        this.setFirstData(firstdata);
        this.dim(Integer.parseInt(this.vars[4]));
        this.setSteps(0);
        this.setIsSaw(issaw);
        this.setIsEff(iseff);
        if (this.isFirstData()) {
            this.setRuns(0);
            this.setFailed(0);
            if (!this.isEff()) {
                this.setBiggDist(0.0);
                this.setXhistAxis(xHistAxis);
                this.setSawExpd(sawExpd);
                this.setAaSlider(aaSlider);
                this.setSawRms(sawRms);
                this.setExpdRuns(expdRuns);
                this.setSawLengths(sawLengths);
                this.setRmsRuns(rmsRuns);
                this.setGreatestY(new double[10]);
                for (int x = 0; x < 10; x++) {
                    this.getGreatestY()[x] = 0.0;
                }
                this.setGreatestY2(0.0);
            } else {
                this.setMaxRuns(maxRuns);
                this.setEffRuns(effRuns);
                this.setSuccRuns(succRuns);
                this.setMCSumWeight(0.0);
            }
            this.setXAxis(xAxis);
            this.getFxplot().setFrameVis();
        }

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
                    if (line.isEmpty()) {
                        continue;
                    }
                    if (!line.substring(0, 1).matches("([0-9]|-|F|E)")) {
                        continue;
                    }
                    if (line.startsWith("F")) {
                        this.setFailed(this.getFailed() + 1);
                        if (this.isEff()) {
                            if (this.getRuns() >= this.getMaxRuns()) {
                                this.getEffRuns().add(null);
                                this.getSuccRuns().add(null);
                            } else {
                                this.getEffRuns().set((int) this.getRuns(), null);
                                this.getSuccRuns().set((int) this.getRuns(), null);
                            }
                            if (this.getRuns() >= this.getMaxRuns()) {
                                this.getXAxis().add((int) this.getRuns());
                            }
                            this.setRuns(this.getRuns() + 1);
                        }
                        continue;
                    }
                    String[] valStr = line.split("(\\s+)");
                    if (!this.isEff()) {
                        /*
                         * STEPS
                         */
                        this.setSteps(Integer.parseInt(valStr[0].trim()));

                        /*
                         * RMS EXPECTED SQRT(A)*S^NU (RED LINE)
                         */
                        this.getSawExpd().add(this.getAaSlider().getValue() * Math.pow(this.getSteps(), this.getNu()));

                        /*
                         * RMS <R^2> (BLUE LINE), gets r^2 from Fortran code
                         */
                        double rms = Double.parseDouble(valStr[1].trim());
                        this.getSawRms().add(rms);

                        /*
                         * LENGTH (YELLOW), gets r^2 from Fortran code and makes it r
                         */
                        double length = Math.sqrt(Double.parseDouble(valStr[1].trim()));
                        if (this.getRuns() > 9) {
                            this.getSawLengths().add(length);
                        } else {
                            this.getSawLengths().set((int) this.getRuns(), length);
                        }

                        this.setBiggDist(length);

                        if (this.getRuns() > 9) {
                            this.getXAxis().add((int) this.getRuns());
                        }

                    } else {
                        this.setMCSumWeight(this.getMCSumWeight() + Double.parseDouble(valStr[1].trim()));
                    }

                    if (!this.isEff()) {
                        /*
                         * RMS EXPECTED A*S^NU (RED LINE)
                         */
                        double expdsum = 0.0;
                        for (int i = 0; i < this.getSawExpd().size(); i++) {
                            expdsum += this.getSawExpd().get(i);
                        }
                        double expdruns = expdsum / (this.getRuns() + 1);
                        if (this.getRuns() > 9) {
                            this.getExpdRuns().add(expdruns);
                        } else {
                            this.getExpdRuns().set((int) this.getRuns(), expdruns);
                        }

                        /*
                         * RMS <R^2> (BLUE LINE)
                         */
                        double rmssum = 0.0;
                        for (int i = 0; i < this.getSawRms().size(); i++) {
                            rmssum += this.getSawRms().get(i);
                        }
                        double rmsruns = Math.sqrt(rmssum / (this.getRuns() + 1));
                        if (this.getRuns() > 9) {
                            this.getRmsRuns().add(rmsruns);
                        } else {
                            this.getRmsRuns().set((int) this.getRuns(), rmsruns);
                        }

                        double bigger = Math.max(expdruns, rmsruns);
                        double biggest = Math.max(bigger, this.getBiggDist());

                        /*
                         * SUCCESSED RUNS AND EFFICIENCY PERCENTAGES
                         */
                        if (!this.isSaw()) {
                            // FIRST GRAPH TITLE
                            double succPros = 100.0 - ((double) this.getFailed() / (double) (this.getFailed() + this.getRuns() + 1) * 100.0);
                            this.getFxplot().setS1McsawTitle(this.getDim(), succPros);
                            // SECOND GRAPH TITLE
                            this.getFxplot().setS2SawTitle(expdruns, rmsruns);
                        }

                        /*
                         * MAX VALUES FOR PLOTS
                         */
                        double greatest = 0.0;
                        double fact = this.isSaw() ? 10.0 : 2.0;
                        if (this.getRuns() <= 9) {
                            this.getGreatestY()[(int) this.getRuns()] = biggest;
                            if (biggest > greatest) {
                                greatest = biggest + fact;
                            }
                        } else {
                            System.arraycopy(this.getGreatestY(), 1, this.getGreatestY(), 0, 9);
                            this.getGreatestY()[9] = biggest;
                            for (double i : this.getGreatestY()) {
                                if (i > greatest) {
                                    greatest = i > greatest ? i + fact : greatest;
                                }
                            }
                        }

                        this.setGreatestY2(biggest > this.getGreatestY2() ? biggest : this.getGreatestY2());
                        this.getFxplot().setS1MaxY(greatest);
                        this.getFxplot().setS2MaxY(this.getGreatestY2());

                        /*
                         * SET PLOTS
                         */
                        if (this.getRuns() > 9) {
                            this.getFxplot().setS1MinX((int) this.getRuns() - 9);
                            this.getFxplot().setS1MaxX((int) this.getRuns() - 1);

                            this.getFxplot().updateS1Data("<Rexp>",
                                this.getXAxis().subList((int) this.getRuns() - 9, (int) this.getRuns()),
                                this.getExpdRuns().subList((int) this.getRuns() - 9, (int) this.getRuns())
                            );
                            this.getFxplot().updateS1Data("<Rrms>",
                                this.getXAxis().subList((int) this.getRuns() - 9, (int) this.getRuns()),
                                this.getRmsRuns().subList((int) this.getRuns() - 9, (int) this.getRuns())
                            );
                            this.getFxplot().setS2MaxX(this.getRuns());

                            this.getFxplot().updateS1Data(this.getLanguage().equals("fin") ? "etäisyys" : "distance",
                                this.getXAxis().subList((int) this.getRuns() - 9, (int) this.getRuns()),
                                this.getSawLengths().subList((int) this.getRuns() - 9, (int) this.getRuns())
                            );

                        } else {
                            this.getFxplot().updateS1Data("<Rexp>", this.getXAxis(), this.getExpdRuns());
                            this.getFxplot().updateS1Data("<Rrms>", this.getXAxis(), this.getRmsRuns());
                            this.getFxplot().updateS1Data(this.getLanguage().equals("fin") ? "etäisyys" : "distance", this.getXAxis(), this.getSawLengths());
                        }

                        this.getFxplot().updateS2Data("<Rexp>", this.getXAxis(), this.getExpdRuns());
                        this.getFxplot().updateS2Data("<Rrms>", this.getXAxis(), this.getRmsRuns());
                        this.getFxplot().updateS2Data(this.getLanguage().equals("fin") ? "etäisyys" : "distance", this.getXAxis(), this.getSawLengths());

                        this.getFxplot().updateS3Data(this.getXhistAxis(), calcHistogram(this.getSawLengths(), this.getDim(), this.isSaw(), this.getSteps()));

                    } else {

                        double efficiency = this.getMCSumWeight() / ((double) (this.getRuns() + 1));
                        double succeeded = 1.0 - (double) this.getFailed() / ((double) (this.getFailed() + this.getRuns() + 1));

                        /*
                         * EFFICIENCY (RED LINE), SUCCEEDED RUNS (BLUE LINE)
                         */
                        if (this.getRuns() >= this.getMaxRuns()) {
                            this.getEffRuns().add(efficiency);
                            this.getSuccRuns().add(succeeded);
                            this.getXAxis().add((int) this.getRuns());
                        } else {
                            this.getEffRuns().set((int) this.getRuns(), efficiency);
                            this.getSuccRuns().set((int) this.getRuns(), succeeded);
                        }

                        /*
                         * SET PLOTS
                         */
                        if (this.getRuns() >= this.getMaxRuns()) {
                            this.getFxplot().setFMaxX(this.getRuns());
                        }

                        this.getFxplot().updateFData(this.getLanguage().equals("fin")
                            ? "tehokkuus" : "efficiency", this.getXAxis(), this.getEffRuns());
                        this.getFxplot().updateFData(this.getLanguage().equals("fin")
                            ? "onnistuneet ajot" : "succeeded runs", this.getXAxis(), this.getSuccRuns());

                    }

                    this.setRuns(this.getRuns() + 1);
                }

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
     * @param dim dimension
     * @return bin data list
     */
    private static List<Double> calcHistogram(List<Double> data, int dim, boolean issaw, int steps) {
        double bin1 = 0;
        double bin2 = 0;
        double bin3 = 0;
        double bin4 = 0;
        double bin5 = 0;
        double bin6 = 0;
        double bin7 = 0;
        double bin8 = 0;
        double bin9 = 0;
        double bin10 = 0;
        double bin11 = 0;
        double bin12 = 0; 
        double bin13 = 0; 
        double bin14 = 0; 
        double bin15 = 0;
        double bin16 = 0;
        double bin17 = 0;
        double bin18 = 0;
        double bin19 = 0; 
        double bin20 = 0;

        List<Double> list = new ArrayList<>();

        for (double d : data) {
            if ((steps < 30 && !issaw)) {
                if (d <= 1.0) {
                    bin1++;
                } else if (d <= 2.0) {
                    bin2++;
                } else if (d <= 3.0) {
                    bin3++;
                } else if (d <= 4.0) {
                    bin4++;
                } else if (d <= 5.0) {
                    bin5++;
                } else if (d <= 6.0) {
                    bin6++;
                } else if (d <= 7.0) {
                    bin7++;
                } else if (d <= 8.0) {
                    bin8++;
                } else if (d <= 9.0) {
                    bin9++;
                } else if (d <= 10.0) {
                    bin10++;
                } else if (d <= 11.0) {
                    bin11++;
                } else if (d <= 12.0) {
                    bin12++;
                } else if (d <= 13.0) {
                    bin13++;
                } else if (d <= 14.0) {
                    bin14++;
                } else if (d <= 15.0) {
                    bin15++;
                } else if (d <= 16.0) {
                    bin16++;
                } else if (d <= 17.0) {
                    bin17++;
                } else if (d <= 18.0) {
                    bin18++;
                } else if (d <= 19.0) {
                    bin19++;
                } else if (d <= 20.0) {
                    bin20++;
                }
            } else if ((dim == 2) || (steps >= 30 && steps < 200 && !issaw)) {
                if (d <= 3.0) {
                    bin1++;
                } else if (d <= 6.0) {
                    bin2++;
                } else if (d <= 9.0) {
                    bin3++;
                } else if (d <= 12.0) {
                    bin4++;
                } else if (d <= 15.0) {
                    bin5++;
                } else if (d <= 18.0) {
                    bin6++;
                } else if (d <= 21.0) {
                    bin7++;
                } else if (d <= 24.0) {
                    bin8++;
                } else if (d <= 27.0) {
                    bin9++;
                } else if (d <= 30.0) {
                    bin10++;
                } else if (d <= 33.0) {
                    bin11++;
                } else if (d <= 36.0) {
                    bin12++;
                } else if (d <= 39.0) {
                    bin13++;
                } else if (d <= 42.0) {
                    bin14++;
                } else if (d <= 45.0) {
                    bin15++;
                } else if (d <= 48.0) {
                    bin16++;
                } else if (d <= 51.0) {
                    bin17++;
                } else if (d <= 54.0) {
                    bin18++;
                } else if (d <= 57.0) {
                    bin19++;
                } else if (d <= 60.0) {
                    bin20++;
                }
            } else if (dim == 3 && !issaw) {
                if (d <= 10.0) {
                    bin1++;
                } else if (d <= 20.0) {
                    bin2++;
                } else if (d <= 30.0) {
                    bin3++;
                } else if (d <= 40.0) {
                    bin4++;
                } else if (d <= 50.0) {
                    bin5++;
                } else if (d <= 60.0) {
                    bin6++;
                } else if (d <= 70.0) {
                    bin7++;
                } else if (d <= 80.0) {
                    bin8++;
                } else if (d <= 90.0) {
                    bin9++;
                } else if (d <= 100.0) {
                    bin10++;
                } else if (d <= 110.0) {
                    bin11++;
                } else if (d <= 120.0) {
                    bin12++;
                } else if (d <= 130.0) {
                    bin13++;
                } else if (d <= 140.0) {
                    bin14++;
                } else if (d <= 150.0) {
                    bin15++;
                } else if (d <= 160.0) {
                    bin16++;
                } else if (d <= 170.0) {
                    bin17++;
                } else if (d <= 180.0) {
                    bin18++;
                } else if (d <= 190.0) {
                    bin19++;
                } else if (d <= 200.0) {
                    bin20++;
                }
            } else if (dim == 3) {
                if (d <= 20.0) {
                    bin1++;
                } else if (d <= 40.0) {
                    bin2++;
                } else if (d <= 60.0) {
                    bin3++;
                } else if (d <= 80.0) {
                    bin4++;
                } else if (d <= 100.0) {
                    bin5++;
                } else if (d <= 120.0) {
                    bin6++;
                } else if (d <= 140.0) {
                    bin7++;
                } else if (d <= 160.0) {
                    bin8++;
                } else if (d <= 180.0) {
                    bin9++;
                } else if (d <= 200.0) {
                    bin10++;
                } else if (d <= 220.0) {
                    bin11++;
                } else if (d <= 240.0) {
                    bin12++;
                } else if (d <= 260.0) {
                    bin13++;
                } else if (d <= 280.0) {
                    bin14++;
                } else if (d <= 300.0) {
                    bin15++;
                } else if (d <= 320.0) {
                    bin16++;
                } else if (d <= 340.0) {
                    bin17++;
                } else if (d <= 360.0) {
                    bin18++;
                } else if (d <= 380.0) {
                    bin19++;
                } else if (d <= 400.0) {
                    bin20++;
                }
            }
        }

        list.add(bin1);
        list.add(bin2);
        list.add(bin3);
        list.add(bin4);
        list.add(bin5);
        list.add(bin6);
        list.add(bin7);
        list.add(bin8);
        list.add(bin9);
        list.add(bin10);
        list.add(bin11);
        list.add(bin12);
        list.add(bin13);
        list.add(bin14);
        list.add(bin15);
        list.add(bin16);
        list.add(bin17);
        list.add(bin18);
        list.add(bin19);
        list.add(bin20);

        return list;
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
     * Create GUI for Real Time SAW
     * @param sliderBox GUI component
     * @param aaSlider GUI component
     * @param pane GUI component
     * @param runSAW GUI component
     * @param runMCSAW GUI component
     * @return REAL TIME RMS SCENE
     */
    public Parent getSceneRealTimeSaw(VBox sliderBox, Slider aaSlider, Pane pane, Button runSAW,
        Button runMCSAW) {
        
        this.setAaSlider(aaSlider);
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(DblSizes.PANEW.getDblSize());
        asettelu.setVgap(4);
        asettelu.setHgap(5);
        asettelu.setPadding(new Insets(0, 0, 0, 0));
        VBox valikko = new VBox();
        valikko.setPadding(new Insets(20, 10, 0, 0));
        valikko.setSpacing(10);

        DropShadow shadow = new DropShadow();
        GetComponents getComponents = new GetComponents();
        Image imgSawFI = new Image("/sawFI.png");
        Image imgSawEN = new Image("/sawEN.png");
        ImageView ivSawFI = new ImageView(imgSawFI);
        ImageView ivSawEN = new ImageView(imgSawEN);
        ivSawFI.setSmooth(true);
        ivSawEN.setSmooth(true);
        getComponents.getPaneView(pane, this.getLanguage().equals("fin")
            ? ivSawFI : ivSawEN, DblSizes.SAWTXTW.getDblSize(), DblSizes.TXTH.getDblSize());

        /*
         * COMPONENTS...
         */
        this.vars[1] = "0"; // (number of particles)
        this.vars[2] = "0"; // (diameter of particle)

        Label labNumSteps = new Label(this.getLanguage().equals("fin") ? "askeleet:" : "steps:");
        TextField setNumSteps = new TextField("");
        setNumSteps.setMaxWidth(DblSizes.SMLCOMPW.getDblSize());
        this.dim(2);
        setNumSteps.setOnKeyReleased(e -> {
            if (isNumInteger(setNumSteps.getText().trim())) {
                this.vars[3] = setNumSteps.getText().trim();
                runSAW.setDisable(true);
                runMCSAW.setDisable(false);
                this.setIsSaw(false);
            } else {
                this.vars[3] = "0";
                runSAW.setDisable(false);
                runMCSAW.setDisable(true);
                this.setIsSaw(true);
            }
            this.getAaSlider().setValue(this.getAmplitude());
        });
        setNumSteps.setOnKeyTyped(e -> {
            if (isNumInteger(setNumSteps.getText().trim())) {
                this.vars[3] = setNumSteps.getText().trim();
                runSAW.setDisable(true);
                runMCSAW.setDisable(false);
                this.setIsSaw(false);
            } else {
                this.vars[3] = "0";
                runSAW.setDisable(false);
                runMCSAW.setDisable(true);
                this.setIsSaw(true);
            }
            this.getAaSlider().setValue(this.getAmplitude());
        });
        runMCSAW.setDisable(true);

        Label labNumDimensions = new Label(this.getLanguage().equals("fin") ? "ulottuvuus:" : "dimension:");

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

        this.setDimension(new HBox(this.setDim2, this.setDim3));
        this.getDimension().setSpacing(5);
        this.setDim2.setOnMouseClicked(f -> {
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.getAaSlider().setValue(this.getAmplitude());
            this.vars[4] = "2";
            this.dim(2);
        });
        this.setDim2.setOnMouseReleased(f -> {
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.getAaSlider().setValue(this.getAmplitude());
            this.vars[4] = "2";
            this.dim(2);
        });
        this.setDim3.setOnMouseClicked(f -> {
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
            this.getAaSlider().setValue(this.getAmplitude());
            this.vars[4] = "3";
            this.dim(3);
        });
        this.setDim3.setOnMouseReleased(f -> {
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
            this.getAaSlider().setValue(this.getAmplitude());
            this.vars[4] = "3";
            this.dim(3);
        });

        this.setStepComps(new VBox(labNumSteps, setNumSteps));
        this.setDimComps(new VBox(labNumDimensions, this.getDimension()));
        this.setComps(new HBox(this.getStepComps(), this.getDimComps()));
        this.getComps().setSpacing(10);

        this.vars[5] = "-"; // efficiency or sawplot    n/a
        this.vars[6] = "-"; // fixed(/spread)           n/a
        this.vars[7] = "-"; // lattice/(free)           n/a
        this.vars[8] = "-"; // save (off)               n/a

        /*
         * ...THEIR PLACEMENTS
         */
        GridPane.setHalignment(this.getComps(), HPos.LEFT);
        asettelu.add(this.getComps(), 0, 0);

        final Pane empty1 = new Pane();
        GridPane.setHalignment(empty1, HPos.CENTER);
        asettelu.add(empty1, 0, 1, 2, 1);

        GridPane.setHalignment(sliderBox, HPos.LEFT);
        asettelu.add(sliderBox, 0, 2, 2, 1);

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 3, 2, 1);

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
     * @return the failed
     */
    private long getFailed() {
        return this.failed;
    }

    /**
     * @param failed the failed to set
     */
    private void setFailed(long failed) {
        this.failed = failed;
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
     * @param xAxis x-axis data array to set
     */
    private void setXAxis(List<Integer> xAxis) {
        this.xAxis = xAxis;
    }

    /**
     * @return x-axis data array
     */
    private List<Integer> getXAxis() {
        return this.xAxis;
    }

    /**
     * @param aaSlider to set
     */
    private void setAaSlider(Slider aaSlider) {
        this.aaSlider = aaSlider;
    }

    /**
     * @return aaSlider
     */
    private Slider getAaSlider() {
        return this.aaSlider;
    }

    /**
     * @param xhistAxis x-axis data array for walk plot to set
     */
    private void setXhistAxis(List<Integer> xhistAxis) {
        this.xhistAxis = xhistAxis;
    }

    /**
     * @return x-axis data array for walk plot
     */
    private List<Integer> getXhistAxis() {
        return this.xhistAxis;
    }

    /**
     * @return the greatestY
     */
    private double[] getGreatestY() {
        return this.greatestY;
    }

    /**
     * @param greatestY the greatestY to set
     */
    private void setGreatestY(double[] greatestY) {
        this.greatestY = greatestY;
    }

    /**
     * @return the greatestY2
     */
    private double getGreatestY2() {
        return this.greatestY2;
    }

    /**
     * @param greatestY2 the greatestY2 to set
     */
    private void setGreatestY2(double greatestY2) {
        this.greatestY2 = greatestY2;
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
     * @return the saw_expd
     */
    private List<Double> getSawExpd() {
        return this.sawExpd;
    }

    /**
     * @param sawExpd the saw_expd to set
     */
    private void setSawExpd(List<Double> sawExpd) {
        this.sawExpd = sawExpd;
    }

    /**
     * @return the saw_rms
     */
    private List<Double> getSawRms() {
        return this.sawRms;
    }

    /**
     * @param sawRms the saw_rms to set
     */
    private void setSawRms(List<Double> sawRms) {
        this.sawRms = sawRms;
    }

    /**
     * @return the expd_runs
     */
    private List<Double> getExpdRuns() {
        return this.expdRuns;
    }

    /**
     * expd_runs to set
     */
    private void setExpdRuns(List<Double> expdRuns) {
        this.expdRuns = expdRuns;
    }

    /**
     * @return the rms_runs
     */
    private List<Double> getRmsRuns() {
        return this.rmsRuns;
    }

    /**
     * @param rmsRuns the rms_runs to set
     */
    private void setRmsRuns(List<Double> rmsRuns) {
        this.rmsRuns = rmsRuns;
    }

    /**
     * @return the eff_runs
     */
    private List<Double> getEffRuns() {
        return this.effRuns;
    }

    /**
     * @param effRuns the eff_runs to set
     */
    private void setEffRuns(List<Double> effRuns) {
        this.effRuns = effRuns;
    }

    /**
     * @return the succ_runs
     */
    private List<Double> getSuccRuns() {
        return this.succRuns;
    }

    /**
     * @param succRuns the succ_runs to set
     */
    private void setSuccRuns(List<Double> succRuns) {
        this.succRuns = succRuns;
    }

    /**
     * @return the firstdata
     */
    private boolean isFirstData() {
        return this.firstdata;
    }

    /**
     * @param firstdata the firstdata to set
     */
    private void setFirstData(boolean firstdata) {
        this.firstdata = firstdata;
    }

    /**
     * @return the steps
     */
    private int getSteps() {
        return this.steps;
    }

    /**
     * @param steps the steps to set
     */
    private void setSteps(int steps) {
        this.steps = steps;
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
     * @param dimcomps the dimcomps to set
     */
    private void setDimComps(VBox dimcomps) {
        this.dimcomps = dimcomps;
    }

    /**
     * @return the dimcomps
     */
    private VBox getDimComps() {
        return this.dimcomps;
    }

    /**
     * @param stepcomps the stepcomps to set
     */
    private void setStepComps(VBox stepcomps) {
        this.stepcomps = stepcomps;
    }

    /**
     * @return the stepcomps
     */
    private VBox getStepComps() {
        return this.stepcomps;
    }

    /**
     * @param comps the comps to set
     */
    private void setComps(HBox comps) {
        this.comps = comps;
    }

    /**
     * @return the comps
     */
    private HBox getComps() {
        return this.comps;
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
     * @return the issaw
     */
    private boolean isSaw() {
        return this.issaw;
    }

    /**
     * issaw to set
     */
    private void setIsSaw(boolean issaw) {
        this.issaw = issaw;
    }

    /**
     * @return the iseff
     */
    private boolean isEff() {
        return this.iseff;
    }

    /**
     * @param iseff the iseff to set
     */
    private void setIsEff(boolean iseff) {
        this.iseff = iseff;
    }

    /**
     * @return the mc_sumweight
     */
    private double getMCSumWeight() {
        return this.mcSumweight;
    }

    /**
     * mc_sumweight to set
     */
    private void setMCSumWeight(double mcSumweight) {
        this.mcSumweight = mcSumweight;
    }

    /**
     * @return the max_runs
     */
    private int getMaxRuns() {
        return this.maxRuns;
    }

    /**
     * max_runs to set
     */
    private void setMaxRuns(int maxRuns) {
        this.maxRuns = maxRuns;
    }

    /**
     * @return the nu
     */
    private double getNu() {
        return this.getDim() == 2 ? 3.0 / 4.0 : 0.587597;
    }

    /**
     * @return the nu
     */
    private double getAmplitude() {
        return this.getDim() == 2 ? 0.64 : 0.56;
    }

}
