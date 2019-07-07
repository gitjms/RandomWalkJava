
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

public class SceneCalculation extends Data {

    final int compwidth = 150;
    final int paneWidth = 200;
    private final Button nappiFixed;
    private final Button nappiLattice;

    @Override
    public String[] getVars() {
        return this.vars;
    }
 
    public SceneCalculation() {
        this.nappiFixed = new Button("FIXED");
        this.nappiLattice = new Button("FREE");
        this.vars = new String[]{
            "0",    // particles
            "0.0",  // size
            "0",    // steps
            "0",    // dimension
            "f",    // fixed(/spread)
            "-",    // (lattice/)free
            "-",    // avoid on/off
            "s"};   // save
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

    // R_RMS VS SQRT(N) CALCULATION
    public Parent getSceneCalc(){
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
        // this.vars[0] = "0" (amount of particles)

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

        // this.vars[4] = "f" (fixed)
        // this.vars[5] = "-" (lattice)
        // this.vars[6] = "-" (avoid)   n/a
        // this.vars[7] = "-" (save)    n/a
        
        // ...THEIR PLACEMENTS
        GridPane.setHalignment(labSizeParticles, HPos.LEFT);
        asettelu.add(labSizeParticles, 0, 0);
        GridPane.setHalignment(setSizeParticles, HPos.CENTER);
        setSizeParticles.setMinWidth(compwidth);
        setSizeParticles.setMaxWidth(compwidth);
        asettelu.add(setSizeParticles, 0, 1);

        GridPane.setHalignment(labNumSteps, HPos.LEFT);
        asettelu.add(labNumSteps, 0, 2);
        GridPane.setHalignment(setNumSteps, HPos.CENTER);
        setNumSteps.setMinWidth(compwidth);
        setNumSteps.setMaxWidth(compwidth);
        asettelu.add(setNumSteps, 0, 3);
        
        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 4);
        GridPane.setHalignment(setNumDimensions, HPos.CENTER);
        setNumDimensions.setMinWidth(compwidth);
        setNumDimensions.setMaxWidth(compwidth);
        asettelu.add(setNumDimensions, 0, 5);

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
                this.vars[4] = "f";
            } else if (this.nappiFixed.getText().equals("FIXED")){
                // BUTTON PRESSED OFF
                this.nappiFixed.setText("SPREAD");
                this.nappiFixed.setBackground(
                    new Background(new BackgroundFill(
                        Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[4] = "-";
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
                this.vars[5] = "-";
            } else if (this.nappiLattice.getText().equals("FREE")){
                // BUTTON PRESSED OFF
                this.nappiLattice.setText("LATTICE");
                this.nappiLattice.setBackground(
                    new Background(new BackgroundFill(
                        Color.ORANGE,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[5] = "l";
            }
        });

        valikko.getChildren().add(this.nappiLattice);

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 6, 2, 1);

        final Pane empty1 = new Pane();
        GridPane.setHalignment(empty1, HPos.CENTER);
        asettelu.add(empty1, 0, 7, 2, 1);

        final Pane empty2 = new Pane();
        GridPane.setHalignment(empty2, HPos.CENTER);
        asettelu.add(empty2, 0, 8, 2, 1);

        return asettelu;
    }
}
