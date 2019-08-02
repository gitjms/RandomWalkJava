
package randomwalkjava;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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

public class SceneMMC extends Data {

    final File folder = new File("C:\\DATA");
    private final int compwidth = 150;
    private final int paneWidth = 200;
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

    @Override
    public String[] getVars() {
        return this.vars;
    }

    public void barrierOn() {
        this.barrier = true;
    }
    public void barrierOff() {
        this.barrier = false;
    }
    public boolean barrierIsOn() {
        return this.barrier;
    }

    public void walkStart() {
        this.walk = true;
    }
    public void walkStop() {
        this.walk = false;
    }
    public boolean walkState() {
        return this.walk;
    }

    public void runtimeStart() {
        this.running = true;
    }
    public boolean runtimeIsRunning() {
        return this.running;
    }
    public void stopRuntime() {
        this.running = false;
        this.runtime.exit(this.exitVal);
    }

    public void timerStart() {
        this.timerRunning = true;
    }
    public void timerStop() {
        this.timer.cancel();
        this.timer.purge();
        this.timerRunning = false;
    }
    public boolean timerIsRunning() {
        return this.timerRunning;
    }

    public BufferedWriter getProcOut() {
        return this.output;
    }

    public void platfStart() {
        this.platfRunning = true;
    }
    public void platfStop() {
        this.process.destroyForcibly();
        this.platfRunning = false;
    }
    public boolean platfIsRunning() {
        return this.platfRunning;
    }

