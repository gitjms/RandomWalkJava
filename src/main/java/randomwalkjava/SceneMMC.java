
package randomwalkjava;

import com.sun.glass.ui.Screen;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.jetbrains.annotations.Contract;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for MMC DIFFUSION
 */
@SuppressWarnings("SameReturnValue")
class SceneMMC extends Data {

    private String language;
    private ToggleButton setCharge1;
    private ToggleButton setCharge2;
    private ToggleButton setDim2;
    private ToggleButton setDim3;
    private final Button nappiLattice;
    private TextField setNumParticles;
    private TextField setSizeParticles;

    private long phaseEnergy;
    private long phaseDiffus;
    private long phaseVisc;
    private double smallest;
    private double greatest;
    private double greatestDiff;
    private double greatestVisc;
    private boolean firstEnergy;
    private boolean firstDiffus;
    private boolean firstVisc;
    private double linewidth;
    private double scalefactor;
    private boolean timerRunning;
    private double animwidth;
    private double center;
    private double initT;
    private double finT;
    private GraphicsContext piirturi;
    private FXPlot fxplot;
    private Button remBarNappiMMC;
    private VBox valikkoMMC;
    private Button runMMC;
    private final Button nappiBalls3D;
    private Button plotMMC;
    private Button closeNappiMMC;
    private Button menuNappiMMC;
    private Button helpNappiMMC;

    private boolean platfRunning;
    private Process process;
    private Runtime runtime;
    private int exitVal;
    private Timer timer;
    private double[][] values;
    private boolean running;
    private boolean barrier;
    private boolean walk;
    private boolean lattice;
    private boolean balls3D;
    private Image redP;
    private Image blueP;
    private Image yellowP;
    private Image grayP;
    private BufferedWriter output;

    private int num_part;
    private double diam;
    private int dim;
    private int charge;
    private List<Double> energy_x;
    private List<Double> energy_y;
    private List<Double> diffusion_x;
    private List<Double> diffusion_y;
    private List<Double> visc_x;
    private List<Double> visc_y;
    private double measure;
    private double differ;

    /**
     * main class gets vars via this
     * @return clone of vars array
     */
    String[] getVars() { return this.vars.clone(); }

    /**
     * initiating scene button and user variable array
     */
    SceneMMC(String language){
        super();
        this.setLanguage(language);
        this.nappiLattice = new Button(this.getLanguage().equals("fin") ? "VAPAA" : "FREE");
        this.nappiBalls3D = new Button(this.getLanguage().equals("fin") ? "PALLOT" : "BALLS");
        this.balls3D = true;
        this.vars = new String[]{
            "0",    // vars[0] particles        USER
            "0.0",  // vars[1] diameter         USER
            "0",    // vars[2] charge           USER
            "0",    // vars[3] steps            n/a
            "0",    // vars[4] dimension        USER
            "m",    // vars[5] mmc              n/a
            "-",    // vars[6] (fixed/)spread   n/a
            "-",    // vars[7] (lattice/)free   USER
            "-"};   // vars[8] save (off)       n/a
    }

    /**
     * MMC execution, uses Timer
     * @param folder datafolder "C:/RWDATA"
     * @param initialDataFile initial particle data
     * @param executable Fortran executable "walk.exe"
     * @param piirturi GraphicsContext which draws the animation
     * @param scalefactor scaling is used in different particle amounts
     * @param animwidth drawing area width
     * @param linewidth width for lines
     * @param remBarNappiMMC removing barrier in animation
     * @param runMMC run animation, no plot
     * @param valikkoMMC valikkoMMC
     * @param plotMMC plot initial and final particle configurations, no animation
     * @param closeNappiMMC close button must be disabled during run
     * @param menuNappiMMC menu button must be disabled during run
     * @param helpNappiMMC help button must be disabled during run
     * @param energy_x fxplot energy graph x-axis container
     * @param energy_y fxplot energy graph y-axis container
     * @param diffusion_x fxplot diffusion graph x-axis container
     * @param diffusion_y fxplot diffusion graph y-axis container
     * @param visc_x fxplot visosity graph x-axis container
     * @param visc_y fxplot visosity graph y-axis container
     * @param newdata if is a new run with new data
     * @param measure area/volume size
     * @param differ difference in between the lattice structure
     */
    void refresh(File folder, File initialDataFile, String executable,
                 GraphicsContext piirturi, double scalefactor, double animwidth, double linewidth,
                 Button remBarNappiMMC, Button runMMC, VBox valikkoMMC, Button plotMMC,
                 Button closeNappiMMC, Button menuNappiMMC, Button helpNappiMMC, List<Double> energy_x,
                 List<Double> energy_y, List<Double> diffusion_x, List<Double> diffusion_y, List<Double> visc_x,
                 List<Double> visc_y, boolean newdata, double measure, double differ) {

        this.setRedP(new Image("/Pred.png"));
        this.setBlueP(new Image("/Pblue.png"));
        this.setYellowP(new Image("/Pyellow.png"));
        this.setGrayP(new Image("/Pgray.png"));

        this.setPiirturi(piirturi);
        this.setLinewidth(linewidth);
        this.setAnimwidth(animwidth);
        this.setRemBarNappiMMC(remBarNappiMMC);
        this.setRunMMC(runMMC);
        this.setValikkoMMC(valikkoMMC);
        this.setPlotMMC(plotMMC);
        this.setCloseNappiMMC(closeNappiMMC);
        this.setMenuNappiMMC(menuNappiMMC);
        this.setHelpNappiMMC(helpNappiMMC);
        this.setCenter(this.getAnimwidth()/2.0);
        this.setMeasure(measure);
        this.setDiffer(differ);
        barrierOn();

        if (newdata) {
            this.setInitT(0);
            this.setNumPart(parseInt(this.vars[0]));
            this.setDiam(parseDouble(this.vars[1]));
            this.setCharge(parseInt(this.vars[2]));
            this.setDim(parseInt(this.vars[4]));
            this.setLattice(this.vars[7].equals("l"));
            this.setBalls3D(this.isBalls3D());
            this.setPhaseEnergy(0);
            this.setPhaseDiffus(0);
            this.setPhaseVisc(0);
            this.setFirstEnergy(false);
            this.setFirstDiffus(false);
            this.setFirstVisc(false);
            this.setScalefactor(scalefactor);
            this.setEnergyX(energy_x);
            this.setEnergyY(energy_y);
            this.setDiffusionX(diffusion_x);
            this.setDiffusionY(diffusion_y);
            this.setViscX(visc_x);
            this.setViscY(visc_y);
            this.energy_x.clear();
            this.energy_y.clear();
            this.diffusion_x.clear();
            this.diffusion_y.clear();
            this.visc_x.clear();
            this.visc_y.clear();
            this.setValues(new double[this.getDim() + 1][this.getNumPart()]);
            clearDots();
        }

        this.getRemBarNappiMMC().setVisible(true);
        this.getPlotMMC().setVisible(false);
        this.getMenuNappiMMC().setDisable(true);
        this.getHelpNappiMMC().setDisable(true);
        this.getCharge1().setDisable(true);
        this.getCharge2().setDisable(true);
        this.getDim2().setDisable(true);
        this.getDim3().setDisable(true);
        this.getNappiLattice().setDisable(true);
        this.getNappiBalls3D().setDisable(true);
        this.getValikkoMMC().getChildren().set(3, this.getRemBarNappiMMC());

        this.getRemBarNappiMMC().setOnMouseClicked(event -> {
            barrierOff();
            this.getValikkoMMC().getChildren().set(3, this.getRunMMC());
            this.getRunMMC().setDisable(true);
            this.getRemBarNappiMMC().setVisible(false);
            this.getCloseNappiMMC().setDisable(true);
            this.getFxplot().setFrameVis();
        });

        piirturi.setLineWidth(linewidth);

        String[] command;

        try
        {
        command = new String[]{"cmd","/c",executable,
            this.vars[0], this.vars[1], this.vars[2], this.vars[3], this.vars[4],
            this.vars[5], this.vars[6], this.vars[7], this.vars[8]};

        this.setRuntime(Runtime.getRuntime());
        runtimeStart();

        this.setProcess(this.getRuntime().exec(command, null, folder));
        walkStart();

        /*
        * DRAW INITIAL PARTICLES
        */
        while (true) {
            if (Files.notExists(initialDataFile.toPath())) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            } else if (Files.exists(initialDataFile.toPath())) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
                clearDots();
                drawInitials( initialDataFile );
                break;
            }
        }

