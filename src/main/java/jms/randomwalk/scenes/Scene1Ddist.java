package jms.randomwalk.scenes;

import enums.DblSizes;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
 * Class for 1D Distance.
 */
public class Scene1Ddist extends Data {

    private String language;
    private final Button nappiLattice;
    private TextField setNumParticles;

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
    public Scene1Ddist(String language) {
        super();
        this.setLanguage(language);
        this.nappiLattice = new Button(this.getLanguage().equals("fin") ? "VAPAA" : "FREE");
        this.vars = new String[]{
            "B",    // vars[0] which simulation         USER
            "0",    // vars[1] particles                USER
            "0.1",  // vars[2] diameter                 n/a
            "0",    // vars[3] steps                    USER
            "1",    // vars[4] dimension (1D)           n/a
            "-",    // vars[5] efficiency or sawplot    n/a
            "f",    // vars[6] fixed                    n/a
            "-",    // vars[7] lattice/free             USER
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
     * Create GUI for 1D distance.
     * @return 1D DISTANCE SCENE
     */
    public Parent getScene1Ddist() {
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
        this.vars[2] = "0"; // (diameter of particl)

        Label labNumSteps = new Label(this.getLanguage().equals("fin") ? "askelten lukumäärä:" : "number of steps:");
        TextField setNumSteps = new TextField("");
        setNumSteps.setOnKeyReleased(e -> {
            if (isNumInteger(setNumSteps.getText().trim())) {
                this.vars[3] = setNumSteps.getText().trim();
            } else {
                this.vars[3] = "0";
            }
        });

        this.vars[4] = "1"; // dimension
        this.vars[5] = "-"; // efficiency or sawplot
        this.vars[6] = "f"; // fixed

        /*
         * ...THEIR PLACEMENTS
         */
        GridPane.setHalignment(labNumParticles, HPos.LEFT);
        asettelu.add(labNumParticles, 0, 0);
        GridPane.setHalignment(this.setNumParticles, HPos.CENTER);
        this.setNumParticles.setMinWidth(DblSizes.BUTW.getDblSize());
        this.setNumParticles.setMaxWidth(DblSizes.BUTW.getDblSize());
        asettelu.add(this.setNumParticles, 0, 1);

        GridPane.setHalignment(labNumSteps, HPos.LEFT);
        asettelu.add(labNumSteps, 0, 2);
        GridPane.setHalignment(setNumSteps, HPos.CENTER);
        setNumSteps.setMinWidth(DblSizes.BUTW.getDblSize());
        setNumSteps.setMaxWidth(DblSizes.BUTW.getDblSize());
        asettelu.add(setNumSteps, 0, 3);

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
