
package randomwalkjava;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
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

public class SceneSimulation extends Data {
    
    final int compwidth = 150;
    final int paneWidth = 200;
    private final Button nappiFixed;
    private final Button nappiLattice;
    private final Button nappiAvoid;

    @Override
    public String[] getVars() {
        return this.vars;
    }
 
    public SceneSimulation() {
        this.nappiFixed = new Button("FIXED");
        this.nappiLattice = new Button("FREE");
        this.nappiAvoid = new Button("AVOID OFF");
        this.vars = new String[]{
            "0",    // vars[0] particles        USER
            "0.0",  // vars[1] diameter         USER
            "0",    // vars[2] charge           USER
            "0",    // vars[3] steps            USER
            "0",    // vars[4] dimension        USER
            "0",    // vars[5] temperature      n/a
            "f",    // vars[6] fixed(/spread)   USER
            "-",    // vars[7] (lattice/)free   USER
            "a",    // vars[8] avoid on(/off)   USER
            "s"};   // vars[9] save (on)        n/a
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

    // RANDOM WALK SIMULATION
    public Parent getSceneSim(){
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
            if ( this.vars[2].equals("0") || this.vars[8].equals("-") ) {
                if (isNumInteger(setSizeParticles.getText().trim())){
                    if (!setSizeParticles.getText().trim().equals("0.1")){
                        setSizeParticles.setText("0.1");
                        this.vars[1] = "0.1";
                    }
                }
            } else
                if (isNumInteger(setSizeParticles.getText().trim()))
                    this.vars[1] = setSizeParticles.getText().trim();
                else
                    this.vars[1] = "0";
        });

        Label labCharge = new Label("charge of particles:");
        TextField setCharge = new TextField("");
        setCharge.setOnKeyReleased(e -> {
            if (isNumInteger(setCharge.getText().trim())){
                if (setCharge.getText().trim().equals("0")){
                    setSizeParticles.setText("0.1");
                    this.vars[1] = "0.1";
                    this.nappiAvoid.setText("AVOID OFF");
                    this.nappiAvoid.setBackground(
                        new Background(new BackgroundFill(
                            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
                    this.vars[1] = "0.1";   // diameter
                    this.vars[2] = "0";     // charge
                    this.vars[8] = "-";     // avoid
                } else {
                    this.vars[2] = setCharge.getText().trim();
                    this.nappiAvoid.setText("AVOID ON");
                    this.nappiAvoid.setBackground(
                        new Background(
                            new BackgroundFill(
                                Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                    this.vars[8] = "a";
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
            if (isNumInteger(setNumSteps.getText().trim())){
                this.vars[4] = setNumDimensions.getText().trim();
            } else
                this.vars[4] = "0";
        });

        // this.vars[5] = "0" temperature      n/a

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

        GridPane.setHalignment(labCharge, HPos.LEFT);
        asettelu.add(labCharge, 0, 4);
        GridPane.setHalignment(setCharge, HPos.CENTER);
        setCharge.setMinWidth(compwidth);
        setCharge.setMaxWidth(compwidth);
        asettelu.add(setCharge, 0, 5);

        GridPane.setHalignment(labNumSteps, HPos.LEFT);
        asettelu.add(labNumSteps, 0, 6);
        GridPane.setHalignment(setNumSteps, HPos.CENTER);
        setNumSteps.setMinWidth(compwidth);
        setNumSteps.setMaxWidth(compwidth);
        asettelu.add(setNumSteps, 0, 7);
        
        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 8);
        GridPane.setHalignment(setNumDimensions, HPos.CENTER);
        setNumDimensions.setMinWidth(compwidth);
        setNumDimensions.setMaxWidth(compwidth);
        asettelu.add(setNumDimensions, 0, 9);

        // BUTTON: FIXED
        this.nappiFixed.setMinWidth(compwidth);
        this.nappiFixed.setMaxWidth(compwidth);
        this.nappiFixed.setBackground(new Background(
            new BackgroundFill(
                Color.ORANGE,CornerRadii.EMPTY,Insets.EMPTY)));
        this.nappiFixed.setId("fixed");
        this.nappiFixed.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                this.nappiFixed.setEffect(shadow);
        });
        this.nappiFixed.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                this.nappiFixed.setEffect(null);
        });
        this.nappiFixed.setOnMouseClicked((MouseEvent event) -> {
            if (this.nappiFixed.getText().equals("SPREAD")){
                // BUTTON PRESSED ON
                this.nappiFixed.setText("FIXED");
                this.nappiFixed.setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.ORANGE,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[6] = "f";
            } else if (this.nappiFixed.getText().equals("FIXED")){
                // BUTTON PRESSED OFF
                this.nappiFixed.setText("SPREAD");
                this.nappiFixed.setBackground(
                    new Background(new BackgroundFill(
                        Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[6] = "-";
            }
        });
        valikko.getChildren().add(this.nappiFixed);

        // BUTTON: LATTICE
        this.nappiLattice.setMinWidth(compwidth);
        this.nappiLattice.setMaxWidth(compwidth);
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

        // BUTTON: AVOID
        this.nappiAvoid.setMinWidth(compwidth);
        this.nappiAvoid.setMaxWidth(compwidth);
        this.nappiAvoid.setBackground(new Background(
            new BackgroundFill(
                Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
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
            if (this.nappiAvoid.getText().equals("AVOID OFF")){
                // BUTTON PRESSED ON
                this.nappiAvoid.setText("AVOID ON");
                this.nappiAvoid.setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[2] = setSizeParticles.getText().trim();
                setSizeParticles.clear();
                setCharge.clear();
                this.vars[8] = "a";
            } else if (this.nappiAvoid.getText().equals("AVOID ON")){
                // BUTTON PRESSED OFF
                this.nappiAvoid.setText("AVOID OFF");
                this.nappiAvoid.setBackground(
                    new Background(new BackgroundFill(
                        Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
                setCharge.setText("0");
                setSizeParticles.setText("0.1");
                this.vars[1] = "0.1";   // diameter
                this.vars[2] = "0";     // charge
                this.vars[8] = "-";     // avoid
            }
        });
        valikko.getChildren().add(this.nappiAvoid);

        // this.vars[9] = "s" save on

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 10, 2, 1);

        final Pane empty = new Pane();
        GridPane.setHalignment(empty, HPos.CENTER);
        asettelu.add(empty, 0, 11, 2, 1);

       return asettelu;
    }

}