        timerStart();
        this.setTimer(new Timer());
        this.getTimer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int i = 0;

                if ( !timerIsRunning()) return;

                while ( barrierIsOn() ) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                }

                if ( !barrierIsOn() ) {
                    setOutput(new BufferedWriter(new OutputStreamWriter(getProcess().getOutputStream())));
                    PrintWriter pw = null;
                    if (getOutput() != null) pw = new PrintWriter(getOutput());
                    if (pw != null) {
                        pw.println("x");
                        pw.flush();
                        pw.close();
                    }
                    try {
                        assert getOutput() != null;
                        getOutput().close();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }

                try (BufferedReader input = new BufferedReader(new InputStreamReader(
                    getProcess().getInputStream()))) {
                    String line;

                    while ((line = input.readLine()) != null){
                        if (line.trim().startsWith("S") || line.isEmpty()) {
                            continue;
                        }
                        if (!line.substring(0,1).matches("([0-9]|-|\\+|\\*)|E|D|V"))
                            continue;
                        if ( !(line.trim().split("(\\s+)")[0].trim().equals("E")
                            || line.trim().split("(\\s+)")[0].trim().equals("D")
                            || line.trim().split("(\\s+)")[0].trim().equals("V")) ) {
                            if (getDim() == 2) {
                                String[] valStr = line.split("(\\s+)");
                                String sign = valStr[0].trim();
                                try {
                                    getValues()[0][i] = Double.parseDouble(valStr[1].trim()) + getCenter() / (getScalefactor() * (int) Screen.getMainScreen().getRenderScale());
                                    getValues()[1][i] = Double.parseDouble(valStr[2].trim()) + getCenter() / (getScalefactor() * (int) Screen.getMainScreen().getRenderScale());
                                    switch (sign) {
                                        case "+":
                                            getValues()[2][i] = 1.0; // red ball
                                            break;
                                        case "-":
                                            getValues()[2][i] = 2.0; // blue ball
                                            break;
                                        case "*":
                                            getValues()[2][i] = 3.0; // yellow ball
                                            break;
                                    }
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                            } else if (getDim() == 3) {
                                String[] valStr = line.split("(\\s+)");
                                String sign = valStr[0].trim();
                                try {
                                    getValues()[0][i] = Double.parseDouble(valStr[1].trim()) + getCenter() / (getScalefactor() * (int) Screen.getMainScreen().getRenderScale());
                                    getValues()[1][i] = Double.parseDouble(valStr[2].trim()) + getCenter() / (getScalefactor() * (int) Screen.getMainScreen().getRenderScale());
                                    getValues()[2][i] = Double.parseDouble(valStr[3].trim()) + getCenter() / getScalefactor();
                                    switch (sign) {
                                        case "+":
                                            getValues()[3][i] = 1.0; // red ball
                                            break;
                                        case "-":
                                            getValues()[3][i] = 2.0; // blue ball
                                            break;
                                        case "*":
                                            getValues()[3][i] = 3.0; // yellow ball
                                            break;
                                    }
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                            }

                            platfStart();
                            javafx.application.Platform.runLater(() -> {
                                if ( !platfIsRunning()) return;

                                // DRAW
                                clearDots();
                                if ( getDim() == 2 && isLattice() ) drawLattice();
                                for (int k = 0; k < getNumPart(); k++){
                                    if ( getDim() == 2 ) {
                                        draw2Dots(getValues()[0][k], getValues()[1][k], getValues()[2][k]);
                                    } else if ( getDim() == 3 ) {
                                        draw3Dots(getValues()[0][k], getValues()[1][k], getValues()[2][k], getValues()[3][k]);
                                    }
                                }
                            });

                            i++;

                            if ( i == getNumPart() ) i = 0;

                        } else {
                            String firstLetter = line.trim().split("(\\s+)")[0].trim();
                            boolean nanFound = false;
                            try {
                                if ( firstLetter.equals("E") && !isFirstEnergy() ) {
                                    double firstNum = Double.parseDouble(line.split("(\\s+)")[1].trim());
                                    setFirstEnergy(true);

                                    if ( isFirstEnergy() ) {
                                        if (!Double.isNaN(firstNum) && !Double.isInfinite(firstNum)
                                            && !Double.isNaN(getPhaseEnergy()) && !Double.isInfinite(getPhaseEnergy())) {
                                            getEnergyY().add(firstNum);
                                            getEnergyX().add((double) getPhaseEnergy());
                                        } else
                                            nanFound = true;
                                        if (!nanFound) {
                                            setPhaseEnergy(getPhaseEnergy() + 1);
                                            setSmallest(0.0);
                                            setGreatest(getEnergyY().get(0));
                                            getFxplot().setEData(getEnergyX(), getEnergyY());
                                        }
                                    }
                                } else if ( firstLetter.equals("E") ) {
                                    double number = Double.parseDouble(line.split("(\\s+)")[1].trim());
                                    if ( !Double.isNaN(number) && !Double.isInfinite(number)
                                        && !Double.isNaN(getPhaseEnergy()) && !Double.isInfinite(getPhaseEnergy()) ) {
                                        getEnergyY().add(number);
                                        getEnergyX().add((double) getPhaseEnergy());
                                    } else
                                        nanFound = true;
                                    if (!nanFound)
                                        setPhaseEnergy(getPhaseEnergy() + 1);
                                } else if ( firstLetter.equals("D") && !isFirstDiffus() ) {
                                    double firstNum = Double.parseDouble(line.split("(\\s+)")[1].trim());
                                    setFirstDiffus(true);

                                    if ( isFirstDiffus() ) {
                                        if (!Double.isNaN(firstNum) && !Double.isInfinite(firstNum)
                                            && !Double.isNaN(getPhaseDiffus()) && !Double.isInfinite(getPhaseDiffus())) {
                                            getDiffusionY().add(firstNum);
                                            getDiffusionX().add((double) getPhaseDiffus());
                                        } else
                                            nanFound = true;

                                        if (!nanFound) {
                                            setPhaseDiffus(getPhaseDiffus() + 1);
                                            setGreatestDiff(getDiffusionY().get(0));
                                            getFxplot().setDData(getDiffusionX(), getDiffusionY());
                                        }
                                    }
                                } else if ( firstLetter.equals("D") ) {
                                    double number = Double.parseDouble(line.split("(\\s+)")[1].trim());
                                    if ( !Double.isNaN(number) && !Double.isInfinite(number) && number > 0.0
                                        && !Double.isNaN(getPhaseDiffus()) && !Double.isInfinite(getPhaseDiffus()) ) {
                                        getDiffusionY().add(number);
                                        getDiffusionX().add((double) getPhaseDiffus());
                                    } else
                                        nanFound = true;
                                    if (!nanFound)
                                        setPhaseDiffus(getPhaseDiffus() + 1);
                                } else if ( firstLetter.equals("V") && !isFirstVisc() ) {
                                    double firstNum = 0.0;
                                    if (getDiffusionY().get((int) getPhaseDiffus() - 1) != 0.0)
                                        firstNum = 1.0e12 * Double.parseDouble(line.split("(\\s+)")[1].trim()) /
                                            getDiffusionY().get((int) getPhaseDiffus() - 1);
                                    setFirstVisc(true);

                                    if ( isFirstVisc() ) {
                                        if (!Double.isNaN(firstNum) && !Double.isInfinite(firstNum)
                                            && !Double.isNaN(getPhaseVisc()) && !Double.isInfinite(getPhaseVisc())) {
                                            getViscY().add(firstNum);
                                            getViscX().add((double) getPhaseVisc());
                                        } else
                                            nanFound = true;

                                        if (!nanFound) {
                                            setPhaseVisc(getPhaseVisc() + 1);
                                            setGreatestVisc(getViscY().get(0));
                                            getFxplot().setVData(getViscX(), getViscY());
                                        }
                                    }
                                } else if ( firstLetter.equals("V") ) {
                                    double number = 0.0;
                                    if (getDiffusionY().get((int) getPhaseDiffus() - 1) != 0.0)
                                        number = 1.0e12 * Double.parseDouble(line.split("(\\s+)")[1].trim()) /
                                            getDiffusionY().get((int) getPhaseDiffus() - 1);

                                    if ( !Double.isNaN(number) && !Double.isInfinite(number) && number > 0.0
                                        && !Double.isNaN(getPhaseVisc()) && !Double.isInfinite(getPhaseVisc()) ) {
                                        getViscY().add(number);
                                        getViscX().add((double) getPhaseVisc());
                                    } else
                                        nanFound = true;
                                    if (!nanFound)
                                        setPhaseVisc(getPhaseVisc() + 1);
                                }
                            } catch (NumberFormatException e) {
                                continue;
                            }

                            if (!nanFound) {
                                Thread.sleep(50);
                                if ( firstLetter.equals("E") && isFirstEnergy() ) {
                                    if (getEnergyY().get((int) getPhaseEnergy() - 1) > getGreatest()) {
                                        setGreatest(getEnergyY().get((int) getPhaseEnergy() - 1));
                                        getFxplot().setEMaxY(getGreatest());
                                    }
                                    if (getCharge() == 2) {
                                        if (getEnergyY().get((int) getPhaseEnergy() - 1) < getSmallest()) {
                                            setSmallest(getEnergyY().get((int) getPhaseEnergy() - 1));
                                            getFxplot().setEMinY(getSmallest());
                                        }
                                    }
                                    setInitT(2.0/3.0*getEnergyY().get(0)/8.617333262145e-5);
                                    getFxplot().updateEData(getEnergyX(), getEnergyY());
                                } else if ( firstLetter.equals("D") && isFirstDiffus() ) {
                                    if (getDiffusionY().get((int) getPhaseDiffus() - 1) > getGreatestDiff()) {
                                        setGreatestDiff(getDiffusionY().get((int) getPhaseDiffus() - 1));
                                        getFxplot().setDMaxY(getGreatestDiff());
                                    }

                                    getFxplot().updateDData(getDiffusionX(), getDiffusionY());
                                } else if ( firstLetter.equals("V") && isFirstVisc() ) {
                                    if (getViscY().get((int) getPhaseVisc() - 1) > getGreatestVisc()) {
                                        setGreatestVisc(getViscY().get((int) getPhaseVisc() - 1));
                                        getFxplot().setVMaxY(getGreatestVisc());
                                    }

                                    getFxplot().updateVData(getViscX(), getViscY());
                                }
                            }
                        }
                    }

                    int time = getViscX().size()-1;
                    getFxplot().setVTitle(time, getViscY().get(getViscY().size()-1));
                    getFxplot().setDTitle(time, getDiffusionY().get(getDiffusionY().size()-1));
                    setFinT(2.0/3.0*getEnergyY().get((int) getPhaseEnergy() - 1)/8.617333262145e-5);
                    double deltaT = getFinT() - getInitT();
                    getFxplot().setDeltaT(deltaT);

                    setExitVal(getProcess().waitFor());
                    if (getExitVal() != 0) {
                        walkStop();
                        platfStop();
                        timerStop();
                        getMenuNappiMMC().setDisable(false);
                        getHelpNappiMMC().setDisable(false);
                        getRunMMC().setDisable(false);
                        getPlotMMC().setVisible(true);
                        getCharge1().setDisable(false);
                        getCharge2().setDisable(false);
                        getDim2().setDisable(false);
                        getDim3().setDisable(false);
                        getNappiLattice().setDisable(false);
                        getNappiBalls3D().setDisable(false);
                        getCloseNappiMMC().setDisable(false);
                        getRuntime().gc();
                        getRuntime().exit(getExitVal());
                    }
                } catch (IOException | InterruptedException e) {
                    platfStop();
                    timerStop();
                    getMenuNappiMMC().setDisable(false);
                    getHelpNappiMMC().setDisable(false);
                    getRunMMC().setDisable(false);
                    getPlotMMC().setVisible(true);
                    getCharge1().setDisable(false);
                    getCharge2().setDisable(false);
                    getDim2().setDisable(false);
                    getDim3().setDisable(false);
                    getNappiLattice().setDisable(false);
                    getNappiBalls3D().setDisable(false);
                    getCloseNappiMMC().setDisable(false);
                    getRuntime().gc();
                    Platform.runLater(() -> {
                        /*
                         * INFO DIALOG
                         */
                        GetDialogs getDialogs = new GetDialogs();
                        Alert alert = getDialogs.getInfo(getLanguage().equals("fin") ? "Ajo on päättynyt." : "Run is finished.");
                        walkStop();
                        alert.show();
                    });
                }
            /*
            * timer run ends
            */
            }
        /*
        * timer ends
        */
        }, 0, 50);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

     /**
      * method for drawing the initial particles
      * @param initialDataFile data from "C:/RWDATA"
      */
     private void drawInitials(File initialDataFile) {

        if ( this.getDim() == 2 && isLattice() ) drawLattice();
        this.getPiirturi().setLineWidth(this.getLinewidth());
        List<double[]> initialData = readDataMMC(initialDataFile, this.getDim());

        this.getPiirturi().setGlobalAlpha(1.0);
        if ( this.getNumPart() < 25 ) this.getPiirturi().setLineWidth(5.0 / (Math.log(this.getNumPart())*this.getScalefactor()));
        else this.getPiirturi().setLineWidth(10.0 / (Math.log(this.getNumPart())*this.getScalefactor()));
        /*
         * Draw barrier line
         */
        this.getPiirturi().setStroke(Color.DIMGRAY);
        this.getPiirturi().strokeLine(
            this.getCenter() / this.getScalefactor(), 0.0,
            this.getCenter() / this.getScalefactor(), 2.0 * this.getCenter() / this.getScalefactor());

        /*
        * Draw initial data spots
        */
        for (int k = 0; k < this.getNumPart(); k++){
            this.getValues()[0][k] = this.getDiffer()/10.0 + initialData.get(k)[0] + this.getCenter() / (this.getScalefactor() * (int) Screen.getMainScreen().getRenderScale());
            this.getValues()[1][k] = this.getDiffer()/10.0 + initialData.get(k)[1] + this.getCenter() / (this.getScalefactor() * (int) Screen.getMainScreen().getRenderScale());
            if ( this.getDim() == 2 ) {
                this.getValues()[2][k] = initialData.get(k)[2];
                draw2Dots(this.getValues()[0][k], this.getValues()[1][k], this.getValues()[2][k]);
            } else if ( this.getDim() == 3 ) {
                this.getValues()[2][k] = initialData.get(k)[2] + this.getCenter() / this.getScalefactor();
                this.getValues()[3][k] = initialData.get(k)[3];
                draw3Dots(this.getValues()[0][k], this.getValues()[1][k], this.getValues()[2][k], this.getValues()[3][k]);
            }
        }
    }

    /**
     * method for clearing the animation area
     */
    private void clearDots(){
        this.getPiirturi().setGlobalAlpha(1.0);
        this.getPiirturi().setGlobalBlendMode(BlendMode.SRC_OVER);
        this.getPiirturi().setFill(Color.BLACK);
        if ( this.getDim() == 2 )
            this.getPiirturi().fillRect(0, 0,
                this.getAnimwidth() / this.getScalefactor(), this.getAnimwidth() / this.getScalefactor());
        else if ( this.getDim() == 3 )
            this.getPiirturi().fillRect(0, 0,
                1.0/this.getScalefactor()*this.getAnimwidth(), 1.0/this.getScalefactor()*this.getAnimwidth());
        this.getPiirturi().fill();
    }

    /**
     * method for drawing the 2D particles
     * @param x x-coordinate of a particle
     * @param y y-coordinate of a particle
     */
    private void draw2Dots(double x, double y, double ball){
        if (isBalls3D()) {
            Image ballImg = null;
            if ( ball == 1.0 ) ballImg = this.getRedP();
            else if ( ball == 2.0 ) ballImg = this.getBlueP();
            else if ( ball == 3.0 ) ballImg = this.getYellowP();
            this.getPiirturi().drawImage(ballImg,
                x - this.getDiam()/2.0, y - this.getDiam()/2.0, this.getDiam(), this.getDiam());
        } else {
            if ( ball == 1 ) this.getPiirturi().setFill(Color.rgb(255,80,80,1)); // red
            else if ( ball == 2 ) this.getPiirturi().setFill(Color.rgb(100,100,255,1)); // blue
            else if ( ball == 3 ) this.getPiirturi().setFill(Color.rgb(255,255,50,1)); // yellow
            this.getPiirturi().setLineWidth(this.getLinewidth());
            this.getPiirturi().fillRoundRect(x - this.getDiam()/2.0, y - this.getDiam()/2.0,
                this.getDiam(), this.getDiam(), this.getDiam(), this.getDiam());
        }
    }

    /**
     * method for drawing the 3D particles
     * @param x x-coordinate of a particle
     * @param y y-coordinate of a particle
     * @param z z-coordinate of a particle
     */
    private void draw3Dots(double x, double y, double z, double ball) {
        final double xypos = this.getDiam() / (Math.log(2.0 * z));
        final double widthheight = 2.75 * xypos;
        if (isBalls3D()) {
            Image ballImg = null;
            if (ball == 1.0) ballImg = this.getRedP();
            else if (ball == 2.0) ballImg = this.getBlueP();
            else if (ball == 3.0) ballImg = this.getYellowP();
            this.getPiirturi().drawImage(ballImg,x - xypos, y - xypos, widthheight, widthheight);
        } else {
            if ( ball == 1 ) this.getPiirturi().setFill(Color.rgb(255,80,80,1.0-z/(20.0*this.getScalefactor()))); // red
            else if ( ball == 2 ) this.getPiirturi().setFill(Color.rgb(100,100,255,1.0-z/(20.0*this.getNumPart()*this.getScalefactor()))); // blue
            else if ( ball == 3 ) this.getPiirturi().setFill(Color.rgb(255,255,50,1.0-z/(20.0*this.getNumPart()*this.getScalefactor()))); // yellow
            this.getPiirturi().setGlobalAlpha(1.0-z/(this.getScalefactor()));
            this.getPiirturi().setLineWidth(this.getLinewidth());
            this.getPiirturi().setGlobalBlendMode(BlendMode.DIFFERENCE);
            this.getPiirturi().fillRoundRect(x - xypos, y - xypos, widthheight, widthheight, widthheight, widthheight
            );
        }
    }
 
    /**
     * method for drawing the lattice structue (only in 2D)
     */
    private void drawLattice() {
        for ( int i = 0; i < (int) this.getMeasure() + 2; i+=2 ) {
            for ( int j = 0; j < (int) this.getMeasure() + 2; j+=2 ) {
                if (isBalls3D()) {
                    this.getPiirturi().drawImage(this.getGrayP(),
                        (double) i + this.getDiffer(), (double) j + this.getDiffer(), 1.0, 1.0);
                } else {
                    this.getPiirturi().setFill(Color.rgb(60,60,60));
                    this.getPiirturi().fillRoundRect(
                        (double) i + this.getDiffer(), (double) j + this.getDiffer(),
                        this.getDiam(), this.getDiam(), this.getDiam(), this.getDiam());
                }
            }
        }
    }

    /**
     * method for checking if user input in GUI is a double
     * @param str GUI input string
     * @return true if input is a double, false otherwise
     */
    private static boolean isNumDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
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
     * Create GUI for MMC
     * @return MMC DIFFUSION SCENE
     */
    Parent getSceneMMC(){
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

        Label labSizeParticles = new Label(this.getLanguage().equals("fin") ? "hiukkasten halkaisija:" : "diameter of particle:");
        this.setSizeParticles = new TextField("");
        this.setSizeParticles.setOnKeyReleased(e -> {
            if (isNumDouble(this.setSizeParticles.getText().trim())){
                this.vars[1] = this.setSizeParticles.getText().trim();
            } else
                this.vars[1] = "0.0";
        });

        Label labCharge = new Label(this.getLanguage().equals("fin") ? "hiukkasten varaus:" : "charge of particles:");

        this.setCharge1 = new ToggleButton("1");
        this.setCharge1.setMinWidth(55);
        this.setCharge1.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setCharge1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setCharge1.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.setCharge1.setEffect(shadow));
        this.setCharge1.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.setCharge1.setEffect(null));

        this.setCharge2 = new ToggleButton("2");
        this.setCharge2.setMinWidth(55);
        this.setCharge2.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setCharge2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setCharge2.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.setCharge2.setEffect(shadow));
        this.setCharge2.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) ->this.setCharge2.setEffect(null));

        HBox setCharge = new HBox(this.setCharge1, this.setCharge2);
        setCharge.setSpacing(40);
        this.setCharge1.setOnMouseClicked(f -> {
            this.setCharge1.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "1";
        });
        this.setCharge2.setOnMouseClicked(f -> {
            this.setCharge1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge2.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "2";
        });

        this.vars[3] = "0"; // steps

        Label labNumDimensions = new Label(this.getLanguage().equals("fin") ? "ulottuvuus:" : "dimension:");
        this.setDim2 = new ToggleButton("2");
        this.setDim2.setMinWidth(55);
        this.setDim2.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setDim2.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.setDim2.setEffect(shadow));
        this.setDim2.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.setDim2.setEffect(null));

        this.setDim3 = new ToggleButton("3");
        this.setDim3.setMinWidth(55);
        this.setDim3.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setDim3.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.setDim3.setEffect(shadow));
        this.setDim3.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.setDim3.setEffect(null));

        HBox setDimension = new HBox(this.setDim2, this.setDim3);
        setDimension.setSpacing(40);
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

        /*
        * ...THEIR PLACEMENTS
        */
        GridPane.setHalignment(labNumParticles, HPos.LEFT);
        asettelu.add(labNumParticles, 0, 0);
        GridPane.setHalignment(this.setNumParticles, HPos.CENTER);
        this.setNumParticles.setMinWidth(this.getCompwidth());
        this.setNumParticles.setMaxWidth(this.getCompwidth());
        asettelu.add(this.setNumParticles, 0, 1);
        
        GridPane.setHalignment(labSizeParticles, HPos.LEFT);
        asettelu.add(labSizeParticles, 0, 2);
        GridPane.setHalignment(this.setSizeParticles, HPos.CENTER);
        this.setSizeParticles.setMinWidth(this.getCompwidth());
        this.setSizeParticles.setMaxWidth(this.getCompwidth());
        asettelu.add(this.setSizeParticles, 0, 3);

        GridPane.setHalignment(labCharge, HPos.LEFT);
        asettelu.add(labCharge, 0, 4);
        GridPane.setHalignment(setCharge, HPos.CENTER);
        setCharge.setMinWidth(this.getCompwidth());
        setCharge.setMaxWidth(this.getCompwidth());
        asettelu.add(setCharge, 0, 5);

        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 6);
        GridPane.setHalignment(setDimension, HPos.CENTER);
        setDimension.setMinWidth(this.getCompwidth());
        setDimension.setMaxWidth(this.getCompwidth());
        asettelu.add(setDimension, 0, 7);

        this.vars[5] = "m"; // mmc
        this.vars[6] = "-"; // spread out

        /*
        * BUTTON: LATTICE
        */
        this.getNappiLattice().setMinWidth(this.getCompwidth());
        this.getNappiLattice().setMaxWidth(this.getCompwidth());
        this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
        this.getNappiLattice().setId("lattice");
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getNappiLattice().setEffect(shadow));
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getNappiLattice().setEffect(null));
        this.getNappiLattice().setOnMouseClicked((MouseEvent event) -> {
            if (this.getNappiLattice().getText().equals("LATTICE") || this.getNappiLattice().getText().equals("HILA")){
                this.getNappiLattice().setText(this.getLanguage().equals("fin") ? "VAPAA" : "FREE");
                this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[7] = "-";
            } else if (this.getNappiLattice().getText().equals("FREE") || this.getNappiLattice().getText().equals("VAPAA")){
                this.getNappiLattice().setText(this.getLanguage().equals("fin") ? "HILA" : "LATTICE");
                this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[7] = "l";
            }
        });
        valikko.getChildren().add(this.getNappiLattice());

        this.vars[8] = "-"; // save off

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 8, 2, 1);

        final Pane empty = new Pane();
        GridPane.setHalignment(empty, HPos.CENTER);
        asettelu.add(empty, 0, 9, 2, 2);

        /*
         * BUTTON: BALLS3D
         */
        this.getNappiBalls3D().setMinWidth(this.getCompwidth());
        this.getNappiBalls3D().setMaxWidth(this.getCompwidth());
        this.getNappiBalls3D().setBackground(new Background(new BackgroundFill(Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
        this.getNappiBalls3D().setId("balls3D");
        this.getNappiBalls3D().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getNappiBalls3D().setEffect(shadow));
        this.getNappiBalls3D().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getNappiBalls3D().setEffect(null));
        this.getNappiBalls3D().setOnMouseClicked((MouseEvent event) -> {
            if (this.getNappiBalls3D().getText().equals("CIRCLES") || this.getNappiBalls3D().getText().equals("YMPYRÄT")){
                this.getNappiBalls3D().setText(this.getLanguage().equals("fin") ? "PALLOT" : "BALLS");
                this.getNappiBalls3D().setBackground(new Background(new BackgroundFill(Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
                this.balls3D = true;
            } else if (this.getNappiBalls3D().getText().equals("BALLS") || this.getNappiBalls3D().getText().equals("PALLOT")){
                this.getNappiBalls3D().setText(this.getLanguage().equals("fin") ? "YMPYRÄT" : "CIRCLES");
                this.getNappiBalls3D().setBackground(new Background(new BackgroundFill(Color.PINK,CornerRadii.EMPTY,Insets.EMPTY)));
                this.balls3D = false;
            }
        });
        valikko.getChildren().add(this.getNappiBalls3D());

        final Pane empty2 = new Pane();
        GridPane.setHalignment(empty2, HPos.CENTER);
        asettelu.add(empty2, 0, 10, 2, 1);

       return asettelu;
    }

    /**
     *
     * @param num_part the num_part to set
     */
    private void setNumPart( int num_part ) { this.num_part = num_part; }

    /**
     * @return the num_part
     */
    @Contract(pure = true)
    private int getNumPart() { return num_part; }

    /**
     *
     * @param diam the diam to set
     */
    private void setDiam( double diam ) { this.diam = diam; }

    /**
     * @return the diam
     */
    @Contract(pure = true)
    private double getDiam() { return diam; }

    /**
     *
     * @param dim the dim to set
     */
    private void setDim( int dim ) { this.dim = dim; }

    /**
     * @return the dim
     */
    @Contract(pure = true)
    private int getDim() { return dim; }

    /**
     * @param charge the charge to set
     */
    @Contract(pure = true)
    private void setCharge( int charge ){ this.charge = charge; }

    /**
     * @return the charge
     */
    @Contract(pure = true)
    private int getCharge() { return charge; }

    /**
     *
     * @param energy_x the energy_x to set
     */
    private void setEnergyX( List<Double> energy_x ) { this.energy_x = energy_x; }

    /**
     * @return the energy_x
     */
    @Contract(pure = true)
    private List<Double> getEnergyX() { return energy_x; }

    /**
     *
     * @param energy_y the energy_y to set
     */
    private void setEnergyY( List<Double> energy_y ) { this.energy_y = energy_y; }

    /**
     * @return the energy_y
     */
    @Contract(pure = true)
    private List<Double> getEnergyY() { return energy_y; }

    /**
     *
     * @param diffusion_x the diffusion_x to set
     */
    private void setDiffusionX( List<Double> diffusion_x ) { this.diffusion_x = diffusion_x; }

    /**
     * @return the diffusion_x
     */
    @Contract(pure = true)
    private List<Double> getDiffusionX() { return diffusion_x; }

    /**
     *
     * @param diffusion_y the diffusion_y to set
     */
    private void setDiffusionY( List<Double> diffusion_y ) { this.diffusion_y = diffusion_y; }

    /**
     * @return the diffusion_y
     */
    @Contract(pure = true)
    private List<Double> getDiffusionY() { return diffusion_y; }

    /**
     *
     * @param visc_x the visc_x to set
     */
    private void setViscX( List<Double> visc_x ) { this.visc_x = visc_x; }

    /**
     * @return the visc_x
     */
    @Contract(pure = true)
    private List<Double> getViscX() { return visc_x; }

    /**
     *
     * @param visc_y the visc_y to set
     */
    private void setViscY( List<Double> visc_y ) { this.visc_y = visc_y; }

    /**
     * @return the visc_y
     */
    @Contract(pure = true)
    private List<Double> getViscY() { return visc_y; }

    /**
     *
     * @param measure the measure to set
     */
    private void setMeasure( double measure ) { this.measure = measure; }

    /**
     * @return the measure
     */
    @Contract(pure = true)
    private double getMeasure() { return measure; }

    /**
     *
     * @param differ the differ to set
     */
    private void setDiffer( double differ ) { this.differ = differ; }

    /**
     * @return the differ
     */
    @Contract(pure = true)
    private double getDiffer() { return differ; }

    /**
     * sets setBarrier to true
     */
    private void barrierOn() { this.setBarrier(true); }

    /**
     * sets barrierOn to false
     */
    private void barrierOff() { this.setBarrier(false); }

    /**
     * @return isBarrier
     */
    boolean barrierIsOn() { return isBarrier(); }

    /**
     * sets setWalk to true
     */
    private void walkStart() { this.setWalk(true); }

    /**
     * sets setWalk to false
     */
    private void walkStop() { this.setWalk(false); }

    /**
     * @return isWalk
     */
    boolean walkState() { return isWalk(); }

    /**
     * sets setRunning to true
     */
    private void runtimeStart() { this.setRunning(true); }

    /**
     * @return isRunning
     */
    boolean runtimeIsRunning() { return isRunning(); }

    /**
     * sets setRunning to false,
     * exits runtime
     */
    void stopRuntime() {
        this.setRunning(false);
        this.getRuntime().exit(this.getExitVal());
    }

    /**
     * sets setTimerRunning to true
     */
    private void timerStart() { this.setTimerRunning(true); }

    /**
     * cancels Timer,
     * purges Timer,
     * sets setTimerRunning to false
     */
    private void timerStop() {
        this.getTimer().cancel();
        this.getTimer().purge();
        this.setTimerRunning(false);
    }

    /**
     * @return isTimerRunning
     */
    boolean timerIsRunning() { return isTimerRunning(); }

    /**
     * @return getOutput
     */
    BufferedWriter getProcOut() { return getOutput(); }

    /**
     * sets setPlatfRunning to true
     */
    private void platfStart() { this.setPlatfRunning(true); }

    /**
     * destroys process
     * sets setPlatfRunning to false
     */
    private void platfStop() {
        this.getProcess().destroyForcibly();
        this.setPlatfRunning(false);
    }

    /**
     * @return isPlatfRunning
     */
    @Contract(pure = true)
    private boolean platfIsRunning() { return isPlatfRunning(); }

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
     * @return the Charge1
     */
    @Contract(pure = true)
    private ToggleButton getCharge1() { return setCharge1; }

    /**
     * @return the Charge2
     */
    @Contract(pure = true)
    private ToggleButton getCharge2() { return setCharge2; }

    /**
     * @return the Dim2
     */
    @Contract(pure = true)
    private ToggleButton getDim2() { return setDim2; }

    /**
     * @return the Dim3
     */
    @Contract(pure = true)
    private ToggleButton getDim3() { return setDim3; }

    /**
     * @return the nappiLattice
     */
    @Contract(pure = true)
    private Button getNappiLattice() { return nappiLattice; }

    /**
     * @return the nappiBalls3D
     */
    @Contract(pure = true)
    private Button getNappiBalls3D() { return nappiBalls3D; }

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
     * @return the remBarNappiMMC
     */
    @Contract(pure = true)
    private Button getRemBarNappiMMC() { return remBarNappiMMC; }

    /**
     * @param remBarNappiMMC the remBarNappiMMC to set
     */
    private void setRemBarNappiMMC( Button remBarNappiMMC ) { this.remBarNappiMMC = remBarNappiMMC; }

    /**
     * @return the runMMC
     */
    @Contract(pure = true)
    private Button getRunMMC() { return runMMC; }

    /**
     * @param runMMC the runMMC to set
     */
    private void setRunMMC( Button runMMC ) { this.runMMC = runMMC; }

    /**
     * @return the valikkoMMC
     */
    @Contract(pure = true)
    private VBox getValikkoMMC() { return valikkoMMC; }

    /**
     * @param valikkoMMC the valikkoMMC to set
     */
    private void setValikkoMMC( VBox valikkoMMC ) { this.valikkoMMC = valikkoMMC; }

    /**
     * @return the plotMMC
     */
    @Contract(pure = true)
    private Button getPlotMMC() { return plotMMC; }

    /**
     * @param plotMMC the plotMMC to set
     */
    private void setPlotMMC( Button plotMMC ) { this.plotMMC = plotMMC; }

    /**
     * @return the closeNappiMMC
     */
    @Contract(pure = true)
    private Button getCloseNappiMMC() { return closeNappiMMC; }

    /**
     * @param closeNappiMMC the closeNappiMMC to set
     */
    private void setCloseNappiMMC( Button closeNappiMMC ) { this.closeNappiMMC = closeNappiMMC; }

    /**
     * @return the menuNappiMMC
     */
    @Contract(pure = true)
    private Button getMenuNappiMMC() { return menuNappiMMC; }

    /**
     * @param menuNappiMMC the menuNappiMMC to set
     */
    private void setMenuNappiMMC( Button menuNappiMMC ) { this.menuNappiMMC = menuNappiMMC; }

    /**
     * @return the helpNappiMMC
     */
    @Contract(pure = true)
    private Button getHelpNappiMMC() { return helpNappiMMC; }

    /**
     * @param helpNappiMMC the helpNappiMMC to set
     */
    private void setHelpNappiMMC( Button helpNappiMMC ) { this.helpNappiMMC = helpNappiMMC; }

    /**
     * @return the phaseDiffus
     */
    @Contract(pure = true)
    private long getPhaseDiffus() { return phaseDiffus; }

    /**
     * @param phaseDiffus the phaseDiffus to set
     */
    private void setPhaseDiffus( long phaseDiffus ) { this.phaseDiffus = phaseDiffus; }

    /**
     * @return the phaseEnergy
     */
    @Contract(pure = true)
    private long getPhaseEnergy() { return phaseEnergy; }

    /**
     * @param phaseEnergy the phaseEnergy to set
     */
    private void setPhaseEnergy( long phaseEnergy ) { this.phaseEnergy = phaseEnergy; }

    /**
     * @return the phaseVisc
     */
    @Contract(pure = true)
    private long getPhaseVisc() { return phaseVisc; }

    /**
     * @param phaseVisc the phaseVisc to set
     */
    private void setPhaseVisc( long phaseVisc ) { this.phaseVisc = phaseVisc; }

    /**
     * @param smallest the smallest to set
     */
    private void setSmallest( double smallest ) { this.smallest = smallest; }

    /**
     * @return the smallest
     */
    @Contract(pure = true)
    private double getSmallest() { return smallest; }

    /**
     * @param greatest the greatest to set
     */
    private void setGreatest( double greatest ) { this.greatest = greatest; }

    /**
     * @return the greatest
     */
    @Contract(pure = true)
    private double getGreatest() { return greatest; }

    /**
     * @param greatestDiff the greatestDiff to set
     */
    private void setGreatestDiff( double greatestDiff ) { this.greatestDiff = greatestDiff; }

    /**
     * @return the greatestDiff
     */
    @Contract(pure = true)
    private double getGreatestDiff() { return greatestDiff; }

    /**
     * @param greatestVisc the greatestVisc to set
     */
    private void setGreatestVisc( double greatestVisc ) { this.greatestVisc = greatestVisc; }

    /**
     * @return the greatestVisc
     */
    @Contract(pure = true)
    private double getGreatestVisc() { return greatestVisc; }

    /**
     * @return the firstEnergy
     */
    @Contract(pure = true)
    private boolean isFirstEnergy() { return firstEnergy; }

    /**
     * @param firstEnergy the firstEnergy to set
     */
    private void setFirstEnergy( boolean firstEnergy ) { this.firstEnergy = firstEnergy; }

    /**
     * @return the firstDiffus
     */
    @Contract(pure = true)
    private boolean isFirstDiffus() { return firstDiffus; }

    /**
     */
    private void setFirstDiffus(boolean firstDiffus) { this.firstDiffus = firstDiffus; }

    /**
     * @return the firstVisc
     */
    @Contract(pure = true)
    private boolean isFirstVisc() { return firstVisc; }

    /**
     */
    private void setFirstVisc(boolean firstVisc) { this.firstVisc = firstVisc; }

    /**
     * @return the linewidth
     */
    @Contract(pure = true)
    private double getLinewidth() { return linewidth; }

    /**
     * @param linewidth the linewidth to set
     */
    private void setLinewidth( double linewidth ) { this.linewidth = linewidth; }

    /**
     * @return the scalefactor
     */
    @Contract(pure = true)
    private double getScalefactor() { return scalefactor; }

    /**
     * @param scalefactor the scalefactor to set
     */
    private void setScalefactor( double scalefactor ) { this.scalefactor = scalefactor; }

    /**
     * @return the timerRunning
     */
    @Contract(pure = true)
    private boolean isTimerRunning() { return timerRunning; }

    /**
     * @param timerRunning the timerRunning to set
     */
    private void setTimerRunning( boolean timerRunning ) { this.timerRunning = timerRunning; }

    /**
     * @return the animwidth
     */
    @Contract(pure = true)
    private double getAnimwidth() { return animwidth; }

    /**
     * @param animwidth the animwidth to set
     */
    private void setAnimwidth( double animwidth ) { this.animwidth = animwidth; }

    /**
     * @return the center
     */
    @Contract(pure = true)
    private double getCenter() { return center; }

    /**
     * @param center the center to set
     */
    private void setCenter( double center ) { this.center = center; }

    /**
     * @return the piirturi
     */
    @Contract(pure = true)
    private GraphicsContext getPiirturi() { return piirturi; }

    /**
     * @param piirturi the piirturi to set
     */
    private void setPiirturi( GraphicsContext piirturi ) { this.piirturi = piirturi; }

    /**
     * @return the platfRunning
     */
    @Contract(pure = true)
    private boolean isPlatfRunning() { return platfRunning; }

    /**
     * @param platfRunning the platfRunning to set
     */
    private void setPlatfRunning( boolean platfRunning ) { this.platfRunning = platfRunning; }

    /**
     * @return the process
     */
    @Contract(pure = true)
    private Process getProcess() { return process; }

    /**
     * @param process the process to set
     */
    private void setProcess( Process process ) { this.process = process; }

    /**
     * @return the runtime
     */
    @Contract(pure = true)
    private Runtime getRuntime() { return runtime; }

    /**
     * @param runtime the runtime to set
     */
    private void setRuntime( Runtime runtime ) { this.runtime = runtime; }

    /**
     * @return the exitVal
     */
    @Contract(pure = true)
    private int getExitVal() { return exitVal; }

    /**
     * @param exitVal the exitVal to set
     */
    private void setExitVal( int exitVal ) { this.exitVal = exitVal; }

    /**
     * @return the timer
     */
    @Contract(pure = true)
    private Timer getTimer() { return timer; }

    /**
     * @param timer the timer to set
     */
    private void setTimer( Timer timer ) { this.timer = timer; }

    /**
     * @return the values
     */
    @Contract(pure = true)
    private double[][] getValues() { return values; }

    /**
     * @param values the values to set
     */
    private void setValues( double[][] values ) { this.values = values; }

    /**
     * @return the running
     */
    @Contract(pure = true)
    private boolean isRunning() { return running; }

    /**
     * @param running the running to set
     */
    private void setRunning( boolean running ) { this.running = running; }

    /**
     * @return the barrier
     */
    @Contract(pure = true)
    private boolean isBarrier() { return barrier; }

    /**
     * @param barrier the barrier to set
     */
    private void setBarrier( boolean barrier ) { this.barrier = barrier; }

    /**
     * @return the walk
     */
    @Contract(pure = true)
    private boolean isWalk() { return walk; }

    /**
     * @param walk the walk to set
     */
    private void setWalk( boolean walk ) { this.walk = walk; }

    /**
     * @return the lattice
     */
    @Contract(pure = true)
    private boolean isLattice() { return lattice; }

    /**
     * @param lattice the lattice to set
     */
    private void setLattice( boolean lattice ) { this.lattice = lattice; }

    /**
     * @return the Balls3D
     */
    @Contract(pure = true)
    private boolean isBalls3D() { return balls3D; }

    /**
     * @param balls3D the Balls3D to set
     */
    private void setBalls3D( boolean balls3D ) { this.balls3D = balls3D; }

    /**
     * @return the redP
     */
    @Contract(pure = true)
    private Image getRedP() { return redP; }

    /**
     * @param redP the redP to set
     */
    private void setRedP( Image redP ) { this.redP = redP; }

    /**
     * @return the blueP
     */
    @Contract(pure = true)
    private Image getBlueP() { return blueP; }

    /**
     * @param blueP the blueP to set
     */
    private void setBlueP( Image blueP ) { this.blueP = blueP; }

    /**
     * @return the yellowP
     */
    @Contract(pure = true)
    private Image getYellowP() { return yellowP; }

    /**
     * @param yellowP the yellowP to set
     */
    private void setYellowP( Image yellowP ) { this.yellowP = yellowP; }

    /**
     * @return the grayP
     */
    @Contract(pure = true) private Image getGrayP() { return grayP; }

    /**
     * @param grayP the grayP to set
     */
    private void setGrayP( Image grayP ) { this.grayP = grayP; }

    /**
     * @return the output
     */
    @Contract(pure = true)
    private BufferedWriter getOutput() { return output; }

    /**
     * @param output the output to set
     */
    private void setOutput( BufferedWriter output ) { this.output = output; }

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
     * @return the initT
     */
    @Contract(pure = true)
    private double getInitT() { return this.initT; }

    /**
     * @param initT the initT to set
     */
    private void setInitT(double initT) { this.initT = initT; }

    /**
     * @return the finT
     */
    @Contract(pure = true)
    private double getFinT() { return this.finT; }

    /**
     * @param finT the finT to set
     */
    private void setFinT(double finT) { this.finT = finT; }

}
