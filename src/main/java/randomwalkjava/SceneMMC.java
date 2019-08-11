
package randomwalkjava;

import com.sun.glass.ui.Screen;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
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
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private ToggleButton setCharge0;
    private ToggleButton setCharge1;
    private ToggleButton setCharge2;
    private ToggleButton setDim2;
    private ToggleButton setDim3;
    private final Button nappiLattice;
    private TextField setNumParticles;
    private TextField setSizeParticles;

    private long phase;
    private double greatest;
    private boolean first;
    private double linewidth;
    private double scalefactor;
    private boolean timerRunning;
    private int animwidth;
    private double center;
    private GraphicsContext piirturi;
    private FXPlot fxplot;
    private Button remBarNappiMMC;
    private Button runMMC;
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
    private Image yellowP;
    private Image grayP;
    private BufferedWriter output;

    private int num_part;
    private double diam;
    private int dim;
    private List<Double> energy_x;
    private List<Double> energy_y;
    private double measure;
    private double diff;

    /**
     * main class gets vars via this
     * @return clone of vars array
     */
    String[] getVars() { return this.vars.clone(); }

    /**
     * initiating scene button and user variable array
     */
    SceneMMC() {
        super();
        this.nappiLattice = new Button("FREE");
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
     * @param folder datafolder C:/RWDATA
     * @param initialDataFile initial particle data
     * @param executable Fortran executable walk.exe
     * @param piirturi GraphicsContext which draws the animation
     * @param scalefactor scaling is used in different particle amounts
     * @param animwidth drawing area width
     * @param linewidth width for lines
     * @param fxplot plotting element for graphs
     * @param remBarNappiMMC removing barrier in animation
     * @param runMMC run animation, no plot
     * @param plotMMC plot initial and final particle configurations, no animation
     * @param closeNappiMMC close button must be disabled during run
     * @param menuNappiMMC menu button must be disabled during run
     * @param helpNappiMMC help button must be disabled during run
     * @param energy_x fxplot energy graph x-axis container
     * @param energy_y fxplot energy graph y-axis container
     * @param newdata if is a new run with new data
     * @param measure area/volume size
     * @param diff difference in between the lattice structure
     */
    void refresh(File folder, File initialDataFile, String executable,
                 GraphicsContext piirturi, double scalefactor, int animwidth,
                 double linewidth, FXPlot fxplot, Button remBarNappiMMC, Button runMMC,
                 Button plotMMC, Button closeNappiMMC, Button menuNappiMMC,
                 Button helpNappiMMC, List<Double> energy_x, List<Double> energy_y,
                 boolean newdata, double measure, double diff) {

         this.setYellowP(new Image("images/Pyellow.png"));
         this.setGrayP(new Image("images/Pgray.png"));

         this.setPiirturi(piirturi);
         this.setLinewidth(linewidth);
         this.setAnimwidth(animwidth);
         this.setScalefactor(scalefactor);
         this.setFxplot(fxplot);
         this.setRemBarNappiMMC(remBarNappiMMC);
         this.setRunMMC(runMMC);
         this.setPlotMMC(plotMMC);
         this.setCloseNappiMMC(closeNappiMMC);
         this.setMenuNappiMMC(menuNappiMMC);
         this.setHelpNappiMMC(helpNappiMMC);
         this.setCenter((double) this.getAnimwidth() / 2.0);
         this.setLattice(this.vars[7].equals("l"));
         barrierOn();

        this.setNumPart(parseInt(this.vars[0]));
        this.setDiam(parseDouble(this.vars[1]));
        this.setDim(parseInt(this.vars[4]));
        this.setEnergyX(energy_x);
        this.setEnergyY(energy_y);
        this.setMeasure(measure);
        this.setDiff(diff);

        if (newdata) {
            this.setPhase(0);
            this.setFirst(false);
            energy_x.clear();
            energy_y.clear();
            clearDots(dim);
        }

        this.getRemBarNappiMMC().setVisible(true);
        this.getPlotMMC().setVisible(false);
        this.getFxplot().setFrameVis();
        this.getMenuNappiMMC().setDisable(true);
        this.getHelpNappiMMC().setDisable(true);
        this.getCharge0().setDisable(true);
        this.getCharge1().setDisable(true);
        this.getCharge2().setDisable(true);
        this.getDim2().setDisable(true);
        this.getDim3().setDisable(true);
        this.getNappiLattice().setDisable(true);
        this.getRunMMC().setDisable(true);
        this.getRemBarNappiMMC().setOnMouseClicked(event -> {
            barrierOff();
            this.getRemBarNappiMMC().setVisible(false);
            this.getCloseNappiMMC().setDisable(true);
        });

        this.setValues(new double[dim][this.getNumPart()]);

        piirturi.setLineWidth(linewidth);

        String[] command;

        try
        {
        command = new String[]{"cmd","/c",executable,
            this.vars[0], this.vars[1], this.vars[2], this.vars[3],
            this.vars[4], this.vars[5], this.vars[6], this.vars[7],
            this.vars[8]};

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
                    Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (Files.exists(initialDataFile.toPath())) {
                clearDots( this.getDim() );
                drawInitials( initialDataFile, this.getNumPart(), this.getDim(), this.getDiam(), this.getMeasure(), this.getDiff() );
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
                        Logger.getLogger(SceneMMC.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                try (BufferedReader input = new BufferedReader(new InputStreamReader(
                    getProcess().getInputStream()))) {
                    String line;

                    while ((line = input.readLine()) != null){
                        if (line.trim().startsWith("S") || line.isEmpty()) {
                            break;
                        }
                        if (!line.substring(0,1).matches("([0-9]|-|\\+)|E"))
                            continue;
                        if (!line.trim().split("(\\s+)")[0].trim().equals("E")) {
                            if (getDim() == 2) {
                                String[] valStr = line.split("(\\s+)");
                                try {
                                    getValues()[0][i] = Double.parseDouble(valStr[0].trim()) + getCenter() / (getScalefactor() * (int) Screen.getMainScreen().getPlatformScaleX());
                                    getValues()[1][i] = Double.parseDouble(valStr[1].trim()) + getCenter() / (getScalefactor() * (int) Screen.getMainScreen().getPlatformScaleY());
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                            } else if (getDim() == 3) {
                                String[] valStr = line.split("(\\s+)");
                                try {
                                    getValues()[0][i] = Double.parseDouble(valStr[0].trim()) + getCenter() / (getScalefactor() * (int) Screen.getMainScreen().getPlatformScaleX());
                                    getValues()[1][i] = Double.parseDouble(valStr[1].trim()) + getCenter() / (getScalefactor() * (int) Screen.getMainScreen().getPlatformScaleY());
                                    getValues()[2][i] = Double.parseDouble(valStr[2].trim()) + getCenter() / getScalefactor();
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                            }

                            platfStart();
                            javafx.application.Platform.runLater(() -> {
                                if ( !platfIsRunning()) return;
                                
                                // DRAW
                                clearDots( getDim() );
                                if ( getDim() == 2 && isLattice() ) drawLattice(getMeasure(), getDiff() );
                                for (int k = 0; k < getNumPart(); k++){
                                    if ( getDim() == 2 ) {
                                        draw2Dots(getValues()[0][k], getValues()[1][k], getDiam());
                                    } else if ( getDim() == 3 ) {
                                        draw3Dots(getValues()[0][k], getValues()[1][k], getValues()[2][k], getDiam());
                                    }
                                }
                            });

                            i++;

                            if ( i == getNumPart() ) i = 0;

                        } else {
                            try {
                                if ( !isFirst() ) {
                                    setFirst(true);
                                    getEnergyY().add(Double.parseDouble(line.split("(\\s+)")[1].trim()));
                                    getEnergyX().add((double) getPhase());
                                    setPhase(getPhase() + 1);
                                    setGreatest(getEnergyY().get(0));
                                    getFxplot().setEData(getEnergyX(), getEnergyY());
                                } else {
                                    getEnergyY().add(Double.parseDouble(line.split("(\\s+)")[1].trim()));
                                    getEnergyX().add((double) getPhase());
                                    setPhase(getPhase() + 1);
                                }
                            } catch (NumberFormatException e) {
                                continue;
                            }

                            Thread.sleep(50);
                            if ( getEnergyY().get((int) getPhase() - 1) > getGreatest() ) {
                                setGreatest(getEnergyY().get((int) getPhase() - 1));
                                getFxplot().setEMaxY(getGreatest());
                            }
                            getFxplot().updateEData(getEnergyX(), getEnergyY());
                        }
                    }

                    setExitVal(getProcess().waitFor());
                    if (getExitVal() != 0) {
                        walkStop();
                        platfStop();
                        timerStop();
                        getMenuNappiMMC().setDisable(false);
                        getHelpNappiMMC().setDisable(false);
                        getRunMMC().setDisable(false);
                        getPlotMMC().setVisible(true);
                        getCharge0().setDisable(false);
                        getCharge1().setDisable(false);
                        getCharge2().setDisable(false);
                        getDim2().setDisable(false);
                        getDim3().setDisable(false);
                        getNappiLattice().setDisable(false);
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
                    getCharge0().setDisable(false);
                    getCharge1().setDisable(false);
                    getCharge2().setDisable(false);
                    getDim2().setDisable(false);
                    getDim3().setDisable(false);
                    getNappiLattice().setDisable(false);
                    getCloseNappiMMC().setDisable(false);
                    getRuntime().gc();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setContentText("Walk finished.");
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
      * @param initialDataFile data from C:/RWDATA
      * @param num_part number of particles
      * @param dim dimension of particle field
      * @param diam diameter of particle
      * @param measure area/volume size of particle field
      * @param diff difference in between the lattice structure
      */
     private void drawInitials(File initialDataFile,
                               int num_part, int dim, double diam, double measure, double diff) {

        if ( dim == 2 && isLattice() ) drawLattice(measure, diff );
        this.getPiirturi().setLineWidth(this.getLinewidth());
        List<double[]> initialData = readDataMMC(initialDataFile, dim);

        this.getPiirturi().setGlobalAlpha(1.0);
        if ( num_part < 25 )
            this.getPiirturi().setLineWidth(5.0 / (Math.log(num_part)*this.getScalefactor()));
        else
            this.getPiirturi().setLineWidth(10.0 / (Math.log(num_part)*this.getScalefactor()));
        this.getPiirturi().setStroke(Color.RED);
        this.getPiirturi().strokeLine(
            this.getCenter() / this.getScalefactor(),
            0.0,
            this.getCenter() / this.getScalefactor(),
            2.0 * this.getCenter() / this.getScalefactor());

        /*
        * Draw initial data spots
        */
        for (int k = 0; k < num_part; k++){
            this.getValues()[0][k] = initialData.get(k)[0] + this.getCenter() / (this.getScalefactor() * (int) Screen.getMainScreen().getPlatformScaleX());
            this.getValues()[1][k] = initialData.get(k)[1] + this.getCenter() / (this.getScalefactor() * (int) Screen.getMainScreen().getPlatformScaleY());
            if ( dim == 2 )
                draw2Dots(this.getValues()[0][k], this.getValues()[1][k], diam);
            else if ( dim == 3 ) {
                this.getValues()[2][k] = initialData.get(k)[2] + this.getCenter() / this.getScalefactor();
                draw3Dots(this.getValues()[0][k], this.getValues()[1][k], this.getValues()[2][k], diam);
            }
        }
    }

    /**
     * method for clearing the animation area
     * @param dim dimension of particle field
     */
    private void clearDots(int dim){
        this.getPiirturi().setGlobalAlpha(1.0);
        this.getPiirturi().setGlobalBlendMode(BlendMode.SRC_OVER);
        this.getPiirturi().setFill(Color.BLACK);
        if ( dim == 2 )
            this.getPiirturi().fillRect(0, 0,
                this.getAnimwidth() / this.getScalefactor(),
                this.getAnimwidth() / this.getScalefactor());
        else if ( dim == 3 )
            this.getPiirturi().fillRect(0, 0,
                1.0/this.getScalefactor()*this.getAnimwidth(),
                1.0/this.getScalefactor()*this.getAnimwidth());
        this.getPiirturi().fill();
    }

    /**
     * method for drawing the 2D particles
     * @param x x-coordinate of a particle
     * @param y y-coordinate of a particle
     * @param diam diameter of particle
     */
    private void draw2Dots(double x, double y, double diam){
        this.getPiirturi().drawImage(this.getYellowP(), x - diam/2.0, y - diam/2.0, diam, diam);
    }

    /**
     * method for drawing the 3D particles
     * @param x x-coordinate of a particle
     * @param y y-coordinate of a particle
     * @param z z-coordinate of a particle
     * @param diam diameter of particle
     */
    private void draw3Dots(double x, double y, double z, double diam){
        this.getPiirturi().setGlobalAlpha( 1.0 / ( Math.log(2.0 * z) ) );
        this.getPiirturi().setLineWidth(this.getLinewidth());
        this.getPiirturi().setGlobalBlendMode(BlendMode.LIGHTEN);
        this.getPiirturi().setFill(Color.YELLOW);
        final double widthheight = 5.0 * diam / (2.0 * Math.log(2.0 * z));
        this.getPiirturi().fillRoundRect(
            x - diam/( Math.log(2.0 * z) ),
            y - diam/( Math.log(2.0 * z) ),
            widthheight, widthheight,
            widthheight, widthheight
        );
    }
 
    /**
     * method for drawing the lattice structue (only in 2D)
     * @param measure area/volume size of particle field
     * @param diff difference in between the lattice structure
     */
    private void drawLattice(double measure, double diff) {
        for ( int i = 0; i < (int) measure + 2; i+=2 ) {
            for ( int j = 0; j < (int) measure + 2; j+=2 ) {
                this.getPiirturi().drawImage(this.getGrayP(),
                    (double) i + diff,
                    (double) j + diff,
                    1.0, 1.0);
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

        Label labSizeParticles = new Label("diameter of particle:");
        this.setSizeParticles = new TextField("");
        this.setSizeParticles.setOnKeyReleased(e -> {
            if (isNumDouble(this.setSizeParticles.getText().trim())){
                this.vars[1] = this.setSizeParticles.getText().trim();
            } else
                this.vars[1] = "0.0";
        });

        Label labCharge = new Label("charge of particles:");
        this.setCharge0 = new ToggleButton("0");
        this.setCharge0.setMinWidth(35);
        this.setCharge0.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setCharge0.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setCharge0.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.setCharge0.setEffect(shadow));
        this.setCharge0.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.setCharge0.setEffect(null));

        this.setCharge1 = new ToggleButton("1");
        this.setCharge1.setMinWidth(35);
        this.setCharge1.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setCharge1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setCharge1.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.setCharge1.setEffect(shadow));
        this.setCharge1.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.setCharge1.setEffect(null));

        this.setCharge2 = new ToggleButton("2");
        this.setCharge2.setMinWidth(35);
        this.setCharge2.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setCharge2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setCharge2.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.setCharge2.setEffect(shadow));
        this.setCharge2.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) ->this.setCharge2.setEffect(null));

        HBox setCharge = new HBox(this.setCharge0, this.setCharge1, this.setCharge2);
        setCharge.setSpacing(20);
        this.setCharge0.setOnMouseClicked(f -> {
            this.setCharge0.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "0";
        });
        this.setCharge1.setOnMouseClicked(f -> {
            this.setCharge0.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge1.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "1";
        });
        this.setCharge2.setOnMouseClicked(f -> {
            this.setCharge0.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge2.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "2";
        });

        this.vars[3] = "0";

        Label labNumDimensions = new Label("dimension:");
        this.setDim2 = new ToggleButton("2");
        this.setDim2.setMinWidth(55);
        this.setDim2.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setDim2.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
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
            if (this.getNappiLattice().getText().equals("LATTICE")){
                this.getNappiLattice().setText("FREE");
                this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[7] = "-";
            } else if (this.getNappiLattice().getText().equals("FREE")){
                this.getNappiLattice().setText("LATTICE");
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
        asettelu.add(empty, 0, 9, 2, 1);

       return asettelu;
    }

    /**
     *
     * @param num_part the num_part to set
     */
    private void setNumPart(int num_part) { this.num_part = num_part; }

    /**
     * @return the num_part
     */
    @Contract(pure = true)
    private int getNumPart() { return num_part; }

    /**
     *
     * @param diam the diam to set
     */
    private void setDiam(double diam) { this.diam = diam; }

    /**
     * @return the diam
     */
    @Contract(pure = true)
    private double getDiam() { return diam; }

    /**
     *
     * @param dim the dim to set
     */
    private void setDim(int dim) { this.dim = dim; }

    /**
     * @return the dim
     */
    @Contract(pure = true)
    private int getDim() { return dim; }

    /**
     *
     * @param energy_x the energy_x to set
     */
    private void setEnergyX(List<Double> energy_x) { this.energy_x = energy_x; }

    /**
     * @return the energy_x
     */
    @Contract(pure = true)
    private List<Double> getEnergyX() { return energy_x; }

    /**
     *
     * @param energy_y the energy_y to set
     */
    private void setEnergyY(List<Double> energy_y) { this.energy_y = energy_y; }

    /**
     * @return the energy_y
     */
    @Contract(pure = true)
    private List<Double> getEnergyY() { return energy_y; }

    /**
     *
     * @param measure the measure to set
     */
    private void setMeasure(double measure) { this.measure = measure; }

    /**
     * @return the measure
     */
    @Contract(pure = true)
    private double getMeasure() { return measure; }

    /**
     *
     * @param diff the diff to set
     */
    private void setDiff(double diff) { this.diff = diff; }

    /**
     * @return the diff
     */
    @Contract(pure = true)
    private double getDiff() { return diff; }

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
    private int getCompwidth() { return 150 / (int) Screen.getMainScreen().getPlatformScaleX(); }

    /**
     * @return the paneWidth
     */
    @Contract(pure = true)
    private int getPaneWidth() { return 200 / (int) Screen.getMainScreen().getPlatformScaleX(); }

    /**
     * @return the Charge0
     */
    @Contract(pure = true)
    private ToggleButton getCharge0() { return setCharge0; }

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
     * @return the fxplot
     */
    @Contract(pure = true)
    private FXPlot getFxplot() { return fxplot; }

    /**
     * @param fxplot the fxplot to set
     */
    private void setFxplot(FXPlot fxplot) { this.fxplot = fxplot; }

    /**
     * @return the remBarNappiMMC
     */
    @Contract(pure = true)
    private Button getRemBarNappiMMC() { return remBarNappiMMC; }

    /**
     * @param remBarNappiMMC the remBarNappiMMC to set
     */
    private void setRemBarNappiMMC(Button remBarNappiMMC) { this.remBarNappiMMC = remBarNappiMMC; }

    /**
     * @return the runMMC
     */
    @Contract(pure = true)
    private Button getRunMMC() { return runMMC; }

    /**
     * @param runMMC the runMMC to set
     */
    private void setRunMMC(Button runMMC) { this.runMMC = runMMC; }

    /**
     * @return the plotMMC
     */
    @Contract(pure = true)
    private Button getPlotMMC() { return plotMMC; }

    /**
     * @param plotMMC the plotMMC to set
     */
    private void setPlotMMC(Button plotMMC) { this.plotMMC = plotMMC; }

    /**
     * @return the closeNappiMMC
     */
    @Contract(pure = true)
    private Button getCloseNappiMMC() { return closeNappiMMC; }

    /**
     * @param closeNappiMMC the closeNappiMMC to set
     */
    private void setCloseNappiMMC(Button closeNappiMMC) { this.closeNappiMMC = closeNappiMMC; }

    /**
     * @return the menuNappiMMC
     */
    @Contract(pure = true)
    private Button getMenuNappiMMC() { return menuNappiMMC; }

    /**
     * @param menuNappiMMC the menuNappiMMC to set
     */
    private void setMenuNappiMMC(Button menuNappiMMC) { this.menuNappiMMC = menuNappiMMC; }

    /**
     * @return the helpNappiMMC
     */
    @Contract(pure = true)
    private Button getHelpNappiMMC() { return helpNappiMMC; }

    /**
     * @param helpNappiMMC the helpNappiMMC to set
     */
    private void setHelpNappiMMC(Button helpNappiMMC) { this.helpNappiMMC = helpNappiMMC; }

    /**
     * @return the phase
     */
    @Contract(pure = true)
    private long getPhase() { return phase; }

    /**
     * @param phase the phase to set
     */
    private void setPhase(long phase) { this.phase = phase; }

    /**
     * @return the greatest
     */
    @Contract(pure = true)
    private double getGreatest() { return greatest; }

    /**
     * @param greatest the greatest to set
     */
    private void setGreatest(double greatest) { this.greatest = greatest; }

    /**
     * @return the first
     */
    @Contract(pure = true)
    private boolean isFirst() { return first; }

    /**
     * @param first the first to set
     */
    private void setFirst(boolean first) { this.first = first; }

    /**
     * @return the linewidth
     */
    @Contract(pure = true)
    private double getLinewidth() { return linewidth; }

    /**
     * @param linewidth the linewidth to set
     */
    private void setLinewidth(double linewidth) { this.linewidth = linewidth; }

    /**
     * @return the scalefactor
     */
    @Contract(pure = true)
    private double getScalefactor() { return scalefactor; }

    /**
     * @param scalefactor the scalefactor to set
     */
    private void setScalefactor(double scalefactor) { this.scalefactor = scalefactor; }

    /**
     * @return the timerRunning
     */
    @Contract(pure = true)
    private boolean isTimerRunning() { return timerRunning; }

    /**
     * @param timerRunning the timerRunning to set
     */
    private void setTimerRunning(boolean timerRunning) { this.timerRunning = timerRunning; }

    /**
     * @return the animwidth
     */
    @Contract(pure = true)
    private int getAnimwidth() { return animwidth; }

    /**
     * @param animwidth the animwidth to set
     */
    private void setAnimwidth(int animwidth) { this.animwidth = animwidth; }

    /**
     * @return the center
     */
    @Contract(pure = true)
    private double getCenter() { return center; }

    /**
     * @param center the center to set
     */
    private void setCenter(double center) { this.center = center; }

    /**
     * @return the piirturi
     */
    @Contract(pure = true)
    private GraphicsContext getPiirturi() { return piirturi; }

    /**
     * @param piirturi the piirturi to set
     */
    private void setPiirturi(GraphicsContext piirturi) { this.piirturi = piirturi; }

    /**
     * @return the platfRunning
     */
    @Contract(pure = true)
    private boolean isPlatfRunning() { return platfRunning; }

    /**
     * @param platfRunning the platfRunning to set
     */
    private void setPlatfRunning(boolean platfRunning) { this.platfRunning = platfRunning; }

    /**
     * @return the process
     */
    @Contract(pure = true)
    private Process getProcess() { return process; }

    /**
     * @param process the process to set
     */
    private void setProcess(Process process) { this.process = process; }

    /**
     * @return the runtime
     */
    @Contract(pure = true)
    private Runtime getRuntime() { return runtime; }

    /**
     * @param runtime the runtime to set
     */
    private void setRuntime(Runtime runtime) { this.runtime = runtime; }

    /**
     * @return the exitVal
     */
    @Contract(pure = true)
    private int getExitVal() { return exitVal; }

    /**
     * @param exitVal the exitVal to set
     */
    private void setExitVal(int exitVal) { this.exitVal = exitVal; }

    /**
     * @return the timer
     */
    @Contract(pure = true)
    private Timer getTimer() { return timer; }

    /**
     * @param timer the timer to set
     */
    private void setTimer(Timer timer) { this.timer = timer; }

    /**
     * @return the values
     */
    @Contract(pure = true)
    private double[][] getValues() { return values; }

    /**
     * @param values the values to set
     */
    private void setValues(double[][] values) { this.values = values; }

    /**
     * @return the running
     */
    @Contract(pure = true)
    private boolean isRunning() { return running; }

    /**
     * @param running the running to set
     */
    private void setRunning(boolean running) { this.running = running; }

    /**
     * @return the barrier
     */
    @Contract(pure = true)
    private boolean isBarrier() { return barrier; }

    /**
     * @param barrier the barrier to set
     */
    private void setBarrier(boolean barrier) { this.barrier = barrier; }

    /**
     * @return the walk
     */
    @Contract(pure = true)
    private boolean isWalk() { return walk; }

    /**
     * @param walk the walk to set
     */
    private void setWalk(boolean walk) { this.walk = walk; }

    /**
     * @return the lattice
     */
    @Contract(pure = true)
    private boolean isLattice() { return lattice; }

    /**
     * @param lattice the lattice to set
     */
    private void setLattice(boolean lattice) { this.lattice = lattice; }

    /**
     * @return the yellowP
     */
    @Contract(pure = true)
    private Image getYellowP() { return yellowP; }

    /**
     * @param yellowP the yellowP to set
     */
    private void setYellowP(Image yellowP) { this.yellowP = yellowP; }

    /**
     * @return the grayP
     */
    @Contract(pure = true)
    private Image getGrayP() { return grayP; }

    /**
     * @param grayP the grayP to set
     */
    private void setGrayP(Image grayP) { this.grayP = grayP; }

    /**
     * @return the output
     */
    @Contract(pure = true)
    private BufferedWriter getOutput() { return output; }

    /**
     * @param output the output to set
     */
    private void setOutput(BufferedWriter output) { this.output = output; }

}
