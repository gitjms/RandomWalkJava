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
 * Class for Path Tracing.
 */
public class ScenePathTracing extends Data {

    private String language;
    private final Button nappiFixed;
    private final Button nappiLattice;
    private ToggleButton setDim1;
    private ToggleButton setDim2;
    private ToggleButton setDim3;
    private TextField setNumParticles;
    private TextField setNumSteps;

    /**
     * Main class gets vars via this.
     * @return clone of vars array
     */
    public String[] getVars() {
        return this.vars.clone();
    }
 
    /**
     * Initiating scene buttons and user variable array.
     * @param language which ui language: finnish or english
     */
    public ScenePathTracing(String language) {
        super();
        this.setLanguage(language);
        this.nappiFixed = new Button(this.getLanguage().equals("fin") ? "KESKITETTY" : "FIXED");
        this.nappiLattice = new Button(this.getLanguage().equals("fin") ? "VAPAA" : "FREE");
        this.vars = new String[]{
            "A",    // vars[0] which simulation         USER
            "0",    // vars[1] particles                USER
            "0.0",  // vars[2] diameter                 n/a
            "0",    // vars[3] steps                    USER
            "0",    // vars[4] dimension                USER
            "-",    // vars[5] efficiency or sawplot    n/a
            "f",    // vars[6] fixed(/spread)           USER
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
     * Create GUI for Path tracing.
     * @return PATH TRACING SCENE
     */
    public Parent getScenePath() {
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
        Label labNumParticles = new Label(this.getLanguage().equals("fin") ? "hiukkasten lukumäärä:" : "number of particles:");
        this.setNumParticles = new TextField("");
        this.setNumParticles.setOnKeyReleased(e -> {
            if (isNumInteger(this.setNumParticles.getText().trim())) {
                if (this.setNumParticles.getText().trim().equals("0")) {
                    this.setNumParticles.setText("1");
                    this.vars[1] = "1";
                } else {
                    this.vars[1] = this.setNumParticles.getText().trim();
                }
            } else {
                this.vars[1] = "0";
            }
        });

        this.vars[2] = "0";

        Label labNumSteps = new Label(this.getLanguage().equals("fin") ? "askelten lukumäärä:" : "number of steps:");
        this.setNumSteps = new TextField("");
        this.setNumSteps.setOnKeyReleased(e -> {
            if (isNumInteger(this.setNumSteps.getText().trim())) {
                this.vars[3] = this.setNumSteps.getText().trim();
            } else {
                this.vars[3] = "0";
            }
        });

        Label labNumDimensions = new Label(this.getLanguage().equals("fin") ? "ulottuvuus:" : "dimension:");
        this.setDim1 = new ToggleButton("1");
        this.setDim1.setMinWidth(DblSizes.SMLBUTW.getDblSize());
        this.setDim1.setMaxWidth(DblSizes.SMLBUTW.getDblSize());
        this.setDim1.setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.setDim1.getFont().getSize()));
        this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setDim1.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim1.setEffect(shadow));
        this.setDim1.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim1.setEffect(null));

        this.setDim2 = new ToggleButton("2");
        this.setDim2.setMinWidth(DblSizes.SMLBUTW.getDblSize());
        this.setDim2.setMaxWidth(DblSizes.SMLBUTW.getDblSize());
        this.setDim2.setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.setDim2.getFont().getSize()));
        this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setDim2.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim2.setEffect(shadow));
        this.setDim2.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim2.setEffect(null));

        this.setDim3 = new ToggleButton("3");
        this.setDim3.setMinWidth(DblSizes.SMLBUTW.getDblSize());
        this.setDim3.setMaxWidth(DblSizes.SMLBUTW.getDblSize());
        this.setDim3.setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.setDim3.getFont().getSize()));
        this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setDim3.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim3.setEffect(shadow));
        this.setDim3.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim3.setEffect(null));

        HBox setDimension = new HBox(this.setDim1, this.setDim2, this.setDim3);
        setDimension.setSpacing(20);
        this.setDim1.setOnMouseClicked(f -> {
            this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.vars[4] = "1";
        });
        this.setDim2.setOnMouseClicked(f -> {
            this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.vars[4] = "2";
        });
        this.setDim3.setOnMouseClicked(f -> {
            this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
            this.vars[4] = "3";
        });

        this.vars[5] = "-"; // efficiency or sawplot        n/a

        /*
        * ...THEIR PLACEMENTS
        */
        GridPane.setHalignment(labNumParticles, HPos.LEFT);
        asettelu.add(labNumParticles, 0, 0);
        GridPane.setHalignment(this.setNumParticles, HPos.CENTER);
        this.setNumParticles.setMinWidth(DblSizes.BIGCOMPW.getDblSize());
        this.setNumParticles.setMaxWidth(DblSizes.BIGCOMPW.getDblSize());
        asettelu.add(this.setNumParticles, 0, 1);

        GridPane.setHalignment(labNumSteps, HPos.LEFT);
        asettelu.add(labNumSteps, 0, 2);
        GridPane.setHalignment(this.setNumSteps, HPos.CENTER);
        this.setNumSteps.setMinWidth(DblSizes.BIGCOMPW.getDblSize());
        this.setNumSteps.setMaxWidth(DblSizes.BIGCOMPW.getDblSize());
        asettelu.add(this.setNumSteps, 0, 3);
        
        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 4);
        GridPane.setHalignment(setDimension, HPos.CENTER);
        setDimension.setMinWidth(DblSizes.BIGCOMPW.getDblSize());
        setDimension.setMaxWidth(DblSizes.BIGCOMPW.getDblSize());
        asettelu.add(setDimension, 0, 5);

        /*
        * BUTTON: FIXED
        */
        this.getNappiFixed().setMinWidth(DblSizes.BIGCOMPW.getDblSize());
        this.getNappiFixed().setMaxWidth(DblSizes.BIGCOMPW.getDblSize());
        this.getNappiFixed().setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.getNappiFixed().getFont().getSize()));
        this.getNappiFixed().setBackground(new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY)));
        this.getNappiFixed().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getNappiFixed().setEffect(shadow));
        this.getNappiFixed().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getNappiFixed().setEffect(null));
        this.getNappiFixed().setOnMouseClicked((MouseEvent event) -> {
            if (this.getNappiFixed().getText().equals("HAJAUTETTU") || this.getNappiFixed().getText().equals("SPREAD")) {
                this.getNappiFixed().setText(this.getLanguage().equals("fin") ? "KESKITETTY" : "FIXED");
                this.getNappiFixed().setBackground(new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY)));
                this.vars[6] = "f";
            } else if (this.getNappiFixed().getText().equals("KESKITETTY") || this.getNappiFixed().getText().equals("FIXED")) {
                this.getNappiFixed().setText(this.getLanguage().equals("fin") ? "HAJAUTETTU" : "SPREAD");
                this.getNappiFixed().setBackground(new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY)));
                this.vars[6] = "-";
            }
        });
        valikko.getChildren().add(this.getNappiFixed());

        /*
        * BUTTON: LATTICE
        */
        this.getNappiLattice().setMinWidth(DblSizes.BIGCOMPW.getDblSize());
        this.getNappiLattice().setMaxWidth(DblSizes.BIGCOMPW.getDblSize());
        this.getNappiLattice().setFont(Font.font("System Regular", FontWeight.EXTRA_BOLD, this.getNappiLattice().getFont().getSize()));
        this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY)));
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getNappiLattice().setEffect(shadow));
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getNappiLattice().setEffect(null));
        this.getNappiLattice().setOnMouseClicked((MouseEvent event) -> {
            if (this.getNappiLattice().getText().equals("HILA") || this.getNappiLattice().getText().equals("LATTICE")) {
                this.getNappiLattice().setText(this.getLanguage().equals("fin") ? "VAPAA" : "FREE");
                this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY)));
                this.vars[7] = "-";
            } else if (this.getNappiLattice().getText().equals("VAPAA") || this.getNappiLattice().getText().equals("FREE")) {
                this.getNappiLattice().setText(this.getLanguage().equals("fin") ? "HILA" : "LATTICE");
                this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY)));
                this.vars[7] = "l";
            }
        });
        valikko.getChildren().add(this.getNappiLattice());

        this.vars[8] = "s"; // save on

        GridPane.setHalignment(valikko, HPos.LEFT);
        asettelu.add(valikko, 0, 10, 2, 1);

        final Pane empty = new Pane();
        GridPane.setHalignment(empty, HPos.CENTER);
        asettelu.add(empty, 0, 11, 2, 1);

        return asettelu;
    }

    /**
     * @return the nappiFixed
     */
    private Button getNappiFixed() {
        return nappiFixed;
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
