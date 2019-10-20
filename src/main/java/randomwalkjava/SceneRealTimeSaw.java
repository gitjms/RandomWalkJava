
package randomwalkjava;

import com.sun.glass.ui.Screen;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
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
    private boolean running;
    private boolean runtimeRunning;
    private boolean firstdata;
    private FXPlot fxplot;
    private int dim;
    private int steps;
    private int memsteps;
    private long runs;
    private double bigg_dist;
    private List<Integer[]> saw_data;
    private List<Double> saw_lengths;
    private List<Double> saw_rms;
    private List<Double> saw_rms2;
    private List<Double> rms_runs;
    private List<Double> rms_runs2;
    private double expected;
    private double sum_expd;
    private List<Integer> xAxis;
    private List<Double> yrmsAxis1;
    private List<Double> yrmsAxis2;
    private List<Double> yexpdAxis;
    private List<Integer> xhistAxis;
    private List<Double> ylenAxis;
    private double[] greatestY;
    private double greatestY2;
    private Runtime runtime;
    private int exitVal;
    private ToggleButton setDim2;
    private ToggleButton setDim3;
    private HBox dim_choice;

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
            "0.1",  // vars[1] diameter         n/a
            "0",    // vars[2] charge           n/a
            "0",    // vars[3] steps            n/a
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
     * @param firstdata true if is first run
     * @param saw_lengths data container
     * @param saw_rms data container
     * @param saw_rms2 data container
     * @param rms_runs data container
     * @param rms_runs2 data container
     * @param xAxis data container
     * @param yrmsAxis1 data container
     * @param yrmsAxis2 data container
     * @param yexpdAxis data container
     * @param xHistAxis data container
     * @param ylenAxis data container
     */
    void refresh(File folder, String executable, boolean firstdata,
                 List<Double> saw_lengths, List<Double> saw_rms, List<Double> saw_rms2, List<Double> rms_runs,
                 List<Double> rms_runs2, List<Integer> xAxis, List<Double> yrmsAxis1, List<Double> yrmsAxis2,
                 List<Double> yexpdAxis, List<Integer> xHistAxis, List<Double> ylenAxis) {

        //this.setFxplot(fxplot);
        this.setSawData(new ArrayList<>());
        this.setSawLengths(saw_lengths);
        this.setRmsRuns(rms_runs);
        this.setRmsRuns2(rms_runs2);
        this.setXAxis(xAxis);
        this.setYrmsAxis1(yrmsAxis1);
        this.setYrmsAxis2(yrmsAxis2);
        this.setYexpdAxis(yexpdAxis);
        this.setXhistAxis(xHistAxis);
        this.setYlenAxis(ylenAxis);
        this.setFirstData(firstdata);
        this.dim(parseInt(this.vars[4]));
        this.getFxplot().setFrameVis();

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
                    if (!line.substring(0,1).matches("([0-9]|-|\\+|S|E)")) continue;

                    if ( !line.trim().startsWith("S") && !line.trim().startsWith("E") ) {
                        this.setSteps(this.getSteps()+1);
                        switch (this.getDim()) {
                            case 2: {
                                String[] valStr = line.split("(\\s+)");
                                try {
                                    int x = Integer.parseInt(valStr[0].trim());
                                    int y = Integer.parseInt(valStr[1].trim());
                                    Integer[] duo = new Integer[2];
                                    duo[0] = x;
                                    duo[1] = y;
                                    this.getSawData().add(duo);
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                                break;
                            }
                            case 3: {
                                String[] valStr = line.split("(\\s+)");
                                try {
                                    int x = Integer.parseInt(valStr[0].trim());
                                    int y = Integer.parseInt(valStr[1].trim());
                                    int z = Integer.parseInt(valStr[2].trim());
                                    Integer[] trio = new Integer[3];
                                    trio[0] = x;
                                    trio[1] = y;
                                    trio[2] = z;
                                    this.getSawData().add(trio);
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                                break;
                            }
                            default:
                                break;
                        }
                    } else if (line.trim().startsWith("E")) {

                        Integer[] first;
                        Integer[] last;

                        double d2 = 0.0;
                        if (this.getDim() == 2) {
                            first = this.getSawData().get(0);
                            last = this.getSawData().get(this.getSawData().size()-1);

                            double dx2 = Math.pow(Math.abs(first[0] - last[0]), 2.0);
                            double dy2 = Math.pow(Math.abs(first[1] - last[1]), 2.0);
                            d2 = dx2 + dy2;
                        } else if (this.getDim() == 3) {
                            first = this.getSawData().get(0);
                            last = this.getSawData().get(this.getSawData().size()-1);

                            double dx2 = Math.pow(Math.abs(first[0] - last[0]), 2.0);
                            double dy2 = Math.pow(Math.abs(first[1] - last[1]), 2.0);
                            double dz2 = Math.pow(Math.abs(first[2] - last[2]), 2.0);
                            d2 = dx2 + dy2 + dz2;
                        }

                        /*
                        * RMS AND LENGTH
                        */
                        double nuu = 3.0/(this.getDim() + 2.0);
                        double factor_B = this.getDim() == 2 ? 0.771 : 1.2167;

                        if (this.getRuns() > 9) this.getSawLengths().add(Math.sqrt(d2));
                        else this.getSawLengths().set((int) this.getRuns(), Math.sqrt(d2));
                        this.setBiggDist(Math.max(this.getBiggDist(), Math.sqrt(d2)));

                        this.getSawRms().add(d2);
                        this.getSawRms2().add(factor_B * Math.pow(this.getMemSteps(), 2.0*nuu) );

                        /*
                         * EXPECTED VALUE
                         */
                        double expd = Math.pow(this.getSteps(), 3.0/(this.getDim() + 2.0) ) / 2.0;
                        double sum_expd = this.getSumExpd() + expd;
                        this.setExpected(expd);
                        this.setSumExpd(sum_expd);
                        this.setMemsteps(this.getSteps());

                    } else if (line.trim().startsWith("S")) {
                        if (this.isFirstData()) {
                            this.setRuns(0);
                            this.setBiggDist(0.0);
                            this.setExpected(0.0);
                            this.setSumExpd(0.0);
                            this.setSawRms(saw_rms);
                            this.setSawRms2(saw_rms2);
                            this.setSteps(0);
                            this.setGreatestY(new double[10]);
                            for (int x = 0; x < 10; x++) this.getGreatestY()[x] = 0.0;
                            this.setGreatestY2(0.0);
                            this.getFxplot().setFrameVis();
                            continue;
                        }

                        if (this.getRuns() > 10) this.getXAxis().add((int) this.getRuns() - 1);

                        /*
                        * SAW DATA (RED LINE)
                        */
                        double rmssum = 0.0;
                        double rmssum2 = 0.0;
                        for ( int i = 0; i < this.getSawRms().size(); i++ ) {
                            rmssum += this.getSawRms().get(i);
                            rmssum2 += this.getSawRms2().get(i);
                        }

                        if (this.getRuns() > 10) {
                            this.getRmsRuns().add(Math.sqrt(rmssum/(this.getMemSteps() * this.getRuns())));
                            this.getRmsRuns2().add(Math.sqrt(rmssum2/(this.getMemSteps() * this.getRuns())));
                        } else {
                            this.getRmsRuns().set((int) this.getRuns() - 1, Math.sqrt(rmssum/(this.getMemSteps() * this.getRuns())));
                            this.getRmsRuns2().set((int) this.getRuns() - 1, Math.sqrt(rmssum2/(this.getMemSteps() * this.getRuns())));
                        }

                        double rmsVal1 = this.getRmsRuns().get((int) this.getRuns() - 1);
                        double rmsVal2 = this.getRmsRuns2().get((int) this.getRuns() - 1);
                        if (this.getRuns() > 10) {
                            this.getYrmsAxis1().add(rmsVal1);
                            this.getYrmsAxis2().add(rmsVal2);
                        } else {
                            this.getYrmsAxis1().set((int) this.getRuns() - 1, rmsVal1);
                            this.getYrmsAxis2().set((int) this.getRuns() - 1, rmsVal2);
                        }

                        double bigger = Math.max(rmsVal1, rmsVal2);

                        /*
                        * EXPECTED RMS (BLUE DASH LINE)
                        */
                        if (this.getRuns() > 10) this.getYexpdAxis().add(this.getExpected());
                        else this.getYexpdAxis().set((int) this.getRuns() - 1, this.getExpected());

                        double bigger2 = Math.max(bigger, this.getExpected());

                        /*
                        * SAW LENGTHS (ORANGE LINE)
                        */
                        double length;
                        if (this.getRuns() > 10) length = this.getSawLengths().get(this.getSawLengths().size() - 1);
                        else length = this.getSawLengths().get((int) this.getRuns() - 1);
                        if (this.getRuns() > 10) this.getYlenAxis().add(length);
                        else this.getYlenAxis().set((int) this.getRuns() - 1, length);

                        double bigger3 = Math.max(bigger2, length);

                        /*
                        * MAX VALUES FOR PLOTS
                        */
                        arraycopy(this.getGreatestY(), 1, this.getGreatestY(), 0, 9);
                        this.getGreatestY()[9] = bigger3;
                        double greatest = 0.0;
                        for (double i : this.getGreatestY()) if ( i > greatest) greatest = i > greatest ? i + 10.0 : greatest;

                        this.setGreatestY2(bigger3 > this.getGreatestY2() ? bigger3 : this.getGreatestY2());
                        this.getFxplot().setS1MaxY(greatest);
                        this.getFxplot().setS2MaxY(this.getGreatestY2());

                        /*
                        * SET PLOTS
                        */
                        if (this.getRuns() > 10) {
                            this.getFxplot().updateS1Data("rms 1",
                                this.getXAxis().subList((int) this.getRuns() - 10, (int) this.getRuns() - 1),
                                this.getYrmsAxis1().subList((int) this.getRuns() - 10, (int) this.getRuns() - 1)
                            );
                            this.getFxplot().updateS1Data("rms 2",
                                this.getXAxis().subList((int) this.getRuns() - 10, (int) this.getRuns() - 1),
                                this.getYrmsAxis2().subList((int) this.getRuns() - 10, (int) this.getRuns() - 1)
                            );
                            this.getFxplot().setS1MinX((int) this.getRuns() - 10);
                            this.getFxplot().setS1MaxX((int) this.getRuns() - 2);
                        } else {
                            this.getFxplot().updateS1Data("rms 1", this.getXAxis(), this.getYrmsAxis1() );
                            this.getFxplot().updateS1Data("rms 2", this.getXAxis(), this.getYrmsAxis2() );
                        }

                        if (this.getRuns() > 10) {
                            this.getFxplot().updateS1Data(this.getLanguage().equals("fin") ? "odotusarvo" : "expected value",
                                this.getXAxis().subList((int) this.getRuns() - 10, (int) this.getRuns() - 1),
                                this.getYexpdAxis().subList((int) this.getRuns() - 10, (int) this.getRuns() - 1)
                            );
                            this.getFxplot().setS2MaxX(this.getRuns() - 1);
                        } else {
                            this.getFxplot().updateS1Data(
                                this.getLanguage().equals("fin") ? "odotusarvo" : "expected value", this.getXAxis(), this.getYexpdAxis() );
                        }

                        if (this.getRuns() > 10)
                            this.getFxplot().updateS1Data(this.getLanguage().equals("fin") ? "etäisyys" : "distance",
                                this.getXAxis().subList( (int) this.getRuns() - 10, (int) this.getRuns() - 1 ),
                                this.getYlenAxis().subList( (int) this.getRuns() - 10, (int) this.getRuns() - 1 )
                            );
                        else this.getFxplot().updateS1Data(
                            this.getLanguage().equals("fin") ? "etäisyys" : "distance", this.getXAxis(), this.getYlenAxis() );

                        this.getFxplot().updateS2Data( "rms 1",this.getXAxis(), this.getYrmsAxis1() );
                        this.getFxplot().updateS2Data( "rms 2",this.getXAxis(), this.getYrmsAxis2() );
                        this.getFxplot().updateS2Data( this.getLanguage().equals("fin") ? "odotusarvo" : "expected value",this.getXAxis(), this.getYexpdAxis() );
                        this.getFxplot().updateS2Data( this.getLanguage().equals("fin") ? "etäisyys" : "distance",this.getXAxis(), this.getYlenAxis() );

                        this.getFxplot().updateS3Data( this.getXhistAxis(), calcHistogram(this.getSawLengths(), this.getDim()) );
                    }
                }

                this.setRuns(this.getRuns() + 1);
                this.setSteps(0);

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
    private static List<Double> calcHistogram(@NotNull List<Double> data, int dim) {
        double bin1 = 0; double bin2 = 0; double bin3 = 0; double bin4 = 0; double bin5 = 0;
        double bin6 = 0; double bin7 = 0; double bin8 = 0; double bin9 = 0; double bin10 = 0;
        double bin11 = 0; double bin12 = 0; double bin13 = 0; double bin14 = 0; double bin15 = 0;
        double bin16 = 0; double bin17 = 0; double bin18 = 0; double bin19 = 0; double bin20 = 0;

        List<Double> list = new ArrayList<>();

        for (double d : data) {
            if (dim == 2) {
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
            } else {
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
        if (dim == 3) {
            list.add(bin11); list.add(bin12); list.add(bin13); list.add(bin14); list.add(bin15);
            list.add(bin16); list.add(bin17); list.add(bin18); list.add(bin19); list.add(bin20);
        }

        return list;
    }

    /**
     * Create GUI for Real Time Rms
     * @return REAL TIME RMS SCENE
     */
    Parent getSceneRealTimeSaw(){
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
        this.vars[0] = "0"; // (number of particles)
        this.vars[1] = "0"; // (diameter of particle)
        this.vars[2] = "0"; // (charge of particles)
        this.vars[3] = "0"; // (number of steps)

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

        //HBox setDimension = new HBox(this.setDim2,this.setDim3);
        this.setDimension(new HBox(this.setDim2,this.setDim3));
        this.getDimension().setSpacing(20);
        this.setDim2.setOnMouseClicked(f -> {
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "2";
        });
        this.setDim3.setOnMouseClicked(f -> {
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
        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 0);
        GridPane.setHalignment(this.getDimension(), HPos.CENTER);
        this.getDimension().setMinWidth(getCompwidth());
        this.getDimension().setMaxWidth(getCompwidth());
        asettelu.add(this.getDimension(), 0, 1);

        final Pane empty1 = new Pane();
        GridPane.setHalignment(empty1, HPos.CENTER);
        asettelu.add(empty1, 0, 2, 2, 1);

        final Pane empty2 = new Pane();
        GridPane.setHalignment(empty2, HPos.CENTER);
        asettelu.add(empty2, 0, 3, 2, 1);

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 4, 2, 1);

        final Pane empty3 = new Pane();
        GridPane.setHalignment(empty3, HPos.CENTER);
        asettelu.add(empty3, 0, 5, 2, 1);

        final Pane empty4 = new Pane();
        GridPane.setHalignment(empty4, HPos.CENTER);
        asettelu.add(empty4, 0, 6, 2, 1);

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
     * @return the memsteps
     */
    @Contract(pure = true)
    private long getMemSteps() { return this.memsteps; }

    /**
     * @param memsteps the memsteps to set
     */
    private void setMemsteps(int memsteps) { this.memsteps = memsteps; }

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
     * @param yrmsAxis1 y-axis data array for walk plot to set
     */
    private void setYrmsAxis1(List<Double> yrmsAxis1) { this.yrmsAxis1 = yrmsAxis1; }

    /**
     * @return y-axis data array for walk plot
     */
    @Contract(pure = true)
    private List<Double> getYrmsAxis1() { return this.yrmsAxis1; }

    /**
     * @param yrmsAxis2 y-axis data array for walk plot to set
     */
    private void setYrmsAxis2(List<Double> yrmsAxis2) { this.yrmsAxis2 = yrmsAxis2; }

    /**
     * @return y-axis data array for walk plot
     */
    @Contract(pure = true)
    private List<Double> getYrmsAxis2() { return this.yrmsAxis2; }

    /**
     * @param yexpdAxis y-axis data array for walk plot to set
     */
    private void setYexpdAxis(List<Double> yexpdAxis) { this.yexpdAxis = yexpdAxis; }

    /**
     * @return y-axis data array for walk plot
     */
    @Contract(pure = true)
    private List<Double> getYexpdAxis() { return this.yexpdAxis; }

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
     * @param ylenAxis y-axis data array for walk plot to set
     */
    private void setYlenAxis(List<Double> ylenAxis) { this.ylenAxis = ylenAxis; }

    /**
     * @return y-axis data array for walk plot
     */
    @Contract(pure = true)
    private List<Double> getYlenAxis() { return this.ylenAxis; }

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
     * @return the saw_data
     */
    @Contract(pure = true)
    private List <Integer[]> getSawData() { return this.saw_data; }

    /**
     * @param saw_data the saw_data to set
     */
    private void setSawData(List<Integer[]> saw_data) { this.saw_data = saw_data; }

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
     * @return the saw_rms2
     */
    @Contract(pure = true)
    private List <Double> getSawRms2() { return this.saw_rms2; }

    /**
     * @param saw_rms2 the saw_rms2 to set
     */
    private void setSawRms2(List<Double> saw_rms2) { this.saw_rms2 = saw_rms2; }

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
     * @return the rms_runs2
     */
    @Contract(pure = true)
    private List<Double> getRmsRuns2() { return this.rms_runs2; }

    /**
     * rms_runs2 to set
     */
    private void setRmsRuns2(List<Double> rms_runs2) { this.rms_runs2 = rms_runs2; }

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
     * @return the expected
     */
    @Contract(pure = true)
    private double getExpected() { return this.expected; }

    /**
     * expected to set
     */
    private void setExpected(double expected) { this.expected = expected; }

    /**
     * @return the sum_expd
     */
    @Contract(pure = true)
    private double getSumExpd() { return this.sum_expd; }

    /**
     * sum_expd to set
     */
    private void setSumExpd(double sum_expd) { this.sum_expd = sum_expd; }

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
}
