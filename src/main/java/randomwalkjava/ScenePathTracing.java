
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
 * Class for Path Tracing
 */
@SuppressWarnings("SameReturnValue")
class ScenePathTracing extends Data {

    private String language;
    private final Button nappiFixed;
    private final Button nappiLattice;
    private ToggleButton setCharge0;
    private ToggleButton setCharge1;
    private ToggleButton setCharge2;
    private ToggleButton setDim1;
    private ToggleButton setDim2;
    private ToggleButton setDim3;
    private TextField setNumParticles;
    private TextField setNumSteps;
    private TextField setSizeParticles;

    /**
     * main class gets vars via this
     * @return clone of vars array
     */
    String[] getVars() {
        return this.vars.clone();
    }
 
    /**
     * initiating scene buttons and user variable array
     */
    ScenePathTracing(String language){
        super();
        this.setLanguage(language);
        this.nappiFixed = new Button(this.getLanguage().equals("fin") ? "KESKITETTY" : "FIXED");
        this.nappiLattice = new Button(this.getLanguage().equals("fin") ? "VAPAA" : "FREE");
        this.vars = new String[]{
            "0",    // vars[0] particles        USER
            "0.0",  // vars[1] diameter         USER
            "0",    // vars[2] charge           USER
            "0",    // vars[3] steps            USER
            "0",    // vars[4] dimension        USER
            "-",    // vars[5] mmc              n/a
            "f",    // vars[6] fixed(/spread)   USER
            "-",    // vars[7] (lattice/)free   USER
            "s"};   // vars[8] save (on)        n/a
    }

