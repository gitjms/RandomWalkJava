
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

public class SceneMMC extends Data {
    
    final int compwidth = 150;
    final int paneWidth = 200;
    private final Button nappiFixed;
    private final Button nappiLattice;

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
            "f",    // vars[6] fixed(/spread)   USER
            "-",    // vars[7] (lattice/)free   USER
            "a",    // vars[8] avoid on(/off)   n/a
            "-"};   // vars[9] save (on)        n/a
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
            }
        });

        Label labSizeParticles = new Label("diameter of particle:");
        TextField setSizeParticles = new TextField("");
        setSizeParticles.setOnKeyReleased(e -> {
            this.vars[1] = setSizeParticles.getText().trim();
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
            }
        });

        Label labNumSteps = new Label("number of steps:");
        TextField setNumSteps = new TextField("");
        setNumSteps.setOnKeyReleased(e -> {
            this.vars[3] = setNumSteps.getText().trim();
        });

        Label labNumDimensions = new Label("dimensions:");
        TextField setNumDimensions = new TextField("");
        setNumDimensions.setOnKeyReleased(e -> {
            this.vars[4] = setNumDimensions.getText().trim();
        });

        Label labTemperature = new Label("temperature:");
        TextField setTemperature = new TextField("");
        setTemperature.setOnKeyReleased(e -> {
            this.vars[5] = setTemperature.getText().trim();
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
        
        GridPane.setHalignment(labTemperature, HPos.LEFT);
        asettelu.add(labTemperature, 0, 10);
        GridPane.setHalignment(setTemperature, HPos.CENTER);
        setTemperature.setMinWidth(compwidth);
        setTemperature.setMaxWidth(compwidth);
        asettelu.add(setTemperature, 0, 11);

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

        // this.vars[9] = "-" save off

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 12, 2, 1);

        final Pane empty = new Pane();
        GridPane.setHalignment(empty, HPos.CENTER);
        asettelu.add(empty, 0, 13, 2, 1);

       return asettelu;
    }

}
