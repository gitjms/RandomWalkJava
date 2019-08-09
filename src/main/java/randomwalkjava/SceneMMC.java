
package randomwalkjava;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.jetbrains.annotations.Contract;

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

    private long phase;
    private double greatest;
    private boolean first;
    private double linewidth;
    private double scalefactor;
    private boolean timerRunning;
    private int animwidth;
    private double center;
    private GraphicsContext piirturi;

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
         this.setCenter((double) this.getAnimwidth() / 2.0);
         this.setLattice(this.vars[7].equals("l"));
         barrierOn();

        int num_part = parseInt(this.vars[0]);
        double diam = parseDouble(this.vars[1]);
        int dim = parseInt(this.vars[4]);

        if (newdata) {
            this.setPhase(0);
            this.setFirst(false);
            energy_x.clear();
            energy_y.clear();
            clearDots(dim);
        }

        remBarNappiMMC.setVisible(true);
        plotMMC.setVisible(false);
        remBarNappiMMC.setOnMouseClicked(event -> {
            barrierOff();
            menuNappiMMC.setDisable(true);
            helpNappiMMC.setDisable(true);
            this.getSetCharge0().setDisable(true);
            this.getSetCharge1().setDisable(true);
            this.getSetCharge2().setDisable(true);
            this.getSetDim2().setDisable(true);
            this.getSetDim3().setDisable(true);
            this.getNappiLattice().setDisable(true);
            closeNappiMMC.setDisable(true);
            remBarNappiMMC.setVisible(false);
            runMMC.setDisable(true);
            fxplot.setFrameVis();
        });

        this.setValues(new double[dim][num_part]);

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
        try {
            Thread.sleep(100);
            clearDots( dim );
            drawInitials( initialDataFile, num_part, dim, diam, measure, diff );
        } catch (InterruptedException ex) {
            Logger.getLogger(SceneMMC.class.getName()).log(Level.SEVERE, null, ex);
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
                            if (dim == 2) {
                                String[] valStr = line.split("(\\s+)");
                                try {
                                        getValues()[0][i] = Double.parseDouble(valStr[0].trim())
                                        + getCenter() / scalefactor;
                                        getValues()[1][i] = Double.parseDouble(valStr[1].trim())
                                        + getCenter() / scalefactor;
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                            } else if (dim == 3) {
                                String[] valStr = line.split("(\\s+)");
                                try {
                                        getValues()[0][i] = Double.parseDouble(valStr[0].trim())
                                        + getCenter() / scalefactor;
                                        getValues()[1][i] = Double.parseDouble(valStr[1].trim())
                                        + getCenter() / scalefactor;
                                        getValues()[2][i] = Double.parseDouble(valStr[2].trim())
                                        + getCenter() / scalefactor;
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                            }

                            platfStart();
                            javafx.application.Platform.runLater(() -> {
                                if ( !platfIsRunning()) return;
                                
                                // DRAW
                                clearDots( dim );
                                if ( dim == 2 && isLattice() )
                                    drawLattice(measure, diff );
                                for (int k = 0; k < num_part; k++){
                                    if ( dim == 2 ) {
                                        draw2Dots(getValues()[0][k], getValues()[1][k],
                                            diam);
                                    } else if ( dim == 3 ) {
                                        draw3Dots(getValues()[0][k], getValues()[1][k],
                                            getValues()[2][k], diam);
                                    }
                                }
                            });

                            i++;

                            if ( i == num_part ) i = 0;

                        } else {
                            try {
                                if ( !isFirst() ) {
                                        setFirst(true);
                                    energy_y.add(Double.parseDouble(line.split("(\\s+)")[1].trim()));
                                    energy_x.add((double) getPhase());
                                        setPhase(getPhase() + 1);
                                        setGreatest(energy_y.get(0));
                                    fxplot.setEData(energy_x, energy_y);
                                } else {
                                    energy_y.add(Double.parseDouble(line.split("(\\s+)")[1].trim()));
                                    energy_x.add((double) getPhase());
                                        setPhase(getPhase() + 1);
                                }
                            } catch (NumberFormatException e) {
                                continue;
                            }

                            Thread.sleep(50);
                            if ( energy_y.get((int) getPhase() - 1) > getGreatest() ) {
                                    setGreatest(energy_y.get((int) getPhase() - 1));
                                fxplot.setEMaxY(getGreatest());
                            }
                            fxplot.updateEData(energy_x, energy_y);
                        }
                    }

                        setExitVal(getProcess().waitFor());
                    if (getExitVal() != 0) {
                        walkStop();
                        platfStop();
                        timerStop();
                        menuNappiMMC.setDisable(false);
                        helpNappiMMC.setDisable(false);
                        runMMC.setDisable(false);
                        plotMMC.setVisible(true);
                            getSetCharge0().setDisable(false);
                            getSetCharge1().setDisable(false);
                            getSetCharge2().setDisable(false);
                            getSetDim2().setDisable(false);
                            getSetDim3().setDisable(false);
                            getNappiLattice().setDisable(false);
                        closeNappiMMC.setDisable(false);
                            getRuntime().gc();
                            getRuntime().exit(getExitVal());
                    }
                } catch (IOException | InterruptedException e) {
                    platfStop();
                    timerStop();
                    menuNappiMMC.setDisable(false);
                    helpNappiMMC.setDisable(false);
                    runMMC.setDisable(false);
                    plotMMC.setVisible(true);
                        getSetCharge0().setDisable(false);
                        getSetCharge1().setDisable(false);
                        getSetCharge2().setDisable(false);
                        getSetDim2().setDisable(false);
                        getSetDim3().setDisable(false);
                        getNappiLattice().setDisable(false);
                    closeNappiMMC.setDisable(false);
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
                               int num_part, int dim, double diam, double measure, double diff) throws IOException {

        if ( dim == 2 && isLattice() )
            drawLattice(measure, diff );
        this.getPiirturi().setLineWidth(getLinewidth());
        List<double[]> initialData = readDataMMC(initialDataFile, dim);

        this.getPiirturi().setGlobalAlpha(1.0);
        if ( num_part < 25 )
            this.getPiirturi().setLineWidth(5.0 / (Math.log(num_part)*this.getScalefactor()));
        else
            this.getPiirturi().setLineWidth(10.0 / (Math.log(num_part)*this.getScalefactor()));
        this.getPiirturi().setStroke(Color.RED);
        this.getPiirturi().strokeLine(this.getCenter() / this.getScalefactor(),
            0.0,
            this.getCenter() / this.getScalefactor(),
            2.0 * this.getCenter() / this.getScalefactor());

        /*
        * Draw initial data spots
        */
        for (int k = 0; k < num_part; k++){
            this.getValues()[0][k] = initialData.get(k)[0]
                + this.getCenter() / this.getScalefactor();
            this.getValues()[1][k] = initialData.get(k)[1]
                + this.getCenter() / this.getScalefactor();
            if ( dim == 2 )
                draw2Dots(this.getValues()[0][k], this.getValues()[1][k], diam);
            else if ( dim == 3 ) {
                this.getValues()[2][k] = initialData.get(k)[2]
                    + this.getCenter() / this.getScalefactor();
                draw3Dots(this.getValues()[0][k], this.getValues()[1][k],
                    this.getValues()[2][k], diam);
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
        TextField setNumParticles = new TextField("");
        setNumParticles.setOnKeyReleased(e -> {
            if (isNumInteger(setNumParticles.getText().trim())){
                if (setNumParticles.getText().trim().equals("0")){
                    setNumParticles.setText("1");
                    this.vars[0] = "1";
                } else {
                    this.vars[0] = setNumParticles.getText().trim();
                }
            } else
                this.vars[0] = "0";
        });

        Label labSizeParticles = new Label("diameter of particle:");
        TextField setSizeParticles = new TextField("");
        setSizeParticles.setOnKeyReleased(e -> {
            if (isNumDouble(setSizeParticles.getText().trim())){
                this.vars[1] = setSizeParticles.getText().trim();
            } else
                this.vars[1] = "0.0";
        });

        Label labCharge = new Label("charge of particles:");
        this.setSetCharge0(new ToggleButton("0"));
        this.getSetCharge0().setMinWidth(35);
        this.getSetCharge0().setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.getSetCharge0().setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.getSetCharge0().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getSetCharge0().setEffect(shadow));
        this.getSetCharge0().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getSetCharge0().setEffect(null));
        this.setSetCharge1(new ToggleButton("1"));
        this.getSetCharge1().setMinWidth(35);
        this.getSetCharge1().setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.getSetCharge1().setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.getSetCharge1().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getSetCharge1().setEffect(shadow));
        this.getSetCharge1().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getSetCharge1().setEffect(null));
        this.setSetCharge2(new ToggleButton("2"));
        this.getSetCharge2().setMinWidth(35);
        this.getSetCharge2().setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.getSetCharge2().setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.getSetCharge2().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getSetCharge2().setEffect(shadow));
        this.getSetCharge2().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getSetCharge2().setEffect(null));
        HBox setCharge = new HBox(getSetCharge0(), getSetCharge1(), getSetCharge2());
        setCharge.setSpacing(20);
        this.getSetCharge0().setOnMouseClicked(f -> {
            this.getSetCharge0().setBackground(new Background(new BackgroundFill(
                Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
            this.getSetCharge1().setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.getSetCharge2().setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "0";
        });
        this.getSetCharge1().setOnMouseClicked(f -> {
            this.getSetCharge0().setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.getSetCharge1().setBackground(new Background(new BackgroundFill(
                Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
            this.getSetCharge2().setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "1";
        });
        this.getSetCharge2().setOnMouseClicked(f -> {
            this.getSetCharge0().setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.getSetCharge1().setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.getSetCharge2().setBackground(new Background(new BackgroundFill(
                Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "2";
        });

        this.vars[3] = "0";

        Label labNumDimensions = new Label("dimension:");
        this.setSetDim2(new ToggleButton("2"));
        this.getSetDim2().setMinWidth(55);
        this.getSetDim2().setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.getSetDim2().setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.getSetDim2().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getSetDim2().setEffect(shadow));
        this.getSetDim2().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getSetDim2().setEffect(null));
        this.setSetDim3(new ToggleButton("3"));
        this.getSetDim3().setMinWidth(55);
        this.getSetDim3().setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.getSetDim3().setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.getSetDim3().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getSetDim3().setEffect(shadow));
        this.getSetDim3().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getSetDim3().setEffect(null));
        HBox setDimension = new HBox(getSetDim2(), getSetDim3());
        setDimension.setSpacing(40);
        this.getSetDim2().setOnMouseClicked(f -> {
            this.getSetDim2().setBackground(new Background(new BackgroundFill(
                Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.getSetDim3().setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "2";
        });
        this.getSetDim3().setOnMouseClicked(f -> {
            this.getSetDim2().setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.getSetDim3().setBackground(new Background(new BackgroundFill(
                Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "3";
        });

        /*
        * ...THEIR PLACEMENTS
        */
        GridPane.setHalignment(labNumParticles, HPos.LEFT);
        asettelu.add(labNumParticles, 0, 0);
        GridPane.setHalignment(setNumParticles, HPos.CENTER);
        setNumParticles.setMinWidth(this.getCompwidth());
        setNumParticles.setMaxWidth(this.getCompwidth());
        asettelu.add(setNumParticles, 0, 1);
        
        GridPane.setHalignment(labSizeParticles, HPos.LEFT);
        asettelu.add(labSizeParticles, 0, 2);
        GridPane.setHalignment(setSizeParticles, HPos.CENTER);
        setSizeParticles.setMinWidth(this.getCompwidth());
        setSizeParticles.setMaxWidth(this.getCompwidth());
        asettelu.add(setSizeParticles, 0, 3);

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
        this.getNappiLattice().setBackground(new Background(
            new BackgroundFill(
                Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
        this.getNappiLattice().setId("lattice");
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getNappiLattice().setEffect(shadow));
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getNappiLattice().setEffect(null));
        this.getNappiLattice().setOnMouseClicked((MouseEvent event) -> {
            if (this.getNappiLattice().getText().equals("LATTICE")){
                this.getNappiLattice().setText("FREE");
                this.getNappiLattice().setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[7] = "-";
            } else if (this.getNappiLattice().getText().equals("FREE")){
                this.getNappiLattice().setText("LATTICE");
                this.getNappiLattice().setBackground(
                    new Background(new BackgroundFill(
                        Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
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
    private int getCompwidth() { return 150; }

    /**
     * @return the paneWidth
     */
    @Contract(pure = true)
    private int getPaneWidth() { return 200; }

    /**
     * @return the setCharge0
     */
    @Contract(pure = true)
    private ToggleButton getSetCharge0() { return setCharge0; }

    /**
     * @param setCharge0 the setCharge0 to set
     */
    private void setSetCharge0(ToggleButton setCharge0) { this.setCharge0 = setCharge0; }

    /**
     * @return the setCharge1
     */
    @Contract(pure = true)
    private ToggleButton getSetCharge1() { return setCharge1; }

    /**
     * @param setCharge1 the setCharge1 to set
     */
    private void setSetCharge1(ToggleButton setCharge1) { this.setCharge1 = setCharge1; }

    /**
     * @return the setCharge2
     */
    @Contract(pure = true)
    private ToggleButton getSetCharge2() { return setCharge2; }

    /**
     * @param setCharge2 the setCharge2 to set
     */
    private void setSetCharge2(ToggleButton setCharge2) { this.setCharge2 = setCharge2; }

    /**
     * @return the setDim2
     */
    @Contract(pure = true)
    private ToggleButton getSetDim2() { return setDim2; }

    /**
     * @param setDim2 the setDim2 to set
     */
    private void setSetDim2(ToggleButton setDim2) { this.setDim2 = setDim2; }

    /**
     * @return the setDim3
     */
    @Contract(pure = true)
    private ToggleButton getSetDim3() { return setDim3; }

    /**
     * @param setDim3 the setDim3 to set
     */
    private void setSetDim3(ToggleButton setDim3) { this.setDim3 = setDim3; }

    /**
     * @return the nappiLattice
     */
    @Contract(pure = true)
    private Button getNappiLattice() { return nappiLattice; }

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
