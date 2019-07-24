
package randomwalkjava;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
import javafx.scene.effect.DropShadow;
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
    
    private final int compwidth = 150;
    private final int paneWidth = 200;
    private long phase;
    private double greatest;
    private final Button nappiLattice;
    private boolean first;
    private double linewidth;
    private double scalefactor;
    private GraphicsContext piirturi;
    private boolean timerRunning;
    private int animwidth;
    private boolean platfRunning;
    private Process process;
    private double center;
    private Runtime runtime;
    private int exitVal;
    private Timer timer;
    private double margin;
    private double[][] values;
    private boolean running;

    @Override
    public String[] getVars() {
        return this.vars;
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
            "0",    // vars[3] steps            USER
            "0",    // vars[4] dimension        USER
            "0",    // vars[5] temperature      USER
            "-",    // vars[6] (fixed/)spread   n/a
            "-",    // vars[7] (lattice/)free   USER
            "a",    // vars[8] avoid on(/off)   n/a
            "-"};   // vars[9] save (off)       n/a
    }

     public void refresh(File folderPath, File initialDataFile, String executable,
        GraphicsContext piirturi, double scalefactor, int animwidth,
        double linewidth, FXPlot fxplot, List<Double> energy_x,
        List<Double> energy_y, boolean newdata) {

        this.piirturi = piirturi;
        this.linewidth = linewidth;
        this.animwidth = animwidth;
        this.scalefactor = scalefactor;

        if (newdata == true) {
            this.phase = 0;
            this.first = false;
            energy_x.clear();
            energy_y.clear();
        }

        this.center = (double) this.animwidth/2.0;

        int num_part = Integer.valueOf(this.vars[0]);
        double diam = Double.valueOf(this.vars[1]);
        double d = diam - diam / ( this.scalefactor);
        int dim = Integer.valueOf(this.vars[4]);
        boolean lattice = this.vars[7].equals("l");
        if ( num_part == 2 && lattice == true)
            this.margin = 200.0;
        else
            this.margin = 0.0;

        this.values = new double[dim][num_part];

        piirturi.setLineWidth(linewidth);

        String[] command = null;

        fxplot.setFrameVis(true);

        try
        {
        command = new String[]{"cmd","/c",executable,
            this.vars[0], this.vars[1], this.vars[2], this.vars[3],
            this.vars[4], this.vars[5], this.vars[6], this.vars[7],
            this.vars[8], this.vars[9]};

        this.runtime = Runtime.getRuntime();
        runtimeStart();
        this.process = this.runtime.exec(command, null, folderPath);

        // DRAW INITIAL PARTICLES
        try {
            Thread.sleep(100);
            drawInitials( initialDataFile, num_part, dim, d);
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

                try (BufferedReader input = new BufferedReader(new InputStreamReader(
                    process.getInputStream())))
                {
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
                                    + (center - margin) / scalefactor;
                                values[1][i] = Double.parseDouble(valStr[1].trim())
                                    + (center - margin) / scalefactor;
                            } catch (NumberFormatException e) {
                                continue;
                            }
                        } else if (dim == 3) {
                            String[] valStr = line.split("(\\s+)");
                            try {
                                values[0][i] = Double.parseDouble(valStr[0].trim())
                                    + (center - margin)/scalefactor;
                                values[1][i] = Double.parseDouble(valStr[1].trim())
                                    + (center - margin)/scalefactor;
                                values[2][i] = Double.parseDouble(valStr[2].trim())
                                    + (center - margin + 50.0)/scalefactor;
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
                                clearDots(dim);
                                for (int k = 0; k < num_part; k++){
                                    if ( dim == 2 ) {
                                        draw2Dots(values[0][k], values[1][k], num_part, d);
                                    } else if ( dim == 3 ) {
                                        draw3Dots(values[0][k], values[1][k],
                                            values[2][k], num_part, d);
                                    }
                                }
                            }
                        });

                        i++;

                        if ( i == num_part ) i = 0;

                    } else {
                        try
                        {
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
                        }
                        catch (NumberFormatException e)
                        {
                        continue;
                        }

                        Thread.sleep(100);
                        if ( energy_y.get((int) phase - 1) > greatest ) {
                            greatest = energy_y.get((int) phase - 1);
                            fxplot.setEMaxY(greatest);
                        }
                        fxplot.updateEData("energy", energy_x, energy_y);
                    }
                }

                exitVal = process.waitFor();
                if (exitVal != 0) {
                    platfStop();
                    timerStop();
                    runtime.gc();
                    runtime.exit(exitVal);
                }

                }
                catch (IOException | InterruptedException e)
                {
                platfStop();
                timerStop();
                runtime.gc();
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setContentText("Walk finished.");
                    alert.show();
                });
                }
                
            }
            
        }, 0, 50);

        }
        catch (IOException e)
        {
        System.out.println(e.getMessage());
        }

    }

    public void drawInitials( File initialDataFile,
        int num_part, int dim, double d ){

        this.piirturi.setLineWidth(linewidth);
        List<double[]> initialData = readDataMMC(initialDataFile, dim);

        // Draw initial data spots
        for (int k = 0; k < num_part; k++){
            this.values[0][k] = initialData.get(k)[0]
                + (this.center - this.margin) / this.scalefactor;
            this.values[1][k] = initialData.get(k)[1]
                + (this.center - this.margin) / this.scalefactor;
            if ( dim == 2 )
                draw2Dots(this.values[0][k], this.values[1][k], num_part, d);
            else if ( dim == 3 ) {
                this.values[2][k] = initialData.get(k)[2]
                    + (this.center - this.margin + 50.0) / this.scalefactor;
                draw3Dots(this.values[0][k], this.values[1][k], this.values[2][k], num_part, d);
            }
        }
    }

    public void clearDots( int dim ){
        this.piirturi.setGlobalAlpha(1.0);
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

    public void draw2Dots(double x, double y, int num_part, double d){
        this.piirturi.setGlobalAlpha(1.0);
        this.piirturi.setLineWidth(this.linewidth);
        this.piirturi.setStroke(Color.YELLOW);
        if ( num_part < 50 ) {
            this.piirturi.strokeRoundRect(x, y, d, d, d, d );
        } else {
            this.piirturi.strokeRect(x, y,
                Math.log10(Math.pow((double) num_part, 2.0))/this.scalefactor,
                Math.log10(Math.pow((double) num_part, 2.0))/this.scalefactor);
        }
    }

    public void draw3Dots(double x, double y, double z, int num_part, double d){
        this.piirturi.setGlobalAlpha(
             Math.pow((double) num_part, 2.0) / Math.log((double) num_part ) * d/z );
        this.piirturi.setLineWidth( d/this.scalefactor );
        this.piirturi.setStroke(Color.YELLOW);
        this.piirturi.strokeRoundRect(x, y,
            num_part*d/(2.0*Math.sqrt(z)), num_part*d/(2.0*Math.sqrt(z)),
            num_part*d/(2.0*Math.sqrt(z)), num_part*d/(2.0*Math.sqrt(z)));
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
        ToggleButton setCharge0 = new ToggleButton("0");
        setCharge0.setMinWidth(35);
        setCharge0.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setCharge0.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setCharge0.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                setCharge0.setEffect(shadow);
        });
        setCharge0.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                setCharge0.setEffect(null);
        });
        ToggleButton setCharge1 = new ToggleButton("1");
        setCharge1.setMinWidth(35);
        setCharge1.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setCharge1.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setCharge1.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                setCharge1.setEffect(shadow);
        });
        setCharge1.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                setCharge1.setEffect(null);
        });
        ToggleButton setCharge2 = new ToggleButton("2");
        setCharge2.setMinWidth(35);
        setCharge2.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setCharge2.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setCharge2.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                setCharge2.setEffect(shadow);
        });
        setCharge2.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                setCharge2.setEffect(null);
        });
        HBox setCharge = new HBox(setCharge0,setCharge1,setCharge2);
        setCharge.setSpacing(20);
        setCharge0.setOnMouseClicked(f -> {
            setCharge0.setBackground(new Background(new BackgroundFill(
                Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
            setCharge1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setCharge2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "0";
        });
        setCharge1.setOnMouseClicked(f -> {
            setCharge0.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setCharge1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
            setCharge2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "1";
        });
        setCharge2.setOnMouseClicked(f -> {
            setCharge0.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setCharge1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setCharge2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTSKYBLUE,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "2";
        });

        Label labMaxIter = new Label("max iteration:");
        TextField setMaxIter = new TextField("");
        setMaxIter.setOnKeyReleased(e -> {
            if (isNumInteger(setMaxIter.getText().trim())){
                this.vars[3] = setMaxIter.getText().trim();
            } else
                this.vars[3] = "0";
        });

        Label labNumDimensions = new Label("dimensions:");
        ToggleButton setDim2 = new ToggleButton("2");
        setDim2.setMinWidth(55);
        setDim2.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setDim2.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setDim2.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                setDim2.setEffect(shadow);
        });
        setDim2.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                setDim2.setEffect(null);
        });
        ToggleButton setDim3 = new ToggleButton("3");
        setDim3.setMinWidth(55);
        setDim3.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setDim3.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setDim3.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                setDim3.setEffect(shadow);
        });
        setDim3.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                setDim3.setEffect(null);
        });
        HBox setDimension = new HBox(setDim2,setDim3);
        setDimension.setSpacing(40);
        setDim2.setOnMouseClicked(f -> {
            setDim2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim3.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "2";
        });
        setDim3.setOnMouseClicked(f -> {
            setDim2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim3.setBackground(new Background(new BackgroundFill(
                Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "3";
        });

        Label labTemperature = new Label("temperature:");
        TextField setTemperature = new TextField("");
        setTemperature.setOnKeyReleased(e -> {
            if (isNumInteger(setTemperature.getText().trim())){
                this.vars[5] = setTemperature.getText().trim();
            } else
                this.vars[5] = "0";
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
        
        GridPane.setHalignment(labMaxIter, HPos.LEFT);
        asettelu.add(labMaxIter, 0, 6);
        GridPane.setHalignment(setMaxIter, HPos.CENTER);
        setMaxIter.setMinWidth(this.compwidth);
        setMaxIter.setMaxWidth(this.compwidth);
        asettelu.add(setMaxIter, 0, 7);
        
        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 8);
        GridPane.setHalignment(setDimension, HPos.CENTER);
        setDimension.setMinWidth(this.compwidth);
        setDimension.setMaxWidth(this.compwidth);
        asettelu.add(setDimension, 0, 9);
        
        GridPane.setHalignment(labTemperature, HPos.LEFT);
        asettelu.add(labTemperature, 0, 10);
        GridPane.setHalignment(setTemperature, HPos.CENTER);
        setTemperature.setMinWidth(this.compwidth);
        setTemperature.setMaxWidth(this.compwidth);
        asettelu.add(setTemperature, 0, 11);

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
        asettelu.add(valikko, 0, 12, 2, 1);

        final Pane empty = new Pane();
        GridPane.setHalignment(empty, HPos.CENTER);
        asettelu.add(empty, 0, 13, 2, 1);

       return asettelu;
    }

}