    public SceneMMC() {
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
            "a",    // vars[8] avoid on(/off)   n/a
            "-"};   // vars[9] save (off)       n/a
    }

     public void refresh(File initialDataFile, String executable,
        GraphicsContext piirturi, double scalefactor, int animwidth,
        double linewidth, FXPlot fxplot, Button remBarNappiMMC, Button runMMC,
        Button plotMMC, Button closeNappiMMC, Button menuNappiMMC,
        Button helpNappiMMC, List<Double> energy_x, List<Double> energy_y,
        boolean newdata, int measure, double diff) {

        //this.yellowP = new Image("images/Mickey.png");
        //this.grayP = new Image("images/Minnie.png");
        this.yellowP = new Image("images/Pyellow.png");
        this.grayP = new Image("images/Pgray.png");

        this.piirturi = piirturi;
        this.linewidth = linewidth;
        this.animwidth = animwidth;
        this.scalefactor = scalefactor;
        this.center = (double) this.animwidth/2.0;
        if ( this.vars[7].equals("l") )
            this.lattice = true;
        else
            this.lattice = false;
        barrierOn();

        int num_part = Integer.valueOf(this.vars[0]);
        double diam = Double.valueOf(this.vars[1]);
        int dim = Integer.valueOf(this.vars[4]);

        if (newdata == true) {
            this.phase = 0;
            this.first = false;
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
            this.setCharge0.setDisable(true);
            this.setCharge1.setDisable(true);
            this.setCharge2.setDisable(true);
            this.setDim2.setDisable(true);
            this.setDim3.setDisable(true);
            this.nappiLattice.setDisable(true);
            closeNappiMMC.setDisable(true);
            remBarNappiMMC.setVisible(false);
            runMMC.setDisable(true);
            fxplot.setFrameVis(true);
        });

        this.values = new double[dim][num_part];

        piirturi.setLineWidth(linewidth);

        String[] command = null;

        try
        {
        command = new String[]{"cmd","/c",executable,
            this.vars[0], this.vars[1], this.vars[2], this.vars[3],
            this.vars[4], this.vars[5], this.vars[6], this.vars[7],
            this.vars[8], this.vars[9]};

        this.runtime = Runtime.getRuntime();
        runtimeStart();

        this.process = this.runtime.exec(command, null, this.folder);
        walkStart();

        // DRAW INITIAL PARTICLES
        try {
            Thread.sleep(100);
            clearDots( dim );
            drawInitials( initialDataFile, num_part, dim, diam, measure, diff );
        } catch (InterruptedException ex) {
            Logger.getLogger(SceneMMC.class.getName()).log(Level.SEVERE, null, ex);
        }

        timerStart();
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int i = 0;

                if ( !timerIsRunning()) return;

                while ( barrierIsOn() == true ) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                }

                if ( barrierIsOn() == false ) {
                    output = new BufferedWriter(new OutputStreamWriter(
                        process.getOutputStream()));// {
                        PrintWriter pw = null;
                        if (output != null)
                            pw = new PrintWriter(output);
                        if (pw != null)
                            pw.println("x");
                        if (pw != null) {
                            pw.flush();
                            pw.close();
                        }
                    try {
                        output.close();
                    } catch (IOException ex) {
                        Logger.getLogger(SceneMMC.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                try (BufferedReader input = new BufferedReader(new InputStreamReader(
                    process.getInputStream()))) {
                    String line = null;

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
                                    values[0][i] = Double.parseDouble(valStr[0].trim())
                                        + center / scalefactor;
                                    values[1][i] = Double.parseDouble(valStr[1].trim())
                                        + center / scalefactor;
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                            } else if (dim == 3) {
                                String[] valStr = line.split("(\\s+)");
                                try {
                                    values[0][i] = Double.parseDouble(valStr[0].trim())
                                        + center / scalefactor;
                                    values[1][i] = Double.parseDouble(valStr[1].trim())
                                        + center / scalefactor;
                                    values[2][i] = Double.parseDouble(valStr[2].trim())
                                        + center / scalefactor;
                                } catch (NumberFormatException e) {
                                    continue;
                                }
                            }

                            platfStart();
                            javafx.application.Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    if ( !platfIsRunning()) return;

                                    // DRAW
                                    clearDots( dim );
                                    if ( lattice == true ) drawLattice( dim, num_part, measure, diff );
                                    for (int k = 0; k < num_part; k++){
                                        if ( dim == 2 ) {
                                            draw2Dots(values[0][k], values[1][k],
                                                num_part, diam);
                                        } else if ( dim == 3 ) {
                                            draw3Dots(values[0][k], values[1][k],
                                                values[2][k], num_part, diam);
                                        }
                                    }
                                }
                            });

                            i++;

                            if ( i == num_part ) i = 0;

                        } else {
                            try {
                                if ( first == false ) {
                                    first = true;
                                    energy_y.add(Double.parseDouble(line.split("(\\s+)")[1].trim()));
                                    energy_x.add((double) phase);
                                    phase++;
                                    greatest = energy_y.get(0);
                                    fxplot.setEData("energy", energy_x, energy_y);
                                } else {
                                    energy_y.add(Double.parseDouble(line.split("(\\s+)")[1].trim()));
                                    energy_x.add((double) phase);
                                    phase++;
                                }
                            } catch (NumberFormatException e) {
                                continue;
                            }

                            Thread.sleep(50);
                            if ( energy_y.get((int) phase - 1) > greatest ) {
                                greatest = energy_y.get((int) phase - 1);
                                fxplot.setEMaxY(greatest);
                            }
                            fxplot.updateEData("energy", energy_x, energy_y);
                        }
                    }

                    exitVal = process.waitFor();
                    if (exitVal != 0) {
                        walkStop();
                        platfStop();
                        timerStop();
                        menuNappiMMC.setDisable(false);
                        helpNappiMMC.setDisable(false);
                        runMMC.setDisable(false);
                        plotMMC.setVisible(true);
                        setCharge0.setDisable(false);
                        setCharge1.setDisable(false);
                        setCharge2.setDisable(false);
                        setDim2.setDisable(false);
                        setDim3.setDisable(false);
                        nappiLattice.setDisable(false);
                        closeNappiMMC.setDisable(false);
                        runtime.gc();
                        runtime.exit(exitVal);
                    }
                } catch (IOException | InterruptedException e) {
                    platfStop();
                    timerStop();
                    menuNappiMMC.setDisable(false);
                    helpNappiMMC.setDisable(false);
                    runMMC.setDisable(false);
                    plotMMC.setVisible(true);
                    setCharge0.setDisable(false);
                    setCharge1.setDisable(false);
                    setCharge2.setDisable(false);
                    setDim2.setDisable(false);
                    setDim3.setDisable(false);
                    nappiLattice.setDisable(false);
                    closeNappiMMC.setDisable(false);
                    runtime.gc();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setContentText("Walk finished.");
                        walkStop();
                        alert.show();
                    });
                }
            // timer run ends
            }
        // timer ends
        }, 0, 50);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void drawInitials( File initialDataFile,
        int num_part, int dim, double diam, int measure, double diff ){

        if ( lattice == true ) drawLattice( dim, num_part, measure, diff );
        this.piirturi.setLineWidth(linewidth);
        List<double[]> initialData = readDataMMC(initialDataFile, dim);

        this.piirturi.setGlobalAlpha(1.0);
        if ( num_part < 25 )
            this.piirturi.setLineWidth(1.0 / (Math.log(num_part)*this.scalefactor));
        else
            this.piirturi.setLineWidth(10.0 / (Math.log(num_part)*this.scalefactor));
        this.piirturi.setStroke(Color.RED);
        this.piirturi.strokeLine(
            this.center / this.scalefactor,
            0.0,
            this.center / this.scalefactor,
            2.0 * this.center / this.scalefactor);

        // Draw initial data spots
        for (int k = 0; k < num_part; k++){
            this.values[0][k] = initialData.get(k)[0]
                + this.center / this.scalefactor;
            this.values[1][k] = initialData.get(k)[1]
                + this.center / this.scalefactor;
            if ( dim == 2 )
                draw2Dots(this.values[0][k], this.values[1][k], num_part, diam);
            else if ( dim == 3 ) {
                this.values[2][k] = initialData.get(k)[2]
                    + this.center / this.scalefactor;
                draw3Dots(this.values[0][k], this.values[1][k],
                    this.values[2][k], num_part, diam);
            }
        }
    }

    public void clearDots( int dim ){
        this.piirturi.setGlobalAlpha(1.0);
        this.piirturi.setGlobalBlendMode(BlendMode.SRC_OVER);
        this.piirturi.setFill(Color.BLACK);
        if ( dim == 2 )
            this.piirturi.fillRect( 0, 0,
                this.animwidth / this.scalefactor,
                this.animwidth / this.scalefactor);
        else if ( dim == 3 )
            this.piirturi.fillRect(0, 0,
                1.0/this.scalefactor*this.animwidth,
                1.0/this.scalefactor*this.animwidth);
        this.piirturi.fill();
    }

    public void draw2Dots(double x, double y, int num_part, double diam){
        this.piirturi.drawImage(this.yellowP, x - diam/2.0, y - diam/2.0, diam, diam);
    }

    public void draw3Dots(double x, double y, double z, int num_part, double diam){
        this.piirturi.setGlobalAlpha( 1.0 / ( Math.log(2.0 * z) ) );
        this.piirturi.setLineWidth(this.linewidth);
        this.piirturi.setGlobalBlendMode(BlendMode.LIGHTEN);
        this.piirturi.setFill(Color.YELLOW);
        this.piirturi.fillRoundRect(
            x - diam/( Math.log(2.0 * z) ),
            y - diam/( Math.log(2.0 * z) ),
            5.0*diam/( 2.0*Math.log(2.0 * z)),  5.0*diam/( 2.0*Math.log(2.0 * z)),
            5.0*diam/( 2.0*Math.log(2.0 * z)),  5.0*diam/( 2.0*Math.log(2.0 * z))
        );
    }
 
    public void drawLattice( int dim, int num_part, int measure, double diff ) {
        if ( dim == 2 ) {
            for ( int i = 0; i < measure + 2; i+=2 ) {
                for ( int j = 0; j < measure + 2; j+=2 ) {
                    this.piirturi.drawImage(this.grayP,
                        (double) i + diff, (double) j + diff,
                        1.0, 1.0);
                }
            }
        }
    }

    public static boolean isNumDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static boolean isNumInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    // RANDOM WALK MMC
    public Parent getSceneMMC(){
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(paneWidth);
        asettelu.setVgap(5);
        asettelu.setHgap(10);
        asettelu.setPadding(new Insets(0, 0, 0, 0));
        VBox valikko = new VBox();
        valikko.setPadding(new Insets(20, 10, 0, 0));
        valikko.setSpacing(10);
        
        DropShadow shadow = new DropShadow();

        // COMPONENTS...
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
        this.setCharge0 = new ToggleButton("0");
        this.setCharge0.setMinWidth(35);
        this.setCharge0.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setCharge0.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setCharge0.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                this.setCharge0.setEffect(shadow);
        });
        this.setCharge0.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                this.setCharge0.setEffect(null);
        });
        this.setCharge1 = new ToggleButton("1");
        this.setCharge1.setMinWidth(35);
        this.setCharge1.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setCharge1.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setCharge1.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                this.setCharge1.setEffect(shadow);
        });
        this.setCharge1.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                this.setCharge1.setEffect(null);
        });
        this.setCharge2 = new ToggleButton("2");
        this.setCharge2.setMinWidth(35);
        this.setCharge2.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setCharge2.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setCharge2.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                this.setCharge2.setEffect(shadow);
        });
        this.setCharge2.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                this.setCharge2.setEffect(null);
        });
        HBox setCharge = new HBox(setCharge0,setCharge1,setCharge2);
        setCharge.setSpacing(20);
        this.setCharge0.setOnMouseClicked(f -> {
            this.setCharge0.setBackground(new Background(new BackgroundFill(
                Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "0";
        });
        this.setCharge1.setOnMouseClicked(f -> {
            this.setCharge0.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "1";
        });
        this.setCharge2.setOnMouseClicked(f -> {
            this.setCharge0.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "2";
        });

        this.vars[3] = "0";

        Label labNumDimensions = new Label("dimensions:");
        this.setDim2 = new ToggleButton("2");
        this.setDim2.setMinWidth(55);
        this.setDim2.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setDim2.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setDim2.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                this.setDim2.setEffect(shadow);
        });
        this.setDim2.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                this.setDim2.setEffect(null);
        });
        this.setDim3 = new ToggleButton("3");
        this.setDim3.setMinWidth(55);
        this.setDim3.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setDim3.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setDim3.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                this.setDim3.setEffect(shadow);
        });
        this.setDim3.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                this.setDim3.setEffect(null);
        });
        HBox setDimension = new HBox(setDim2,setDim3);
        setDimension.setSpacing(40);
        this.setDim2.setOnMouseClicked(f -> {
            this.setDim2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "2";
        });
        this.setDim3.setOnMouseClicked(f -> {
            this.setDim2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(
                Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "3";
        });

        // ...THEIR PLACEMENTS
        GridPane.setHalignment(labNumParticles, HPos.LEFT);
        asettelu.add(labNumParticles, 0, 0);
        GridPane.setHalignment(setNumParticles, HPos.CENTER);
        setNumParticles.setMinWidth(this.compwidth);
        setNumParticles.setMaxWidth(this.compwidth);
        asettelu.add(setNumParticles, 0, 1);
        
        GridPane.setHalignment(labSizeParticles, HPos.LEFT);
        asettelu.add(labSizeParticles, 0, 2);
        GridPane.setHalignment(setSizeParticles, HPos.CENTER);
        setSizeParticles.setMinWidth(this.compwidth);
        setSizeParticles.setMaxWidth(this.compwidth);
        asettelu.add(setSizeParticles, 0, 3);

        GridPane.setHalignment(labCharge, HPos.LEFT);
        asettelu.add(labCharge, 0, 4);
        GridPane.setHalignment(setCharge, HPos.CENTER);
        setCharge.setMinWidth(this.compwidth);
        setCharge.setMaxWidth(this.compwidth);
        asettelu.add(setCharge, 0, 5);

        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 6);
        GridPane.setHalignment(setDimension, HPos.CENTER);
        setDimension.setMinWidth(this.compwidth);
        setDimension.setMaxWidth(this.compwidth);
        asettelu.add(setDimension, 0, 7);

        this.vars[5] = "m"; // mmc
        this.vars[6] = "-"; // spread out

        // BUTTON: LATTICE
        this.nappiLattice.setMinWidth(this.compwidth);
        this.nappiLattice.setMaxWidth(this.compwidth);
        this.nappiLattice.setBackground(new Background(
            new BackgroundFill(
                Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
        this.nappiLattice.setId("lattice");
        this.nappiLattice.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                this.nappiLattice.setEffect(shadow);
        });
        this.nappiLattice.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                this.nappiLattice.setEffect(null);
        });
        this.nappiLattice.setOnMouseClicked((MouseEvent event) -> {
            if (this.nappiLattice.getText().equals("LATTICE")){
                // BUTTON PRESSED ON
                this.nappiLattice.setText("FREE");
                this.nappiLattice.setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[7] = "-";
            } else if (this.nappiLattice.getText().equals("FREE")){
                // BUTTON PRESSED OFF
                this.nappiLattice.setText("LATTICE");
                this.nappiLattice.setBackground(
                    new Background(new BackgroundFill(
                        Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[7] = "l";
            }
        });
        valikko.getChildren().add(this.nappiLattice);

        this.vars[8] = "a"; // avoid on
        this.vars[9] = "-"; // save off

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 8, 2, 1);

        final Pane empty = new Pane();
        GridPane.setHalignment(empty, HPos.CENTER);
        asettelu.add(empty, 0, 9, 2, 1);

       return asettelu;
    }

}
