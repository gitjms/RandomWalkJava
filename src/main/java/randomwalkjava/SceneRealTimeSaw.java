
package randomwalkjava;

import com.sun.glass.ui.Screen;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.System.arraycopy;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for Real Time Saw
 */
@SuppressWarnings("SameReturnValue")
class SceneRealTimeSaw extends Data {

    private String language;
    private VBox stepcomps;
    private VBox dimcomps;
    private HBox comps;
    private boolean issaw;
    private boolean running;
    private boolean runtimeRunning;
    private boolean firstdata;
    private FXPlot fxplot;
    private int dim;
    private int steps;
    private long runs;
    private long failed;
    private double bigg_dist;
    private double mu1;
    private double mu2;
    private List<Double> saw_lengths;
    private List<Double> saw_rms;
    private List<Double> saw_rmsruns;
    private List<Double> rms_runs;
    private List<Double> expected;
    private List<Integer> xAxis;
    private List<Integer> xhistAxis;
    private double[] greatestY;
    private double greatestY2;
    private Runtime runtime;
    private int exitVal;
    private ToggleButton setDim2;
    private ToggleButton setDim3;
    private Button setFactors;
    private Button setHalfFact;
    private HBox dim_choice;
    private HBox opt_choice;
    private Slider gamSlider;
    private Slider aaSlider;
    private boolean half;

    /**
     * main class gets vars via this
     * @return clone of vars array
     */
    String[] getVars() { return this.vars.clone(); }

    /**
     * initiating user variable array and other variables
     */
    SceneRealTimeSaw(String language){
        super();
        this.setLanguage(language);
        this.vars = new String[]{
            "0",    // vars[0] particles        n/a
            "0",    // vars[1] diameter         n/a
            "0",    // vars[2] charge           n/a
            "0",    // vars[3] steps            USER
            "0",    // vars[4] dimension        USER
            "-",    // vars[5] mmc              n/a
            "-",    // vars[6] fixed(/spread)   n/a
            "-",    // vars[7] lattice/(free)   n/a
            "-"};   // vars[8] save (off)       n/a
        this.running = false;
    }