    /**
     * method for checking if user input in GUI is a double
     * @param str GUI input string
     * @return true if input is a double, false otherwise
     */
    private static boolean isNumDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
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
     * Create GUI for Path tracing
     * @return PATH TRACING SCENE
     */
    Parent getScenePath(){
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
        Label labNumParticles = new Label(this.getLanguage().equals("fin") ? "hiukkasten lukumäärä:" : "number of particles:");
        this.setNumParticles = new TextField("");
        this.setNumParticles.setOnKeyReleased(e -> {
            if (isNumInteger(this.setNumParticles.getText().trim())){
                if (this.setNumParticles.getText().trim().equals("0")){
                    this.setNumParticles.setText("1");
                    this.vars[0] = "1";
                } else {
                    this.vars[0] = this.setNumParticles.getText().trim();
                }
            } else
                this.vars[0] = "0";
        });

        Label labSizeParticles = new Label(this.getLanguage().equals("fin") ? "hiukkasten halkaisija:" : "diameter of particle:");
        this.setSizeParticles = new TextField("");
        this.setSizeParticles.setOnKeyReleased(e -> {
            if (isNumDouble(this.setSizeParticles.getText().trim()))
                this.vars[1] = this.setSizeParticles.getText().trim();
            else
                this.vars[1] = "0.0";
        });

        Label labCharge = new Label(this.getLanguage().equals("fin") ? "hiukkasten varaus:" : "charge of particles:");
        this.setCharge0 = new ToggleButton("0");
        this.setCharge0.setMinWidth(35);
        this.setCharge0.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setCharge0.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setCharge0.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setCharge0.setEffect(shadow));
        this.setCharge0.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setCharge0.setEffect(null));

        this.setCharge1 = new ToggleButton("1");
        this.setCharge1.setMinWidth(35);
        this.setCharge1.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setCharge1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setCharge1.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setCharge1.setEffect(shadow));
        this.setCharge1.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setCharge1.setEffect(null));

        this.setCharge2 = new ToggleButton("2");
        this.setCharge2.setMinWidth(35);
        this.setCharge2.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setCharge2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setCharge2.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setCharge2.setEffect(shadow));
        this.setCharge2.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setCharge2.setEffect(null));

        HBox setCharge = new HBox(this.setCharge0,this.setCharge1,this.setCharge2);
        setCharge.setSpacing(20);
        this.setCharge0.setOnMouseClicked(f -> {
            this.setCharge0.setBackground(new Background(new BackgroundFill(Color.CYAN,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "0";
        });
        this.setCharge1.setOnMouseClicked(f -> {
            this.setCharge0.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge1.setBackground(new Background(new BackgroundFill(Color.CYAN,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "1";
        });
        this.setCharge2.setOnMouseClicked(f -> {
            this.setCharge0.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setCharge2.setBackground(new Background(new BackgroundFill(Color.CYAN,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[2] = "2";
        });

        Label labNumSteps = new Label(this.getLanguage().equals("fin") ? "askelten lukumäärä:" : "number of steps:");
        this.setNumSteps = new TextField("");
        this.setNumSteps.setOnKeyReleased(e -> {
            if (isNumInteger(this.setNumSteps.getText().trim())){
                this.vars[3] = this.setNumSteps.getText().trim();
            } else
                this.vars[3] = "0";
        });

        Label labNumDimensions = new Label(this.getLanguage().equals("fin") ? "ulottuvuus:" : "dimension:");
        this.setDim1 = new ToggleButton("1");
        this.setDim1.setMinWidth(35);
        this.setDim1.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setDim1.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim1.setEffect(shadow));
        this.setDim1.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim1.setEffect(null));

        this.setDim2 = new ToggleButton("2");
        this.setDim2.setMinWidth(35);
        this.setDim2.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setDim2.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim2.setEffect(shadow));
        this.setDim2.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim2.setEffect(null));

        this.setDim3 = new ToggleButton("3");
        this.setDim3.setMinWidth(35);
        this.setDim3.setFont(Font.font("System Regular",FontWeight.BOLD, 15));
        this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        this.setDim3.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> setDim3.setEffect(shadow));
        this.setDim3.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> setDim3.setEffect(null));

        HBox setDimension = new HBox(this.setDim1,this.setDim2,this.setDim3);
        setDimension.setSpacing(20);
        this.setDim1.setOnMouseClicked(f -> {
            this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "1";
        });
        this.setDim2.setOnMouseClicked(f -> {
            this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "2";
        });
        this.setDim3.setOnMouseClicked(f -> {
            this.setDim1.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim2.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
            this.setDim3.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK,CornerRadii.EMPTY,Insets.EMPTY)));
            this.vars[4] = "3";
        });

        this.vars[5] = "-"; // mmc n/a

        /*
        * ...THEIR PLACEMENTS
        */
        GridPane.setHalignment(labNumParticles, HPos.LEFT);
        asettelu.add(labNumParticles, 0, 0);
        GridPane.setHalignment(this.setNumParticles, HPos.CENTER);
        this.setNumParticles.setMinWidth(getCompwidth());
        this.setNumParticles.setMaxWidth(getCompwidth());
        asettelu.add(this.setNumParticles, 0, 1);
        
        GridPane.setHalignment(labSizeParticles, HPos.LEFT);
        asettelu.add(labSizeParticles, 0, 2);
        GridPane.setHalignment(this.setSizeParticles, HPos.CENTER);
        this.setSizeParticles.setMinWidth(getCompwidth());
        this.setSizeParticles.setMaxWidth(getCompwidth());
        asettelu.add(this.setSizeParticles, 0, 3);

        GridPane.setHalignment(labCharge, HPos.LEFT);
        asettelu.add(labCharge, 0, 4);
        GridPane.setHalignment(setCharge, HPos.CENTER);
        setCharge.setMinWidth(getCompwidth());
        setCharge.setMaxWidth(getCompwidth());
        asettelu.add(setCharge, 0, 5);

        GridPane.setHalignment(labNumSteps, HPos.LEFT);
        asettelu.add(labNumSteps, 0, 6);
        GridPane.setHalignment(this.setNumSteps, HPos.CENTER);
        this.setNumSteps.setMinWidth(getCompwidth());
        this.setNumSteps.setMaxWidth(getCompwidth());
        asettelu.add(this.setNumSteps, 0, 7);
        
        GridPane.setHalignment(labNumDimensions, HPos.LEFT);
        asettelu.add(labNumDimensions, 0, 8);
        GridPane.setHalignment(setDimension, HPos.CENTER);
        setDimension.setMinWidth(getCompwidth());
        setDimension.setMaxWidth(getCompwidth());
        asettelu.add(setDimension, 0, 9);

        /*
        * BUTTON: FIXED
        */
        this.getNappiFixed().setMinWidth(getCompwidth());
        this.getNappiFixed().setMaxWidth(getCompwidth());
        this.getNappiFixed().setBackground(new Background(new BackgroundFill(Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
        this.getNappiFixed().setId("fixed");
        this.getNappiFixed().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getNappiFixed().setEffect(shadow));
        this.getNappiFixed().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getNappiFixed().setEffect(null));
        this.getNappiFixed().setOnMouseClicked((MouseEvent event) -> {
            if (this.getNappiFixed().getText().equals("HAJAUTETTU") || this.getNappiFixed().getText().equals("SPREAD")){
                this.getNappiFixed().setText(this.getLanguage().equals("fin") ? "KESKITETTY" : "FIXED");
                this.getNappiFixed().setBackground(new Background(new BackgroundFill(Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[6] = "f";
            } else if (this.getNappiFixed().getText().equals("KESKITETTY") || this.getNappiFixed().getText().equals("FIXED")){
                this.getNappiFixed().setText(this.getLanguage().equals("fin") ? "HAJAUTETTU" : "SPREAD");
                this.getNappiFixed().setBackground(new Background(new BackgroundFill(Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[6] = "-";
            }
        });
        valikko.getChildren().add(this.getNappiFixed());

        /*
        * BUTTON: LATTICE
        */
        this.getNappiLattice().setMinWidth(getCompwidth());
        this.getNappiLattice().setMaxWidth(getCompwidth());
        this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
        this.getNappiLattice().setId("lattice");
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> this.getNappiLattice().setEffect(shadow));
        this.getNappiLattice().addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> this.getNappiLattice().setEffect(null));
        this.getNappiLattice().setOnMouseClicked((MouseEvent event) -> {
            if (this.getNappiLattice().getText().equals("HILA") || this.getNappiLattice().getText().equals("LATTICE")){
                this.getNappiLattice().setText(this.getLanguage().equals("fin") ? "VAPAA" : "FREE");
                this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
                this.vars[7] = "-";
            } else if (this.getNappiLattice().getText().equals("VAPAA") || this.getNappiLattice().getText().equals("FREE")){
                this.getNappiLattice().setText(this.getLanguage().equals("fin") ? "HILA" : "LATTICE");
                this.getNappiLattice().setBackground(new Background(new BackgroundFill(Color.GOLD,CornerRadii.EMPTY,Insets.EMPTY)));
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
     * @return the nappiFixed
     */
    @Contract(pure = true)
    private Button getNappiFixed() {
        return nappiFixed;
    }

    /**
     * @return the nappiLattice
     */
    @Contract(pure = true)
    private Button getNappiLattice() {
        return nappiLattice;
    }

    /**
     * @return the language
     */
    @Contract(pure = true)
    private String getLanguage() { return this.language; }

    /**
     * @param language the language to set
     */
    private void setLanguage(String language) { this.language = language; }

}
