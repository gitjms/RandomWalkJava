
package randomwalkjava;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SceneMMC extends Data {
    
    private final int compwidth = 150;
    private final int paneWidth = 200;
    private long phase;
    private boolean running;
    private double greatest;
    private final Button nappiFixed;
    private final Button nappiLattice;
    private boolean first;

    @Override
    public String[] getVars() {
        return this.vars;
    }
 
    public SceneMMC() {
        this.nappiFixed = new Button("FIXED");
        this.nappiLattice = new Button("FREE");
        this.vars = new String[]{
            "0",    // vars[0] particles        USER
            "0.0",  // vars[1] diameter         USER
            "1",    // vars[2] charge           USER
            "0",    // vars[3] steps            USER
            "0",    // vars[4] dimension        USER
            "0",    // vars[5] temperature      USER
            "-",    // vars[6] (fixed/)spread   n/a
            "-",    // vars[7] (lattice/)free   USER
            "a",    // vars[8] avoid on(/off)   n/a
            "-"};   // vars[9] save (on)        n/a
        this.running = false;
        this.phase = 0;
        this.first = false;
        this.greatest = 0.0;
    }

    public void start() {
        this.running = true;
    }

    public void stop() {
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
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

     public void refresh(File folderPath, String executable,
        GraphicsContext piirturi, double scalefactor, int animwidth, double linewidth,
        FXPlot fxplot, List<Double> energy_x, List<Double> energy_y, boolean newdata) {

        int i = 0;
        int j = 0;

        if (newdata == true) {
            this.phase = 0;
            this.first = false;
        }

        int width = 900;
        int height = 900;

        double centerX = width/2;
        double centerY = height/2;

        int num_part = Integer.valueOf(this.vars[0]);
        double diam = Double.valueOf(this.vars[1]);
        int num_steps = Integer.valueOf(this.vars[3]) + 1;
        int steps = Integer.valueOf(this.vars[3]);
        int dim = Integer.valueOf(this.vars[4]);

        double[] muistiX = new double[num_part];
        double[] muistiY = new double[num_part];
        double[] muistiZ = new double[num_part];

        double[][] values;
        if( dim < 3 )
            values = new double[2][num_part];
        else
            values = new double[3][num_part];

        String[] command = null;

        fxplot.setFrameVis(true);

        piirturi.setLineWidth(linewidth);

        try {
            command = new String[]{"cmd","/c",executable,
                this.vars[0], this.vars[1], this.vars[2], this.vars[3],
                this.vars[4], this.vars[5], this.vars[6], this.vars[7],
                this.vars[8], this.vars[9]};

            // FOR DEBUGGING
            //FileOutputStream fos = new FileOutputStream(command[0]);

            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec(command, null, folderPath);
            
            int exitVal;
            try (BufferedReader input = new BufferedReader(new InputStreamReader(
                process.getInputStream()))) {

                String line = null;

                // FOR DEBUGGING
                /*StreamGobbler outputGobbler = new StreamGobbler(
                    process.getInputStream(), "", fos);
                outputGobbler.start();*/

                while ((line = input.readLine()) != null){
                    if (line.trim().startsWith("S")) {
                        break;
                    }
                    if (!line.substring(0,1).matches("([0-9]|-|\\+)|E"))
                        continue;
                    if (!line.trim().split("(\\s+)")[0].trim().equals("E")) {
                        if (dim == 1) {
                            try {
                                values[0][i] = Double.parseDouble(line.trim()) + centerX/scalefactor;
                            } catch (NumberFormatException e) {
                                continue;
                            }
                            values[1][i] = centerY;
                        } else if (dim == 2) {
                            String[] valStr = line.split("(\\s+)");
                            try {
                                values[0][i] = Double.parseDouble(valStr[0].trim()) + centerX/scalefactor;
                                values[1][i] = Double.parseDouble(valStr[1].trim()) + centerX/scalefactor;
                            } catch (NumberFormatException e) {
                                continue;
                            }
                        } else if (dim == 3) {
                            String[] valStr = line.split("(\\s+)");
                            try {
                                values[0][i] = Double.parseDouble(valStr[0].trim()) + centerX/scalefactor;
                                values[1][i] = Double.parseDouble(valStr[1].trim()) + centerX/scalefactor;
                                values[2][i] = Double.parseDouble(valStr[2].trim()) + 1.2*centerX/scalefactor;
                            } catch (NumberFormatException e) {
                                continue;
                            }
                        }
                        // RED SOURCE DOT
                        /*if ( j == 0 && i == 0 ) {
                            piirturi.setFill(Color.RED);
                            if (dim == 1) {
                                piirturi.fillRect(
                                    values[0][i], centerY,
                                    expected / ( 10.0 * Math.sqrt(Math.log10(steps)) ),
                                    Math.sqrt(centerY/2.0));
                            } else if (dim == 2) {
                                piirturi.fillRect(
                                    values[0][i], values[1][i],
                                    expected * Math.log10(steps) / ( Math.sqrt(steps) * dim ),
                                    expected * Math.log10(steps) / ( Math.sqrt(steps) * dim ));
                            } else if (dim == 3) {
                                piirturi.fillRect(
                                    values[0][i] + Math.cos(values[2][i]),
                                    values[1][i] + Math.sin(values[2][i]),
                                    expected * Math.log10(steps) / ( Math.sqrt(steps) * dim ),
                                    expected * Math.log10(steps) / ( Math.sqrt(steps) * dim ));
                            }
                            piirturi.setStroke(Color.YELLOW);
                        }*/

                        if ( j > 0){
                            for (int k = 0; k < num_part; k++){
                                if ( dim < 3 ) {
                                    piirturi.strokeRoundRect(values[0][k], values[1][k], 1.0, 1.0, 1.0, 1.0);
                                    piirturi.setStroke(Color.BLACK);
                                    piirturi.strokeRoundRect(muistiX[k], muistiY[k], 1.0, 1.0, 1.0, 1.0);
                                    piirturi.setStroke(Color.YELLOW);
                                } else {
                                    linewidth = 10.0 * Math.log10((double) steps)
                                        / ( values[2][k] * scalefactor );
                                    piirturi.setLineWidth(linewidth);
                                    piirturi.strokeRoundRect(
                                        values[0][k], values[1][k],
                                        diam * values[0][i] + Math.cos(values[2][i]),
                                        diam * values[1][i] + Math.sin(values[2][i]),
                                        diam * values[0][i] + Math.cos(values[2][i]),
                                        diam * values[1][i] + Math.sin(values[2][i]));
                                    piirturi.setStroke(Color.BLACK);
                                    piirturi.strokeRoundRect(
                                        muistiX[k], muistiY[k],
                                        diam * muistiX[k] + Math.cos(muistiZ[k]),
                                        diam * muistiY[k] + Math.sin(muistiZ[k]),
                                        diam * muistiX[k] + Math.cos(muistiZ[k]),
                                        diam * muistiY[k] + Math.sin(muistiZ[k]));
                                        piirturi.setStroke(Color.YELLOW);
                                }
                            }
                        }
                        if ( dim < 3 ) {
                            muistiX[i] = values[0][i];
                            muistiY[i] = values[1][i];
                        } else {
                            muistiX[i] = values[0][i]
                                + Math.cos(values[2][i]);
                            muistiY[i] = values[1][i]
                                + Math.sin(values[2][i]);
                            muistiZ[i] = values[2][i];
                        }

                        i++;

                        if ( i == num_part ){
                            i = 0;
                            j++;
                        }

                        if ( j == num_steps ) {
                            i = 0;
                            j = 0;
                        }

                    } else {
                        try {
                            if ( this.first == false ) {
                                this.first = true;
                                energy_y.add(Double.parseDouble(line.split("(\\s+)")[1].trim()));
                                energy_x.add((double) this.phase);
                                this.phase++;
                                this.greatest = energy_y.get(0);
                                fxplot.setEData("energy", energy_x, energy_y);
                            } else {
                                energy_y.add(Double.parseDouble(line.split("(\\s+)")[1].trim()));
                                energy_x.add((double) this.phase);
                                this.phase++;
                            }
                        } catch (NumberFormatException e) {
                            continue;
                        }

                        Thread.sleep(100);
                        if ( energy_y.get((int) this.phase - 1) > this.greatest ) {
                            this.greatest = energy_y.get((int) this.phase - 1);
                            fxplot.setEMaxY(this.greatest);
                        }
                        fxplot.updateEData("energy", energy_x, energy_y);
                    }
                }

                exitVal = process.waitFor();
                if (exitVal != 0) {
                    runtime.exit(exitVal);
                }
                // FOR DEBUGGING
                //fos.flush();
                //fos.close();
            }

        } catch (IOException | InterruptedException e) {
            //System.out.println(e.getMessage());
        }
        // FOR ONE ROUND OPERATION
        //stop();
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
        TextField setCharge = new TextField("");
        setCharge.setOnKeyReleased(e -> {
            if (isNumInteger(setCharge.getText().trim())){
                int num = Integer.valueOf(setCharge.getText().trim());
                if ( num < 1 || num > 2 ){
                    setCharge.setText("1");
                    this.vars[2] = "1";
                }
            } else
                this.vars[2] = "0";
        });

        Label labNumSteps = new Label("number of steps:");
        TextField setNumSteps = new TextField("");
        setNumSteps.setOnKeyReleased(e -> {
            if (isNumInteger(setNumSteps.getText().trim())){
                this.vars[3] = setNumSteps.getText().trim();
            } else
                this.vars[3] = "0";
        });

        Label labNumDimensions = new Label("dimensions:");
        TextField setNumDimensions = new TextField("");
        setNumDimensions.setOnKeyReleased(e -> {
            if (isNumInteger(setNumDimensions.getText().trim())){
                this.vars[4] = setNumDimensions.getText().trim();
            } else
                this.vars[4] = "0";
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

        GridPane.setHalignment(labNumSteps, HPos.LEFT);
        asettelu.add(labNumSteps, 0, 6);
        GridPane.setHalignment(setNumSteps, HPos.CENTER);
        setNumSteps.setMinWidth(this.compwidth);
        setNumSteps.setMaxWidth(this.compwidth);
        asettelu.add(setNumSteps, 0, 7);
        
        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 8);
        GridPane.setHalignment(setNumDimensions, HPos.CENTER);
        setNumDimensions.setMinWidth(this.compwidth);
        setNumDimensions.setMaxWidth(this.compwidth);
        asettelu.add(setNumDimensions, 0, 9);
        
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
                        Color.ORANGE,CornerRadii.EMPTY,Insets.EMPTY)));
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
