package jms.randomwalk.scenes;

import enums.DblSizes;
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
import jms.randomwalk.datahandling.Data;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for Rms Calculation.
 */
public class SceneCalculation extends Data {

    private String language;
    private final Button nappiLattice;

    /**
     * Main class gets vars via this.
     * @return clone of vars array
     */
    public String[] getVars() {
        return this.vars.clone();
    }
 
    /**
     * Initiating scene button and user variable array.
     * @param language which ui language: finnish or english
     */
    public SceneCalculation(String language) {
        super();
        this.setLanguage(language);
        this.nappiLattice = new Button(this.getLanguage().equals("fin") ? "VAPAA" : "FREE");
        this.vars = new String[]{
            "C",    // vars[0] which simulation         n/a
            "1",    // vars[1] particles                USER
            "0",    // vars[2] diameter                 n/a
            "0",    // vars[3] steps                    USER
            "0",    // vars[4] dimension                USER
            "-",    // vars[5] efficiency or sawplot    n/a
            "f",    // vars[6] fixed(/spread)           n/a
            "-",    // vars[7] (lattice/)free           USER
            "s"};   // vars[8] save (on)                n/a
    }

    /**
     * Method for checking if user input in GUI is an integer.
     * @param str GUI input string
     * @return true if input is an integer, false otherwise
     */
    private static boolean isNumInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Create GUI for R_rms calculation.
     * @return CALCULATION SCENE
     */
    public Parent getSceneCalc() {
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(DblSizes.PANEW.getDblSize());
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

        Label labNumPart = new Label(this.getLanguage().equals("fin") ? "ajojen / hiukkasten\nlukumäärä:" : "number of runs/particles:");
        TextField setNumPart = new TextField("");
        setNumPart.setOnKeyReleased(e -> {
            if (isNumInteger(setNumPart.getText().trim())) {
                this.vars[1] = setNumPart.getText().trim();
            } else {
                this.vars[1] = "1";
            }
        });
        this.vars[2] = "0"; // (diameter of particle)

        Label labNumSteps = new Label(this.getLanguage().equals("fin") ? "askelten lukumäärä:" : "number of steps:");
        TextField setNumSteps = new TextField("");
        setNumSteps.setOnKeyReleased(e -> {
            if (isNumInteger(setNumSteps.getText().trim())) {
                this.vars[3] = setNumSteps.getText().trim();
            } else {
                this.vars[3] = "0";
            }
        });

        Label labNumDimensions = new Label(this.getLanguage().equals("fin") ? "ulottuvuus:" : "dimension:");
        ToggleButton setDim1 = new ToggleButton("1");
        setDim1.setMinWidth(DblSizes.SMLBUTW.getDblSize());
        setDim1.setMaxWidth(DblSizes.SMLBUTW.getDblSize());
        setDim1.setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, setDim1.getFont().getSize()));
        setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        setDim1.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim1.setEffect(shadow));
        setDim1.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim1.setEffect(null));
        ToggleButton setDim2 = new ToggleButton("2");
        setDim2.setMinWidth(DblSizes.SMLBUTW.getDblSize());
        setDim2.setMaxWidth(DblSizes.SMLBUTW.getDblSize());
        setDim2.setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, setDim2.getFont().getSize()));
        setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        setDim2.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim2.setEffect(shadow));
        setDim2.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim2.setEffect(null));
        ToggleButton setDim3 = new ToggleButton("3");
        setDim3.setMinWidth(DblSizes.SMLBUTW.getDblSize());
        setDim3.setMaxWidth(DblSizes.SMLBUTW.getDblSize());
        setDim3.setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, setDim3.getFont().getSize()));
        setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        setDim3.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim3.setEffect(shadow));
        setDim3.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim3.setEffect(null));
        HBox setDimension = new HBox(setDim1, setDim2, setDim3);
        setDimension.setSpacing(20);
        setDim1.setOnMouseClicked(f -> {
            setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
            setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.vars[4] = "1";
        });
        setDim2.setOnMouseClicked(f -> {
            setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
            setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.vars[4] = "2";
        });
        setDim3.setOnMouseClicked(f -> {
            setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
            this.vars[4] = "3";
        });

        this.vars[5] = "-"; // efficiency or sawplot    n/a
        this.vars[6] = "f"; // fixed(/spread)

        /*
         * BUTTON: LATTICE (TOGGLE)
         */
        this.getNappiLattice().setMinWidth(DblSizes.BUTW.getDblSize());
        this.getNappiLattice().setMaxWidth(DblSizes.BUTW.getDblSize());
        this.getNappiLattice().setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.getNappiLattice().getFont().getSize()));
        this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY)));
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getNappiLattice().setEffect(shadow));
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getNappiLattice().setEffect(null));
        this.getNappiLattice().setOnMouseClicked((MouseEvent event) -> {
            if (this.getNappiLattice().getText().equals("LATTICE") || this.getNappiLattice().getText().equals("HILA")) {
                this.getNappiLattice().setText(this.getLanguage().equals("fin") ? "VAPAA" : "FREE");
                this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY)));
                this.vars[7] = "-";
            } else if (this.getNappiLattice().getText().equals("FREE") || this.getNappiLattice().getText().equals("VAPAA")) {
                this.getNappiLattice().setText(this.getLanguage().equals("fin") ? "HILA" : "LATTICE");
                this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY)));
                this.vars[7] = "l";
            }
        });
        valikko.getChildren().add(this.getNappiLattice());

        this.vars[8] = "s"; // save on

        /*
        * ...THEIR PLACEMENTS
        */
        GridPane.setHalignment(labNumPart, HPos.LEFT);
        asettelu.add(labNumPart, 0, 0);
        GridPane.setHalignment(setNumPart, HPos.CENTER);
        setNumPart.setMinWidth(DblSizes.BUTW.getDblSize());
        setNumPart.setMaxWidth(DblSizes.BUTW.getDblSize());
        asettelu.add(setNumPart, 0, 1);

        GridPane.setHalignment(labNumSteps, HPos.LEFT);
        asettelu.add(labNumSteps, 0, 2);
        GridPane.setHalignment(setNumSteps, HPos.CENTER);
        setNumSteps.setMinWidth(DblSizes.BUTW.getDblSize());
        setNumSteps.setMaxWidth(DblSizes.BUTW.getDblSize());
        asettelu.add(setNumSteps, 0, 3);
        
        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 4);
        GridPane.setHalignment(setDimension, HPos.CENTER);
        setDimension.setMinWidth(DblSizes.BUTW.getDblSize());
        setDimension.setMaxWidth(DblSizes.BUTW.getDblSize());
        asettelu.add(setDimension, 0, 5);

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 6, 2, 1);

        return asettelu;
    }

    /**
     * @return the nappiLattice
     */
    private Button getNappiLattice() {
        return nappiLattice;
    }

    /**
     * @return the language
     */
    private String getLanguage() {
        return this.language;
    }

    /**
     * @param language the language to set
     */
    private void setLanguage(String language) {
        this.language = language;
    }

}
