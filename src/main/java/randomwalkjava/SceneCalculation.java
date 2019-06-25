
package randomwalkjava;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class SceneCalculation extends Data {

    final int compwidth = 150;
    final int paneWidth = 200;

    @Override
    public String[] getVars() {
        return this.vars;
    }
 
    public SceneCalculation() {
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

    // R_RMS VS SQRT(N) CALCULATION
    public Parent getSceneCalc(){
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(paneWidth);
        asettelu.setVgap(5);
        asettelu.setHgap(10);
        asettelu.setPadding(new Insets(0, 0, 0, 0));

        // COMPONENTS...
        // this.vars[1] = "0" (amount of particles)

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

        Label setNumSkips = new Label("skip between steps:");
        TextField fieldNumSkips = new TextField("");
        fieldNumSkips.setOnKeyReleased(e -> {
            this.vars[3] = fieldNumSkips.getText().trim();
        });

        Label setNumDimensions = new Label("dimensions:");
        TextField fieldNumDimensions = new TextField("");
        fieldNumDimensions.setOnKeyReleased(e -> {
            this.vars[4] = fieldNumDimensions.getText().trim();
        });

        // this.vars[5] = "-" (avoid)
        // this.vars[6] = "-" (save)
        // this.vars[7] = "-" (xgraph)
        
        // ...THEIR PLACEMENTS
        GridPane.setHalignment(setSizeParticles, HPos.LEFT);
        asettelu.add(setSizeParticles, 0, 0);
        GridPane.setHalignment(fieldSizeParticles, HPos.CENTER);
        fieldSizeParticles.setMinWidth(compwidth);
        fieldSizeParticles.setMaxWidth(compwidth);
        asettelu.add(fieldSizeParticles, 0, 1);
        
        GridPane.setHalignment(setNumSteps, HPos.LEFT);
        asettelu.add(setNumSteps, 0, 2);
        GridPane.setHalignment(fieldNumSteps, HPos.CENTER);
        fieldNumSteps.setMinWidth(compwidth);
        fieldNumSteps.setMaxWidth(compwidth);
        asettelu.add(fieldNumSteps, 0, 3);

        GridPane.setHalignment(setNumSkips, HPos.LEFT);
        asettelu.add(setNumSkips, 0, 4);
        GridPane.setHalignment(fieldNumSkips, HPos.CENTER);
        fieldNumSkips.setMinWidth(compwidth);
        fieldNumSkips.setMaxWidth(compwidth);
        asettelu.add(fieldNumSkips, 0, 5);
        
        GridPane.setHalignment(setNumDimensions, HPos.LEFT);
        asettelu.add(setNumDimensions, 0, 6);
        GridPane.setHalignment(fieldNumDimensions, HPos.CENTER);
        fieldNumDimensions.setMinWidth(compwidth);
        fieldNumDimensions.setMaxWidth(compwidth);
        asettelu.add(fieldNumDimensions, 0, 7);
        
        final Pane empty = new Pane();
        GridPane.setHalignment(empty, HPos.CENTER);
        asettelu.add(empty, 0, 8, 2, 1);

        return asettelu;
    }
}
