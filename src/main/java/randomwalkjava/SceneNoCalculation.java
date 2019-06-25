
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
import javafx.scene.paint.Color;

public class SceneNoCalculation extends Data {
    
    final int compwidth = 150;
    final int paneWidth = 200;
    public Button nappiAvoid;
    public Button nappiSave;
    public Button nappiXgraph;

    @Override
    public String[] getVars() {
        return this.vars;
    }
 
    public SceneNoCalculation() {
        this.nappiAvoid = new Button("AVOID");
        this.nappiSave = new Button("SAVE");
        this.nappiXgraph = new Button("XGRAPH");
        this.vars = new String[]{"","","","","","","",""};
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
    public Parent getSceneNoCalc(){
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(paneWidth);
        asettelu.setVgap(5);
        asettelu.setHgap(10);
        asettelu.setPadding(new Insets(0, 0, 0, 0));
        
        DropShadow shadow = new DropShadow();

        // COMPONENTS...
        Label setNumParticles = new Label("number of particles:");
        TextField fieldNumParticles = new TextField("");
        fieldNumParticles.setOnKeyReleased(e -> {
            if (isNumInteger(fieldNumParticles.getText().trim())){
                if (fieldNumParticles.getText().trim().equals("0")){
                    fieldNumParticles.setText("1");
                    this.vars[0] = "1";
                }
            }
        });

        Label setSizeParticles = new Label("diameter of particle:");
        TextField fieldSizeParticles = new TextField("");
        fieldSizeParticles.setOnKeyReleased(e -> {
            this.vars[1] = fieldSizeParticles.getText().trim();
        });
        
        Label setNumSteps = new Label("number of steps:");
        TextField fieldNumSteps = new TextField("");
        fieldNumSteps.setOnKeyReleased(e -> {
            this.vars[2] = fieldNumSteps.getText().trim();
        });

        // this.vars[3] = "0" (skip) => no skip

        Label setNumDimensions = new Label("dimensions:");
        TextField fieldNumDimensions = new TextField("");
        fieldNumDimensions.setOnKeyReleased(e -> {
            this.vars[4] = fieldNumDimensions.getText().trim();
        });
        
        Label setAvoid = new Label("self-avoid or cross:");
        Label setSave = new Label("save or real time:");
        Label setXgraph = new Label("XGraph or normal:");
        
        // ...THEIR PLACEMENTS
        GridPane.setHalignment(setNumParticles, HPos.LEFT);
        asettelu.add(setNumParticles, 0, 0);
        GridPane.setHalignment(fieldNumParticles, HPos.CENTER);
        fieldNumParticles.setMinWidth(compwidth);
        fieldNumParticles.setMaxWidth(compwidth);
        asettelu.add(fieldNumParticles, 0, 1);
        
        GridPane.setHalignment(setSizeParticles, HPos.LEFT);
        asettelu.add(setSizeParticles, 0, 2);
        GridPane.setHalignment(fieldSizeParticles, HPos.CENTER);
        fieldSizeParticles.setMinWidth(compwidth);
        fieldSizeParticles.setMaxWidth(compwidth);
        asettelu.add(fieldSizeParticles, 0, 3);
        
        GridPane.setHalignment(setNumSteps, HPos.LEFT);
        asettelu.add(setNumSteps, 0, 4);
        GridPane.setHalignment(fieldNumSteps, HPos.CENTER);
        fieldNumSteps.setMinWidth(compwidth);
        fieldNumSteps.setMaxWidth(compwidth);
        asettelu.add(fieldNumSteps, 0, 5);
        
        GridPane.setHalignment(setNumDimensions, HPos.LEFT);
        asettelu.add(setNumDimensions, 0, 6);
        GridPane.setHalignment(fieldNumDimensions, HPos.CENTER);
        fieldNumDimensions.setMinWidth(compwidth);
        fieldNumDimensions.setMaxWidth(compwidth);
        asettelu.add(fieldNumDimensions, 0, 7);
        
        // BUTTON: AVOID
        GridPane.setHalignment(setAvoid, HPos.LEFT);
        asettelu.add(setAvoid, 0, 8);
        this.nappiAvoid.setMinWidth(compwidth);
        this.nappiAvoid.setMaxWidth(compwidth);
        GridPane.setHalignment(this.nappiAvoid, HPos.LEFT);
        asettelu.add(this.nappiAvoid, 0, 9, 2, 1);
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
                this.vars[5] = "a";
            } else if (this.nappiAvoid.getText().equals("AVOID ON")){
                // BUTTON PRESSED OFF
                this.nappiAvoid.setText("AVOID");
                this.nappiAvoid.setBackground(
                    new Background(new BackgroundFill(
                        Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[5] = "-";
            }
        });

        // BUTTON: SAVE / REAL TIME
        GridPane.setHalignment(setSave, HPos.LEFT);
        asettelu.add(setSave, 0, 9);
        this.nappiSave.setMinWidth(compwidth);
        this.nappiSave.setMaxWidth(compwidth);
        GridPane.setHalignment(this.nappiSave, HPos.LEFT);
        asettelu.add(this.nappiSave, 0, 10, 2, 1);
        this.nappiSave.setBackground(new Background(
            new BackgroundFill(
                Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
        this.nappiSave.setId("save");
        this.nappiSave.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                this.nappiSave.setEffect(shadow);
        });
        this.nappiSave.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                this.nappiSave.setEffect(null);
        });
        this.nappiSave.setOnMouseClicked((MouseEvent event) -> {
            if (this.nappiSave.getText().equals("SAVE")){
                // BUTTON PRESSED FROM SAVE TO REAL TIME
                this.nappiSave.setText("REAL TIME");
                this.nappiSave.setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.DARKORANGE,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[6] = "-";
            } else if (this.nappiSave.getText().equals("REAL TIME")){
                // BUTTON PRESSED FROM REAL TIME TO SAVE
                this.nappiSave.setText("SAVE");
                this.nappiSave.setBackground(
                    new Background(new BackgroundFill(
                        Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[6] = "s";
            }
        });

        // BUTTON: XGRAPH
        GridPane.setHalignment(setXgraph, HPos.LEFT);
        asettelu.add(setXgraph, 0, 11);
        this.nappiXgraph.setMinWidth(compwidth);
        this.nappiXgraph.setMaxWidth(compwidth);
        GridPane.setHalignment(this.nappiXgraph, HPos.LEFT);
        asettelu.add(this.nappiXgraph, 0, 12, 2, 1);
        this.nappiXgraph.setBackground(new Background(
            new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.nappiXgraph.setId("xgraph");
        this.nappiXgraph.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                this.nappiXgraph.setEffect(shadow);
        });
        this.nappiXgraph.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                this.nappiXgraph.setEffect(null);
        });
        this.nappiXgraph.setOnMouseClicked((MouseEvent event) -> {
            if (this.nappiXgraph.getText().equals("XGRAPH")){
                // BUTTON PRESSED ON
                this.nappiXgraph.setText("XGRAPH ON");
                this.nappiXgraph.setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[7] = "x";
            } else if(this.nappiXgraph.getText().equals("XGRAPH ON")){
                // BUTTON PRESSED OFF
                this.nappiXgraph.setText("XGRAPH");
                this.nappiXgraph.setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[7] = "-";
            }
        });

        final Pane empty = new Pane();
        GridPane.setHalignment(empty, HPos.CENTER);
        asettelu.add(empty, 0, 13, 2, 1);

        return asettelu;
    }

}