    /**
     * Real Time Rms
     * @param folder datafolder "C:/RWDATA"
     * @param executable Fortran executable "walk.exe"
     * @param firstdata true if is first run
     * @param saw_lengths data container
     * @param expected data container
     * @param saw_rms data container
     * @param saw_rmsruns data container
     * @param rms_runs data container
     * @param xAxis data container
     * @param xHistAxis data container
     * @param issaw saw or cbmc
     * @param gamSlider Slider
     * @param aaSlider Slider
     */
    void refresh(File folder, String executable, boolean firstdata, List<Double> saw_lengths, List<Double> expected,
                 List<Double> saw_rms, List<Double> saw_rmsruns, List<Double> rms_runs, List<Integer> xAxis,
                 List<Integer> xHistAxis, boolean issaw, Slider gamSlider, Slider aaSlider) {


        this.setFirstData(firstdata);
        this.dim(parseInt(this.vars[4]));
        this.setSteps(0);
        this.setIsSaw(issaw);
        if (this.isFirstData()) {
            this.setRuns(0);
            this.setFailed(0);
            if (issaw) {
                this.setMu1(0.0);
                this.setMu2(0.0);
            }
            this.setGamSlider(gamSlider);
            this.setAaSlider(aaSlider);
            this.setExpected(expected);
            this.setBiggDist(0.0);
            this.setRmsRuns(rms_runs);
            this.setXAxis(xAxis);
            this.setXhistAxis(xHistAxis);
            this.setSawRms(saw_rms);
            this.setSawRmsRuns(saw_rmsruns);
            this.setSawLengths(saw_lengths);
            this.setGreatestY(new double[10]);
            for (int x = 0; x < 10; x++) this.getGreatestY()[x] = 0.0;
            this.setGreatestY2(0.0);
            this.getFxplot().setFrameVis();
        }

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
                    if (line.isEmpty()) continue;
                    if (!line.substring(0,1).matches("([0-9]|-|F)")) continue;
                    if (line.substring(0,1).equals("F")) {
                        this.setFailed(this.getFailed() + 1);
                        continue;
                    }
                    String[] valStr = line.split("(\\s+)");
                    try {
                        /*
                         * STEPS
                         */
                        this.setSteps(Integer.parseInt(valStr[0].trim()));

                        /*
                         * EXPECTED (BLUE)
                         */
                        double expd = Double.parseDouble(valStr[1].trim());
                        if (this.getRuns() > 9) this.getExpected().add(this.isHalf() ? 0.5*expd : expd);
                        else this.getExpected().set((int) this.getRuns(), this.isHalf() ? 0.5*expd : expd);

                        double bigger = Math.max(this.isHalf() ? 0.5*expd : expd, this.getBiggDist());

                        /*
                         * RMS
                         */
                        double rms = Double.parseDouble(valStr[2].trim());
                        this.getSawRms().add(this.isHalf() ? 0.5*rms : rms);

                        double bigger2 = Math.max(bigger, this.isHalf() ? 0.5*rms : rms);

                        /*
                         * SAW DATA (RED LINE)
                         */
                        if (this.getRuns() > 9) this.getSawRmsRuns().add(this.isHalf() ? 0.5*rms : rms);
                        else this.getSawRmsRuns().set((int) this.getRuns(), this.isHalf() ? 0.5*rms : rms);

                        /*
                         * LENGTH (YELLOW)
                         */
                        double length = Double.parseDouble(valStr[3].trim());
                        if (this.getRuns() > 9) this.getSawLengths().add(length);
                        else this.getSawLengths().set((int) this.getRuns(), length);

                        this.setBiggDist(Math.max(bigger2, length));

                    } catch (NumberFormatException ignored) {
                    }

                    if (this.getRuns() > 9) this.getXAxis().add((int) this.getRuns());

                    /*
                     * SAW DATA (GREEN LINE)
                     */
                    double rmssum = 0.0;
                    for ( int i = 0; i < this.getSawRms().size(); i++ ) rmssum += this.getSawRms().get(i);
                    double rmssum2 = rmssum/(this.getRuns()+1);
                    if (this.getRuns() > 9) {
                        this.getRmsRuns().add(rmssum2);
                    } else {
                        if (this.getRuns() == 0)
                            this.getRmsRuns().set((int) this.getRuns(), 0.0);
                        else
                            this.getRmsRuns().set((int) this.getRuns(), rmssum2);
                    }

                    double biggest = Math.max(rmssum2, this.getBiggDist());

                    if (this.isSaw()) {
                        /*
                         * CONNECTIVE CONSTANT µ
                         */
                        this.setMu1(this.getMu1() + Math.pow((this.getRuns()+1), 1.0/this.getSteps()));
                        this.setMu2(this.getMu2() + Math.pow((this.getRuns()+1)/(this.getAaSlider().getValue()
                            * Math.pow(this.getSteps(), this.getGamSlider().getValue() - 1.0)), 1.0/this.getSteps()));
                        this.getFxplot().setS1SawTitle(this.getDim(), this.getMu1()/(this.getRuns()+1), this.getMu2()/(this.getRuns()+1));
                    } else
                        this.getFxplot().setS1CbmcTitle(this.getDim(), (double) this.getFailed()/(double) (this.getFailed() + this.getRuns()+1) * 100.0);

                    /*
                     * MAX VALUES FOR PLOTS
                     */
                    arraycopy(this.getGreatestY(), 1, this.getGreatestY(), 0, 9);
                    this.getGreatestY()[9] = biggest;
                    double greatest = 0.0;
                    for (double i : this.getGreatestY()) if ( i > greatest) greatest = i > greatest ? i + 10.0 : greatest;

                    this.setGreatestY2(biggest > this.getGreatestY2() ? biggest : this.getGreatestY2());
                    this.getFxplot().setS1MaxY(greatest);
                    this.getFxplot().setS2MaxY(this.getGreatestY2());

                    /*
                     * SET PLOTS
                     */
                    if (this.getRuns() > 9) {
                        this.getFxplot().updateS1Data("Rrms",
                            this.getXAxis().subList((int) this.getRuns() - 9, (int) this.getRuns()),
                            this.getSawRmsRuns().subList((int) this.getRuns() - 9, (int) this.getRuns())
                        );
                        this.getFxplot().updateS1Data("<Rrms>",
                            this.getXAxis().subList((int) this.getRuns() - 9, (int) this.getRuns()),
                            this.getRmsRuns().subList((int) this.getRuns() - 9, (int) this.getRuns())
                        );
                        this.getFxplot().setS1MinX((int) this.getRuns() - 9);
                        this.getFxplot().setS1MaxX((int) this.getRuns());
                    } else {
                        this.getFxplot().updateS1Data("Rrms", this.getXAxis(), this.getSawRmsRuns() );
                        this.getFxplot().updateS1Data("<Rrms>", this.getXAxis(), this.getRmsRuns() );
                    }

                    if (this.getRuns() > 9) {
                        this.getFxplot().updateS1Data(this.getLanguage().equals("fin") ? "odotusarvo" : "expected value",
                            this.getXAxis().subList((int) this.getRuns() - 9, (int) this.getRuns()),
                            this.getExpected().subList((int) this.getRuns() - 9, (int) this.getRuns())
                        );
                        this.getFxplot().setS2MaxX(this.getRuns());
                    } else {
                        this.getFxplot().updateS1Data(
                            this.getLanguage().equals("fin") ? "odotusarvo" : "expected value", this.getXAxis(), this.getExpected() );
                    }

                    if (this.getRuns() > 9)
                        this.getFxplot().updateS1Data(this.getLanguage().equals("fin") ? "etäisyys" : "distance",
                            this.getXAxis().subList( (int) this.getRuns() - 9, (int) this.getRuns() ),
                            this.getSawLengths().subList( (int) this.getRuns() - 9, (int) this.getRuns() )
                        );
                    else this.getFxplot().updateS1Data(
                        this.getLanguage().equals("fin") ? "etäisyys" : "distance", this.getXAxis(), this.getSawLengths() );

                    this.getFxplot().updateS2Data( "Rrms",this.getXAxis(), this.getSawRmsRuns() );
                    this.getFxplot().updateS2Data( "<Rrms>",this.getXAxis(), this.getRmsRuns() );
                    this.getFxplot().updateS2Data( this.getLanguage().equals("fin") ? "odotusarvo" : "expected value",this.getXAxis(), this.getExpected() );
                    this.getFxplot().updateS2Data( this.getLanguage().equals("fin") ? "etäisyys" : "distance",this.getXAxis(), this.getSawLengths() );

                    this.getFxplot().updateS3Data( this.getXhistAxis(), calcHistogram(this.getSawLengths(), this.getDim(), this.isSaw()) );

                    this.setRuns(this.getRuns() + 1);
                }

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
     * @param dim dimension
     * @return bin data list
     */
    @NotNull
    @Contract(pure = true)
    private static List<Double> calcHistogram(@NotNull List<Double> data, int dim, boolean issaw) {
        double bin1 = 0; double bin2 = 0; double bin3 = 0; double bin4 = 0; double bin5 = 0;
        double bin6 = 0; double bin7 = 0; double bin8 = 0; double bin9 = 0; double bin10 = 0;
        double bin11 = 0; double bin12 = 0; double bin13 = 0; double bin14 = 0; double bin15 = 0;
        double bin16 = 0; double bin17 = 0; double bin18 = 0; double bin19 = 0; double bin20 = 0;

        List<Double> list = new ArrayList<>();

        for (double d : data) {
            if (dim == 2 && !issaw) {
                if (d <= 1.0) bin1++;
                else if (d <= 2.0) bin2++;
                else if (d <= 3.0) bin3++;
                else if (d <= 4.0) bin4++;
                else if (d <= 5.0) bin5++;
                else if (d <= 6.0) bin6++;
                else if (d <= 7.0) bin7++;
                else if (d <= 8.0) bin8++;
                else if (d <= 9.0) bin9++;
                else if (d <= 10.0) bin10++;
                else if (d <= 11.0) bin11++;
                else if (d <= 12.0) bin12++;
                else if (d <= 13.0) bin13++;
                else if (d <= 14.0) bin14++;
                else if (d <= 15.0) bin15++;
                else if (d <= 16.0) bin16++;
                else if (d <= 17.0) bin17++;
                else if (d <= 18.0) bin18++;
                else if (d <= 19.0) bin19++;
                else if (d <= 20.0) bin20++;
            } else if (dim == 2 || (dim == 3 && !issaw)) {
                if (d <= 3.0) bin1++;
                else if (d <= 6.0) bin2++;
                else if (d <= 9.0) bin3++;
                else if (d <= 12.0) bin4++;
                else if (d <= 15.0) bin5++;
                else if (d <= 18.0) bin6++;
                else if (d <= 21.0) bin7++;
                else if (d <= 24.0) bin8++;
                else if (d <= 27.0) bin9++;
                else if (d <= 30.0) bin10++;
                else if (d <= 33.0) bin11++;
                else if (d <= 36.0) bin12++;
                else if (d <= 39.0) bin13++;
                else if (d <= 42.0) bin14++;
                else if (d <= 45.0) bin15++;
                else if (d <= 48.0) bin16++;
                else if (d <= 51.0) bin17++;
                else if (d <= 54.0) bin18++;
                else if (d <= 57.0) bin19++;
                else if (d <= 60.0) bin20++;
            } else if (dim == 3) {
                if (d <= 10.0) bin1++;
                else if (d <= 20.0) bin2++;
                else if (d <= 30.0) bin3++;
                else if (d <= 40.0) bin4++;
                else if (d <= 50.0) bin5++;
                else if (d <= 60.0) bin6++;
                else if (d <= 70.0) bin7++;
                else if (d <= 80.0) bin8++;
                else if (d <= 90.0) bin9++;
                else if (d <= 100.0) bin10++;
                else if (d <= 110.0) bin11++;
                else if (d <= 120.0) bin12++;
                else if (d <= 130.0) bin13++;
                else if (d <= 140.0) bin14++;
                else if (d <= 150.0) bin15++;
                else if (d <= 160.0) bin16++;
                else if (d <= 170.0) bin17++;
                else if (d <= 180.0) bin18++;
                else if (d <= 190.0) bin19++;
                else if (d <= 200.0) bin20++;
            }
        }

        list.add(bin1); list.add(bin2); list.add(bin3); list.add(bin4); list.add(bin5);
        list.add(bin6); list.add(bin7); list.add(bin8); list.add(bin9); list.add(bin10);
        list.add(bin11); list.add(bin12); list.add(bin13); list.add(bin14); list.add(bin15);
        list.add(bin16); list.add(bin17); list.add(bin18); list.add(bin19); list.add(bin20);

        return list;
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
    Parent getSceneRealTimeSaw(Slider sliderGam, Slider sliderAa, Pane pane, Button runSAW, Button runCBMC){
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(getPaneWidth());
        asettelu.setVgap(4);
        asettelu.setHgap(5);
        asettelu.setPadding(new Insets(0, 0, 0, 0));
        VBox valikko = new VBox();
        valikko.setPadding(new Insets(20, 10, 0, 0));
        valikko.setSpacing(10);

        DropShadow shadow = new DropShadow();
        GetComponents getComponents = new GetComponents();
        Image imgSawFI_one = new Image("file:src/main/resources/mathcardSawFI_one.png");
        Image imgSawEN_one = new Image("file:src/main/resources/mathcardSawEN_one.png");
        Image imgSawFI_half = new Image("file:src/main/resources/mathcardSawFI_half.png");
        Image imgSawEN_half = new Image("file:src/main/resources/mathcardSawEN_half.png");
        ImageView ivSawFI_one = new ImageView(imgSawFI_one);
        ImageView ivSawEN_one = new ImageView(imgSawEN_one);
        ImageView ivSawFI_half = new ImageView(imgSawFI_half);
        ImageView ivSawEN_half = new ImageView(imgSawEN_half);

        /*
         * COMPONENTS...
         */
        this.vars[0] = "0"; // (number of particles)
        this.vars[1] = "0"; // (diameter of particle)
        this.vars[2] = "0"; // (charge of particles)

        Label labNumSteps = new Label(this.getLanguage().equals("fin") ? "askeleet:" : "steps:");
        TextField setNumSteps = new TextField("");
        setNumSteps.setMaxWidth(this.getBigCompwidth());
        setNumSteps.setOnKeyReleased(e -> {
            if (isNumInteger(setNumSteps.getText().trim())){
                this.vars[3] = setNumSteps.getText().trim();
                runSAW.setDisable(true);
                runCBMC.setDisable(false);
            } else {
                this.vars[3] = "0";
                runSAW.setDisable(false);
                runCBMC.setDisable(true);
            }
        });
        runCBMC.setDisable(true);

        Label labNumDimensions = new Label(this.getLanguage().equals("fin") ? "ulottuvuus:" : "dimension:");

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

        this.setDimension(new HBox(this.setDim2,this.setDim3));
        this.getDimension().setSpacing(5);
        this.setDim2.setOnMouseClicked(f -> {
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            sliderGam.setValue(43.0/32.0);
            sliderAa.setValue(1.1771);
            this.vars[4] = "2";
            this.dim(2);
        });
        this.setDim3.setOnMouseClicked(f -> {
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            sliderGam.setValue(7.0/6.0);
            sliderAa.setValue(1.205);
            this.vars[4] = "3";
            this.dim(3);
        });

        this.setStepComps(new VBox(labNumSteps, setNumSteps));
        this.setDimComps(new VBox(labNumDimensions, this.getDimension()));
        this.setComps(new HBox(this.getStepComps(), this.getDimComps()));
        this.getComps().setSpacing(10);

        this.vars[5] = "-"; // mmc              n/a
        this.vars[6] = "-"; // fixed(/spread)   n/a
        this.vars[7] = "-"; // lattice/(free)   n/a
        this.vars[8] = "-"; // save (off)       n/a

        Label labOptions = new Label(this.getLanguage().equals("fin") ? "säätöä:" : "adjustment:");

        this.setFactors = new Button(this.getLanguage().equals("fin") ? "OLETUSARVOT" : "DEFAULTS");
        this.setFactors.setMinWidth(130);
        this.setFactors.setMaxWidth(130);
        this.setFactors.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setFactors.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setFactors.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setFactors.setEffect(shadow));
        this.setFactors.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setFactors.setEffect(null));
        this.setFactors.setOnMouseClicked(f -> {
            if (this.getDim() == 2 || this.getDim() == 3) {
                sliderGam.setValue(this.getDim() == 2 ? 43.0/32.0 : 7.0/6.0);
                sliderAa.setValue(this.getDim() == 2 ? 1.1771 : 1.205);
            }
        });
        this.setHalfFact = new Button("\u00B7 \u00BD"); // 1/2
        this.setHalfFact.setMinWidth(55);
        this.setHalfFact.setMaxWidth(55);
        this.setHalfFact.setFont(Font.font("System Regular",FontWeight.EXTRA_BOLD, 15));
        this.setHalfFact.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setHalfFact.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setHalfFact.setEffect(shadow));
        this.setHalfFact.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setHalfFact.setEffect(null));
        this.setHalf(false);
        this.setHalfFact.setOnMouseClicked(f -> {
            if (this.isHalf()) {
                pane.getChildren().removeAll();
                pane.getChildren().add(this.getLanguage().equals("fin") ? getComponents.getPane2(ivSawFI_one) : getComponents.getPane2(ivSawEN_one));
                this.setHalfFact.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
                this.setHalf(false);
            } else {
                pane.getChildren().removeAll();
                pane.getChildren().add(this.getLanguage().equals("fin") ? getComponents.getPane2(ivSawFI_half) : getComponents.getPane2(ivSawEN_half));
                this.setHalfFact.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
                this.setHalf(true);
            }
        });
        this.setOptions(new HBox(this.setFactors, this.setHalfFact));
        this.getOptions().setSpacing(15);

        /*
         * ...THEIR PLACEMENTS
         */
        GridPane.setHalignment(this.getComps(), HPos.LEFT);
        asettelu.add(this.getComps(), 0, 0);

        final Pane empty1 = new Pane();
        GridPane.setHalignment(empty1, HPos.CENTER);
        asettelu.add(empty1, 0, 1, 2, 1);

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 2, 2, 1);

        GridPane.setHalignment(labOptions, HPos.LEFT);
        asettelu.add(labOptions, 0, 3);
        GridPane.setHalignment(this.getOptions(), HPos.LEFT);
        asettelu.add(this.getOptions(), 0, 4);

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
    private double getBigCompwidth() { return 130.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the paneWidth
     */
    @Contract(pure = true)
    private double getPaneWidth() { return 200.0 / Screen.getMainScreen().getRenderScale(); }

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
     * @return the failed
     */
    @Contract(pure = true)
    private long getFailed() { return this.failed; }

    /**
     * @param failed the failed to set
     */
    private void setFailed(long failed) { this.failed = failed; }

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
     * @param xAxis x-axis data array to set
     */
    private void setXAxis(List<Integer> xAxis) { this.xAxis = xAxis; }

    /**
     * @return x-axis data array
     */
    @Contract(pure = true)
    private List<Integer> getXAxis() { return this.xAxis; }

    /**
     * @param expected y-axis data array for walk plot to set
     */
    private void setExpected(List<Double> expected) { this.expected = expected; }

    /**
     * @return y-axis data array for walk plot
     */
    @Contract(pure = true)
    private List<Double> getExpected() { return this.expected; }

    /**
     * @param gamSlider to set
     */
    private void setGamSlider(Slider gamSlider) { this.gamSlider = gamSlider; }

    /**
     * @return gamSlider
     */
    @Contract(pure = true)
    private Slider getGamSlider() { return this.gamSlider; }

    /**
     * @param aaSlider to set
     */
    private void setAaSlider(Slider aaSlider) { this.aaSlider = aaSlider; }

    /**
     * @return aaSlider
     */
    @Contract(pure = true)
    private Slider getAaSlider() { return this.aaSlider; }

    /**
     * @param xhistAxis x-axis data array for walk plot to set
     */
    private void setXhistAxis(List<Integer> xhistAxis) { this.xhistAxis = xhistAxis; }

    /**
     * @return x-axis data array for walk plot
     */
    @Contract(pure = true)
    private List<Integer> getXhistAxis() { return this.xhistAxis; }

    /**
     * @return the greatestY
     */
    @Contract(pure = true)
    private double[] getGreatestY() { return this.greatestY; }

    /**
     * @param greatestY the greatestY to set
     */
    private void setGreatestY(double[] greatestY) { this.greatestY = greatestY; }

    /**
     * @return the greatestY2
     */
    @Contract(pure = true)
    private double getGreatestY2() { return this.greatestY2; }

    /**
     * @param greatestY2 the greatestY2 to set
     */
    private void setGreatestY2(double greatestY2) { this.greatestY2 = greatestY2; }

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
     * @return the saw_lengths
     */
    @Contract(pure = true)
    private List <Double> getSawLengths() { return this.saw_lengths; }

    /**
     * @param saw_lengths the saw_lengths to set
     */
    private void setSawLengths(List<Double> saw_lengths) { this.saw_lengths = saw_lengths; }

    /**
     * @return the saw_rms
     */
    @Contract(pure = true)
    private List <Double> getSawRms() { return this.saw_rms; }

    /**
     * @param saw_rms the saw_rms to set
     */
    private void setSawRms(List<Double> saw_rms) { this.saw_rms = saw_rms; }

    /**
     * @return the saw_rmsruns
     */
    @Contract(pure = true)
    private List <Double> getSawRmsRuns() { return this.saw_rmsruns; }

    /**
     * @param saw_rmsruns the saw_rmsruns to set
     */
    private void setSawRmsRuns(List<Double> saw_rmsruns) { this.saw_rmsruns = saw_rmsruns; }

    /**
     * @return the rms_runs
     */
    @Contract(pure = true)
    private List<Double> getRmsRuns() { return this.rms_runs; }

    /**
     * rms_runs to set
     */
    private void setRmsRuns(List<Double> rms_runs) { this.rms_runs = rms_runs; }

    /**
     * @return the firstdata
     */
    @Contract(pure = true)
    private boolean isFirstData() { return this.firstdata; }

    /**
     * @param firstdata the firstdata to set
     */
    private void setFirstData( boolean firstdata ) { this.firstdata = firstdata; }

    /**
     * @return the steps
     */
    @Contract(pure = true)
    private int getSteps() { return this.steps; }

    /**
     * @param steps the steps to set
     */
    private void setSteps(int steps) { this.steps = steps; }

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
     * @return the dim_choice
     */
    @Contract(pure = true)
    HBox getDimension() { return this.dim_choice; }

    /**
     * @param dim_choice the dim_choice to set
     */
    private void setDimension(HBox dim_choice) { this.dim_choice = dim_choice; }

    /**
     * @param dimcomps the dimcomps to set
     */
    private void setDimComps(VBox dimcomps) { this.dimcomps = dimcomps; }

    /**
     * @return the dimcomps
     */
    @Contract(pure = true)
    private VBox getDimComps() { return this.dimcomps; }

    /**
     * @param stepcomps the stepcomps to set
     */
    private void setStepComps(VBox stepcomps) { this.stepcomps = stepcomps; }

    /**
     * @return the stepcomps
     */
    @Contract(pure = true)
    private VBox getStepComps() { return this.stepcomps; }

    /**
     * @param comps the comps to set
     */
    private void setComps(HBox comps) { this.comps = comps; }

    /**
     * @return the comps
     */
    @Contract(pure = true)
    private HBox getComps() { return this.comps; }

    /**
     * @return the opt_choice
     */
    @Contract(pure = true)
    private HBox getOptions() { return this.opt_choice; }

    /**
     * @param opt_choice the opt_choice to set
     */
    private void setOptions(HBox opt_choice) { this.opt_choice = opt_choice; }

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
     * @return the fxplot
     */
    @Contract(pure = true)
    FXPlot getFxplot() { return fxplot; }

    /**
     * @param fxplot the fxplot to set
     */
    void setFxplot( FXPlot fxplot ) { this.fxplot = fxplot; }

    /**
     * @return the mu1
     */
    @Contract(pure = true)
    private double getMu1() { return this.mu1; }

    /**
     * mu1 to set
     */
    private void setMu1(double mu1) { this.mu1 = mu1; }

    /**
     * @return the mu2
     */
    @Contract(pure = true)
    private double getMu2() { return this.mu2; }

    /**
     * mu to set
     */
    private void setMu2(double mu2) { this.mu2 = mu2; }

    /**
     * @return half
     */
    @Contract(pure = true)
    private boolean isHalf() { return this.half; }

    /**
     * half to set
     */
    @Contract(pure = true)
    private void setHalf(boolean half) { this.half = half; }

    /**
     * @return the issaw
     */
    @Contract(pure = true)
    private boolean isSaw() { return this.issaw; }

    /**
     * issaw to set
     */
    private void setIsSaw(boolean issaw) { this.issaw = issaw; }
}
