package jms.randomwalk.scenes;

import enums.DblSizes;
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
import jms.randomwalk.datahandling.Data;
import jms.randomwalk.plots.FXPlot;
import jms.randomwalk.ui.GetDialogs;

import java.io.*;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.apache.maven.surefire.shade.booter.org.apache.commons.lang3.SystemUtils;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for DIFFUSION.
 */
public class SceneDiff extends Data {

    private String language;
    private final boolean isWin;
    private ToggleButton setDim2;
    private ToggleButton setDim3;
    private final Button nappiLattice;
    private final Button nappiMobilVisc;
    private TextField setNumParticles;
    private TextField setSizeParticles;

    private long phaseEnergy;
    private long phaseDiffus;
    private long phaseVisc;
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
    private double initE;
    private double finE;
    private GraphicsContext piirturi;
    private FXPlot fxplot;
    private Button remBarNappiDiff;
    private Button cancelNappiDiff;
    private VBox valikkoDiff;
    private Button runDiff;
    private final Button nappiBalls3D;
    private Button plotDiff;
    private Button closeNappiDiff;
    private Button menuNappiDiff;
    private Button helpNappiDiff;

    private boolean platfNotRunning;
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
    private Image yellowP;
    private Image grayP;
    private BufferedWriter output;
    private double walktime;
    private final NumberFormat formatter;

    private int numPart;
    private double diam;
    private int dim;
    private List<Double> energyX;
    private List<Double> energyY;
    private List<Double> diffusionX;
    private List<Double> diffusionY;
    private List<Double> viscX;
    private List<Double> viscY;
    private double measure;
    private double differ;
    private boolean iscancel;
    private boolean ismobility;

    /**
     * Main class gets vars via this.
     * @return clone of vars array
     */
    public String[] getVars() { 
        return this.vars.clone();
    }

    /**
     * Initiating scene button and user variable array.
     * @param language which ui language: finnish or english
     */
    public SceneDiff(String language) {
        super();
        this.setLanguage(language);
        this.isWin = SystemUtils.IS_OS_WINDOWS;
        this.nappiLattice = new Button(this.getLanguage().equals("fin") ? "VAPAA" : "FREE");
        this.nappiBalls3D = new Button(this.getLanguage().equals("fin") ? "PALLOT" : "BALLS");
        this.nappiMobilVisc = new Button(this.getLanguage().equals("fin") ? "VISKOSITEETTI" : "VISCOSITY");
        this.ismobility = false;
        this.balls3D = true;
        this.formatter = new DecimalFormat("0.00");
        this.vars = new String[]{
            "D",    // vars[0] which simulation                 USER
            "0",    // vars[1] particles                        USER
            "0.0",  // vars[2] diameter                         USER
            "0",    // vars[3] steps                            n/a
            "0",    // vars[4] dimension                        USER
            "-",    // vars[5] calcfix, fcclattice, or sawplot  n/a
            "-",    // vars[6] (fixed/)spread                   n/a
            "-",    // vars[7] (lattice/)free                   USER
            "-"};   // vars[8] save (off)                       n/a
    }

