
package randomwalkjava;

import com.sun.glass.ui.Screen;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.jetbrains.annotations.Contract;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for Rms Calculation
 */
@SuppressWarnings("SameReturnValue")
class SceneCalculation extends Data {

    private final Button nappiLattice;

    /**
     * main class gets vars via this
     * @return clone of vars array
     */
    String[] getVars() {
        return this.vars.clone();
    }
 
    /**
     * initiating scene button and user variable array
     */
    SceneCalculation() {
        super();
        this.nappiLattice = new Button("FREE");
        this.vars = new String[]{
            "0",    // vars[0] particles        n/a
            "0",    // vars[1] diameter         n/a
            "0",    // vars[2] charge           n/a
            "0",    // vars[3] steps            USER
            "0",    // vars[4] dimension        USER
            "-",    // vars[5] mmc              n/a
            "f",    // vars[6] fixed(/spread)   n/a
            "-",    // vars[7] (lattice/)free   USER
            "s"};   // vars[8] save (on)        n/a
    }

    /**
     * method for checking if user input in GUI is an integer
     * @param str GUI input string
     * @return true if input is an integer, false otherwise
     */
    private static boolean isNumInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * Create GUI for R_rms calculation
     * @return CALCULATION SCENE
     */
    Parent getSceneCalc(){
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(getPaneWidth());
        asettelu.setVgap(5);
        asettelu.setHgap(10);
        asettelu.setPadding(new Insets(0, 0, 0, 0));
        VBox valikko = new VBox();
        valikko.setPadding(new Insets(20, 10, 0, 0));
        valikko.setSpacing(10);

        DropShadow shadow = new DropShadow();

        /*
        * COMPONENTS...
        */
        this.vars[0] = "0"; // (amount of particles)
        this.vars[1] = "0"; // (diameter of particl)
        this.vars[2] = "0"; // (charge of particles)

        Label labNumSteps = new Label("number of steps:");
        TextField setNumSteps = new TextField("");
        setNumSteps.setOnKeyReleased(e -> {
            if (isNumInteger(setNumSteps.getText().trim())){
                this.vars[3] = setNumSteps.getText().trim();
            } else
                this.vars[3] = "0";
        });

        Label labNumDimensions = new Label("dimension:");
        ToggleButton setDim1 = new ToggleButton("1");
        setDim1.setMinWidth(35);
        setDim1.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setDim1.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setDim1.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim1.setEffect(shadow));
        setDim1.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim1.setEffect(null));
        ToggleButton setDim2 = new ToggleButton("2");
        setDim2.setMinWidth(35);
        setDim2.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setDim2.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setDim2.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim2.setEffect(shadow));
        setDim2.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim2.setEffect(null));
        ToggleButton setDim3 = new ToggleButton("3");
        setDim3.setMinWidth(35);
        setDim3.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        setDim3.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        setDim3.addEventHandler(
            MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim3.setEffect(shadow));
        setDim3.addEventHandler(
            MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim3.setEffect(null));
        HBox setDimension = new HBox(setDim1,setDim2,setDim3);
        setDimension.setSpacing(20);
        setDim1.setOnMouseClicked(f -> {
            setDim1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim3.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "1";
        });
        setDim2.setOnMouseClicked(f -> {
            setDim1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim3.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "2";
        });
        setDim3.setOnMouseClicked(f -> {
            setDim1.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim2.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            setDim3.setBackground(new Background(new BackgroundFill(
                Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "3";
        });

        this.vars[5] = "-"; // mmc

        /*
        * ...THEIR PLACEMENTS
        */
        GridPane.setHalignment(labNumSteps, HPos.LEFT);
        asettelu.add(labNumSteps, 0, 0);
        GridPane.setHalignment(setNumSteps, HPos.CENTER);
        setNumSteps.setMinWidth(getCompwidth());
        setNumSteps.setMaxWidth(getCompwidth());
        asettelu.add(setNumSteps, 0, 1);
        
        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 2);
        GridPane.setHalignment(setDimension, HPos.CENTER);
        setDimension.setMinWidth(getCompwidth());
        setDimension.setMaxWidth(getCompwidth());
        asettelu.add(setDimension, 0, 3);

        this.vars[6] = "f"; // fixed(/spread)

        /*
        * BUTTON: LATTICE (TOGGLE)
        */
        this.getNappiLattice().setMinWidth(getCompwidth());
        this.getNappiLattice().setMaxWidth(getCompwidth());
        this.getNappiLattice().setBackground(new Background(
            new BackgroundFill(
                Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
        this.getNappiLattice().setId("lattice");
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getNappiLattice().setEffect(shadow));
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getNappiLattice().setEffect(null));
        this.getNappiLattice().setOnMouseClicked((MouseEvent event) -> {
            if (this.getNappiLattice().getText().equals("LATTICE")){
                this.getNappiLattice().setText("FREE");
                this.getNappiLattice().setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[7] = "-";
            } else if (this.getNappiLattice().getText().equals("FREE")){
                this.getNappiLattice().setText("LATTICE");
                this.getNappiLattice().setBackground(
                    new Background(new BackgroundFill(
                        Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[7] = "l";
            }
        });
        valikko.getChildren().add(this.getNappiLattice());

        this.vars[8] = "s"; // save on

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 4, 2, 1);

        final Pane empty1 = new Pane();
        GridPane.setHalignment(empty1, HPos.CENTER);
        asettelu.add(empty1, 0, 5, 2, 1);

        final Pane empty2 = new Pane();
        GridPane.setHalignment(empty2, HPos.CENTER);
        asettelu.add(empty2, 0, 6, 2, 1);

        return asettelu;
    }

    /**
     * @return the compwidth
     */
    @Contract(pure = true)
    private double getCompwidth() { return 150.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the paneWidth
     */
    @Contract(pure = true)
    private double getPaneWidth() { return 200.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the nappiLattice
     */
    @Contract(pure = true)
    private Button getNappiLattice() {
        return nappiLattice;
    }
}
