
package randomwalkjava;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

public class SceneAnim extends Data {
    
    final int compwidth = 150;
    final int paneWidth = 200;
    private Button nappiAvoid;
    private boolean running;

    @Override
    public String[] getVars() {
        return this.vars;
    }
 
    public SceneAnim() {
        this.nappiAvoid = new Button("AVOID");
        this.vars = new String[]{"0","0.0","0","0","-","-"};
        this.running = false;
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

    public void refresh(File folderPath, String executable, GraphicsContext piirturi, double scalefactor) {
        int width = 800;
        int height = 800;

        double centerX = width/2;
        double centerY = height/2;
        //double centerY = height/2;

        int num_part = Integer.valueOf(this.vars[0]);
        int dim = Integer.valueOf(this.vars[3]);
        int num_steps = Integer.valueOf(this.vars[2]) + 1;

        double[] muistiX = new double[num_part];
        double[] muistiY = new double[num_part];

        double[][][] values = new double[dim][num_steps][num_part];
        String[] command = null;

        try {
            command = new String[]{"cmd","/c",executable,
                this.vars[0], this.vars[1], this.vars[2],
                this.vars[3], this.vars[4], this.vars[5]};

            FileOutputStream fos = new FileOutputStream(command[0]);
            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec(command, null, folderPath);
            
            int exitVal;
            try (BufferedReader input = new BufferedReader(new InputStreamReader(
                process.getInputStream()))) {

                //StreamGobbler errorGobbler = new StreamGobbler(
                //    process.getErrorStream(), "ERROR ");
                //errorGobbler.start();
                String line = null;

                StreamGobbler outputGobbler = new StreamGobbler(
                    process.getInputStream(), "", fos);
                outputGobbler.start();

                int i = 0;
                
                if (dim == 1)
                    while ((line = input.readLine()) != null){
                        values[0][i][1] = Double.valueOf(line.split("(\\s+)")[0].trim()) + centerX;
                        i++;
                    }
                else if (dim == 2) {
                    while (i < num_steps) {
                        line = input.readLine();
                        if (line == null || line.trim().startsWith("S"))
                            break;
                        int j = 0;
                        while (j < num_part) {
                        if ( Double.valueOf(this.vars[2]) < 1000.0 ) {
                            values[0][i][j] = Double.valueOf(line.split("(\\s+)")[0].trim())
                                + scalefactor * ( Math.log10(Double.valueOf(this.vars[2])) + 1.0 )
                                + 1.0 / scalefactor * Math.sqrt(num_steps);
                            values[1][i][j] = Double.valueOf(line.split("(\\s+)")[1].trim())
                                + scalefactor * ( Math.log10(Double.valueOf(this.vars[2])) + 1.0 )
                                + 1.0 / scalefactor * Math.sqrt(num_steps);
                        } else if ( Double.valueOf(this.vars[2]) < 10000.0 ) {
                            values[0][i][j] = Double.valueOf(line.split("(\\s+)")[0].trim())
                                + scalefactor * ( Math.log10(Double.valueOf(this.vars[2])) + 7.0 )
                                + 1.0 / scalefactor * Math.sqrt(num_steps);
                            values[1][i][j] = Double.valueOf(line.split("(\\s+)")[1].trim())
                                + scalefactor * ( Math.log10(Double.valueOf(this.vars[2])) + 7.0 )
                                + 1.0 / scalefactor * Math.sqrt(num_steps);
                        } else if ( Double.valueOf(this.vars[2]) < 100000.0 ) {
                            values[0][i][j] = Double.valueOf(line.split("(\\s+)")[0].trim())
                                + scalefactor * ( Math.log10(Double.valueOf(this.vars[2])) + 50.0 )
                                + 1.0 / scalefactor * Math.sqrt(num_steps);
                            values[1][i][j] = Double.valueOf(line.split("(\\s+)")[1].trim())
                                + scalefactor * ( Math.log10(Double.valueOf(this.vars[2])) + 50.0 )
                                + 1.0 / scalefactor * Math.sqrt(num_steps);
                        } else if ( Double.valueOf(this.vars[2]) < 1000000.0 ) {
                            values[0][i][j] = Double.valueOf(line.split("(\\s+)")[0].trim())
                                + scalefactor * ( Math.log10(Double.valueOf(this.vars[2])) + 100.0 )
                                + 1.0 / scalefactor * Math.sqrt(num_steps);
                            values[1][i][j] = Double.valueOf(line.split("(\\s+)")[1].trim())
                                + scalefactor * ( Math.log10(Double.valueOf(this.vars[2])) + 100.0 )
                                + 1.0 / scalefactor * Math.sqrt(num_steps);
                        } else {
                            values[0][i][j] = Double.valueOf(line.split("(\\s+)")[0].trim())
                                + Math.pow(scalefactor,Math.log10(Double.valueOf(this.vars[2]))/2.0) * Math.sqrt(num_steps);
                            values[1][i][j] = Double.valueOf(line.split("(\\s+)")[1].trim())
                                + Math.pow(scalefactor,Math.log10(Double.valueOf(this.vars[2]))/2.0) * Math.sqrt(num_steps);
                        }

                        muistiX[j] = values[0][i][j];
                        muistiY[j] = values[1][i][j];
                        
                        j++;
                       
                        }
                        
                        if (i > 0) {
                            for (int k = 0; k < num_part; k++)
                                for (int s = 0; s < num_steps; s++)
                                    piirturi.strokeLine(muistiX[k], muistiY[k], values[0][s][k], values[1][s][k]);
                        }
                        //piirturi.strokeLine(muistiX, muistiY, values[0][i], values[1][i]);
                        //piirturi.stroke();
                        i++;
                        /*if (i%1000 == 0) {
                            piirturi.setFill(javafx.scene.paint.Color.BLACK);
                            piirturi.strokeRect(0, 0, width, height);
                            piirturi.setFill(javafx.scene.paint.Color.YELLOW);
                            piirturi.strokeText("walks: "+i, centerX, 50, 200.0);
                            i = 0;
                        }*/
                    }
                } else if (dim == 3)
                    while ((line = input.readLine()) != null){
                        String[] parts = line.trim().split("\t");
                        values[0][i][1] = Double.valueOf(line.split("(\\s+)")[0].trim()) + centerX;
                        values[1][i][1] = Double.valueOf(line.split("(\\s+)")[1].trim()) + centerY;
                        values[2][i][1] = Double.valueOf(line.split("(\\s+)")[2].trim());// + centerZ;
                        i++;
                    }

                exitVal = process.waitFor();
                if (exitVal != 0) {
                    runtime.exit(exitVal);
                }
                fos.flush();
                fos.close();
            }

        } catch (IOException | InterruptedException e) {
            //System.out.println(e.getMessage());
        }
        stop();
    }

    // RANDOM WALK ANIMATION
    public Parent getSceneAnim(){
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
            }
        });

        Label labSizeParticles = new Label("diameter of particle:");
        TextField setSizeParticles = new TextField("");
        setSizeParticles.setOnKeyReleased(e -> {
            this.vars[1] = setSizeParticles.getText().trim();
        });

        Label labNumSteps = new Label("number of steps:");
        TextField setNumSteps = new TextField("");
        setNumSteps.setOnKeyReleased(e -> {
            this.vars[2] = setNumSteps.getText().trim();
        });

        Label labNumDimensions = new Label("dimensions:");
        TextField setNumDimensions = new TextField("");
        setNumDimensions.setOnKeyReleased(e -> {
            this.vars[3] = setNumDimensions.getText().trim();
        });

        // ...THEIR PLACEMENTS
        GridPane.setHalignment(labNumParticles, HPos.LEFT);
        asettelu.add(labNumParticles, 0, 0);
        GridPane.setHalignment(setNumParticles, HPos.CENTER);
        setNumParticles.setMinWidth(compwidth);
        setNumParticles.setMaxWidth(compwidth);
        asettelu.add(setNumParticles, 0, 1);
        
        GridPane.setHalignment(labSizeParticles, HPos.LEFT);
        asettelu.add(labSizeParticles, 0, 2);
        GridPane.setHalignment(setSizeParticles, HPos.CENTER);
        setSizeParticles.setMinWidth(compwidth);
        setSizeParticles.setMaxWidth(compwidth);
        asettelu.add(setSizeParticles, 0, 3);

        GridPane.setHalignment(labNumSteps, HPos.LEFT);
        asettelu.add(labNumSteps, 0, 4);
        GridPane.setHalignment(setNumSteps, HPos.CENTER);
        setNumSteps.setMinWidth(compwidth);
        setNumSteps.setMaxWidth(compwidth);
        asettelu.add(setNumSteps, 0, 5);
        
        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 6);
        GridPane.setHalignment(setNumDimensions, HPos.CENTER);
        setNumDimensions.setMinWidth(compwidth);
        setNumDimensions.setMaxWidth(compwidth);
        asettelu.add(setNumDimensions, 0, 7);

        // BUTTON: AVOID
        this.nappiAvoid.setMinWidth(compwidth);
        this.nappiAvoid.setMaxWidth(compwidth);
        this.nappiAvoid.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.nappiAvoid.setId("avoid");
        this.nappiAvoid.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                this.nappiAvoid.setEffect(shadow);
        });
        this.nappiAvoid.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                this.nappiAvoid.setEffect(null);
        });
        this.nappiAvoid.setOnMouseClicked((MouseEvent event) -> {
            if (this.nappiAvoid.getText().equals("AVOID")){
                // BUTTON PRESSED ON
                this.nappiAvoid.setText("AVOID ON");
                this.nappiAvoid.setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[4] = "a";
            } else if (this.nappiAvoid.getText().equals("AVOID ON")){
                // BUTTON PRESSED OFF
                this.nappiAvoid.setText("AVOID");
                this.nappiAvoid.setBackground(
                    new Background(new BackgroundFill(
                        Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[4] = "-";
            }
        });

        valikko.getChildren().add(this.nappiAvoid);
        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 8, 2, 1);

        final Pane empty1 = new Pane();
        GridPane.setHalignment(empty1, HPos.CENTER);
        asettelu.add(empty1, 0, 9, 2, 1);

        final Pane empty2 = new Pane();
        GridPane.setHalignment(empty2, HPos.CENTER);
        asettelu.add(empty2, 0, 10, 2, 1);

        return asettelu;
    }

}
