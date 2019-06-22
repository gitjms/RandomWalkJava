
package randomwalkjava;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class NappiInput extends Data {
    
    public NappiInput() {
    }

    public Parent getNappiInput(){
        GridPane asettelu = new GridPane();
        asettelu.setVgap(5);
        asettelu.setHgap(0);
        asettelu.setPadding(new Insets(0, 0, 0, 0));

        Label setNumParticles = new Label("number of particles:");
        TextField fieldNumParticles = new TextField("");
        
        Label setSizeParticles = new Label("size of particle:");
        TextField fieldSizeParticles = new TextField("");
        
        Label setNumSteps = new Label("number of steps:");
        TextField fieldNumSteps = new TextField("");
        
        Label setNumDimensions = new Label("number of dimensions:");
        TextField fieldNumDimensions = new TextField("");

        Label setAvoid = new Label("self-avoid or cross:");
        Label setXgraph = new Label("XGraph or normal:");
        
        // ...THEIR PLACEMENTS
        GridPane.setHalignment(setNumParticles, HPos.LEFT);
        asettelu.add(setNumParticles, 0, 0);
        GridPane.setHalignment(fieldNumParticles, HPos.CENTER);
        asettelu.add(fieldNumParticles, 0, 1);
        
        GridPane.setHalignment(setSizeParticles, HPos.LEFT);
        asettelu.add(setSizeParticles, 0, 3);
        GridPane.setHalignment(fieldSizeParticles, HPos.CENTER);
        asettelu.add(fieldSizeParticles, 0, 4);
        
        GridPane.setHalignment(setNumSteps, HPos.LEFT);
        asettelu.add(setNumSteps, 0, 6);
        GridPane.setHalignment(fieldNumSteps, HPos.CENTER);
        asettelu.add(fieldNumSteps, 0, 7);
        
        GridPane.setHalignment(setNumDimensions, HPos.LEFT);
        asettelu.add(setNumDimensions, 0, 9);
        GridPane.setHalignment(fieldNumDimensions, HPos.CENTER);
        asettelu.add(fieldNumDimensions, 0, 10);
        
        // BUTTON: AVOID
        GridPane.setHalignment(setAvoid, HPos.LEFT);
        asettelu.add(setAvoid, 0, 12);
        Button nappiAvoid = new Button("          AVOID          ");
        nappiAvoid.setMinWidth(200);
        nappiAvoid.setMaxWidth(200);
        GridPane.setHalignment(nappiAvoid, HPos.CENTER);
        asettelu.add(nappiAvoid, 0, 13, 2, 1);

        // BUTTON: XGRAPH
        GridPane.setHalignment(setXgraph, HPos.LEFT);
        asettelu.add(setXgraph, 0, 15);
        Button nappiXgraph = new Button("         XGRAPH          ");
        nappiXgraph.setMinWidth(200);
        nappiXgraph.setMaxWidth(200);
        GridPane.setHalignment(nappiXgraph, HPos.CENTER);
        asettelu.add(nappiXgraph, 0, 16, 2, 1);
        
        nappiAvoid.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiAvoid.setId("avoid");
        nappiXgraph.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        nappiXgraph.setId("xgraph");
        
        nappiAvoid.setOnMouseClicked((MouseEvent event) -> {
            if(nappiAvoid.getText().equals("          AVOID          ")){
                // BUTTON PRESED ON
                nappiAvoid.setText("      AVOID SET ON       ");
                nappiAvoid.setBackground(new Background(new BackgroundFill(Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                super.setVar(4,"a");
            } else if(nappiAvoid.getText().equals("      AVOID SET ON       ")){
                // BUTTON PRESED OFF
                nappiAvoid.setText("          AVOID          ");
                nappiAvoid.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
                super.setVar(4,"-");
            }
        });
        nappiXgraph.setOnMouseClicked((MouseEvent event) -> {
            if(nappiXgraph.getText().equals("         XGRAPH          ")){
                // BUTTON PRESED ON
                nappiXgraph.setText("      XGRAPH SET ON      ");
                nappiXgraph.setBackground(new Background(new BackgroundFill(Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                super.setVar(5,"x");
            } else if(nappiXgraph.getText().equals("      XGRAPH SET ON      ")){
                // BUTTON PRESED OFF
                nappiXgraph.setText("         XGRAPH          ");
                nappiAvoid.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
                super.setVar(5,"-");
            }
        });

        return asettelu;
    }
}