    /**
     * Diffusion execution, uses Timer.
     * @param folder datafolder "C:/RWDATA"
     * @param initialDataFile initial particle data
     * @param executable Fortran executable "walk.exe"
     * @param piirturi GraphicsContext which draws the animation
     * @param scalefactor scaling is used in different particle amounts
     * @param animwidth drawing area width
     * @param linewidth width for lines
     * @param remBarNappiDiff removing barrier in animation
     * @param cancelNappiDiff removing barrier in animation
     * @param runDiff run animation, no plot
     * @param valikkoDiff valikkoDiff
     * @param plotDiff plot initial and final particle configurations, no animation
     * @param closeNappiDiff close button must be disabled during run
     * @param menuNappiDiff menu button must be disabled during run
     * @param helpNappiDiff help button must be disabled during run
     * @param energyX fxplot energy graph x-axis container
     * @param energyY fxplot energy graph y-axis container
     * @param diffusionX fxplot diffusion graph x-axis container
     * @param diffusionY fxplot diffusion graph y-axis container
     * @param viscX fxplot visosity or mobility graph x-axis container
     * @param viscY fxplot visosity or mobility graph y-axis container
     * @param newdata if is a new run with new data
     * @param measure area/volume size
     * @param differ difference in between the lattice structure
     */
    public void refresh(File folder, File initialDataFile, String executable,
        GraphicsContext piirturi, double scalefactor, double animwidth, double linewidth,
        Button remBarNappiDiff, Button cancelNappiDiff, Button runDiff, VBox valikkoDiff, Button plotDiff,
        Button closeNappiDiff, Button menuNappiDiff, Button helpNappiDiff, List<Double> energyX,
        List<Double> energyY, List<Double> diffusionX, List<Double> diffusionY, List<Double> viscX,
        List<Double> viscY, boolean newdata, double measure, double differ) {

        this.setYellowP(new Image("/Pyellow.png"));
        this.setGrayP(new Image("/Pgray.png"));

        this.setPiirturi(piirturi);
        this.setLinewidth(linewidth);
        this.setAnimwidth(animwidth);
        this.setRemBarNappiDiff(remBarNappiDiff);
        this.setCancelNappiDiff(cancelNappiDiff);
        this.setRunDiff(runDiff);
        this.setValikkoDiff(valikkoDiff);
        this.setPlotDiff(plotDiff);
        this.setCloseNappiDiff(closeNappiDiff);
        this.setMenuNappiDiff(menuNappiDiff);
        this.setHelpNappiDiff(helpNappiDiff);
        this.setCenter(this.getAnimwidth() / 2.0);
        this.setMeasure(measure);
        this.setDiffer(differ);
        barrierOn();

        if (newdata) {
            this.setNumPart(Integer.parseInt(this.vars[1]));
            this.setDiam(Double.parseDouble(this.vars[2]));
            this.setDim(Integer.parseInt(this.vars[4]));
            this.setLattice(this.vars[7].equals("l"));
            this.setBalls3D(this.isBalls3D());
            this.setPhaseEnergy(0);
            this.setPhaseDiffus(0);
            this.setPhaseVisc(0);
            this.setFirstEnergy(false);
            this.setFirstDiffus(false);
            this.setFirstVisc(false);
            this.setScalefactor(scalefactor);
            this.setEnergyX(energyX);
            this.setEnergyY(energyY);
            this.setDiffusionX(diffusionX);
            this.setDiffusionY(diffusionY);
            this.setViscX(viscX);
            this.setViscY(viscY);
            this.energyX.clear();
            this.energyY.clear();
            this.diffusionX.clear();
            this.diffusionY.clear();
            this.viscX.clear();
            this.viscY.clear();
            this.setValues(new double[this.getDim() + 1][this.getNumPart()]);
            clearDots();
            this.setWalkTime(0.0);
            this.getPlotDiff().setVisible(false);
            this.setIsCancel(false);
        }

        this.getMenuNappiDiff().setDisable(true);
        this.getHelpNappiDiff().setDisable(true);
        this.getDim2().setDisable(true);
        this.getDim3().setDisable(true);
        this.getNappiLattice().setDisable(true);
        this.getNappiMobilVisc().setDisable(true);
        this.getNappiBalls3D().setDisable(true);
        this.getValikkoDiff().getChildren().set(3, this.getRemBarNappiDiff());
        this.getValikkoDiff().getChildren().set(4, this.getCancelNappiDiff());
        this.getRemBarNappiDiff().setVisible(true);
        this.getCancelNappiDiff().setVisible(true);

        this.getRemBarNappiDiff().setOnMouseClicked(event -> {
            barrierOff();
            this.getValikkoDiff().getChildren().set(3, this.getRunDiff());
            this.getRunDiff().setDisable(true);
            this.getRemBarNappiDiff().setVisible(false);
            this.getCancelNappiDiff().setVisible(false);
            this.getCloseNappiDiff().setDisable(true);
            this.getFxplot().setFrameVis();
        });

        this.getCancelNappiDiff().setOnMouseClicked(event -> {
            barrierOff();
            this.setIsCancel(true);
            this.getValikkoDiff().getChildren().set(3, this.getRunDiff());
            this.getRunDiff().setDisable(true);
            this.getRemBarNappiDiff().setVisible(false);
            this.getCancelNappiDiff().setVisible(false);
            this.getCloseNappiDiff().setDisable(true);
            this.getValikkoDiff().getChildren().set(4, this.getPlotDiff());
            this.getPlotDiff().setVisible(true);
        });

        piirturi.setLineWidth(linewidth);

        String[] command;
        
        try {
            if (this.isWin) {
                command = new String[]{"cmd", "/c", executable,
                    this.vars[0], this.vars[1], this.vars[2], this.vars[3], this.vars[4],
                    this.vars[5], this.vars[6], this.vars[7], this.vars[8]};
            } else {
                command = new String[]{"./" + executable,
                    this.vars[0], this.vars[1], this.vars[2], this.vars[3], this.vars[4],
                    this.vars[5], this.vars[6], this.vars[7], this.vars[8]};
            }
            
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
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                } else if (Files.exists(initialDataFile.toPath())) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                    clearDots();
                    drawInitials(initialDataFile);
                    break;
                }
            }

            timerStart();
            this.setTimer(new Timer());
            this.getTimer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    int i = 0;

                    if (!timerIsRunning()) {
                        return;
                    }

                    // WAIT FOR BARRIER REMOVAL
                    while (barrierIsOn()) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(500);
                        } catch (InterruptedException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                    // CONTINUE
                    if (!barrierIsOn()) {
                        setOutput(new BufferedWriter(new OutputStreamWriter(getProcess().getOutputStream())));
                        PrintWriter pw = null;
                        if (getOutput() != null) {
                            pw = new PrintWriter(getOutput());
                        }
                        if (pw != null) {
                            if (isCancel()) {
                                pw.println("-");
                            } else {
                                pw.println("x");
                            }
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

                    if (!isCancel()) {
                        try (BufferedReader input = new BufferedReader(new InputStreamReader(
                            getProcess().getInputStream()))) {
                            String line;

                            while ((line = input.readLine()) != null) {
                                if (line.trim().startsWith("S") || line.isEmpty()) {
                                    continue;
                                }
                                if (!line.substring(0, 1).matches("([0-9]|-)|E|D|V|T")) {
                                    continue;
                                }
                                if (!(line.trim().split("(\\s+)")[0].trim().equals("E")
                                    || line.trim().split("(\\s+)")[0].trim().equals("D")
                                    || line.trim().split("(\\s+)")[0].trim().equals("V")
                                    || line.trim().split("(\\s+)")[0].trim().equals("T"))) {
                                    if (getDim() == 2) {
                                        String[] valStr = line.split("(\\s+)");
                                        getValues()[0][i] = Double.parseDouble(valStr[0].trim()) + getCenter() / (getScalefactor());
                                        getValues()[1][i] = Double.parseDouble(valStr[1].trim()) + getCenter() / (getScalefactor());
                                    } else if (getDim() == 3) {
                                        String[] valStr = line.split("(\\s+)");
                                        getValues()[0][i] = Double.parseDouble(valStr[0].trim()) + getCenter() / (getScalefactor());
                                        getValues()[1][i] = Double.parseDouble(valStr[1].trim()) + getCenter() / (getScalefactor());
                                        getValues()[2][i] = Double.parseDouble(valStr[2].trim()) + getCenter() / getScalefactor();
                                    }

                                    platfStart();
                                    javafx.application.Platform.runLater(() -> {
                                        if (platfNotRunning()) {
                                            return;
                                        }

                                        // DRAW
                                        clearDots();
                                        if (getDim() == 2 && isLattice()) {
                                            drawLattice();
                                        }
                                        for (int k = 0; k < getNumPart(); k++) {
                                            if (getDim() == 2) {
                                                draw2Dots(getValues()[0][k], getValues()[1][k]);
                                            } else if (getDim() == 3) {
                                                draw3Dots(getValues()[0][k], getValues()[1][k], getValues()[2][k]);
                                            }
                                        }
                                    });

                                    i++;

                                    if (i == getNumPart()) {
                                        i = 0;
                                    }

                                } else {
                                    String firstLetter = line.trim().split("(\\s+)")[0].trim();
                                    boolean nanFound = false;
                                    if (firstLetter.equals("E") && !isFirstEnergy()) {
                                        double firstNum = Double.parseDouble(line.split("(\\s+)")[1].trim());
                                        setFirstEnergy(true);

                                        if (isFirstEnergy()) {
                                            if (!Double.isNaN(firstNum) && !Double.isInfinite(firstNum)
                                                && !Double.isNaN(getPhaseEnergy()) && !Double.isInfinite(getPhaseEnergy())) {
                                                getEnergyY().add(firstNum);
                                                getEnergyX().add((double) getPhaseEnergy());
                                                setInitE(firstNum);
                                            } else {
                                                nanFound = true;
                                            }
                                            if (!nanFound) {
                                                setPhaseEnergy(getPhaseEnergy() + 1);
                                                setGreatest(getEnergyY().get(0));
                                                getFxplot().setEData(getEnergyX(), getEnergyY());
                                            }
                                        }
                                    } else if (firstLetter.equals("E")) {
                                        double number = Double.parseDouble(line.split("(\\s+)")[1].trim());
                                        if (!Double.isNaN(number) && !Double.isInfinite(number)
                                            && !Double.isNaN(getPhaseEnergy()) && !Double.isInfinite(getPhaseEnergy())) {
                                            getEnergyY().add(number);
                                            getEnergyX().add((double) getPhaseEnergy());
                                        } else {
                                            nanFound = true;
                                        }
                                        if (!nanFound) {
                                            setPhaseEnergy(getPhaseEnergy() + 1);
                                        }
                                    } else if (firstLetter.equals("D") && !isFirstDiffus()) {
                                        // 1e8 is scaling for the graph
                                        double firstNum = 1e8 * Double.parseDouble(line.split("(\\s+)")[1].trim());
                                        setFirstDiffus(true);

                                        if (isFirstDiffus()) {
                                            if (!Double.isNaN(firstNum) && !Double.isInfinite(firstNum)
                                                && !Double.isNaN(getPhaseDiffus()) && !Double.isInfinite(getPhaseDiffus())) {
                                                getDiffusionY().add(firstNum);
                                                getDiffusionX().add((double) getPhaseDiffus());
                                            } else {
                                                nanFound = true;
                                            }

                                            if (!nanFound) {
                                                setPhaseDiffus(getPhaseDiffus() + 1);
                                                setGreatestDiff(getDiffusionY().get(0));
                                                getFxplot().setDData(getDiffusionX(), getDiffusionY());
                                            }
                                        }
                                    } else if (firstLetter.equals("D")) {
                                        // 1e8 is scaling for the graph
                                        double number = 1e8 * Double.parseDouble(line.split("(\\s+)")[1].trim());
                                        if (!Double.isNaN(number) && !Double.isInfinite(number) && number > 0.0
                                            && !Double.isNaN(getPhaseDiffus()) && !Double.isInfinite(getPhaseDiffus())) {
                                            getDiffusionY().add(number);
                                            getDiffusionX().add((double) getPhaseDiffus());
                                        } else {
                                            nanFound = true;
                                        }
                                        if (!nanFound) {
                                            setPhaseDiffus(getPhaseDiffus() + 1);
                                        }
                                    } else if (firstLetter.equals("V") && !isFirstVisc()) {
                                        double firstNum = 0.0;
                                        if (getDiffusionY().get((int) getPhaseDiffus() - 1) != 0.0) {
                                            if (isMobility()) {
                                                // electrical mobility eta = 3qD/(2E) [cm^2/(Vs)]
                                                // mu = 3/2 * D[cm^2/s] * q/E[eV] = 3/2 * D[cm^2/s]/E[V] = [cm^2/(Vs)]
                                                firstNum = 3.0 / 2.0 * getDiffusionY().get((int) getPhaseDiffus() - 1)
                                                    / getEnergyY().get((int) getPhaseEnergy() - 1);
                                            } else { // viscosity
                                                // eta = [J/m] / [m^2/s] = [kg/m s] = [Pa s]
                                                // 1e-4: D [cm^2/s] -> D [m^2/s]
                                                // 1e-8: diffusion scaling back to normal
                                                // 1e-4 * 1e-8 = 1e-12
                                                firstNum = Double.parseDouble(line.split("(\\s+)")[1].trim()) /
                                                    (1e-12 * getDiffusionY().get((int) getPhaseDiffus() - 1));
                                            }
                                        }
                                        setFirstVisc(true);

                                        if (isFirstVisc()) {
                                            if (!Double.isNaN(firstNum) && !Double.isInfinite(firstNum)
                                                && !Double.isNaN(getPhaseVisc()) && !Double.isInfinite(getPhaseVisc())) {
                                                getViscY().add(firstNum);
                                                getViscX().add((double) getPhaseVisc());
                                            } else {
                                                nanFound = true;
                                            }

                                            if (!nanFound) {
                                                setPhaseVisc(getPhaseVisc() + 1);
                                                setGreatestVisc(getViscY().get(0));
                                                if (isMobility()) {
                                                    getFxplot().setVData(getViscX(), getViscY(), "mobil");
                                                } else {
                                                    getFxplot().setVData(getViscX(), getViscY(), "visc");
                                                }
                                            }
                                        }
                                    } else if (firstLetter.equals("V")) {
                                        double number = 0.0;
                                        if (getDiffusionY().get((int) getPhaseDiffus() - 1) != 0.0) {
                                            if (isMobility()) {
                                                // electrical mobility eta = 3qD/(2E) [cm^2/(Vs)]
                                                // mu = 3/2 * D[cm^2/s] * q/E[eV] = 3/2 * D[cm^2/s]/E[V] = [cm^2/(Vs)]
                                                number = 3.0 / 2.0 * getDiffusionY().get((int) getPhaseDiffus() - 1)
                                                    / getEnergyY().get((int) getPhaseEnergy() - 1);
                                            } else { // viscosity
                                                // eta = [J/m] / [m^2/s] = [kg/m s] = [Pa s]
                                                // 1e-4: D [cm^2/s] -> D [m^2/s]
                                                // 1e-8: diffusion scaling back to normal
                                                // 1e-4 * 1e-8 = 1e-12
                                                number = Double.parseDouble(line.split("(\\s+)")[1].trim()) /
                                                    (1e-12 * getDiffusionY().get((int) getPhaseDiffus() - 1));
                                            }
                                        }
                                        if (!Double.isNaN(number) && !Double.isInfinite(number) && number > 0.0
                                            && !Double.isNaN(getPhaseVisc()) && !Double.isInfinite(getPhaseVisc())) {
                                            getViscY().add(number);
                                            getViscX().add((double) getPhaseVisc());
                                        } else {
                                            nanFound = true;
                                        }
                                        if (!nanFound) {
                                            setPhaseVisc(getPhaseVisc() + 1);
                                        }
                                    } else if (firstLetter.equals("T")) {
                                        setWalkTime(Double.parseDouble(line.split("(\\s+)")[1].trim()));
                                    }

                                    if (!nanFound) {
                                        TimeUnit.MILLISECONDS.sleep(50);
                                        if (firstLetter.equals("E") && isFirstEnergy()) {
                                            if (getEnergyY().get((int) getPhaseEnergy() - 1) > getGreatest()) {
                                                setGreatest(getEnergyY().get((int) getPhaseEnergy() - 1));
                                                getFxplot().setEMaxY(getGreatest());
                                            }
                                            getFxplot().updateEData(getEnergyX(), getEnergyY());
                                        } else if (firstLetter.equals("D") && isFirstDiffus()) {
                                            if (getDiffusionY().get((int) getPhaseDiffus() - 1) > getGreatestDiff()) {
                                                setGreatestDiff(getDiffusionY().get((int) getPhaseDiffus() - 1));
                                                getFxplot().setDMaxY(getGreatestDiff());
                                            }
                                            getFxplot().updateDData(getDiffusionX(), getDiffusionY());
                                        } else if (firstLetter.equals("V") && isFirstVisc()) {
                                            if (getViscY().get((int) getPhaseVisc() - 1) > getGreatestVisc()) {
                                                setGreatestVisc(getViscY().get((int) getPhaseVisc() - 1));
                                                getFxplot().setVMaxY(getGreatestVisc());
                                            }

                                            if (isMobility()) {
                                                getFxplot().updateVData(getViscX(), getViscY(), "mobil");
                                            } else {
                                                getFxplot().updateVData(getViscX(), getViscY(), "visc");
                                            }
                                        }
                                    }
                                }
                            }

                            if (getViscY().size() > 0) {
                                if (isMobility()) {
                                    getFxplot().setVTitle(getViscY().get(getViscY().size() - 1), "mobil");
                                } else {
                                    getFxplot().setVTitle(getViscY().get(getViscY().size() - 1), "visc");
                                }
                            }
                            if (getDiffusionY().size() > 0) {
                                // 1e-8 is scaling back to normal
                                getFxplot().setDTitle(1e-8 * getDiffusionY().get(getDiffusionY().size() - 1));
                            }
                            if (getEnergyY().size() > 0) {
                                setFinE(getEnergyY().get((int) getPhaseEnergy() - 1));
                            }
                            double deltaE = getFinE() - getInitE();
                            getFxplot().setDeltaE(deltaE);

                            setExitVal(getProcess().waitFor());
                            if (getExitVal() != 0) {
                                walkStop();
                                platfStop();
                                timerStop();
                                getMenuNappiDiff().setDisable(false);
                                getHelpNappiDiff().setDisable(false);
                                getRunDiff().setDisable(false);
                                getPlotDiff().setVisible(true);
                                getDim2().setDisable(false);
                                getDim3().setDisable(false);
                                getNappiLattice().setDisable(false);
                                getNappiMobilVisc().setDisable(false);
                                getNappiBalls3D().setDisable(false);
                                getCloseNappiDiff().setDisable(false);
                                getRuntime().gc();
                                getRuntime().exit(getExitVal());
                            }
                        } catch (IOException | InterruptedException e) {
                            platfStop();
                            timerStop();
                            getMenuNappiDiff().setDisable(false);
                            getHelpNappiDiff().setDisable(false);
                            getRunDiff().setDisable(false);
                            getPlotDiff().setVisible(true);
                            getDim2().setDisable(false);
                            getDim3().setDisable(false);
                            getNappiLattice().setDisable(false);
                            getNappiMobilVisc().setDisable(false);
                            getNappiBalls3D().setDisable(false);
                            getCloseNappiDiff().setDisable(false);
                            getRuntime().gc();
                            Platform.runLater(() -> {
                                getValikkoDiff().getChildren().set(4, getPlotDiff());
                                getPlotDiff().setVisible(true);
                                /*
                                 * INFO DIALOG
                                 */
                                GetDialogs getDialogs = new GetDialogs();
                                Alert alert = getDialogs.getInfo(getLanguage().equals("fin")
                                    ? "Ajo on päättynyt.\nKulkukesto: " + formatter.format(getWalkTime()) + "s"
                                    : "Run is finished.\nWalk Time: " + formatter.format(getWalkTime()) + "s");
                                walkStop();
                                alert.show();
                            });
                        }
                    } else {
                        platfStop();
                        timerStop();
                        getMenuNappiDiff().setDisable(false);
                        getHelpNappiDiff().setDisable(false);
                        getRunDiff().setDisable(false);
                        getPlotDiff().setVisible(true);
                        getDim2().setDisable(false);
                        getDim3().setDisable(false);
                        getNappiLattice().setDisable(false);
                        getNappiMobilVisc().setDisable(false);
                        getNappiBalls3D().setDisable(false);
                        getCloseNappiDiff().setDisable(false);
                        getRuntime().gc();
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
     * Method for drawing the initial particles.
     * @param initialDataFile data from "C:/RWDATA"
     */
    private void drawInitials(File initialDataFile) {

        if (this.getDim() == 2 && isLattice()) {
            drawLattice();
        }
        this.getPiirturi().setLineWidth(this.getLinewidth());
        List<double[]> initialData = readDataDiff(initialDataFile, this.getDim());

        this.getPiirturi().setGlobalAlpha(1.0);
        if (this.getNumPart() < 25) {
            this.getPiirturi().setLineWidth(5.0 / (Math.log(this.getNumPart()) * this.getScalefactor()));
        } else {
            this.getPiirturi().setLineWidth(10.0 / (Math.log(this.getNumPart()) * this.getScalefactor()));
        }
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
        for (int k = 0; k < this.getNumPart(); k++) {
            this.getValues()[0][k] = this.getDiffer() / 10.0 + initialData.get(k)[0] + this.getCenter()
                / (this.getScalefactor());
            this.getValues()[1][k] = this.getDiffer() / 10.0 + initialData.get(k)[1] + this.getCenter()
                / (this.getScalefactor());
            if (this.getDim() == 2) {
                draw2Dots(this.getValues()[0][k], this.getValues()[1][k]);
            } else if (this.getDim() == 3) {
                this.getValues()[2][k] = initialData.get(k)[2] + this.getCenter() / this.getScalefactor();
                draw3Dots(this.getValues()[0][k], this.getValues()[1][k], this.getValues()[2][k]);
            }
        }
    }

    /**
     * Method for clearing the animation area.
     */
    private void clearDots() {
        this.getPiirturi().setGlobalAlpha(1.0);
        this.getPiirturi().setGlobalBlendMode(BlendMode.SRC_OVER);
        this.getPiirturi().setFill(Color.BLACK);
        if (this.getDim() == 2) {
            this.getPiirturi().fillRect(0, 0, this.getAnimwidth() / this.getScalefactor(), this.getAnimwidth() / this.getScalefactor());
        } else if (this.getDim() == 3) {
            this.getPiirturi().fillRect(0, 0, 1.0 / this.getScalefactor() * this.getAnimwidth(), 1.0 / this.getScalefactor() * this.getAnimwidth());
        }
        this.getPiirturi().fill();
    }

    /**
     * Mmethod for drawing the 2D particles.
     * @param x x-coordinate of a particle
     * @param y y-coordinate of a particle
     */
    private void draw2Dots(double x, double y) {
        if (isBalls3D()) {
            this.getPiirturi().drawImage(this.getYellowP(),
                x - this.getDiam() / 2.0, y - this.getDiam() / 2.0,
                this.getDiam(), this.getDiam());
        } else {
            this.getPiirturi().setFill(Color.rgb(255, 255, 50, 1)); // yellow
            this.getPiirturi().setLineWidth(this.getLinewidth());
            this.getPiirturi().fillRoundRect(
                x - this.getDiam() / 2.0, y - this.getDiam() / 2.0,
                this.getDiam(), this.getDiam(), this.getDiam(), this.getDiam());
        }
    }

    /**
     * Method for drawing the 3D particles.
     * @param x x-coordinate of a particle
     * @param y y-coordinate of a particle
     * @param z z-coordinate of a particle
     */
    private void draw3Dots(double x, double y, double z) {
        final double xypos = this.getDiam() / Math.log(2.0 * z);
        final double widthheight = 2.75 * xypos;
        if (isBalls3D()) {
            this.getPiirturi().drawImage(this.getYellowP(), x - xypos, y - xypos, widthheight, widthheight);
        } else {
            this.getPiirturi().setFill(Color.rgb(255, 255, 50, 1.0 - z / (20.0 * this.getNumPart() * this.getScalefactor()))); // yellow
            this.getPiirturi().setGlobalAlpha(1.0 - z / (this.getScalefactor()));
            this.getPiirturi().setLineWidth(this.getLinewidth());
            this.getPiirturi().setGlobalBlendMode(BlendMode.DIFFERENCE);
            this.getPiirturi().fillRoundRect(x - xypos, y - xypos, widthheight, widthheight, widthheight, widthheight
            );
        }
    }
 
    /**
     * Method for drawing the lattice structue (only in 2D).
     */
    private void drawLattice() {
        // SC = simple cubic
        for (int i = 0; i < (int) this.getMeasure() + 2; i += 2) {
            for (int j = 0; j < (int) this.getMeasure() + 2; j += 2) {
                if (isBalls3D()) {
                    this.getPiirturi().drawImage(this.getGrayP(),
                        (double) i + this.getDiffer(), (double) j + this.getDiffer(), 1.0, 1.0);
                } else {
                    this.getPiirturi().setFill(Color.rgb(60, 60, 60));
                    this.getPiirturi().fillRoundRect(
                        (double) i + this.getDiffer(), (double) j + this.getDiffer(),
                        1.0, 1.0, 1.0, 1.0);
                }
            }
        }
    }

    /**
     * Method for checking if user input in GUI is a double.
     * @param str GUI input string
     * @return true if input is a double, false otherwise
     */
    private static boolean isNumDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
     * Create GUI for Diffusion.
     * @return DIFFUSION SCENE
     */
    public Parent getSceneDiff() {
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(DblSizes.PANEW.getDblSize());
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

        Label labSizeParticles = new Label(this.getLanguage().equals("fin") ? "hiukkasten halkaisija:" : "diameter of particle:");
        this.setSizeParticles = new TextField("");
        this.setSizeParticles.setOnKeyReleased(e -> {
            if (isNumDouble(this.setSizeParticles.getText().trim())) {
                this.vars[2] = this.setSizeParticles.getText().trim();
            } else {
                this.vars[2] = "0.0";
            }
        });

        this.vars[3] = "0"; // steps

        Label labNumDimensions = new Label(this.getLanguage().equals("fin") ? "ulottuvuus:" : "dimension:");
        this.setDim2 = new ToggleButton("2");
        this.setDim2.setMinWidth(DblSizes.BIGBUTW.getDblSize());
        this.setDim2.setMaxWidth(DblSizes.BIGBUTW.getDblSize());
        this.setDim2.setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.setDim2.getFont().getSize()));
        this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setDim2.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.setDim2.setEffect(shadow));
        this.setDim2.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.setDim2.setEffect(null));

        this.setDim3 = new ToggleButton("3");
        this.setDim3.setMinWidth(DblSizes.BIGBUTW.getDblSize());
        this.setDim3.setMaxWidth(DblSizes.BIGBUTW.getDblSize());
        this.setDim3.setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.setDim3.getFont().getSize()));
        this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setDim3.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.setDim3.setEffect(shadow));
        this.setDim3.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.setDim3.setEffect(null));

        HBox setDimension = new HBox(this.setDim2, this.setDim3);
        setDimension.setSpacing(40);
        this.setDim2.setOnMouseClicked(f -> {
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.vars[4] = "2";
        });
        this.setDim3.setOnMouseClicked(f -> {
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
            this.vars[4] = "3";
        });

        this.vars[5] = "-"; // calcfix, or sawplot
        this.vars[6] = "-"; // spread out

        /*
         * BUTTON: BALLS3D
         */
        this.getNappiBalls3D().setMinWidth(DblSizes.BUTW.getDblSize());
        this.getNappiBalls3D().setMaxWidth(DblSizes.BUTW.getDblSize());
        this.getNappiBalls3D().setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.getNappiBalls3D().getFont().getSize()));
        this.getNappiBalls3D().setBackground(new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY)));
        this.getNappiBalls3D().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getNappiBalls3D().setEffect(shadow));
        this.getNappiBalls3D().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getNappiBalls3D().setEffect(null));
        this.getNappiBalls3D().setOnMouseClicked((MouseEvent event) -> {
            if (this.getNappiBalls3D().getText().equals("CIRCLES") || this.getNappiBalls3D().getText().equals("YMPYRÄT")) {
                this.getNappiBalls3D().setText(this.getLanguage().equals("fin") ? "PALLOT" : "BALLS");
                this.getNappiBalls3D().setBackground(new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY)));
                this.balls3D = true;
            } else if (this.getNappiBalls3D().getText().equals("BALLS") || this.getNappiBalls3D().getText().equals("PALLOT")) {
                this.getNappiBalls3D().setText(this.getLanguage().equals("fin") ? "YMPYRÄT" : "CIRCLES");
                this.getNappiBalls3D().setBackground(new Background(new BackgroundFill(Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));
                this.balls3D = false;
            }
        });
        valikko.getChildren().add(this.getNappiBalls3D());

        /*
        * BUTTON: LATTICE
        */
        this.getNappiLattice().setMinWidth(DblSizes.BUTW.getDblSize());
        this.getNappiLattice().setMaxWidth(DblSizes.BUTW.getDblSize());
        this.getNappiLattice().setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.getNappiLattice().getFont().getSize()));
        this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY)));
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getNappiLattice().setEffect(shadow));
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getNappiLattice().setEffect(null));
        this.getNappiLattice().setOnMouseClicked((MouseEvent event) -> {
            if (this.getNappiLattice().getText().equals("LATTICE") || this.getNappiLattice().getText().equals("HILA")) {
                this.getNappiLattice().setText(this.getLanguage().equals("fin") ? "VAPAA" : "FREE");
                this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY)));
                this.vars[7] = "-";
            } else if (this.getNappiLattice().getText().equals("FREE") || this.getNappiLattice().getText().equals("VAPAA")) {
                this.getNappiLattice().setText(this.getLanguage().equals("fin") ? "HILA" : "LATTICE");
                this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.LIGHTSALMON, CornerRadii.EMPTY, Insets.EMPTY)));
                this.vars[7] = "l";
            }
        });
        valikko.getChildren().add(this.getNappiLattice());

        /*
         * BUTTON: MOBILVISC
         */
        this.getNappiMobilVisc().setMinWidth(DblSizes.BUTW.getDblSize());
        this.getNappiMobilVisc().setMaxWidth(DblSizes.BUTW.getDblSize());
        this.getNappiMobilVisc().setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.getNappiMobilVisc().getFont().getSize()));
        this.getNappiMobilVisc().setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.getNappiMobilVisc().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getNappiMobilVisc().setEffect(shadow));
        this.getNappiMobilVisc().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getNappiMobilVisc().setEffect(null));
        this.getNappiMobilVisc().setOnMouseClicked((MouseEvent event) -> {
            if (this.getNappiMobilVisc().getText().equals("LIIKKUVUUS") || this.getNappiMobilVisc().getText().equals("MOBILITY")) {
                this.getNappiMobilVisc().setText(this.getLanguage().equals("fin") ? "VISKOSITEETTI" : "VISCOSITY");
                this.getNappiMobilVisc().setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
                this.setIsMobility(false);
            } else if (this.getNappiMobilVisc().getText().equals("VISKOSITEETTI") || this.getNappiMobilVisc().getText().equals("VISCOSITY")) {
                this.getNappiMobilVisc().setText(this.getLanguage().equals("fin") ? "LIIKKUVUUS" : "MOBILITY");
                this.getNappiMobilVisc().setBackground(new Background(new BackgroundFill(Color.THISTLE, CornerRadii.EMPTY, Insets.EMPTY)));
                this.setIsMobility(true);
            }
        });
        valikko.getChildren().add(this.getNappiMobilVisc());

        this.vars[8] = "-"; // save off

        /*
         * ...THEIR PLACEMENTS
         */
        GridPane.setHalignment(labNumParticles, HPos.LEFT);
        asettelu.add(labNumParticles, 0, 0);
        GridPane.setHalignment(this.setNumParticles, HPos.CENTER);
        this.setNumParticles.setMinWidth(DblSizes.BUTW.getDblSize());
        this.setNumParticles.setMaxWidth(DblSizes.BUTW.getDblSize());
        asettelu.add(this.setNumParticles, 0, 1);

        GridPane.setHalignment(labSizeParticles, HPos.LEFT);
        asettelu.add(labSizeParticles, 0, 2);
        GridPane.setHalignment(this.setSizeParticles, HPos.CENTER);
        this.setSizeParticles.setMinWidth(DblSizes.BUTW.getDblSize());
        this.setSizeParticles.setMaxWidth(DblSizes.BUTW.getDblSize());
        asettelu.add(this.setSizeParticles, 0, 3);

        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 4);
        GridPane.setHalignment(setDimension, HPos.CENTER);
        setDimension.setMinWidth(DblSizes.BUTW.getDblSize());
        setDimension.setMaxWidth(DblSizes.BUTW.getDblSize());
        asettelu.add(setDimension, 0, 5);

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 6, 2, 1);

        return asettelu;
    }

    /**
     *
     * @param numPart the num_part to set
     */
    private void setNumPart(int numPart) {
        this.numPart = numPart;
    }

    /**
     * @return the num_part
     */
    private int getNumPart() {
        return numPart;
    }

    /**
     *
     * @param diam the diam to set
     */
    private void setDiam(double diam) {
        this.diam = diam;
    }

    /**
     * @return the diam
     */
    private double getDiam() {
        return diam;
    }

    /**
     *
     * @param dim the dim to set
     */
    private void setDim(int dim) {
        this.dim = dim;
    }

    /**
     * @return the dim
     */
    private int getDim() {
        return dim;
    }

    /**
     *
     * @param energyX the energy_x to set
     */
    private void setEnergyX(List<Double> energyX) {
        this.energyX = energyX;
    }

    /**
     * @return the energy_x
     */
    private List<Double> getEnergyX() {
        return energyX;
    }

    /**
     *
     * @param energyY the energy_y to set
     */
    private void setEnergyY(List<Double> energyY) {
        this.energyY = energyY;
    }

    /**
     * @return the energy_y
     */
    private List<Double> getEnergyY() {
        return energyY;
    }

    /**
     *
     * @param diffusionX the diffusion_x to set
     */
    private void setDiffusionX(List<Double> diffusionX) {
        this.diffusionX = diffusionX;
    }

    /**
     * @return the diffusion_x
     */
    private List<Double> getDiffusionX() {
        return diffusionX;
    }

    /**
     *
     * @param diffusionY the diffusion_y to set
     */
    private void setDiffusionY(List<Double> diffusionY) {
        this.diffusionY = diffusionY;
    }

    /**
     * @return the diffusion_y
     */
    private List<Double> getDiffusionY() {
        return diffusionY;
    }

    /**
     *
     * @param viscX the visc_x to set
     */
    private void setViscX(List<Double> viscX) {
        this.viscX = viscX;
    }

    /**
     * @return the visc_x
     */
    private List<Double> getViscX() {
        return viscX;
    }

    /**
     *
     * @param viscY the visc_y to set
     */
    private void setViscY(List<Double> viscY) {
        this.viscY = viscY;
    }

    /**
     * @return the visc_y
     */
    private List<Double> getViscY() {
        return viscY;
    }

    /**
     *
     * @param measure the measure to set
     */
    private void setMeasure(double measure) {
        this.measure = measure;
    }

    /**
     * @return the measure
     */
    private double getMeasure() {
        return measure;
    }

    /**
     *
     * @param differ the differ to set
     */
    private void setDiffer(double differ) {
        this.differ = differ;
    }

    /**
     * @return the differ
     */
    private double getDiffer() {
        return differ;
    }

    /**
     * sets setBarrier to true
     */
    private void barrierOn() {
        this.setBarrier(true);
    }

    /**
     * sets barrierOn to false
     */
    private void barrierOff() {
        this.setBarrier(false);
    }

    /**
     * @return isBarrier
     */
    public boolean barrierIsOn() {
        return isBarrier();
    }

    /**
     * sets setWalk to true
     */
    private void walkStart() {
        this.setWalk(true);
    }

    /**
     * sets setWalk to false
     */
    private void walkStop() {
        this.setWalk(false);
    }

    /**
     * @return isWalk
     */
    public boolean walkState() {
        return isWalk();
    }

    /**
     * sets setRunning to true
     */
    private void runtimeStart() {
        this.setRunning(true);
    }

    /**
     * @return isRunning
     */
    public boolean runtimeIsRunning() {
        return isRunning();
    }

    /**
     * sets setRunning to false, exits runtime
     */
    public void stopRuntime() {
        this.setRunning(false);
        this.getRuntime().exit(this.getExitVal());
    }

    /**
     * sets setTimerRunning to true
     */
    private void timerStart() {
        this.setTimerRunning(true);
    }

    /**
     * cancels Timer, purges Timer, sets setTimerRunning to false
     */
    private void timerStop() {
        this.getTimer().cancel();
        this.getTimer().purge();
        this.setTimerRunning(false);
    }

    /**
     * @return isTimerRunning
     */
    public boolean timerIsRunning() {
        return isTimerRunning();
    }

    /**
     * @return getOutput
     */
    public BufferedWriter getProcOut() {
        return getOutput();
    }

    /**
     * sets platfNotRunning to false
     */
    private void platfStart() {
        this.platfNotRunning(false);
    }

    /**
     * destroys process, sets platfNotRunning to true
     */
    private void platfStop() {
        this.getProcess().destroyForcibly();
        this.platfNotRunning(true);
    }

    /**
     * @return the Dim2
     */
    private ToggleButton getDim2() {
        return setDim2;
    }

    /**
     * @return the Dim3
     */
    private ToggleButton getDim3() {
        return setDim3;
    }

    /**
     * @return the nappiLattice
     */
    private Button getNappiLattice() {
        return nappiLattice;
    }

    /**
     * @return the nappiMobilVisc
     */
    private Button getNappiMobilVisc() {
        return nappiMobilVisc;
    }

    /**
     * @return the nappiBalls3D
     */
    private Button getNappiBalls3D() {
        return nappiBalls3D;
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
     * @return the remBarNappiDiff
     */
    private Button getRemBarNappiDiff() {
        return remBarNappiDiff;
    }

    /**
     * @param remBarNappiDiff the remBarNappiDiff to set
     */
    private void setRemBarNappiDiff(Button remBarNappiDiff) {
        this.remBarNappiDiff = remBarNappiDiff;
    }

    /**
     * @return the cancelNappiDiff
     */
    private Button getCancelNappiDiff() {
        return cancelNappiDiff;
    }

    /**
     * @param cancelNappiDiff the cancelNappiDiff to set
     */
    private void setCancelNappiDiff(Button cancelNappiDiff) {
        this.cancelNappiDiff = cancelNappiDiff;
    }

    /**
     * @return the runDiff
     */
    private Button getRunDiff() {
        return runDiff;
    }

    /**
     * @param runDiff the runDiff to set
     */
    private void setRunDiff(Button runDiff) {
        this.runDiff = runDiff;
    }

    /**
     * @return the valikkoDiff
     */
    private VBox getValikkoDiff() {
        return valikkoDiff;
    }

    /**
     * @param valikkoDiff the valikkoDiff to set
     */
    private void setValikkoDiff(VBox valikkoDiff) {
        this.valikkoDiff = valikkoDiff;
    }

    /**
     * @return the plotDiff
     */
    private Button getPlotDiff() { 
        return plotDiff; 
    }

    /**
     * @param plotDiff the plotDiff to set
     */
    private void setPlotDiff(Button plotDiff) {
        this.plotDiff = plotDiff;
    }

    /**
     * @return the closeNappiDiff
     */
    private Button getCloseNappiDiff() {
        return closeNappiDiff;
    }

    /**
     * @param closeNappiDiff the closeNappiDiff to set
     */
    private void setCloseNappiDiff(Button closeNappiDiff) {
        this.closeNappiDiff = closeNappiDiff;
    }

    /**
     * @return the menuNappiDiff
     */
    private Button getMenuNappiDiff() {
        return menuNappiDiff;
    }

    /**
     * @param menuNappiDiff the menuNappiDiff to set
     */
    private void setMenuNappiDiff(Button menuNappiDiff) {
        this.menuNappiDiff = menuNappiDiff;
    }

    /**
     * @return the helpNappiDiff
     */
    private Button getHelpNappiDiff() {
        return helpNappiDiff;
    }

    /**
     * @param helpNappiDiff the helpNappiDiff to set
     */
    private void setHelpNappiDiff(Button helpNappiDiff) {
        this.helpNappiDiff = helpNappiDiff;
    }

    /**
     * @return the phaseDiffus
     */
    private long getPhaseDiffus() {
        return phaseDiffus;
    }

    /**
     * @param phaseDiffus the phaseDiffus to set
     */
    private void setPhaseDiffus(long phaseDiffus) {
        this.phaseDiffus = phaseDiffus;
    }

    /**
     * @return the phaseEnergy
     */
    private long getPhaseEnergy() {
        return phaseEnergy;
    }

    /**
     * @param phaseEnergy the phaseEnergy to set
     */
    private void setPhaseEnergy(long phaseEnergy) {
        this.phaseEnergy = phaseEnergy;
    }

    /**
     * @return the phaseVisc
     */
    private long getPhaseVisc() {
        return phaseVisc;
    }

    /**
     * @param phaseVisc the phaseVisc to set
     */
    private void setPhaseVisc(long phaseVisc) {
        this.phaseVisc = phaseVisc;
    }

    /**
     * @param greatest the greatest to set
     */
    private void setGreatest(double greatest) {
        this.greatest = greatest;
    }

    /**
     * @return the greatest
     */
    private double getGreatest() {
        return greatest;
    }

    /**
     * @param greatestDiff the greatestDiff to set
     */
    private void setGreatestDiff(double greatestDiff) {
        this.greatestDiff = greatestDiff;
    }

    /**
     * @return the greatestDiff
     */
    private double getGreatestDiff() {
        return greatestDiff;
    }

    /**
     * @param greatestVisc the greatestVisc to set
     */
    private void setGreatestVisc(double greatestVisc) {
        this.greatestVisc = greatestVisc;
    }

    /**
     * @return the greatestVisc
     */
    private double getGreatestVisc() {
        return greatestVisc;
    }

    /**
     * @return the firstEnergy
     */
    private boolean isFirstEnergy() {
        return firstEnergy;
    }

    /**
     * @param firstEnergy the firstEnergy to set
     */
    private void setFirstEnergy(boolean firstEnergy) {
        this.firstEnergy = firstEnergy;
    }

    /**
     * @return the firstDiffus
     */
    private boolean isFirstDiffus() {
        return firstDiffus;
    }

    /**
     */
    private void setFirstDiffus(boolean firstDiffus) {
        this.firstDiffus = firstDiffus;
    }

    /**
     * @return the firstVisc
     */
    private boolean isFirstVisc() {
        return firstVisc;
    }

    /**
     */
    private void setFirstVisc(boolean firstVisc) {
        this.firstVisc = firstVisc;
    }

    /**
     * @return the linewidth
     */
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
     * @return the scalefactor
     */
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
     * @return the timerRunning
     */
    private boolean isTimerRunning() {
        return timerRunning;
    }

    /**
     * @param timerRunning the timerRunning to set
     */
    private void setTimerRunning(boolean timerRunning) {
        this.timerRunning = timerRunning;
    }

    /**
     * @return the animwidth
     */
    private double getAnimwidth() {
        return animwidth;
    }

    /**
     * @param animwidth the animwidth to set
     */
    private void setAnimwidth(double animwidth) {
        this.animwidth = animwidth;
    }

    /**
     * @return the center
     */
    private double getCenter() {
        return center;
    }

    /**
     * @param center the center to set
     */
    private void setCenter(double center) {
        this.center = center;
    }

    /**
     * @return the piirturi
     */
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
     * @return the platfNotRunning
     */
    private boolean platfNotRunning() {
        return platfNotRunning;
    }

    /**
     * @param platfNotRunning the platfNotRunning to set
     */
    private void platfNotRunning(boolean platfNotRunning) {
        this.platfNotRunning = platfNotRunning;
    }

    /**
     * @return the process
     */
    private Process getProcess() {
        return process;
    }

    /**
     * @param process the process to set
     */
    private void setProcess(Process process) {
        this.process = process;
    }

    /**
     * @return the runtime
     */
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
    private int getExitVal() {
        return exitVal;
    }

    /**
     * @param exitVal the exitVal to set
     */
    private void setExitVal(int exitVal) {
        this.exitVal = exitVal;
    }

    /**
     * @return the timer
     */
    private Timer getTimer() {
        return timer;
    }

    /**
     * @param timer the timer to set
     */
    private void setTimer(Timer timer) {
        this.timer = timer;
    }

    /**
     * @return the values
     */
    private double[][] getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    private void setValues(double[][] values) {
        this.values = values;
    }

    /**
     * @return the running
     */
    private boolean isRunning() {
        return running;
    }

    /**
     * @param running the running to set
     */
    private void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * @return the barrier
     */
    private boolean isBarrier() {
        return barrier;
    }

    /**
     * @param barrier the barrier to set
     */
    private void setBarrier(boolean barrier) {
        this.barrier = barrier;
    }

    /**
     * @return the walk
     */
    private boolean isWalk() {
        return walk;
    }

    /**
     * @param walk the walk to set
     */
    private void setWalk(boolean walk) {
        this.walk = walk;
    }

    /**
     * @return the lattice
     */
    private boolean isLattice() {
        return lattice;
    }

    /**
     * @param lattice the lattice to set
     */
    private void setLattice(boolean lattice) {
        this.lattice = lattice;
    }

    /**
     * @return the Balls3D
     */
    private boolean isBalls3D() {
        return balls3D;
    }

    /**
     * @param balls3D the Balls3D to set
     */
    private void setBalls3D(boolean balls3D) {
        this.balls3D = balls3D;
    }

    /**
     * @return the yellowP
     */
    private Image getYellowP() {
        return yellowP;
    }

    /**
     * @param yellowP the yellowP to set
     */
    private void setYellowP(Image yellowP) {
        this.yellowP = yellowP;
    }

    /**
     * @return the grayP
     */
    private Image getGrayP() {
        return grayP;
    }

    /**
     * @param grayP the grayP to set
     */
    private void setGrayP(Image grayP) {
        this.grayP = grayP;
    }

    /**
     * @return the output
     */
    private BufferedWriter getOutput() {
        return output;
    }

    /**
     * @param output the output to set
     */
    private void setOutput(BufferedWriter output) {
        this.output = output;
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
     * @return the initE
     */
    private double getInitE() {
        return this.initE;
    }

    /**
     * @param initE the initE to set
     */
    private void setInitE(double initE) {
        this.initE = initE;
    }

    /**
     * @return the finE
     */
    private double getFinE() {
        return this.finE;
    }

    /**
     * @param finE the finE to set
     */
    private void setFinE(double finE) {
        this.finE = finE;
    }

    /**
     * @return the walktime
     */
    private double getWalkTime() {
        return this.walktime;
    }

    /**
     * @param walktime the walktime to set
     */
    private void setWalkTime(double walktime) {
        this.walktime = walktime;
    }

    /**
     * @return the iscancel
     */
    private boolean isCancel() {
        return this.iscancel;
    }

    /**
     * @param iscancel the iscancel to set
     */
    private void setIsCancel(boolean iscancel) {
        this.iscancel = iscancel;
    }

    /**
     * @return the ismobility
     */
    private boolean isMobility() {
        return this.ismobility;
    }

    /**
     * @param ismobility the ismobility to set
     */
    private void setIsMobility(boolean ismobility) {
        this.ismobility = ismobility;
    }
}
